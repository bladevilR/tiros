package org.jeecg.modules.quality.measureanalyse.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.quality.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.quality.measurealert.mapper.BuPlanFormValuesQualityMapper;
import org.jeecg.modules.quality.measureanalyse.bean.vo.BuMeasureAnalyseQueryVO;
import org.jeecg.modules.quality.measureanalyse.service.BuMeasureAnalyseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 测量值分析 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/20
 */
@Slf4j
@Service
public class BuMeasureAnalyseServiceImpl implements BuMeasureAnalyseService {

    @Resource
    private BuPlanFormValuesQualityMapper buPlanFormValuesQualityMapper;


    /**
     * @see BuMeasureAnalyseService#getMeasureTrend(BuMeasureAnalyseQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<Map<String, Object>> getMeasureTrend(BuMeasureAnalyseQueryVO queryVO) throws Exception {
        List<String> itemIdList = queryVO.getItemIdList();
        if (CollectionUtils.isEmpty(itemIdList)) {
            throw new JeecgBootException("待分析测量项为空");
        }
        queryVO.toStartEndDate();

        List<Map<String, Object>> resultList = new ArrayList<>();

        // 查询测量结果
        List<BuPlanFormValues> formValuesList = buPlanFormValuesQualityMapper.selectListForAnalyseByCondition(queryVO);

        Map<String, List<BuPlanFormValues>> itemNameFormValuesListMap = formValuesList.stream()
                .filter(values -> StringUtils.isNotBlank(values.getItemName()))
                .collect(Collectors.groupingBy(BuPlanFormValues::getItemName));
        for (Map.Entry<String, List<BuPlanFormValues>> itemNameFormValuesListEntry : itemNameFormValuesListMap.entrySet()) {
            String itemName = itemNameFormValuesListEntry.getKey();
            List<BuPlanFormValues> valuesList = itemNameFormValuesListEntry.getValue();

            Map<String, Object> thresholdMap = new LinkedHashMap<>();
            thresholdMap.put("type", itemName + "参考值");
            Map<String, Object> measureMap = new LinkedHashMap<>();
            measureMap.put("type", itemName + "测量值");

            Double threshold = valuesList.get(0).getThreshold();

            if (Arrays.asList(3, 4).contains(queryVO.getDateType())) {
                // 如果是月或时间段，测量值取每次的值
                addEachMeasurement(thresholdMap, measureMap, threshold, valuesList);
            } else {
                // 如果是年或者季度，测量值取每个月的平均值
                List<Integer> monthList = getMonthList(queryVO);
                addAverageMeasurement(thresholdMap, measureMap, threshold, valuesList, monthList);
            }

            resultList.add(thresholdMap);
            resultList.add(measureMap);
        }

        return resultList;
    }


    private void addEachMeasurement(Map<String, Object> thresholdMap, Map<String, Object> measureMap, Double threshold, List<BuPlanFormValues> formValuesList) {
        for (BuPlanFormValues formValues : formValuesList) {
            Date alertTime = formValues.getAlertTime();
            if (null != alertTime) {
                String date = DateUtils.datetimeFormat.get().format(alertTime);
                Double value = getItemValue(formValues);

                thresholdMap.put(date, threshold);
                measureMap.put(date, value);
            }
        }
    }

    private void addAverageMeasurement(Map<String, Object> thresholdMap, Map<String, Object> measureMap, Double threshold, List<BuPlanFormValues> formValuesList, List<Integer> monthList) {
        Map<Integer, List<BuPlanFormValues>> monthFormValuesListMap = formValuesList.stream()
                .collect(Collectors.groupingBy(values -> values.getAlertTime().getMonth() + 1));

        for (Integer month : monthList) {
            String monthStr = month + "月";
            Double averageValue = getItemAverageValue(monthFormValuesListMap.get(month));

            thresholdMap.put(monthStr, threshold);
            measureMap.put(monthStr, averageValue);
        }
    }

    private Double getItemAverageValue(List<BuPlanFormValues> formValuesList) {
        Double averageValue = null;

        if (CollectionUtils.isNotEmpty(formValuesList)) {
            Double totalValue = 0D;
            int count = 0;
            for (BuPlanFormValues formValues : formValuesList) {
                Double itemValue = getItemValue(formValues);
                if (itemValue != null) {
                    totalValue = totalValue + itemValue;
                    count++;
                }
            }
            if (count != 0) {
                averageValue = totalValue / count;
            }
        }

        return averageValue;
    }

    private Double getItemValue(BuPlanFormValues formValues) {
        if (formValues.getAlertHappen() == 1 && formValues.getStatus() == 1) {
            // 如果是需要预警，且预警已关闭的记录，取修正值
            return formValues.getFixedValue();
        }
        return formValues.getAlertValue();
    }

    private List<Integer> getMonthList(BuMeasureAnalyseQueryVO queryVO) {
        int startMonth = 0;
        int endMonth = 0;

        Integer dateType = queryVO.getDateType();
        Integer year = queryVO.getYear();
        Integer quarter = queryVO.getQuarter();

        if (2 == dateType) {
            startMonth = (quarter - 1) * 3 + 1;
            endMonth = startMonth + 2;
        } else if (1 == dateType) {
            startMonth = 1;
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            if (year != currentYear) {
                endMonth = 12;
            } else {
                endMonth = calendar.get(Calendar.MONTH) + 1;
            }
        }

        List<Integer> monthList = new ArrayList<>();
        for (int i = startMonth; i <= endMonth; i++) {
            monthList.add(i);
        }

        return monthList;
    }

}
