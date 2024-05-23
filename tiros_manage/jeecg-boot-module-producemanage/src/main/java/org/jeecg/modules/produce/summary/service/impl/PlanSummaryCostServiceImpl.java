package org.jeecg.modules.produce.summary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.produce.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.produce.cost.bean.bo.MaterialCostSumBO;
import org.jeecg.modules.produce.cost.mapper.BuRptBoardSysMaterialProduceMapper;
import org.jeecg.modules.produce.cost.mapper.BuWorkOrderMaterialProduceMapper;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.cost.*;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryCostService;
import org.jeecg.modules.produce.trainhistory.mapper.BuMaximoTrainAssetProduceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 维修成本情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryCostServiceImpl implements PlanSummaryCostService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;
    @Resource
    private BuRptBoardSysMaterialProduceMapper buRptBoardSysMaterialProduceMapper;
    @Resource
    private BuMaximoTrainAssetProduceMapper buMaximoTrainAssetProduceMapper;
    @Resource
    private BuWorkOrderMaterialProduceMapper buWorkOrderMaterialProduceMapper;


    /**
     * @see PlanSummaryCostService#getCostInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public CostInfo getCostInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);
        List<PlanBase> planBaseList = planSummaryBaseService.listLinePlanBase(planBase);
        List<Integer> yearList = new ArrayList<>();
        List<Integer> monthList = new ArrayList<>();
        planSummaryBaseService.getYearAndMonthList(planBase, yearList, monthList);
        Map<String, String> systemIdNameMap = planSummaryBaseService.getLineSystemIdNameMap(planBase);

        // 查询该线路、修程下的物料成本统计
        LambdaQueryWrapper<BuRptBoardSysMaterial> sysMaterialWrapper = new LambdaQueryWrapper<BuRptBoardSysMaterial>()
                .eq(BuRptBoardSysMaterial::getLineId, planBase.getLineId())
                .eq(BuRptBoardSysMaterial::getProgramId, planBase.getRepairProgramId());
        List<BuRptBoardSysMaterial> lineSysMaterialList = buRptBoardSysMaterialProduceMapper.selectList(sysMaterialWrapper);

        // 本列车
        // 各系统物资消耗
        List<BuRptBoardSysMaterial> sysMaterialList = lineSysMaterialList.stream()
                .filter(sysMaterial -> planBase.getTrainNo().equals(sysMaterial.getTrainNo())
                        && yearList.contains(sysMaterial.getYear())
                        && monthList.contains(sysMaterial.getMonth()))
                .collect(Collectors.toList());
        List<SystemCost> systemList = getSystemCostList(sysMaterialList, systemIdNameMap);
        // 物资主要消耗占比
        List<MaterialCostSumBO> materialCostSumBOList = buWorkOrderMaterialProduceMapper.selectMaterialCostSumBOListByPlanId(planId, 2);
        List<MaterialCost> materialTopList = getMaterialTopList(materialCostSumBOList);
        CostCurrent current = new CostCurrent()
                .setSystemList(systemList)
                .setMaterialTopList(materialTopList);

        // 本线路
        // 各车辆成本
        CostLine line = getCostLine(lineSysMaterialList);
        // 本列和上列比较信息
        String currentLastCompareInfo = getCurrentLastCompareInfo(planBaseList);
        line.setCurrentLastCompareInfo(currentLastCompareInfo);

        return new CostInfo()
                .setCurrent(current)
                .setLine(line);
    }


    private List<SystemCost> getSystemCostList(List<BuRptBoardSysMaterial> sysMaterialList, Map<String, String> systemIdNameMap) {
        if (CollectionUtils.isEmpty(sysMaterialList)) {
            return new ArrayList<>();
        }

        List<SystemCost> systemCostList = new ArrayList<>();
        for (Map.Entry<String, String> systemIdNameEntry : systemIdNameMap.entrySet()) {
            String systemId = systemIdNameEntry.getKey();
            String systemName = systemIdNameEntry.getValue();

            List<BuRptBoardSysMaterial> materialList = sysMaterialList.stream()
                    .filter(material -> systemId.equals(material.getSysId()))
                    .collect(Collectors.toList());

            BigDecimal mustCost = materialList.stream()
                    .map(BuRptBoardSysMaterial::getMustCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal randomCost = materialList.stream()
                    .map(BuRptBoardSysMaterial::getRandomCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal consumeCost = materialList.stream()
                    .map(BuRptBoardSysMaterial::getConsumeCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            SystemCost systemCost = new SystemCost()
                    .setSystemName(systemName)
                    .setMustCost(mustCost)
                    .setRandomCost(randomCost)
                    .setMustRandomCost(mustCost.add(randomCost))
                    .setConsumeCost(consumeCost);
            systemCostList.add(systemCost);
        }

        systemCostList.sort(Comparator.comparing(SystemCost::getSystemName, Comparator.nullsLast(Comparator.naturalOrder())));
        return systemCostList;
    }

    private List<MaterialCost> getMaterialTopList(List<MaterialCostSumBO> materialCostSumBOList) {
        if (CollectionUtils.isEmpty(materialCostSumBOList)) {
            return new ArrayList<>();
        }

        BigDecimal totalCost = materialCostSumBOList.stream()
                .map(MaterialCostSumBO::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<MaterialCost> materialCostList = new ArrayList<>();
        for (MaterialCostSumBO materialCostSumBO : materialCostSumBOList) {
            Double percent = PercentUtils.percent(materialCostSumBO.getTotalCost().doubleValue(), totalCost.doubleValue());
            MaterialCost materialCost = new MaterialCost()
                    .setMaterialTypeName(materialCostSumBO.getMaterialTypeName())
                    .setCost(materialCostSumBO.getTotalCost())
                    .setPercent(percent);
            materialCostList.add(materialCost);
        }

        int resultSize = 11;
        List<MaterialCost> resultList = new ArrayList<>();
        if (materialCostList.size() > resultSize) {
            BigDecimal otherMaterialCost = BigDecimal.ZERO;
            for (int i = 0; i < materialCostList.size(); i++) {
                MaterialCost materialCost = materialCostList.get(i);
                if (i < resultSize) {
                    resultList.add(materialCost);
                } else {
                    otherMaterialCost = otherMaterialCost.add(materialCost.getCost());
                }
            }
            MaterialCost otherMaterial = new MaterialCost()
                    .setMaterialTypeName("其他")
                    .setCost(otherMaterialCost)
                    .setPercent(PercentUtils.percent(otherMaterialCost.doubleValue(), totalCost.doubleValue()));
            resultList.add(otherMaterial);
        } else {
            resultList = materialCostList;
        }

        return resultList;
    }

    private CostLine getCostLine(List<BuRptBoardSysMaterial> lineSysMaterialList) {
        // 查询各车辆的车厢数
        Map<String, Integer> trainNoCarsNumMap = new HashMap<>();
        List<Map<String, Object>> trainCarsNumList = buMaximoTrainAssetProduceMapper.selectTrainCarsNum();
        for (Map<String, Object> trainCarsNum : trainCarsNumList) {
            String trainNo = (String) trainCarsNum.get("trainNo");
            BigDecimal carsNum = DataTypeCastUtil.transNumber(trainCarsNum.get("carsNum"));
            trainNoCarsNumMap.put(trainNo, carsNum.intValue());
        }

        // 获取各车辆成本
        List<TrainCost> trainCostList = new ArrayList<>();
        for (BuRptBoardSysMaterial sysMaterial : lineSysMaterialList) {
            Integer repairIndex = sysMaterial.getRepairIndex();
            String trainNo = sysMaterial.getTrainNo();
            BigDecimal cost = sysMaterial.getMustCost().add(sysMaterial.getRandomCost()).add(sysMaterial.getConsumeCost());

            addToTrainCost(trainCostList, repairIndex, trainNo, cost);
        }

        // 综合计算
        BigDecimal totalTrainCost = BigDecimal.ZERO;
        BigDecimal totalCarCost = BigDecimal.ZERO;
        for (TrainCost trainCost : trainCostList) {
            Integer carsNum = trainNoCarsNumMap.getOrDefault(trainCost.getTrainNo(), 1);
            BigDecimal trainAverage = trainCost.getSelfRepair().add(trainCost.getOutsourceRepair());
            BigDecimal carAverage = trainAverage.divide(BigDecimal.valueOf(carsNum), 2, BigDecimal.ROUND_HALF_UP);

            trainCost.setTrainAverage(trainAverage)
                    .setCarAverage(carAverage);

            totalTrainCost = totalTrainCost.add(trainAverage);
            totalCarCost = totalCarCost.add(carAverage);
        }
        int trainSize = trainCostList.size();
        BigDecimal totalTrainAverage = totalTrainCost.divide(BigDecimal.valueOf(trainSize), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalCarAverage = totalCarCost.divide(BigDecimal.valueOf(trainSize), 2, BigDecimal.ROUND_HALF_UP);

        return new CostLine()
                .setTrainAverage(totalTrainAverage)
                .setCarAverage(totalCarAverage)
                .setTrainList(trainCostList);
    }

    private void addToTrainCost(List<TrainCost> trainCostList, Integer repairIndex, String trainNo, BigDecimal selfRepair) {
        List<TrainCost> matchTrain = trainCostList.stream()
                .filter(trainCost -> repairIndex.equals(trainCost.getRepairIndex()) && trainNo.equals(trainCost.getTrainNo()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(matchTrain)) {
            TrainCost trainCost = matchTrain.get(0);
            trainCost.setSelfRepair(trainCost.getSelfRepair().add(selfRepair));
        } else {
            TrainCost trainCost = new TrainCost()
                    .setRepairIndex(repairIndex)
                    .setTrainNo(trainNo)
                    .setSelfRepair(selfRepair)
                    .setOutsourceRepair(BigDecimal.ZERO);
            trainCostList.add(trainCost);
        }
    }

    private String getCurrentLastCompareInfo(List<PlanBase> planBaseList) {
        if (CollectionUtils.isEmpty(planBaseList) || planBaseList.size() < 2) {
            return "";
        }

        int planSize = planBaseList.size();
        // 本列车
        PlanBase currentPlan = planBaseList.get(planSize - 1);
        UseCategoryCost currentPlanUseCategoryCost = getPlanUseCategoryCost(currentPlan);
        // 上列车
        PlanBase lastPlan = planBaseList.get(planSize - 2);
        UseCategoryCost lastPlanUseCategoryCost = getPlanUseCategoryCost(lastPlan);

        BigDecimal totalCostDiff = currentPlanUseCategoryCost.getTotalCost().subtract(lastPlanUseCategoryCost.getTotalCost());
        BigDecimal mustRandomCostDiff = currentPlanUseCategoryCost.getMustRandomCost().subtract(lastPlanUseCategoryCost.getMustRandomCost());
        BigDecimal mustCostDiff = currentPlanUseCategoryCost.getMustCost().subtract(lastPlanUseCategoryCost.getMustCost());
        BigDecimal randomCostDiff = currentPlanUseCategoryCost.getRandomCost().subtract(lastPlanUseCategoryCost.getRandomCost());
        BigDecimal consumeCostDiff = currentPlanUseCategoryCost.getConsumeCost().subtract(lastPlanUseCategoryCost.getConsumeCost());
        BigDecimal otherCostDiff = currentPlanUseCategoryCost.getOtherCost().subtract(lastPlanUseCategoryCost.getOtherCost());

        String otherCostInfo = otherCostDiff.abs().compareTo(BigDecimal.valueOf(50)) <= 0 ? "" : "，其他（未归类）成本" + getDiffString(otherCostDiff);
        return String.format(
                "%s车较%s车成本%s，其中备品备件成本%s（必换件%s，故障件%s），耗材成本%s%s。",
                currentPlan.getTrainNo(), lastPlan.getTrainNo(),
                getDiffString(totalCostDiff),
                getDiffString(mustRandomCostDiff),
                getDiffString(mustCostDiff),
                getDiffString(randomCostDiff),
                getDiffString(consumeCostDiff),
                otherCostInfo
        );
    }

    private UseCategoryCost getPlanUseCategoryCost(PlanBase planBase) {
        UseCategoryCost useCategoryCost = new UseCategoryCost()
                .setTotalCost(BigDecimal.ZERO)
                .setMustCost(BigDecimal.ZERO)
                .setRandomCost(BigDecimal.ZERO)
                .setMustRandomCost(BigDecimal.ZERO)
                .setConsumeCost(BigDecimal.ZERO)
                .setOtherCost(BigDecimal.ZERO);

        List<MaterialCostSumBO> materialCostSumBOList = buWorkOrderMaterialProduceMapper.selectMaterialCostSumBOListByPlanId(planBase.getPlanId(), 3);
        for (MaterialCostSumBO materialCostSumBO : materialCostSumBOList) {
            Integer useCategory = materialCostSumBO.getUseCategory();
            BigDecimal totalCost = materialCostSumBO.getTotalCost();

            useCategoryCost.setTotalCost(useCategoryCost.getTotalCost().add(totalCost));
            if (useCategory == 1) {
                useCategoryCost.setMustCost(totalCost);
                useCategoryCost.setMustRandomCost(useCategoryCost.getMustRandomCost().add(totalCost));
            } else if (useCategory == 2) {
                useCategoryCost.setRandomCost(totalCost);
                useCategoryCost.setMustRandomCost(useCategoryCost.getMustRandomCost().add(totalCost));
            } else if (useCategory == 3) {
                useCategoryCost.setConsumeCost(totalCost);
            } else {
                useCategoryCost.setOtherCost(useCategoryCost.getOtherCost().add(totalCost));
            }
        }

        return useCategoryCost;
    }

    private String getDiffString(BigDecimal diff) {
        diff = diff.divide(BigDecimal.valueOf(10000), 2, BigDecimal.ROUND_HALF_UP);
        int compare = diff.compareTo(BigDecimal.ZERO);
        if (compare == 0) {
            return "持平";
        } else if (compare < 0) {
            return "减少" + diff.abs().stripTrailingZeros().toPlainString() + "万元";
        } else {
            return "增加" + diff.abs().stripTrailingZeros().toPlainString() + "万元";
        }
    }

}
