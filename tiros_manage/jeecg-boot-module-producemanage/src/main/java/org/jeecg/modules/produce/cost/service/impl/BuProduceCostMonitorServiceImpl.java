package org.jeecg.modules.produce.cost.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.produce.cost.bean.vo.*;
import org.jeecg.modules.produce.cost.mapper.BuWorkOrderMaterialProduceMapper;
import org.jeecg.modules.produce.cost.service.BuProduceCostMonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物资消耗 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-22
 */
@Slf4j
@Service
public class BuProduceCostMonitorServiceImpl implements BuProduceCostMonitorService {

    @Resource
    private BuWorkOrderMaterialProduceMapper buWorkOrderMaterialProduceMapper;
    @Resource
    private AssetTypeCacheService assetTypeCacheService;


    /**
     * @see BuProduceCostMonitorService#pageOrderMaterial(BuCostQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkOrderMaterial> pageOrderMaterial(BuCostQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        queryVO.toStartEndDate();
//        String sysName = setQueryAssetTypeIdListBySysId(queryVO);

        IPage<BuWorkOrderMaterial> page = buWorkOrderMaterialProduceMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        // 设置消耗数量价格
        setConsumeAmountAndPrice(page.getRecords());

        return page;
    }

    /**
     * @see BuProduceCostMonitorService#countCost(BuCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public CostCountVO countCost(BuCostQueryVO queryVO) throws Exception {
        queryVO.toStartEndDate();
//        String sysName = setQueryAssetTypeIdListBySysId(queryVO);

        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialProduceMapper.selectSimpleListByCondition(queryVO);
        // 设置消耗数量价格
        setConsumeAmountAndPrice(orderMaterialList);

        CostCountVO costCountVO = new CostCountVO()
                .setTotal(new BigDecimal(0))
                .setMust(new BigDecimal(0))
                .setRandom(new BigDecimal(0))
                .setConsume(new BigDecimal(0));

        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            BigDecimal totalAmount = orderMaterialList.stream()
                    .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            costCountVO.setTotal(totalAmount);

            Map<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListMap = orderMaterialList.stream()
                    .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUseCategory));
            for (Map.Entry<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListEntry : useCategoryOrderMaterialListMap.entrySet()) {
                Integer useCategory = useCategoryOrderMaterialListEntry.getKey();
                List<BuWorkOrderMaterial> materialList = useCategoryOrderMaterialListEntry.getValue();

                BigDecimal expendAmount = materialList.stream()
                        .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (useCategory == 1) {
                    costCountVO.setMust(expendAmount);
                } else if (useCategory == 2) {
                    costCountVO.setRandom(expendAmount);
                } else if (useCategory == 3) {
                    costCountVO.setConsume(expendAmount);
                }
            }
        }

        return costCountVO;
    }

    /**
     * @see BuProduceCostMonitorService#getCostStatistics(BuCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public CostStatisticsVO getCostStatistics(BuCostQueryVO queryVO) throws Exception {
        if (null != queryVO.getSingle() && queryVO.getSingle() && StringUtils.isBlank(queryVO.getTrainNo())) {
            throw new JeecgBootException("列成本分析需指定车辆");
        }

        queryVO.toStartEndDate();
//        String sysName = setQueryAssetTypeIdListBySysId(queryVO);

        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialProduceMapper.selectSimpleListByCondition(queryVO);
        // 设置消耗数量价格
        setConsumeAmountAndPrice(orderMaterialList);

        CostStatisticsVO costStatisticsVO = new CostStatisticsVO()
                .setSysFieldList(new ArrayList<>())
                .setSysDataList(new ArrayList<>())
                .setCategoryDataList(new ArrayList<>())
                .setMustDataList(new ArrayList<>())
                .setRandomDataList(new ArrayList<>())
                .setConsumeDataList(new ArrayList<>());

        setCostStatisticsVO(costStatisticsVO, orderMaterialList);

        if (null != queryVO.getAverage() && queryVO.getAverage()) {
            long trainNumber = orderMaterialList.stream()
                    .map(BuWorkOrderMaterial::getTrainNo)
                    .distinct()
                    .count();
            averageCostStatisticsVO(costStatisticsVO, trainNumber);
        }

        return costStatisticsVO;
    }

    /**
     * @see BuProduceCostMonitorService#getCostYearTrend()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public CostCompareYearTrendVO getCostYearTrend() throws Exception {
        // 没有查询条件，查询所有的工单物料
        BuCostQueryVO queryVO = new BuCostQueryVO();
        setTimeRecentSixYears(queryVO);

        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialProduceMapper.selectSimpleListByCondition(queryVO);
        // 设置消耗数量价格
        setConsumeAmountAndPrice(orderMaterialList);

        // 添加车辆架大修成本趋势(叠柱状图)
        Map<String, Object> mustData = new HashMap<>(8);
        mustData.put("type", "必换件");
        Map<String, Object> randomData = new HashMap<>(8);
        randomData.put("type", "偶换件");
        Map<String, Object> consumeData = new HashMap<>(8);
        consumeData.put("type", "耗材");
        List<String> yearList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 5; i >= 0; i--) {
            String year = (currentYear - i) + "";
            yearList.add(year);
            mustData.put(year, 0D);
            randomData.put(year, 0D);
            consumeData.put(year, 0D);
        }

        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            Map<Integer, List<BuWorkOrderMaterial>> yearOrderMaterialListMap = orderMaterialList.stream()
                    .filter(orderMaterial -> null != orderMaterial.getConsumeTime())
                    .collect(Collectors.groupingBy(orderMaterial -> DateUtils.getYear(orderMaterial.getConsumeTime())));
            for (Map.Entry<Integer, List<BuWorkOrderMaterial>> yearOrderMaterialListEntry : yearOrderMaterialListMap.entrySet()) {
                Integer year = yearOrderMaterialListEntry.getKey();
                List<BuWorkOrderMaterial> yearOrderMaterialList = yearOrderMaterialListEntry.getValue();

                Map<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListMap = yearOrderMaterialList.stream()
                        .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUseCategory));
                for (Map.Entry<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListEntry : useCategoryOrderMaterialListMap.entrySet()) {
                    Integer useCategory = useCategoryOrderMaterialListEntry.getKey();
                    List<BuWorkOrderMaterial> materialList = useCategoryOrderMaterialListEntry.getValue();

                    BigDecimal expendAmount = materialList.stream()
                            .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (useCategory == 1) {
                        mustData.put(year.toString(), expendAmount.doubleValue());
                    } else if (useCategory == 2) {
                        randomData.put(year.toString(), expendAmount.doubleValue());
                    } else if (useCategory == 3) {
                        consumeData.put(year.toString(), expendAmount.doubleValue());
                    }
                }
            }
        }

        return new CostCompareYearTrendVO()
                .setFieldList(yearList)
                .setDataList(Arrays.asList(mustData, randomData, consumeData));
    }

    /**
     * @see BuProduceCostMonitorService#compareCostCount(BuCostCompareQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuCostCompareResultVO compareCostCount(BuCostCompareQueryVO compareQueryVO) throws Exception {
        if (null == compareQueryVO.getDoCompare()) {
            compareQueryVO.setDoCompare(false);
        }
        Boolean doCompare = compareQueryVO.getDoCompare();

        if (!doCompare) {
            CostStatisticsVO result1 = getCostStatistics(compareQueryVO.getQuery1());
            return transformToCostCompareResultVO(false, result1, new CostStatisticsVO());
        }

        CostStatisticsVO result1 = getCostStatistics(compareQueryVO.getQuery1());
        CostStatisticsVO result2 = getCostStatistics(compareQueryVO.getQuery2());
        return transformToCostCompareResultVO(true, result1, result2);
    }


    private void setConsumeAmountAndPrice(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            if (null == orderMaterial.getConsumeAmount()) {
                orderMaterial.setConsumeAmount(BigDecimal.ZERO);
            }
            if (null == orderMaterial.getConsumeTotalPrice()) {
                orderMaterial.setConsumeTotalPrice(BigDecimal.ZERO);
            }
            if (BigDecimal.ZERO.compareTo(orderMaterial.getConsumeAmount()) < 0) {
                orderMaterial.setUnitPrice(orderMaterial.getConsumeTotalPrice().divide(orderMaterial.getConsumeAmount(), 8, BigDecimal.ROUND_HALF_UP));
            } else {
                orderMaterial.setUnitPrice(BigDecimal.ZERO);
            }
        }

        orderMaterialList.removeIf(orderMaterial ->
                BigDecimal.ZERO.compareTo(orderMaterial.getConsumeAmount()) >= 0
                        && BigDecimal.ZERO.compareTo(orderMaterial.getConsumeTotalPrice()) >= 0
                        && BigDecimal.ZERO.compareTo(orderMaterial.getUnitPrice()) >= 0);
    }

    private List<PieChartVO> getSystemPieChartVOList(List<BuWorkOrderMaterial> orderMaterialList) {
        List<PieChartVO> pieChartVOList = new ArrayList<>();
        Map<String, List<BuWorkOrderMaterial>> systemNameOrderMaterialListMap = orderMaterialList.stream()
                .filter(applyDetail -> StringUtils.isNotBlank(applyDetail.getSystemName()))
                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getSystemName));

        for (Map.Entry<String, List<BuWorkOrderMaterial>> systemNameOrderMaterialListEntry : systemNameOrderMaterialListMap.entrySet()) {
            String systemName = systemNameOrderMaterialListEntry.getKey();
            List<BuWorkOrderMaterial> materialList = systemNameOrderMaterialListEntry.getValue();

            BigDecimal systemExpend = materialList.stream()
                    .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            PieChartVO pieChartVO = new PieChartVO()
                    .setItem(systemName)
                    .setCount(systemExpend.doubleValue());
            pieChartVOList.add(pieChartVO);
        }

        return pieChartVOList;
    }

    private void setCostStatisticsVO(CostStatisticsVO costStatisticsVO, List<BuWorkOrderMaterial> orderMaterialList) {
        PieChartVO mustCount = new PieChartVO().setItem("必换件").setCount(0D);
        PieChartVO randomCount = new PieChartVO().setItem("偶换件").setCount(0D);
        PieChartVO consumeCount = new PieChartVO().setItem("耗材").setCount(0D);

        Map<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListMap = orderMaterialList.stream()
                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUseCategory));
        for (Map.Entry<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListEntry : useCategoryOrderMaterialListMap.entrySet()) {
            Integer useCategory = useCategoryOrderMaterialListEntry.getKey();
            List<BuWorkOrderMaterial> materialList = useCategoryOrderMaterialListEntry.getValue();

            BigDecimal expendAmount = materialList.stream()
                    .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            List<PieChartVO> systemPieChartVOList = getSystemPieChartVOList(materialList);

//            sysFieldSet.addAll(systemPieChartVOList.stream()
//                    .map(PieChartVO::getItem)
//                    .collect(Collectors.toSet()));

            if (useCategory == 1) {
                mustCount.setCount(expendAmount.doubleValue());
                // 添加必换件消耗分类占比(饼图)
                costStatisticsVO.setMustDataList(systemPieChartVOList);
            } else if (useCategory == 2) {
                randomCount.setCount(expendAmount.doubleValue());
                // 添加偶换件消耗分类占比(饼图)
                costStatisticsVO.setRandomDataList(systemPieChartVOList);
            } else if (useCategory == 3) {
                consumeCount.setCount(expendAmount.doubleValue());
                // 添加耗材消耗分类占比(饼图)
                costStatisticsVO.setConsumeDataList(systemPieChartVOList);
            }
        }

        // 添加物料消耗分类占比(饼图)
        costStatisticsVO.setCategoryDataList(Arrays.asList(mustCount, randomCount, consumeCount));

        // 添加系统物料消耗统计(多柱状图)
        Map<String, BuTrainAssetTypeBO> idSysBOList = assetTypeCacheService.mapSys(null);
        Set<String> sysFieldSet = idSysBOList.values().stream()
                .map(BuTrainAssetTypeBO::getShortName)
                .collect(Collectors.toSet());
        Map<String, Object> mustSysData = new HashMap<>();
        mustSysData.put("type", "必换件");
        Map<String, Object> randomSysData = new HashMap<>();
        randomSysData.put("type", "偶换件");
        Map<String, Object> consumeSysData = new HashMap<>();
        consumeSysData.put("type", "耗材");
        for (String sysName : sysFieldSet) {
            mustSysData.put(sysName, 0D);
            randomSysData.put(sysName, 0D);
            consumeSysData.put(sysName, 0D);
        }
        List<PieChartVO> mustDataList = costStatisticsVO.getMustDataList();
        mustDataList.forEach(must -> mustSysData.put(must.getItem(), must.getCount()));
        List<PieChartVO> randomDataList = costStatisticsVO.getRandomDataList();
        randomDataList.forEach(random -> randomSysData.put(random.getItem(), random.getCount()));
        List<PieChartVO> consumeDataList = costStatisticsVO.getConsumeDataList();
        consumeDataList.forEach(consume -> consumeSysData.put(consume.getItem(), consume.getCount()));
        costStatisticsVO.setSysFieldList(new ArrayList<>(sysFieldSet));
        costStatisticsVO.setSysDataList(Arrays.asList(mustSysData, randomSysData, consumeSysData));
    }

    private void averageCostStatisticsVO(CostStatisticsVO costStatisticsVO, long trainNumber) {
        averagePieChartVOList(costStatisticsVO.getCategoryDataList(), trainNumber);
        averagePieChartVOList(costStatisticsVO.getMustDataList(), trainNumber);
        averagePieChartVOList(costStatisticsVO.getRandomDataList(), trainNumber);
        averagePieChartVOList(costStatisticsVO.getConsumeDataList(), trainNumber);

        List<Map<String, Object>> sysDataList = costStatisticsVO.getSysDataList();
        if (CollectionUtils.isNotEmpty(sysDataList)) {
            for (Map<String, Object> map : sysDataList) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals("type")) {
                        continue;
                    }
                    if (entry.getValue() instanceof Double) {
                        double value = (Double) entry.getValue();
                        if (value != 0) {
                            BigDecimal divide = new BigDecimal(value).divide(new BigDecimal(trainNumber), 8, BigDecimal.ROUND_HALF_UP);
                            entry.setValue(divide.doubleValue());
                        }
                    }
                }
            }
        }
    }

    private void averagePieChartVOList(List<PieChartVO> pieChartVOList, long trainNumber) {
        if (CollectionUtils.isNotEmpty(pieChartVOList)) {
            for (PieChartVO pieChartVO : pieChartVOList) {
                Double count = pieChartVO.getCount();
                if (null != count && count != 0) {
                    BigDecimal divide = new BigDecimal(count).divide(new BigDecimal(trainNumber), 8, BigDecimal.ROUND_HALF_UP);
                    pieChartVO.setCount(divide.doubleValue());
                }
            }
        }
    }

    private BuCostCompareResultVO transformToCostCompareResultVO(boolean doCompare, CostStatisticsVO result1, CostStatisticsVO result2) {
        BuCostCompareResultVO costCompareResultVO = new BuCostCompareResultVO();

        String compareName1 = "比较项1";
        String compareName2 = "比较项2";
        if (!doCompare) {
            compareName1 = "物料成本";
        }

        // 比较项1
        List<String> sysFieldList1 = result1.getSysFieldList();
        List<PieChartVO> categoryDataList1 = result1.getCategoryDataList();
        List<PieChartVO> mustDataList1 = result1.getMustDataList();
        List<PieChartVO> randomDataList1 = result1.getRandomDataList();
        List<PieChartVO> consumeDataList1 = result1.getConsumeDataList();

        // 比较项2
        List<String> sysFieldList2 = new ArrayList<>();
        List<PieChartVO> categoryDataList2 = new ArrayList<>();
        List<PieChartVO> mustDataList2 = new ArrayList<>();
        List<PieChartVO> randomDataList2 = new ArrayList<>();
        List<PieChartVO> consumeDataList2 = new ArrayList<>();
        if (doCompare) {
            sysFieldList2 = result2.getSysFieldList();
            categoryDataList2 = result2.getCategoryDataList();
            mustDataList2 = result2.getMustDataList();
            randomDataList2 = result2.getRandomDataList();
            consumeDataList2 = result2.getConsumeDataList();
        }

        // 系统物料消耗对比
        Set<String> sysFieldSet = new HashSet<>(8);
        sysFieldSet.addAll(sysFieldList1);
        if (doCompare) {
            sysFieldSet.addAll(sysFieldList2);
        }
        Map<String, Object> sysData1 = new HashMap<>(8);
        sysData1.put("type", compareName1);
        for (String sysField : sysFieldSet) {
            getSysTotalCost(mustDataList1, randomDataList1, consumeDataList1, sysData1, sysField);
        }
        Map<String, Object> sysData2 = new HashMap<>(8);
        if (doCompare) {
            sysData2.put("type", compareName2);
            for (String sysField : sysFieldSet) {
                getSysTotalCost(mustDataList2, randomDataList2, consumeDataList2, sysData2, sysField);
            }
        }
        costCompareResultVO.setSystemFieldList(new ArrayList<>(sysFieldSet));
        if (doCompare) {
            costCompareResultVO.setSystemDataList(Arrays.asList(sysData1, sysData2));
        } else {
            costCompareResultVO.setSystemDataList(Collections.singletonList(sysData1));
        }


        // 物料分类消耗对比
        Map<String, Object> categoryData1 = getCategoryData(categoryDataList1, compareName1);
        Map<String, Object> categoryData2 = new HashMap<>(4);
        if (doCompare) {
            categoryData2 = getCategoryData(categoryDataList2, compareName2);
        }
        costCompareResultVO.setCategoryFieldList(Arrays.asList("必换件", "偶换件", "耗材"));
        if (doCompare) {
            costCompareResultVO.setCategoryDataList(Arrays.asList(categoryData1, categoryData2));
        } else {
            costCompareResultVO.setCategoryDataList(Collections.singletonList(categoryData1));
        }

        // 必换件系统消耗对比
        Map<String, Object> mustData1 = new HashMap<>(8);
        mustData1.put("type", compareName1);
        for (String sysField : sysFieldSet) {
            double mustCost1 = mustDataList1.stream()
                    .filter(must -> must.getItem().equals(sysField))
                    .mapToDouble(PieChartVO::getCount)
                    .sum();
            mustData1.put(sysField, mustCost1);
        }
        Map<String, Object> mustData2 = new HashMap<>(8);
        if (doCompare) {
            mustData2.put("type", compareName2);
            for (String sysField : sysFieldSet) {
                double mustCost2 = mustDataList2.stream()
                        .filter(must -> must.getItem().equals(sysField))
                        .mapToDouble(PieChartVO::getCount)
                        .sum();
                mustData2.put(sysField, mustCost2);
            }
        }
        costCompareResultVO.setMustSystemFieldList(new ArrayList<>(sysFieldSet));
        if (doCompare) {
            costCompareResultVO.setMustSystemDataList(Arrays.asList(mustData1, mustData2));
        } else {
            costCompareResultVO.setMustSystemDataList(Collections.singletonList(mustData1));
        }

        return costCompareResultVO;
    }

    private void getSysTotalCost(List<PieChartVO> mustDataList, List<PieChartVO> randomDataList, List<PieChartVO> consumeDataList, Map<String, Object> sysData, String sysField) {
        double mustCost = mustDataList.stream()
                .filter(must -> must.getItem().equals(sysField))
                .mapToDouble(PieChartVO::getCount)
                .sum();
        double randomCost = randomDataList.stream()
                .filter(random -> random.getItem().equals(sysField))
                .mapToDouble(PieChartVO::getCount)
                .sum();
        double consumeCost = consumeDataList.stream()
                .filter(consume -> consume.getItem().equals(sysField))
                .mapToDouble(PieChartVO::getCount)
                .sum();
        sysData.put(sysField, mustCost + randomCost + consumeCost);
    }

    private Map<String, Object> getCategoryData(List<PieChartVO> categoryDataList, String type) {
        Map<String, Object> categoryData = new HashMap<>(4);
        categoryData.put("type", type);
        for (PieChartVO pieChartVO : categoryDataList) {
            if ("必换件".equals(pieChartVO.getItem())) {
                categoryData.put("必换件", pieChartVO.getCount());
            }
            if ("偶换件".equals(pieChartVO.getItem())) {
                categoryData.put("偶换件", pieChartVO.getCount());
            }
            if ("耗材".equals(pieChartVO.getItem())) {
                categoryData.put("耗材", pieChartVO.getCount());
            }
        }
        return categoryData;
    }

    private void setTimeRecentSixYears(BuCostQueryVO queryVO) {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.MONTH, Calendar.DECEMBER);
        int monthLastDay = endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        endCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 59);
        queryVO.setEndDate(endCalendar.getTime());

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.YEAR, -6);
        startCalendar.set(Calendar.MONTH, Calendar.JANUARY);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        queryVO.setStartDate(startCalendar.getTime());
    }

}
