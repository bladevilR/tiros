package org.jeecg.modules.produce.summary.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanProduceMapper;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 列计划总结 基础 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryBaseServiceImpl implements PlanSummaryBaseService {

    @Resource
    private BuRepairPlanProduceMapper buRepairPlanProduceMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see PlanSummaryBaseService#getPlanBase(String)
     */
    @Override
    public PlanBase getPlanBase(String planId) throws Exception {
        BuRepairPlan plan = buRepairPlanProduceMapper.selectPlanById(planId);
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }

        return new PlanBase()
                .setPlanId(plan.getId())
                .setLineId(plan.getLineId())
                .setLineName(plan.getLineName())
                .setRepairIndex(plan.getItemNo())
                .setTrainNo(plan.getTrainNo())
                .setRepairProgramId(plan.getRepairProgramId())
                .setRepairProgramName(plan.getRepairProgramName())
                .setStartDate(plan.getStartDate())
                .setFinishDate(plan.getFinishDate())
                .setActStart(plan.getActStart())
                .setActFinish(plan.getActFinish())
                .setExchangeId(plan.getExchangeId())
                .setPeriodDays(plan.getDuration())
                .setRepairDays(plan.getActDuration());
    }

    /**
     * @see PlanSummaryBaseService#listLinePlanBase(PlanBase)
     */
    @Override
    public List<PlanBase> listLinePlanBase(PlanBase planBase) {
        String lineId = planBase.getLineId();
        String repairProgramId = planBase.getRepairProgramId();
        List<BuRepairPlan> planList = buRepairPlanProduceMapper.selectPlanListByLineAndRepairProgram(lineId, repairProgramId);

        List<PlanBase> planBaseList = new ArrayList<>();
        for (BuRepairPlan plan : planList) {
            PlanBase base = new PlanBase()
                    .setPlanId(plan.getId())
                    .setLineId(plan.getLineId())
                    .setLineName(plan.getLineName())
                    .setRepairIndex(plan.getItemNo())
                    .setTrainNo(plan.getTrainNo())
                    .setRepairProgramId(plan.getRepairProgramId())
                    .setRepairProgramName(plan.getRepairProgramName())
                    .setStartDate(plan.getStartDate())
                    .setFinishDate(plan.getFinishDate())
                    .setActStart(plan.getActStart())
                    .setActFinish(plan.getActFinish())
                    .setExchangeId(plan.getExchangeId())
                    .setPeriodDays(plan.getDuration())
                    .setRepairDays(plan.getActDuration());
            planBaseList.add(base);
        }

        return planBaseList;
    }

    /**
     * @see PlanSummaryBaseService#getLineSystemIdNameMap(PlanBase)
     */
    @Override
    public Map<String, String> getLineSystemIdNameMap(PlanBase planBase) {
        String lineId = planBase.getLineId();
        String trainTypeId = buRepairPlanProduceMapper.selectTrainTypeIdByLineId(lineId);

        Map<String, BuTrainAssetTypeBO> sysMap = assetTypeCacheService.mapSys(trainTypeId);

        Map<String, String> systemIdNameMap = new HashMap<>();
        for (Map.Entry<String, BuTrainAssetTypeBO> sysEntry : sysMap.entrySet()) {
            String sysId = sysEntry.getKey();
            BuTrainAssetTypeBO sysAssetTypeBO = sysEntry.getValue();
            String sysShortName = sysAssetTypeBO.getShortName();

            systemIdNameMap.put(sysId, sysShortName);
        }

        return systemIdNameMap;
    }

    /**
     * @see PlanSummaryBaseService#getYearAndMonthList(PlanBase, List, List)
     */
    @Override
    public void getYearAndMonthList(PlanBase planBase, List<Integer> yearList, List<Integer> monthList) {
        Date actStart = planBase.getActStart();
        Date actFinish = planBase.getActFinish();

        Calendar start = Calendar.getInstance();
        start.setTime(actStart);
        Calendar finish = Calendar.getInstance();
        finish.setTime(actFinish);

        while (start.before(finish)) {
            addToList(start, yearList, monthList);
            start.add(Calendar.MONTH, 1);
        }
        addToList(finish, yearList, monthList);
    }


    private void addToList(Calendar date, List<Integer> yearList, List<Integer> monthList) {
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        if (!yearList.contains(year)) {
            yearList.add(year);
        }
        if (!monthList.contains(month)) {
            monthList.add(month);
        }
    }

}
