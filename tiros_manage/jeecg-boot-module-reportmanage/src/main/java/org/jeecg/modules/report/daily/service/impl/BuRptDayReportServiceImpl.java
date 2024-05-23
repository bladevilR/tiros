package org.jeecg.modules.report.daily.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.report.daily.bean.*;
import org.jeecg.modules.report.daily.bean.vo.BuDayReportQueryVO;
import org.jeecg.modules.report.daily.mapper.*;
import org.jeecg.modules.report.daily.service.BuRptDayReportService;
import org.jeecg.modules.report.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.mapper.BuFaultInfoReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 生产日报 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Service
public class BuRptDayReportServiceImpl extends ServiceImpl<BuRptDayReportMapper, BuRptDayReport> implements BuRptDayReportService {

    @Resource
    private BuRptDayReportMapper buRptDayReportMapper;
    @Resource
    private BuRptDayReportWorkPlanMapper buRptDayReportWorkPlanMapper;
    @Resource
    private BuRptDayReportNextWorkMapper buRptDayReportNextWorkMapper;
    @Resource
    private BuRptDayReportOutinMapper buRptDayReportOutinMapper;
    @Resource
    private BuRptDayReportFaultMapper buRptDayReportFaultMapper;
    @Resource
    private BuRptDayReportOtherFaultMapper buRptDayReportOtherFaultMapper;
    @Resource
    private BuWorkOrderTaskReportMapper buWorkOrderTaskReportMapper;
    @Resource
    private BuOutsourceOutinDetailReportMapper buOutsourceOutinDetailReportMapper;
    @Resource
    private BuFaultInfoReportMapper buFaultInfoReportMapper;
    @Resource
    private BuMtrWorkshopGroupReportMapper buMtrWorkshopGroupReportMapper;


    /**
     * @see BuRptDayReportService#getDayReport(BuDayReportQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRptDayReport getDayReport(BuDayReportQueryVO queryVO) throws Exception {
        BuRptDayReport dayReport = buRptDayReportMapper.selectByCondition(queryVO);
        if (null == dayReport) {
            // 生成生产日报
            dayReport = generateDayReport(queryVO);
        }

        extractOtherFaultMessage(dayReport);
        return dayReport;
    }

    /**
     * @see BuRptDayReportService#generateDayReport(BuDayReportQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRptDayReport generateDayReport(BuDayReportQueryVO queryVO) throws Exception {
        // 获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String lineId = null;
        String workshopId = null;
        String companyId = null;
        if (null != sysUser) {
            lineId = sysUser.getLineId();
            workshopId = sysUser.getWorkshopId();
            companyId = sysUser.getCompanyId();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String dayString = simpleDateFormat.format(queryVO.getDay());
        if (StringUtils.isBlank(queryVO.getWorkshop())) {
            String workshopName = buRptDayReportMapper.selectWorkshopNameByWorkshopId(queryVO.getWorkshopId());
            queryVO.setWorkshop(workshopName);
        }

        // 生产日报信息
        List<Map<String, Object>> dbMapList = buRptDayReportMapper.selectRepairInfo(queryVO);
        double mileage = 0D;
        int dayIndex = 0;
        for (Map<String, Object> dbMap : dbMapList) {
            Object dbMileage = dbMap.get("mileage");
            if (dbMileage != null) {
                mileage = DataTypeCastUtil.transNumber(dbMileage).doubleValue();
            }
            Object dbDayIndex = dbMap.get("dayIndex");
            if (dbDayIndex != null) {
                dayIndex = DataTypeCastUtil.transNumber(dbDayIndex).intValue();
            }
        }

        String reportId = UUIDGenerator.generate();
        BuRptDayReport dayReport = new BuRptDayReport()
                .setId(reportId)
                .setTitle(queryVO.getTrainNo() + queryVO.getWorkshop() + dayString + "生产日报")
                .setPeriod(dayString + "-" + dayString)
                .setTrainNo(queryVO.getTrainNo())
                .setMileage(mileage)
                .setReportDate(queryVO.getDay())
                .setDayIndex(dayIndex)
                .setWorkshopId(queryVO.getWorkshopId())
                .setWorkshop(queryVO.getWorkshop())
                .setCompanyId(companyId)
                .setLineId(lineId)
                .setUserName("")
                .setWorkPlanList(new ArrayList<>())
                .setNextWorkList(new ArrayList<>())
                .setOutinList(new ArrayList<>())
                .setFaultList(new ArrayList<>())
                .setOtherFaultList(new ArrayList<>());

        // 当日生产
        generateWorkPlanList(queryVO, dayReport);
        // 次日生产安排
        BuDayReportQueryVO nextDayQueryVO = getNextDayReportQueryVO(queryVO);
        generateNextWorkList(nextDayQueryVO, dayReport);
        // 委外件送修
        generateOutinList(queryVO, dayReport);
        // 当日故障
        generateFaultList(queryVO, dayReport);
        // 当日工装/设备故障
        dayReport.setToolFault("无");
        dayReport.setAssetFault("无");

        return dayReport;
    }

    /**
     * @see BuRptDayReportService#saveDayReport(BuRptDayReport)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveDayReport(BuRptDayReport dayReport) throws Exception {
        // 判断数据库是否存在，存在则删除旧的日报
        LambdaQueryWrapper<BuRptDayReport> wrapper = new LambdaQueryWrapper<BuRptDayReport>()
                .eq(BuRptDayReport::getReportDate, dayReport.getReportDate())
                .eq(BuRptDayReport::getTrainNo, dayReport.getTrainNo())
                .eq(BuRptDayReport::getWorkshopId, dayReport.getWorkshopId());
        List<BuRptDayReport> dbDayReportList = buRptDayReportMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(dbDayReportList)) {
            List<String> dbDayReportIdList = dbDayReportList.stream()
                    .map(BuRptDayReport::getId)
                    .collect(Collectors.toList());
            // 删除关联信息
//            deleteDayReportRelation(dbDayReportIdList);
            // 删除旧的日报
            buRptDayReportMapper.deleteBatchIds(dbDayReportIdList);
        }

        // 插入生产日报
        buRptDayReportMapper.insert(dayReport);
        // 插入关联信息
//        insertDayReportRelation(dayReport);
        // 暂时不使用关联表，使用json格式的reportContent保存日报消息

        return true;
    }


    private void generateWorkPlanList(BuDayReportQueryVO queryVO, BuRptDayReport dayReport) {
        // 查询车间班组，显示车间所有班组，班组任务可为空
        LambdaQueryWrapper<BuMtrWorkshopGroup> groupWrapper = new LambdaQueryWrapper<BuMtrWorkshopGroup>()
                .eq(BuMtrWorkshopGroup::getWorkshopId, dayReport.getWorkshopId());
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupReportMapper.selectList(groupWrapper);

        // 查询工单任务
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskReportMapper.selectListByBuDayReportQueryVO(queryVO);
        // 工单任务按班组分组
        Map<String, List<BuWorkOrderTask>> groupIdOrderTaskListMap = new HashMap<>(8);
        Map<String, BigDecimal> taskIdWorkTimeSumMap = new HashMap<>(8);
        Map<String, Set<String>> taskIdUserIdSetMap = new HashMap<>(8);
        if (CollectionUtils.isNotEmpty(orderTaskList)) {
            groupIdOrderTaskListMap = orderTaskList.stream()
                    .collect(Collectors.groupingBy(BuWorkOrderTask::getGroupId));

            // 查询任务人员安排
            List<String> orderTaskIdList = orderTaskList.stream()
                    .map(BuWorkOrderTask::getId)
                    .collect(Collectors.toList());
            List<Map<String, Object>> taskIdStaffArrangeList = buWorkOrderTaskReportMapper.selectTaskStaffArrangeByTaskIdList(orderTaskIdList);

            for (Map<String, Object> taskIdStaffArrange : taskIdStaffArrangeList) {
                String taskId = (String) taskIdStaffArrange.get("taskId");
                String userId = (String) taskIdStaffArrange.get("userId");
                BigDecimal workTime = DataTypeCastUtil.transNumber(taskIdStaffArrange.get("workTime"));

                BigDecimal workTimeSum = taskIdWorkTimeSumMap.getOrDefault(taskId, BigDecimal.ZERO);
                taskIdWorkTimeSumMap.put(taskId, workTimeSum.add(workTime));

                Set<String> userIdSet = taskIdUserIdSetMap.getOrDefault(taskId, new HashSet<>());
                userIdSet.add(userId);
                taskIdUserIdSetMap.put(taskId, userIdSet);
            }
        }

        // 生成当日生产数据
        List<BuRptDayReportWorkPlan> workPlanList = dayReport.getWorkPlanList();
        for (BuMtrWorkshopGroup group : groupList) {
            String groupName = group.getGroupName();
            List<BuWorkOrderTask> taskList = groupIdOrderTaskListMap.get(group.getId());
            if (null == taskList) {
                taskList = new ArrayList<>();
            }

            // 过滤工单任务：已完成，且任务完成时间和日报日期为同一天
            List<BuWorkOrderTask> finishTaskList = taskList.stream()
                    .filter(task -> task.getTaskStatus() == 2 && DateUtils.isSameDay(task.getTaskFinish(), queryVO.getDay()))
                    .collect(Collectors.toList());

            boolean finished = taskList.size() == finishTaskList.size();
            BigDecimal workTimeSum = finishTaskList.stream()
                    .map(task -> taskIdWorkTimeSumMap.getOrDefault(task.getId(), BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 获取任务人员安排数量，按人员id去重
            Set<String> allUserIdSet = new HashSet<>();
            for (BuWorkOrderTask task : taskList) {
                Set<String> userIdSet = taskIdUserIdSetMap.getOrDefault(task.getId(), new HashSet<>());
                allUserIdSet.addAll(userIdSet);
            }
            int arrangePeoplesSum = allUserIdSet.size();

            BuRptDayReportWorkPlan workPlan = new BuRptDayReportWorkPlan()
                    .setReportId(dayReport.getId())
                    .setGroupName(groupName)
                    .setPlanContent(getWorkContents(taskList))
                    .setActContent(getWorkContents(finishTaskList))
                    .setFinished(finished ? 1 : 0)
                    .setPeoples(arrangePeoplesSum)
                    .setWorkTime(workTimeSum.floatValue());
            if (0 == taskList.size()) {
                workPlan.setFinished(null);
            }
            workPlanList.add(workPlan);
        }

    }

    private void generateNextWorkList(BuDayReportQueryVO nextDayQueryVO, BuRptDayReport dayReport) {
        // 查询车间班组，显示车间所有班组，班组任务可为空
        LambdaQueryWrapper<BuMtrWorkshopGroup> groupWrapper = new LambdaQueryWrapper<BuMtrWorkshopGroup>()
                .eq(BuMtrWorkshopGroup::getWorkshopId, dayReport.getWorkshopId());
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupReportMapper.selectList(groupWrapper);

        // 查询工单任务，按班组分组
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskReportMapper.selectListByBuDayReportQueryVO(nextDayQueryVO);
        Map<String, List<BuWorkOrderTask>> groupIdOrderTaskListMap = new HashMap<>(8);
        if (CollectionUtils.isNotEmpty(orderTaskList)) {
            groupIdOrderTaskListMap = orderTaskList.stream()
                    .collect(Collectors.groupingBy(BuWorkOrderTask::getGroupId));
        }

        // 生成次日生产安排数据
        List<BuRptDayReportNextWork> nextWorkList = dayReport.getNextWorkList();
        for (BuMtrWorkshopGroup group : groupList) {
            String groupName = group.getGroupName();
            List<BuWorkOrderTask> taskList = groupIdOrderTaskListMap.get(group.getId());
            if (null == taskList) {
                taskList = new ArrayList<>();
            }

            // 过滤工单任务：计划工单的任务
            List<BuWorkOrderTask> planTaskList = taskList.stream()
                    .filter(task -> task.getOrderType() == 1)
                    .collect(Collectors.toList());
            // 过滤工单任务：临时工单的任务
            List<BuWorkOrderTask> tempTaskList = taskList.stream()
                    .filter(task -> task.getOrderType() == 3)
                    .collect(Collectors.toList());
            // 过滤工单任务：故障工单的任务
            List<BuWorkOrderTask> faultTaskList = taskList.stream()
                    .filter(task -> task.getOrderType() == 2)
                    .collect(Collectors.toList());

            BuRptDayReportNextWork nextWork = new BuRptDayReportNextWork()
                    .setReportId(dayReport.getId())
                    .setGroupName(groupName)
                    .setWorkContent(getWorkContents(planTaskList))
                    .setTempWork(getWorkContents(tempTaskList))
                    .setTrackContent(getWorkContents(faultTaskList));
            nextWorkList.add(nextWork);
        }

    }

    private void generateOutinList(BuDayReportQueryVO queryVO, BuRptDayReport dayReport) {
        // 查询工单任务
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskReportMapper.selectListByBuDayReportQueryVO(queryVO);
        Set<String> orderIdSet = orderTaskList.stream()
                .map(BuWorkOrderTask::getOrderId)
                .collect(Collectors.toSet());
        // 查询委外出入库明细
        List<BuOutsourceOutinDetail> outinDetailList = buOutsourceOutinDetailReportMapper.selectListByOrderIdList(new ArrayList<>(orderIdSet), queryVO.getTrainNo());

        // 生成委外件送修数据
        List<BuRptDayReportOutin> outinList = dayReport.getOutinList();
        if (CollectionUtils.isNotEmpty(outinDetailList)) {
            Map<String, List<BuOutsourceOutinDetail>> assetTypeIdOutinDetailListMap = outinDetailList.stream()
                    .collect(Collectors.groupingBy(BuOutsourceOutinDetail::getAssetTypeId));
            for (Map.Entry<String, List<BuOutsourceOutinDetail>> assetTypeIdOutinDetailListEntry : assetTypeIdOutinDetailListMap.entrySet()) {
                String assetTypeId = assetTypeIdOutinDetailListEntry.getKey();
                List<BuOutsourceOutinDetail> detailList = assetTypeIdOutinDetailListEntry.getValue();
                String assetTypeName = detailList.get(0).getAssetTypeName();

                // 过滤出入库明细：出库
                List<BuOutsourceOutinDetail> outDetailList = detailList.stream()
                        .filter(detail -> detail.getBillType() == 0)
                        .collect(Collectors.toList());
                // 过滤出入库明细：入库
                List<BuOutsourceOutinDetail> inDetailList = detailList.stream()
                        .filter(detail -> detail.getBillType() == 1)
                        .collect(Collectors.toList());

                BuRptDayReportOutin outin = new BuRptDayReportOutin()
                        .setReportId(dayReport.getId())
                        .setAssetTypeId(assetTypeId)
                        .setAssetTypeName(assetTypeName)
                        .setSendDesc(getOutInDesc(outDetailList))
                        .setReceiveDesc(getOutInDesc(inDetailList));
                outinList.add(outin);
            }
        }
    }

    private void generateFaultList(BuDayReportQueryVO queryVO, BuRptDayReport dayReport) {
        // 查询故障信息
        List<BuFaultInfo> faultInfoList = buFaultInfoReportMapper.selectListByBuDayReportQueryVO(queryVO);
        extractHandleDesc(faultInfoList);

        List<BuRptDayReportFault> faultList = dayReport.getFaultList();
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            for (BuFaultInfo faultInfo : faultInfoList) {
                BuRptDayReportFault fault = new BuRptDayReportFault()
                        .setReportId(dayReport.getId())
                        .setFaultTime(faultInfo.getReportTime())
                        .setReportUser(faultInfo.getReportUserName())
                        .setFaultDesc(faultInfo.getFaultDesc())
                        .setIsHandle(faultInfo.getStatus() == 0 ? 0 : 1)
                        .setDutyEngineer(faultInfo.getDutyEngineerName())
                        .setHandleDesc(faultInfo.getHandleDesc());
                faultList.add(fault);
            }
        }
    }

    private String getWorkContents(List<BuWorkOrderTask> orderTaskList) {
        StringBuilder contents = new StringBuilder();
        if (CollectionUtils.isNotEmpty(orderTaskList)) {
            AtomicInteger index = new AtomicInteger(1);
            for (BuWorkOrderTask task : orderTaskList) {
                contents.append(index.getAndIncrement())
                        .append("、")
                        .append(task.getTaskContent())
                        .append("\r\n");
            }
        }
        return contents.toString();
    }

    private BuDayReportQueryVO getNextDayReportQueryVO(BuDayReportQueryVO queryVO) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(queryVO.getDay());
        calendar.add(Calendar.DATE, 1);
        Date nextDay = calendar.getTime();
        return new BuDayReportQueryVO()
                .setDay(nextDay)
                .setTrainNo(queryVO.getTrainNo())
                .setWorkshopId(queryVO.getWorkshopId())
                .setWorkshop(queryVO.getWorkshop());
    }

    private String getOutInDesc(List<BuOutsourceOutinDetail> outinDetailList) {
        StringBuilder desc = new StringBuilder();
        if (CollectionUtils.isNotEmpty(outinDetailList)) {
            for (BuOutsourceOutinDetail outinDetail : outinDetailList) {
                Integer billType = outinDetail.getBillType();
                desc.append(DateUtils.date_sdf.get().format(outinDetail.getTransferDate()))
                        .append(billType == 0 ? "送修" : "返厂")
                        .append(outinDetail.getAssetName())
                        .append("(")
                        .append(outinDetail.getAssetNo())
                        .append(")")
                        .append(outinDetail.getAmount())
                        .append("件")
                        .append("；")
                        .append(System.lineSeparator());
            }
        }
        return desc.toString();
    }

    private void extractHandleDesc(List<BuFaultInfo> faultInfoList) {
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            for (BuFaultInfo faultInfo : faultInfoList) {
                List<BuFaultHandleRecord> handleRecordList = faultInfo.getHandleRecordList();
                if (CollectionUtils.isNotEmpty(handleRecordList)) {
                    String solutionDescJoin = handleRecordList.stream()
                            .map(BuFaultHandleRecord::getSolutionDesc)
                            .collect(Collectors.joining(";\r\n"));
                    faultInfo.setHandleDesc(solutionDescJoin);
                }
            }
        }
    }

    private void extractOtherFaultMessage(BuRptDayReport dayReport) {
        if (null == dayReport) {
            return;
        }

        List<BuRptDayReportOtherFault> otherFaultList = dayReport.getOtherFaultList();
        if (CollectionUtils.isNotEmpty(otherFaultList)) {
            List<String> toolFaultDescList = otherFaultList.stream()
                    .filter(otherFault -> otherFault.getFaultType() == 1)
                    .map(BuRptDayReportOtherFault::getFaultDesc)
                    .collect(Collectors.toList());
            List<String> assetFaultDescList = otherFaultList.stream()
                    .filter(otherFault -> otherFault.getFaultType() == 2)
                    .map(BuRptDayReportOtherFault::getFaultDesc)
                    .collect(Collectors.toList());
            dayReport.setToolFault(StringUtils.join(toolFaultDescList, ";"))
                    .setAssetFault(StringUtils.join(assetFaultDescList, ";"));
        }
    }

    private void deleteDayReportRelation(List<String> reportIdList) {
        if (CollectionUtils.isEmpty(reportIdList)) {
            return;
        }

        buRptDayReportWorkPlanMapper.delete(new LambdaQueryWrapper<BuRptDayReportWorkPlan>()
                .in(BuRptDayReportWorkPlan::getReportId, reportIdList));
        buRptDayReportNextWorkMapper.delete(new LambdaQueryWrapper<BuRptDayReportNextWork>()
                .in(BuRptDayReportNextWork::getReportId, reportIdList));
        buRptDayReportOutinMapper.delete(new LambdaQueryWrapper<BuRptDayReportOutin>()
                .in(BuRptDayReportOutin::getReportId, reportIdList));
        buRptDayReportFaultMapper.delete(new LambdaQueryWrapper<BuRptDayReportFault>()
                .in(BuRptDayReportFault::getReportId, reportIdList));
        buRptDayReportOtherFaultMapper.delete(new LambdaQueryWrapper<BuRptDayReportOtherFault>()
                .in(BuRptDayReportOtherFault::getReportId, reportIdList));
    }

    private void insertDayReportRelation(BuRptDayReport dayReport) {
        if (null == dayReport) {
            return;
        }

        List<BuRptDayReportWorkPlan> workPlanList = dayReport.getWorkPlanList();
        List<BuRptDayReportNextWork> nextWorkList = dayReport.getNextWorkList();
        List<BuRptDayReportOutin> outinList = dayReport.getOutinList();
        List<BuRptDayReportFault> faultList = dayReport.getFaultList();
        if (CollectionUtils.isNotEmpty(workPlanList)) {
            for (BuRptDayReportWorkPlan workPlan : workPlanList) {
                buRptDayReportWorkPlanMapper.insert(workPlan);
            }
        }
        if (CollectionUtils.isNotEmpty(nextWorkList)) {
            for (BuRptDayReportNextWork nextWork : nextWorkList) {
                buRptDayReportNextWorkMapper.insert(nextWork);
            }
        }
        if (CollectionUtils.isNotEmpty(outinList)) {
            for (BuRptDayReportOutin outin : outinList) {
                buRptDayReportOutinMapper.insert(outin);
            }
        }
        if (CollectionUtils.isNotEmpty(faultList)) {
            for (BuRptDayReportFault fault : faultList) {
                buRptDayReportFaultMapper.insert(fault);
            }
        }

        String toolFault = dayReport.getToolFault();
        String assetFault = dayReport.getAssetFault();
        BuRptDayReportOtherFault toolOtherFault = new BuRptDayReportOtherFault()
                .setReportId(dayReport.getId())
                .setFaultType(1)
                .setFaultDesc(toolFault);
        buRptDayReportOtherFaultMapper.insert(toolOtherFault);
        BuRptDayReportOtherFault assetOtherFault = new BuRptDayReportOtherFault()
                .setReportId(dayReport.getId())
                .setFaultType(2)
                .setFaultDesc(assetFault);
        buRptDayReportOtherFaultMapper.insert(assetOtherFault);
    }

}
