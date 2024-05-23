package org.jeecg.modules.tiros.rpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.rpt.service.KpiRptService;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.dispatch.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.mapper.BuFaultInfoMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.dispatch.workorder.mapper.BuRepairTaskStaffArrangeMapper;
import org.jeecg.modules.produce.kpi.bean.BuRptPersonKpi;
import org.jeecg.modules.produce.kpi.mapper.BuRptPersonKpiMapper;
import org.jeecg.modules.tiros.rpt.bean.BuKpiRptBO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 绩效统计 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
@Service
public class KpiRptServiceImpl implements KpiRptService {

    @Resource
    private BuRepairTaskStaffArrangeMapper buRepairTaskStaffArrangeMapper;
    @Resource
    private BuFaultInfoMapper buFaultInfoMapper;
    @Resource
    private BuRptPersonKpiMapper buRptPersonKpiMapper;


    /**
     * @see KpiRptService#increaseTimeKpiByOrder(String)
     */
    @Override
    public boolean increaseTimeKpiByOrder(String orderId) {
        List<BuRepairTaskStaffArrange> arrangeList = buRepairTaskStaffArrangeMapper.selectListForRptByOrderId(orderId);
        if (CollectionUtils.isEmpty(arrangeList)) {
            return true;
        }

        List<BuKpiRptBO> kpiRptBOList = new ArrayList<>();
        for (BuRepairTaskStaffArrange arrange : arrangeList) {
            if (null == arrange || null == arrange.getWorkTime() || BigDecimal.ZERO.compareTo(arrange.getWorkTime()) == 0) {
                continue;
            }

            transToKpiRptBO(arrange, kpiRptBOList);
        }
        if (CollectionUtils.isEmpty(kpiRptBOList)) {
            return true;
        }

        // 更新绩效统计数据
        for (BuKpiRptBO kpiRptBO : kpiRptBOList) {
            updateKpiRptData(kpiRptBO, 1);
        }

        return true;
    }

    /**
     * @see KpiRptService#increaseFaultKpiByFault(String)
     */
    @Override
    public boolean increaseFaultKpiByFault(String faultId) {
        if (StringUtils.isBlank(faultId)) {
            return true;
        }
        // 查询故障
        BuFaultInfo fault = buFaultInfoMapper.selectFaultForRptById(faultId);
        if (null == fault) {
            return true;
        }
        // 只有已提交的故障才增加对应数量
        if (null == fault.getSubmitStatus() || 0 == fault.getSubmitStatus()) {
            return true;
        }

        List<BuFaultHandleRecord> handleRecordList = buFaultInfoMapper.selectHandleRecordListByFaultId(faultId);

        List<BuKpiRptBO> kpiRptBOList = new ArrayList<>();
        transToKpiRptBO(fault, kpiRptBOList);
        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            for (BuFaultHandleRecord handleRecord : handleRecordList) {
                if (null == handleRecord) {
                    continue;
                }

                transToKpiRptBO(fault, handleRecord, kpiRptBOList);
            }
        }
        if (CollectionUtils.isEmpty(kpiRptBOList)) {
            return true;
        }

        // 更新绩效统计数据
        for (BuKpiRptBO kpiRptBO : kpiRptBOList) {
            updateKpiRptData(kpiRptBO, 1);
        }

        return true;
    }

    /**
     * @see KpiRptService#decreaseFaultKpiByFault(String)
     */
    @Override
    public boolean decreaseFaultKpiByFault(String faultId) {
        BuFaultInfo fault = buFaultInfoMapper.selectFaultForRptById(faultId);
        List<BuFaultHandleRecord> handleRecordList = buFaultInfoMapper.selectHandleRecordListByFaultId(faultId);
        if (null == fault) {
            return true;
        }

        List<BuKpiRptBO> kpiRptBOList = new ArrayList<>();
        transToKpiRptBO(fault, kpiRptBOList);
        if (CollectionUtils.isNotEmpty(handleRecordList)) {
            for (BuFaultHandleRecord handleRecord : handleRecordList) {
                if (null == handleRecord) {
                    continue;
                }

                transToKpiRptBO(fault, handleRecord, kpiRptBOList);
            }
        }
        if (CollectionUtils.isEmpty(kpiRptBOList)) {
            return true;
        }

        // 更新绩效统计数据
        for (BuKpiRptBO kpiRptBO : kpiRptBOList) {
            updateKpiRptData(kpiRptBO, 2);
        }

        return true;
    }


    private void transToKpiRptBO(BuRepairTaskStaffArrange arrange, List<BuKpiRptBO> kpiRptBOList) {
        BuKpiRptBO result = null;

        for (BuKpiRptBO kpiRptBO : kpiRptBOList) {
            if (isSame(arrange, kpiRptBO)) {
                result = kpiRptBO;

                int workTime = arrange.getWorkTime().intValue();
                result.setTotalTime(result.getTotalTime() + workTime);
                if (2 == arrange.getTaskType()) {
                    // 故障任务
                    result.setFaultTime(result.getFaultTime() + workTime);
                } else {
                    result.setRepairTime(result.getRepairTime() + workTime);
                }

                break;
            }
        }
        if (result == null) {
            result = new BuKpiRptBO()
                    .setUserId(arrange.getUserId())
                    .setUserName(arrange.getUserName())
                    .setWorkNo(arrange.getWorkNo())
                    .setDepotId(arrange.getDepotId())
                    .setCompanyId(arrange.getCompanyId())
                    .setWorkshopId(arrange.getWorkshopId())
                    .setLineId(arrange.getLineId())
                    .setGroupId(arrange.getGroupId())
                    .setGroupName(arrange.getGroupName())
                    .setRptDate(arrange.getTaskFinish())
                    .setYear(DateUtils.getYear(arrange.getTaskFinish()))
                    .setMonth(DateUtils.getMonth(arrange.getTaskFinish()))
                    .setTotalTime(0)
                    .setRepairTime(0)
                    .setFaultTime(0)
                    .setTotalHappen(0)
                    .setFaultAmount(0)
                    .setHandleAmount(0)
                    .setPlanId(arrange.getPlanId());

            int workTime = arrange.getWorkTime().intValue();
            result.setTotalTime(result.getTotalTime() + workTime);
            if (2 == arrange.getTaskType()) {
                // 故障任务
                result.setFaultTime(result.getFaultTime() + workTime);
            } else {
                result.setRepairTime(result.getRepairTime() + workTime);
            }

            kpiRptBOList.add(result);
        }
    }

    private boolean isSame(BuRepairTaskStaffArrange arrange, BuKpiRptBO kpiRptBO) {
        boolean sameUser = arrange.getUserId().equals(kpiRptBO.getUserId());
        if (!sameUser) {
            return false;
        }
        boolean sameDay = DateUtils.isSameDay(kpiRptBO.getRptDate(), arrange.getTaskFinish());
        if (!sameDay) {
            return false;
        }
        boolean samePlan = StringUtils.isBlank(arrange.getPlanId()) ? StringUtils.isBlank(kpiRptBO.getPlanId()) : arrange.getPlanId().equals(kpiRptBO.getPlanId());
        if (!samePlan) {
            return false;
        }
        boolean sameDepot = StringUtils.isBlank(arrange.getDepotId()) ? StringUtils.isBlank(kpiRptBO.getDepotId()) : arrange.getDepotId().equals(kpiRptBO.getDepotId());
        if (!sameDepot) {
            return false;
        }
        boolean sameCompany = StringUtils.isBlank(arrange.getCompanyId()) ? StringUtils.isBlank(kpiRptBO.getCompanyId()) : arrange.getCompanyId().equals(kpiRptBO.getCompanyId());
        if (!sameCompany) {
            return false;
        }
        boolean sameWorkshop = StringUtils.isBlank(arrange.getWorkshopId()) ? StringUtils.isBlank(kpiRptBO.getWorkshopId()) : arrange.getWorkshopId().equals(kpiRptBO.getWorkshopId());
        if (!sameWorkshop) {
            return false;
        }
        boolean sameLine = StringUtils.isBlank(arrange.getLineId()) ? StringUtils.isBlank(kpiRptBO.getLineId()) : arrange.getLineId().equals(kpiRptBO.getLineId());
        if (!sameLine) {
            return false;
        }
        boolean sameGroup = StringUtils.isBlank(arrange.getGroupId()) ? StringUtils.isBlank(kpiRptBO.getGroupId()) : arrange.getGroupId().equals(kpiRptBO.getGroupId());
        if (!sameGroup) {
            return false;
        }

        return true;
    }

    private void transToKpiRptBO(BuFaultInfo fault, List<BuKpiRptBO> kpiRptBOList) {
        BuKpiRptBO result = null;

        for (BuKpiRptBO kpiRptBO : kpiRptBOList) {
            if (isSame(fault, kpiRptBO)) {
                result = kpiRptBO;

                result.setFaultAmount(result.getFaultAmount() + 1);
                result.setTotalHappen(result.getTotalHappen() + 1);

                break;
            }
        }
        if (result == null) {
            result = new BuKpiRptBO()
                    .setUserId(fault.getReportUserId())
                    .setUserName(fault.getReportUserName())
                    .setWorkNo(fault.getReportUserWorkNo())
                    .setDepotId(fault.getDepotId())
                    .setCompanyId(fault.getCompanyId())
                    .setWorkshopId(fault.getWorkshopId())
                    .setLineId(fault.getLineId())
                    .setGroupId(fault.getReportGroupId())
                    .setGroupName(fault.getReportGroupName())
                    .setRptDate(fault.getReportTime())
                    .setYear(DateUtils.getYear(fault.getReportTime()))
                    .setMonth(DateUtils.getMonth(fault.getReportTime()))
                    .setTotalTime(0)
                    .setRepairTime(0)
                    .setFaultTime(0)
                    .setTotalHappen(0)
                    .setFaultAmount(0)
                    .setHandleAmount(0)
                    .setPlanId(fault.getPlanId());

            result.setFaultAmount(result.getFaultAmount() + 1);
            result.setTotalHappen(result.getTotalHappen() + 1);

            kpiRptBOList.add(result);
        }
    }

    private boolean isSame(BuFaultInfo fault, BuKpiRptBO kpiRptBO) {
        boolean sameUser = fault.getReportUserId().equals(kpiRptBO.getUserId());
        if (!sameUser) {
            return false;
        }
        boolean sameDay = DateUtils.isSameDay(kpiRptBO.getRptDate(), fault.getReportTime());
        if (!sameDay) {
            return false;
        }
        boolean samePlan = StringUtils.isBlank(fault.getPlanId()) ? StringUtils.isBlank(kpiRptBO.getPlanId()) : fault.getPlanId().equals(kpiRptBO.getPlanId());
        if (!samePlan) {
            return false;
        }
        boolean sameDepot = StringUtils.isBlank(fault.getDepotId()) ? StringUtils.isBlank(kpiRptBO.getDepotId()) : fault.getDepotId().equals(kpiRptBO.getDepotId());
        if (!sameDepot) {
            return false;
        }
        boolean sameCompany = StringUtils.isBlank(fault.getCompanyId()) ? StringUtils.isBlank(kpiRptBO.getCompanyId()) : fault.getCompanyId().equals(kpiRptBO.getCompanyId());
        if (!sameCompany) {
            return false;
        }
        boolean sameWorkshop = StringUtils.isBlank(fault.getWorkshopId()) ? StringUtils.isBlank(kpiRptBO.getWorkshopId()) : fault.getWorkshopId().equals(kpiRptBO.getWorkshopId());
        if (!sameWorkshop) {
            return false;
        }
        boolean sameLine = StringUtils.isBlank(fault.getLineId()) ? StringUtils.isBlank(kpiRptBO.getLineId()) : fault.getLineId().equals(kpiRptBO.getLineId());
        if (!sameLine) {
            return false;
        }
        boolean sameGroup = StringUtils.isBlank(fault.getReportGroupId()) ? StringUtils.isBlank(kpiRptBO.getGroupId()) : fault.getReportGroupId().equals(kpiRptBO.getGroupId());
        if (!sameGroup) {
            return false;
        }

        return true;
    }

    private void transToKpiRptBO(BuFaultInfo fault, BuFaultHandleRecord handleRecord, List<BuKpiRptBO> kpiRptBOList) {
        BuKpiRptBO result = null;

        for (BuKpiRptBO kpiRptBO : kpiRptBOList) {
            if (isSame(fault, handleRecord, kpiRptBO)) {
                result = kpiRptBO;

                result.setHandleAmount(result.getHandleAmount() + 1);
                result.setTotalHappen(result.getTotalHappen() + 1);

                break;
            }
        }
        if (result == null) {
            result = new BuKpiRptBO()
                    .setUserId(handleRecord.getSolutionUserId())
                    .setUserName(handleRecord.getSolutionUserName())
                    .setWorkNo(handleRecord.getSolutionUserWorkNo())
                    .setDepotId(fault.getDepotId())
                    .setCompanyId(fault.getCompanyId())
                    .setWorkshopId(fault.getWorkshopId())
                    .setLineId(fault.getLineId())
                    .setGroupId(handleRecord.getSolutionGroupId())
                    .setGroupName(handleRecord.getSolutionGroupName())
                    .setRptDate(handleRecord.getSolutionTime())
                    .setYear(DateUtils.getYear(handleRecord.getSolutionTime()))
                    .setMonth(DateUtils.getMonth(handleRecord.getSolutionTime()))
                    .setTotalTime(0)
                    .setRepairTime(0)
                    .setFaultTime(0)
                    .setTotalHappen(0)
                    .setFaultAmount(0)
                    .setHandleAmount(0)
                    .setPlanId(fault.getPlanId());

            result.setHandleAmount(result.getHandleAmount() + 1);
            result.setTotalHappen(result.getTotalHappen() + 1);

            kpiRptBOList.add(result);
        }
    }

    private boolean isSame(BuFaultInfo fault, BuFaultHandleRecord handleRecord, BuKpiRptBO kpiRptBO) {
        boolean sameUser = handleRecord.getSolutionUserId().equals(kpiRptBO.getUserId());
        if (!sameUser) {
            return false;
        }
        boolean sameDay = DateUtils.isSameDay(kpiRptBO.getRptDate(), handleRecord.getSolutionTime());
        if (!sameDay) {
            return false;
        }
        boolean samePlan = StringUtils.isBlank(fault.getPlanId()) ? StringUtils.isBlank(kpiRptBO.getPlanId()) : fault.getPlanId().equals(kpiRptBO.getPlanId());
        if (!samePlan) {
            return false;
        }
        boolean sameDepot = StringUtils.isBlank(fault.getDepotId()) ? StringUtils.isBlank(kpiRptBO.getDepotId()) : fault.getDepotId().equals(kpiRptBO.getDepotId());
        if (!sameDepot) {
            return false;
        }
        boolean sameCompany = StringUtils.isBlank(fault.getCompanyId()) ? StringUtils.isBlank(kpiRptBO.getCompanyId()) : fault.getCompanyId().equals(kpiRptBO.getCompanyId());
        if (!sameCompany) {
            return false;
        }
        boolean sameWorkshop = StringUtils.isBlank(fault.getWorkshopId()) ? StringUtils.isBlank(kpiRptBO.getWorkshopId()) : fault.getWorkshopId().equals(kpiRptBO.getWorkshopId());
        if (!sameWorkshop) {
            return false;
        }
        boolean sameLine = StringUtils.isBlank(fault.getLineId()) ? StringUtils.isBlank(kpiRptBO.getLineId()) : fault.getLineId().equals(kpiRptBO.getLineId());
        if (!sameLine) {
            return false;
        }
        boolean sameGroup = StringUtils.isBlank(handleRecord.getSolutionGroupId()) ? StringUtils.isBlank(kpiRptBO.getGroupId()) : handleRecord.getSolutionGroupId().equals(kpiRptBO.getGroupId());
        if (!sameGroup) {
            return false;
        }

        return true;
    }

    /**
     * 更新并保存绩效信息
     *
     * @param kpiRptBO 更新数据
     * @param type     类型  1表示新增，2表示减少
     */
    private void updateKpiRptData(BuKpiRptBO kpiRptBO, int type) {
        if (null == kpiRptBO) {
            return;
        }

        // 获取查询wrapper
        LambdaQueryWrapper<BuRptPersonKpi> wrapper = initQueryWrapper(kpiRptBO);
        if (null == wrapper) {
            return;
        }

        // 绩效统计，不存在新增，存在修改数量
        BuRptPersonKpi kpi = buRptPersonKpiMapper.selectOne(wrapper);
        if (null == kpi) {
            kpi = new BuRptPersonKpi();
            BeanUtils.copyProperties(kpiRptBO, kpi);
            kpi.setId(UUIDGenerator.generate());
            buRptPersonKpiMapper.insert(kpi);
        } else {
            updateTimeAndFault(kpi, kpiRptBO, type);
            buRptPersonKpiMapper.updateById(kpi);
        }
    }

    private LambdaQueryWrapper<BuRptPersonKpi> initQueryWrapper(BuKpiRptBO kpiRptBO) {
        boolean valid = checkKpiRptBOValid(kpiRptBO);
        if (!valid) {
            return null;
        }

        return new LambdaQueryWrapper<BuRptPersonKpi>()
                .eq(BuRptPersonKpi::getUserId, kpiRptBO.getUserId())
                .eq(BuRptPersonKpi::getDepotId, kpiRptBO.getDepotId())
                .eq(BuRptPersonKpi::getCompanyId, kpiRptBO.getCompanyId())
                .eq(BuRptPersonKpi::getWorkshopId, kpiRptBO.getWorkshopId())
                .eq(BuRptPersonKpi::getLineId, kpiRptBO.getLineId())
                .eq(BuRptPersonKpi::getGroupId, kpiRptBO.getGroupId())
                .eq(BuRptPersonKpi::getRptDate, kpiRptBO.getRptDate())
                .eq(BuRptPersonKpi::getYear, kpiRptBO.getYear())
                .eq(BuRptPersonKpi::getMonth, kpiRptBO.getMonth())
                .eq(BuRptPersonKpi::getPlanId, kpiRptBO.getPlanId());
    }

    private boolean checkKpiRptBOValid(BuKpiRptBO kpiRptBO) {
        if (StringUtils.isBlank(kpiRptBO.getUserId())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getUserName())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getWorkNo())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getDepotId())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getCompanyId())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getWorkshopId())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getLineId())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getGroupId())) {
            return false;
        }
        if (StringUtils.isBlank(kpiRptBO.getGroupName())) {
            return false;
        }
        if (null == kpiRptBO.getRptDate()) {
            return false;
        }
        if (null == kpiRptBO.getYear()) {
            return false;
        }
        if (null == kpiRptBO.getMonth()) {
            return false;
        }

        return true;
    }

    /**
     * 更新绩效数据
     *
     * @param kpi      原数据
     * @param kpiRptBO 更新数据
     * @param type     类型  1表示新增，2表示减少
     */
    private void updateTimeAndFault(BuRptPersonKpi kpi, BuKpiRptBO kpiRptBO, int type) {
        if (1 == type) {
            kpi.setTotalTime(kpi.getTotalTime() + kpiRptBO.getTotalTime());
            kpi.setRepairTime(kpi.getRepairTime() + kpiRptBO.getRepairTime());
            kpi.setFaultTime(kpi.getFaultTime() + kpiRptBO.getFaultTime());
            kpi.setTotalHappen(kpi.getTotalHappen() + kpiRptBO.getTotalHappen());
            kpi.setFaultAmount(kpi.getFaultAmount() + kpiRptBO.getFaultAmount());
            kpi.setHandleAmount(kpi.getHandleAmount() + kpiRptBO.getHandleAmount());
        } else if (2 == type) {
            kpi.setTotalTime(kpi.getTotalTime() - kpiRptBO.getTotalTime());
            kpi.setRepairTime(kpi.getRepairTime() - kpiRptBO.getRepairTime());
            kpi.setFaultTime(kpi.getFaultTime() - kpiRptBO.getFaultTime());
            kpi.setTotalHappen(kpi.getTotalHappen() - kpiRptBO.getTotalHappen());
            kpi.setFaultAmount(kpi.getFaultAmount() - kpiRptBO.getFaultAmount());
            kpi.setHandleAmount(kpi.getHandleAmount() - kpiRptBO.getHandleAmount());
        }
    }

}
