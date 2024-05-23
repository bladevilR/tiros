package org.jeecg.modules.dispatch.workorder.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.rpt.service.AlertRecordService;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.rpt.service.PlanProgressUpdateService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.OrderStatusCheckUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormWorkRecord;
import org.jeecg.modules.dispatch.planform.mapper.BuPlanFormWorkRecordMapper;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskMustReplace;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanTaskMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairTaskMustReplaceMapper;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.bo.BuRepairReguDetailBO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskQueryVOForApp;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskSubmitVO;
import org.jeecg.modules.dispatch.workorder.mapper.*;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Service
public class BuWorkOrderTaskServiceImpl extends ServiceImpl<BuWorkOrderTaskMapper, BuWorkOrderTask> implements BuWorkOrderTaskService {

    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuWorkOrderAnnexMapper buWorkOrderAnnexMapper;
    @Resource
    private BuRepairTaskStaffArrangeMapper buRepairTaskStaffArrangeMapper;
    @Resource
    private BuRepairTaskMustReplaceMapper buRepairTaskMustReplaceMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderMaterialMapper buWorkOrderMaterialMapper;
    @Resource
    private BuMaterialApplyDetailDispatchMapper buMaterialApplyDetailDispatchMapper;
    @Resource
    private BuWorkOrderToolMapper buWorkOrderToolMapper;
    @Resource
    private BuWorkOrderTaskEquMapper buWorkOrderTaskEquMapper;
    @Resource
    private BuRepairPlanTaskMapper buRepairPlanTaskMapper;
    @Resource
    private BuWorkOrderTaskFormInstMapper buWorkOrderTaskFormInstMapper;
    @Resource
    private BuPlanFormWorkRecordMapper buPlanFormWorkRecordMapper;
    @Resource
    private BuOutsourceOutinDetailMapper buOutsourceOutinDetailMapper;
    @Resource
    private BuOutsourceOutinMapper buOutsourceOutinMapper;
    @Resource
    private BuMaximoLocationDispatchMapper buMaximoLocationDispatchMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private AlertRecordService alertRecordService;
    @Resource
    private PlanProgressUpdateService planProgressUpdateService;
    @Resource
    private WorkflowForwardService workflowForwardService;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private BuPlWorkRecordExcelDataMapper workRecordExcelDataMapper;

    private static final String MAXIMO_ASSET_CHANGE_URL_CONFIG_CODE = "maximoAssetChangeUrlPrefix";


    /**
     * @see BuWorkOrderTaskService#listTask(BuWorkOrderTaskQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderTask> listTask(BuWorkOrderTaskQueryVO queryVO) throws Exception {
        //        if (CollectionUtils.isNotEmpty(taskList)) {
//            // 暂时不计算进度
//            taskList.forEach(task -> task.setProgress("0%"));
//        }
        return buWorkOrderTaskMapper.selectTaskList(queryVO);
    }

    /**
     * @see BuWorkOrderTaskService#listTaskForApp(BuWorkOrderTaskQueryVOForApp)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderTask> listTaskForApp(BuWorkOrderTaskQueryVOForApp queryVO) throws Exception {
        //        if (CollectionUtils.isNotEmpty(taskList)) {
//            // 暂时不计算进度
//            taskList.forEach(task -> task.setProgress("0%"));
//        }
        return buWorkOrderTaskMapper.selectTaskListForApp(queryVO);
    }

    /**
     * @see BuWorkOrderTaskService#startOrderTask(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean startOrderTask(String taskId) throws Exception {
        Date now = new Date();

        BuWorkOrderTask orderTask = buWorkOrderTaskMapper.selectById(taskId);

        // 工单任务状态为未开始，才进行更新
        if (null == orderTask.getTaskStatus() || 0 == orderTask.getTaskStatus()) {
            String orderId = orderTask.getOrderId();
            BuWorkOrder order = buWorkOrderMapper.selectById(orderId);

            // 更新工单任务开始
            orderTask.setTaskStart(now)
                    .setTaskStatus(1);
            buWorkOrderTaskMapper.updateById(orderTask);

            boolean isFromPlan = null != order.getGenerate() && 1 == order.getGenerate();
            if (isFromPlan) {
                planProgressUpdateService.updatePlanTaskStart(Collections.singletonList(orderTask.getTaskObjId()), now);
            }
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        return true;
    }

    /**
     * @see BuWorkOrderTaskService#submitTask(BuWorkOrderTaskSubmitVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitTask(BuWorkOrderTaskSubmitVO submitVO) throws Exception {
        Date now = new Date();
        String orderTaskId = submitVO.getTaskId();

        // 查询工单任务
        BuWorkOrderTask orderTask = buWorkOrderTaskMapper.selectById(orderTaskId);
        if (null == orderTask) {
            throw new JeecgBootException("工单任务不存在");
        }
        String orderId = orderTask.getOrderId();
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        // 检查工单状态
        OrderStatusCheckUtil.checkWorkingOrderSubmitToMonitor(order.getOrderCode(), order.getOrderStatus());

        // 修改工单任务的状态
        Date taskStart = orderTask.getTaskStart();
        if (null == taskStart) {
            taskStart = now;
        }
        int taskHour = (int) ((now.getTime() - taskStart.getTime()) / (3600 * 1000));
        orderTask.setTaskTime(taskHour)
                .setReportTime(submitVO.getReportTime())
                .setTaskStatus(2)
                .setTaskFinish(now);
        buWorkOrderTaskMapper.updateById(orderTask);

        // 修改工单的作业状态
        boolean taskAllFinished = taskAllFinishedUpdateOrderWorkStatus(now, orderId);

        // 任务提交同时，修改列计划任务/列计划的状态
        // 修改列计划任务的状态
        planProgressUpdateService.updatePlanTaskFinish(Collections.singletonList(orderTask.getTaskObjId()), now);
        // 修改列计划的进度和状态
        planProgressUpdateService.updatePlanProgressAndStatus(Collections.singletonList(order.getPlanId()), now);

        // 如果是委外接收任务，去修改委外接收任务和对应的委外送修任务的状态
        Integer outTask = orderTask.getOutTask();
        if (outTask != null) {
            //设置合同的当前车辆
            BuOutsourceOutin outin = buOutsourceOutinMapper.selectOne(Wrappers.<BuOutsourceOutin>lambdaQuery().eq(BuOutsourceOutin::getOrderTaskId, orderTaskId));
            if (outin != null) {
                String contractId = outin.getContractId();
                if (StringUtils.isNotEmpty(contractId)) {
                    buOutsourceOutinMapper.updateContractCurTrain(contractId, outin.getTrainNo(), outin.getBillType());
                }
            }
        }
        if (outTask != null && outTask == 2) {
            updateOutsourceStatus(now, orderTaskId);
        }

        // 关键任务，发送消息给质检员
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null != orderTask.getImportant() && 1 == orderTask.getImportant()) {
            sendMessageByNewThread(orderTask, sysUser.getUsername());
        }

        // 更新任务关联的作业记录表实例的完工日期
        updateTaskRelationFormWorkRecordInstFinishDate(orderTaskId, now);

        if (taskAllFinished) {
            // 工单下任务都已完成，提交工单当前流程的第一个任务
            Map<String, Object> vars = new HashMap<>();
            vars.put("writeSubmit", 1);
            workflowForwardService.completeOrderFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "提交工单任务（作业填报后提交）", vars);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        return true;
    }

    /**
     * @see BuWorkOrderTaskService#submitWorkingOrderForApp(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitWorkingOrderForApp(String orderId) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 查询工单
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        // 检查工单状态
        OrderStatusCheckUtil.checkWorkingOrderSubmitToMonitor(order.getOrderCode(), order.getOrderStatus());

        // 修改工单任务的状态
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .eq(BuWorkOrderTask::getOrderId, orderId);
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskMapper.selectList(orderTaskWrapper);
        for (BuWorkOrderTask orderTask : orderTaskList) {
            Date taskStart = orderTask.getTaskStart();
            if (null == taskStart) {
                taskStart = now;
            }
            int taskHour = (int) ((now.getTime() - taskStart.getTime()) / (3600 * 1000));
            orderTask.setTaskTime(taskHour)
                    .setReportTime(taskHour)
                    .setTaskStatus(2)
                    .setTaskFinish(now);
            buWorkOrderTaskMapper.updateById(orderTask);

            // 任务提交同时，修改列计划任务/列计划的状态
            // 修改列计划任务的状态
            planProgressUpdateService.updatePlanTaskFinish(Collections.singletonList(orderTask.getTaskObjId()), now);
            // 修改列计划的进度和状态
            planProgressUpdateService.updatePlanProgressAndStatus(Collections.singletonList(order.getPlanId()), now);

            // 如果是委外接收任务，去修改委外接收任务和对应的委外送修任务的状态
            Integer outTask = orderTask.getOutTask();
            if (outTask != null) {
                //设置合同的当前车辆
                BuOutsourceOutin outin = buOutsourceOutinMapper.selectOne(Wrappers.<BuOutsourceOutin>lambdaQuery().eq(BuOutsourceOutin::getOrderTaskId, orderTask.getId()));
                if (outin != null) {
                    String contractId = outin.getContractId();
                    if (StringUtils.isNotEmpty(contractId)) {
                        buOutsourceOutinMapper.updateContractCurTrain(contractId, outin.getTrainNo(), outin.getBillType());
                    }
                }
            }
            if (outTask != null && outTask == 2) {
                updateOutsourceStatus(now, orderTask.getId());
            }

            // 关键任务，发送消息给质检员
            if (null != orderTask.getImportant() && 1 == orderTask.getImportant()) {
                sendMessageByNewThread(orderTask, sysUser.getUsername());
            }

            // 更新任务关联的作业记录表实例的完工日期
            updateTaskRelationFormWorkRecordInstFinishDate(orderTask.getId(), now);
        }

        // 修改工单的作业状态
        boolean taskAllFinished = taskAllFinishedUpdateOrderWorkStatus(now, orderId);

        if (taskAllFinished) {
            // 工单下任务都已完成，提交工单当前流程的第一个任务
            Map<String, Object> vars = new HashMap<>();
            vars.put("writeSubmit", 1);
            vars.put("needCheck", 0);
            workflowForwardService.completeOrderFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP提交工单给工班长（作业填报后提交）", vars);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        return true;
    }

    /**
     * @see BuWorkOrderTaskService#listPlanFormsByTaskId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderTaskFormInst> listPlanFormsByTaskId(String taskId) {
        List<BuWorkOrderTaskFormInst> formInstList = buWorkOrderTaskMapper.selectOrderTaskFormInstListByOrderTaskId(taskId);

        // 表单是检查记录表时，检查人显示为已检查/未检查
        for (BuWorkOrderTaskFormInst formInst : formInstList) {
            if (null != formInst.getInstType() && 4 == formInst.getInstType()) {
                if (null != formInst.getStatus() && 1 == formInst.getStatus()) {
                    formInst.setCheckUserName("已检查");
                } else {
                    formInst.setCheckUserName("未检查");
                }
            }
        }

        return formInstList;
    }

    /**
     * @see BuWorkOrderTaskService#selectTaskRelatedInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkOrderTaskRelatedInfo selectTaskRelatedInfo(String workOrderTaskId) {
        // 工单信息
        BuWorkOrder workOrder = buWorkOrderMapper.selectByTaskId(workOrderTaskId);
        // 任务信息
        BuWorkOrderTask workOrderTask = buWorkOrderTaskMapper.selectTaskByTaskId(workOrderTaskId);

        // 安全提示、技术要求，去规程里找
        List<BuRepairReguDetailBO> reguDetailBOList = buWorkOrderTaskMapper.selectTaskRelativeReguDetailList(workOrderTaskId);
        JobRequirement jobRequirement = joinJobRequirement(reguDetailBOList);
        // 安全预想，取工单任务的安全预想
        jobRequirement.setSafetyExpectationId(null);
        jobRequirement.setSafetyExpectation(workOrderTask.getSafeNotice());

        List<BuWorkOrderTaskMustReplace> mustReplaces = new ArrayList<>();
        if (1 == workOrderTask.getTaskType()) {
            // 列计划的必换件
            List<BuRepairTaskMustReplace> planTaskMustReplaceList = buRepairTaskMustReplaceMapper.selectListByTaskId(workOrderTask.getTaskObjId());
            if (CollectionUtils.isNotEmpty(planTaskMustReplaceList)) {
                for (BuRepairTaskMustReplace planTaskMustReplace : planTaskMustReplaceList) {
                    BuWorkOrderTaskMustReplace orderTaskMustReplace = new BuWorkOrderTaskMustReplace();
                    BeanUtils.copyProperties(planTaskMustReplace, orderTaskMustReplace);
                    orderTaskMustReplace.setOrderTaskId(workOrderTaskId);
                    mustReplaces.add(orderTaskMustReplace);
                }
            }
        }

        // 工单任务的物料
        List<BuWorkOrderMaterial> materials = buWorkOrderMaterialMapper.selectListByOrderTaskId(workOrderTaskId);
        // 设置来源库位和托盘名称信息
        setLocationWarehouseNamesAndPalletNames(materials);

        // 工单任务的工具
        List<BuWorkOrderTool> tools = buWorkOrderToolMapper.selectWorkOrderTool(workOrderTaskId);

        // 工单任务的文档文件和关联的规程明细的文档文件，综合去重
        List<BuOtherData> workOrderTaskFile = buWorkOrderTaskMapper.selectWorkOrderTaskFile(workOrderTaskId);
        List<BuOtherData> reguDetailFile = buWorkOrderTaskMapper.selectReguDetailFile(workOrderTaskId);
        List<BuOtherData> allFile = deduplicateFile(workOrderTaskFile, reguDetailFile);

        // 任务人员安排
        List<BuRepairTaskStaffArrange> staffArrangeList = buRepairTaskStaffArrangeMapper.selectListByOrderTaskId(workOrderTaskId);

        // 任务附件
        List<BuWorkOrderAnnex> annexList = buWorkOrderAnnexMapper.selectListByTaskId(workOrderTaskId);

        // 任务目标设备
        List<BuWorkOrderTaskEqu> equipmentList = buWorkOrderTaskEquMapper.selectListByTaskId(workOrderTaskId);
        setEquipmentLocationName(equipmentList);

        List<BuRepairTaskRegu> regus = new ArrayList<>();
        Integer taskType = workOrderTask.getTaskType();
        String taskObjId = workOrderTask.getTaskObjId();
        // 计划任务
        if (1 == taskType) {
            // 规程
            regus = buRepairPlanTaskMapper.selectTaskRepairPlanReguInfo(taskObjId);
        }
        // 作业项任务
        if (4 == taskType) {
            // 规程
            regus = buWorkOrderTaskMapper.selectAsPlanTaskRepairPlanReguInfoByReguDetailId(taskObjId);
        }
        // 作业指导书
        List<BuWorkOrderBookStep> bookSteps = buWorkOrderTaskMapper.selectTaskAllBookSteps(workOrderTaskId);

        BuWorkOrderTaskRelatedInfo taskRelatedInfo = new BuWorkOrderTaskRelatedInfo()
                .setRequirement(jobRequirement)
                .setMustReplaces(mustReplaces)
                .setMaterials(materials)
                .setTools(tools)
                .setOtherData(allFile)
                .setStaffArranges(staffArrangeList)
                .setAnnexList(annexList)
                .setEquipments(equipmentList)
                .setRegus(regus)
                .setBookSteps(bookSteps)
                .setWorkOrder(workOrder)
                .setTask(workOrderTask);

        // 设置工单maximo设备更换链接
        setOrderMaximoAssetChangeUrl(workOrder, taskRelatedInfo);
//        // 通过工单任务关联工位，查找列计划中的表单，添加到表单中
//        addFormsFromPlanByWorkstationRef(taskRelatedInfo);

        return taskRelatedInfo;
    }

    @Override
    public List<BuWorkOrderBookStep> selectTaskRelatedInfoOfBookSteps(String id) {
        return buWorkOrderTaskMapper.selectTaskAllBookSteps(id);
    }


    private List<BuOtherData> deduplicateFile
            (List<BuOtherData> workOrderTaskFile, List<BuOtherData> reguDetailFile) {
        Map<String, BuOtherData> fileIdFileMap = new HashMap<>(16);

        if (CollectionUtils.isNotEmpty(workOrderTaskFile)) {
            workOrderTaskFile.forEach(file -> fileIdFileMap.put(file.getFileId(), file));
        }
        if (CollectionUtils.isNotEmpty(reguDetailFile)) {
            reguDetailFile.forEach(file -> fileIdFileMap.put(file.getFileId(), file));
        }

        return new ArrayList<>(fileIdFileMap.values());
    }

    private JobRequirement joinJobRequirement(List<BuRepairReguDetailBO> reguDetailList) {
        AtomicInteger order = new AtomicInteger(999);
        List<BuRepairReguDetailBO> flatReguDetailList = new ArrayList<>();
        recursionAddFlatReguDetail(reguDetailList, flatReguDetailList, order);
        flatReguDetailList = deduplicateReguDetail(flatReguDetailList);

        flatReguDetailList.sort(Comparator.comparing(BuRepairReguDetailBO::getOrder));
//        AtomicInteger safeNoticeOrder = new AtomicInteger(1);
//        AtomicInteger requirementOrder = new AtomicInteger(1);
        StringBuilder safeNoticeBuilder = new StringBuilder();
        StringBuilder requirementBuilder = new StringBuilder();
        for (BuRepairReguDetailBO reguDetailBO : flatReguDetailList) {
            String safeNotice = reguDetailBO.getSafeNotice();
            String requirement = reguDetailBO.getRequirement();
            if (StringUtils.isNotBlank(safeNotice)) {
//                safeNoticeBuilder.append(safeNoticeOrder.getAndIncrement()).append(". ").append(safeNotice).append(System.lineSeparator());
                safeNoticeBuilder.append(safeNotice).append(System.lineSeparator());
            }
            if (StringUtils.isNotBlank(requirement)) {
//                requirementBuilder.append(requirementOrder.getAndIncrement()).append(". ").append(requirement).append(System.lineSeparator());
                requirementBuilder.append(requirement).append(System.lineSeparator());
            }
        }

        return new JobRequirement()
                .setSafeNotice(safeNoticeBuilder.toString())
                .setRequirement(requirementBuilder.toString());
    }

    private void recursionAddFlatReguDetail(List<BuRepairReguDetailBO> reguDetailList,
                                            List<BuRepairReguDetailBO> flatReguDetailList,
                                            AtomicInteger order) {
        if (CollectionUtils.isNotEmpty(reguDetailList)) {
            for (BuRepairReguDetailBO reguDetailBO : reguDetailList) {
                reguDetailBO.setOrder(order.getAndDecrement());
                flatReguDetailList.add(reguDetailBO);
                recursionAddFlatReguDetail(reguDetailBO.getParentList(), flatReguDetailList, order);
            }
        }
    }

    private List<BuRepairReguDetailBO> deduplicateReguDetail(List<BuRepairReguDetailBO> reguDetailList) {
        if (CollectionUtils.isEmpty(reguDetailList)) {
            return new ArrayList<>();
        }

        List<BuRepairReguDetailBO> resultList = new ArrayList<>();
        Set<String> detailIdSet = reguDetailList.stream()
                .map(BuRepairReguDetailBO::getId)
                .collect(Collectors.toSet());

        for (String detailId : detailIdSet) {
            List<BuRepairReguDetailBO> duplicateList = reguDetailList.stream()
                    .filter(detail -> detailId.equals(detail.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(duplicateList)) {
                resultList.add(duplicateList.get(0));
            }
        }

        return resultList;
    }

    private boolean orderTaskAllFinished(List<BuWorkOrderTask> taskList) {
        long finishCount = taskList.stream().filter(task -> null != task.getTaskStatus() && 2 == task.getTaskStatus()).count();
        return finishCount == taskList.size();
    }

    private boolean taskAllFinishedUpdateOrderWorkStatus(Date now, String orderId) {
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .eq(BuWorkOrderTask::getOrderId, orderId);
        List<BuWorkOrderTask> allOrderTaskList = buWorkOrderTaskMapper.selectList(orderTaskWrapper);

        boolean taskAllFinished = orderTaskAllFinished(allOrderTaskList);

        if (taskAllFinished) {
            // 工单下所有任务都已完成,修改工单作业状态=作业完成2
            BuWorkOrder order = new BuWorkOrder()
                    .setId(orderId)
                    .setWorkStatus(2)
                    .setActFinish(now);
            buWorkOrderMapper.updateById(order);
        }

        return taskAllFinished;
    }

    private void updateOutsourceStatus(Date now, String orderTaskId) {
        List<BuOutsourceOutinDetail> inDetailList = buOutsourceOutinDetailMapper.selectInDetailListByOrderTaskId(orderTaskId);
        List<String> outDetailIdList = inDetailList.stream()
                .map(BuOutsourceOutinDetail::getOutDetailId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<BuOutsourceOutinDetail> outDetailList = buOutsourceOutinDetailMapper.selectListByIdList(outDetailIdList);

        List<String> outInDetailIdList = new ArrayList<>();
        for (BuOutsourceOutinDetail inDetail : inDetailList) {
            inDetail.setReturnStatus(1).setReturnTime(now);
            buOutsourceOutinDetailMapper.updateById(inDetail);
            outInDetailIdList.add(inDetail.getId());
        }
        for (BuOutsourceOutinDetail outDetail : outDetailList) {
            outDetail.setReturnStatus(1);
            buOutsourceOutinDetailMapper.updateById(outDetail);
            outInDetailIdList.add(outDetail.getId());
        }

        // 设置委外逾期预警为已处理
        alertRecordService.setAlertRecordHandled(6, outInDetailIdList);
    }

    private void sendMessageByNewThread(BuWorkOrderTask orderTask, String fromUser) {
        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                List<String> userNameList = sysBaseAPI.listUsernameByTypeAndParam(2, Collections.singletonList("qualityInspector"));
                if (CollectionUtils.isNotEmpty(userNameList)) {
                    String usernames = String.join(",", userNameList);
                    sysBaseAPI.sendSysAnnouncement(fromUser, usernames, "质检通知", orderTask.getTaskContent());
                }
            } catch (Exception ex) {
                log.error("开线程发送消息，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();
    }

    private void updateTaskRelationFormWorkRecordInstFinishDate(String orderTaskId, Date now) {
        if (StringUtils.isBlank(orderTaskId)) {
            return;
        }

        LambdaQueryWrapper<BuWorkOrderTaskFormInst> formInstWrapper = new LambdaQueryWrapper<BuWorkOrderTaskFormInst>()
                .eq(BuWorkOrderTaskFormInst::getWorkOrderTaskId, orderTaskId)
                .eq(BuWorkOrderTaskFormInst::getInstType, 3);
        List<BuWorkOrderTaskFormInst> formInstList = buWorkOrderTaskFormInstMapper.selectList(formInstWrapper);
        if (CollectionUtils.isEmpty(formInstList)) {
            return;
        }

        List<String> workRecordInstIdList = formInstList.stream()
                .map(BuWorkOrderTaskFormInst::getFormInstId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(workRecordInstIdList)) {
            buPlanFormWorkRecordMapper.updateFinishDateByIdList(workRecordInstIdList,now);
        }

        //保存在excelData完工日期   暂时去掉
       /* List<BuPlanFormWorkRecord> formWorkRecords = buPlanFormWorkRecordMapper.selectList(workRecordInstWrapper);

        if (CollectionUtils.isNotEmpty(formWorkRecords)) {
            formWorkRecords.forEach(form -> {
                if (form.getWorkRecordType() == 2) {
                    BuPlWorkRecordExcelData excelData = workRecordExcelDataMapper.selectById(form.getId());
                    if (excelData != null) {
                        String result = TirosUtil.replaceExcelData(excelData.getResult(), "完工日期:", "完工日期:" + DateUtil.format(now, "yyyy-MM-dd"));
                        workRecordExcelDataMapper.updateById(new BuPlWorkRecordExcelData().setId(form.getId()).setResult(result));
                    }
                }
            });
        }*/
    }

    private void setLocationWarehouseNamesAndPalletNames(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        Set<String> orderMaterialIdSet = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toSet());
        List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailDispatchMapper.selectListWithAssignByOrderMaterialIdList(new ArrayList<>(orderMaterialIdSet));
        if (CollectionUtils.isEmpty(allApplyDetailList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            List<String> sourceLocationNameList = new ArrayList<>();
            List<String> palletNameList = new ArrayList<>();
            List<String> sourceLocationAndPalletNameList = new ArrayList<>();

            List<BuMaterialApplyDetail> applyDetailList = allApplyDetailList.stream()
                    .filter(applyDetail -> orderMaterial.getId().equals(applyDetail.getOrderMaterialId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(applyDetailList)) {
                continue;
            }

            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                List<BuMaterialAssignDetail> assignDetailList = applyDetail.getAssignDetailList();
                if (CollectionUtils.isEmpty(assignDetailList)) {
                    continue;
                }

                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    String sourceLocationName = TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName());
                    String palletName = assignDetail.getPalletName();
                    if (!sourceLocationNameList.contains(sourceLocationName)) {
                        sourceLocationNameList.add(sourceLocationName);
                    }
                    if (StringUtils.isNotBlank(palletName)) {
                        if (!palletNameList.contains(palletName)) {
                            palletNameList.add(palletName);
                        }
                    }
                    sourceLocationAndPalletNameList.add(sourceLocationName + " | " + assignDetail.getAmount() + " | " + (StringUtils.isNotBlank(palletName) ? palletName : ""));
                }
            }

            orderMaterial.setSourceLocationName(String.join(", ", sourceLocationNameList))
                    .setPalletName(String.join(", ", palletNameList))
                    .setSourceLocationAndPalletName(String.join(", ", sourceLocationAndPalletNameList));
        }
    }

    private void setEquipmentLocationName(List<BuWorkOrderTaskEqu> equipmentList) {
        if (CollectionUtils.isEmpty(equipmentList)) {
            return;
        }

        // 查询位置名称
        Map<String, String> locationCodeNameMap = new HashMap<>();
        List<String> locationCodeList = equipmentList.stream()
                .map(BuWorkOrderTaskEqu::getLocationCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(locationCodeList)) {
            List<List<String>> locationCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(locationCodeList);
            for (List<String> locationCodeBatchSub : locationCodeBatchSubList) {
                LambdaQueryWrapper<BuMaximoLocation> locationWrapper = new LambdaQueryWrapper<BuMaximoLocation>()
                        .in(BuMaximoLocation::getCode, locationCodeBatchSub);
                List<BuMaximoLocation> subLocationList = buMaximoLocationDispatchMapper.selectList(locationWrapper);
                subLocationList.forEach(location -> locationCodeNameMap.put(location.getCode(), location.getName()));
            }
        }
        // 设置位置名称
        for (BuWorkOrderTaskEqu equipment : equipmentList) {
            equipment.setLocationName(locationCodeNameMap.get(equipment.getLocationCode()));
        }
    }

    private void setOrderMaximoAssetChangeUrl(BuWorkOrder order, BuWorkOrderTaskRelatedInfo taskRelatedInfo) {
        if (null == order || null == order.getMaximoWorkOrderId()) {
            return;
        }

        Long maximoWorkOrderId = order.getMaximoWorkOrderId();
        String configValue = sysConfigService.getConfigValueByCode(MAXIMO_ASSET_CHANGE_URL_CONFIG_CODE);
        if (StringUtils.isNotBlank(configValue) && !"0".equals(configValue)) {
            taskRelatedInfo.setMaximoAssetChangeUrl(configValue + maximoWorkOrderId);
        }
    }

}
