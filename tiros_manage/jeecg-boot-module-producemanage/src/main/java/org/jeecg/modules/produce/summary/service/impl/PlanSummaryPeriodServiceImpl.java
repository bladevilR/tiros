package org.jeecg.modules.produce.summary.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanProduceMapper;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.period.PeriodDay;
import org.jeecg.modules.produce.summary.bean.period.PeriodInfo;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryPeriodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 维修周期控制情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryPeriodServiceImpl implements PlanSummaryPeriodService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;
    @Resource
    private BuRepairPlanProduceMapper repairPlanProduceMapper;


    /**
     * @see PlanSummaryPeriodService#getPeriodInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public PeriodInfo getPeriodInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);
        PeriodDay currentPeriodDay = (PeriodDay) new PeriodDay().setPlannedDays(planBase.getPeriodDays())
                .setActualDays(planBase.getRepairDays())
                .setTrainNo(planBase.getTrainNo())
                .setRepairIndex(planBase.getRepairIndex());

        List<BuRepairPlan> repairPlanList = repairPlanProduceMapper.selectRepairDayList(planBase.getLineId(), planBase.getRepairProgramId());
        List<PeriodDay> periodDayList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repairPlanList)) {
            repairPlanList.forEach(plan -> {
                PeriodDay periodDay = (PeriodDay) new PeriodDay().setPlannedDays(plan.getDuration())
                        .setActualDays(plan.getActDuration())
                        .setTrainNo(plan.getTrainNo())
                        .setRepairIndex(plan.getItemNo());
                periodDayList.add(periodDay);
            });
        }

        return new PeriodInfo()
                .setCurrentPeriodDay(currentPeriodDay)
                .setLinePeriodDayList(periodDayList);
    }

}
