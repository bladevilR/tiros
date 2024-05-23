package org.jeecg.modules.group.measurealert.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.rpt.service.AlertRecordService;
import org.jeecg.modules.group.measurealert.bean.BuPlanFormDataRecord;
import org.jeecg.modules.group.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.group.measurealert.bean.BuWorkMeasureItem;
import org.jeecg.modules.group.measurealert.bean.vo.BuWorkMeasureAlertCloseVO;
import org.jeecg.modules.group.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;
import org.jeecg.modules.group.measurealert.mapper.BuPlanFormDataRecordGroupMapper;
import org.jeecg.modules.group.measurealert.mapper.BuPlanFormValuesGroupMapper;
import org.jeecg.modules.group.measurealert.service.BuPlanFormValuesGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 测量记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Slf4j
@Service
public class BuPlanFormValuesGroupServiceImpl extends ServiceImpl<BuPlanFormValuesGroupMapper, BuPlanFormValues> implements BuPlanFormValuesGroupService {

    @Resource
    private BuPlanFormValuesGroupMapper buPlanFormValuesGroupMapper;
    @Resource
    private BuPlanFormDataRecordGroupMapper buPlanFormDataRecordGroupMapper;
    @Resource
    private AlertRecordService alertRecordService;


    /**
     * @see BuPlanFormValuesGroupService#page(BuWorkMeasureAlertQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuPlanFormValues> page(BuWorkMeasureAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuPlanFormValues> page = buPlanFormValuesGroupMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        setWorkstationName(page.getRecords());
        setThresholdOperatorText(page.getRecords());

        return page;
    }

    /**
     * @see BuPlanFormValuesGroupService#close(BuWorkMeasureAlertCloseVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean close(BuWorkMeasureAlertCloseVO closeVO) throws Exception {
        BuPlanFormValues dataValue = buPlanFormValuesGroupMapper.selectById(closeVO.getId());
        if (null == dataValue) {
            throw new JeecgBootException("测量预警记录不存在");
        }

        String measureThresholdId = dataValue.getMeasureThresholdId();
        BuWorkMeasureItem measureItem = buPlanFormValuesGroupMapper.selectMeasureItem(measureThresholdId);

        // 获取是否预警，0不需要预警 1操作符预警 2操作符2预警
        int alertType = checkAlertType(closeVO.getNewValue(), measureItem);
        if (0 == alertType) {
            dataValue
                    .setFixedValue(closeVO.getNewValue())
                    .setFixDesc(closeVO.getRemark())
                    .setStatus(1);
            buPlanFormValuesGroupMapper.updateById(dataValue);

            // 把新的值写入到表单数据值中
            writeNewValueToDataRecord(dataValue, closeVO.getNewValue());

            // 设置测量异常预警为已处理
            alertRecordService.setAlertRecordHandled(5, Collections.singletonList(closeVO.getId()));
        } else {
            throw new JeecgBootException("新的测量值超出阈值，请重新设置");
        }

        return true;
    }


    private void setWorkstationName(List<BuPlanFormValues> formValuesList) {
        if (CollectionUtils.isNotEmpty(formValuesList)) {
            for (BuPlanFormValues formValues : formValuesList) {
                List<String> workstationNameList = formValues.getWorkstationNameList();
                if (CollectionUtils.isNotEmpty(workstationNameList)) {
                    formValues.setWorkstationNames(String.join(",", workstationNameList));
                }
            }
        }
    }

    private void setThresholdOperatorText(List<BuPlanFormValues> formValuesList) {
        if (CollectionUtils.isNotEmpty(formValuesList)) {
            Map<Integer, String> operatorTextMap = initOperatorTextMap();

            for (BuPlanFormValues formValues : formValuesList) {
                Integer thresholdOperator = formValues.getThresholdOperator();
                Double thresholdValue = formValues.getThresholdValue();
                Integer thresholdOperator2 = formValues.getThresholdOperator2();
                Double thresholdValue2 = formValues.getThresholdValue2();

                String thresholdValueDesc = "";
                if (null != thresholdOperator && null != thresholdValue) {
                    String operatorText = operatorTextMap.get(thresholdOperator);
                    if (StringUtils.isNotBlank(operatorText)) {
                        thresholdValueDesc = thresholdValueDesc + operatorText + thresholdValue;
                    }
                }
                if (null != thresholdOperator2 && null != thresholdValue2) {
                    String operatorText2 = operatorTextMap.get(thresholdOperator2);
                    if (StringUtils.isNotBlank(operatorText2)) {
                        thresholdValueDesc = thresholdValueDesc + operatorText2 + thresholdValue2;
                    }
                }
                formValues.setThresholdValueDesc(thresholdValueDesc);
            }
        }
    }

    private Map<Integer, String> initOperatorTextMap() {
        Map<Integer, String> operatorTextMap = new HashMap<>(8);

        operatorTextMap.put(1, "等于");
        operatorTextMap.put(2, "小于");
        operatorTextMap.put(3, "小于等于");
        operatorTextMap.put(4, "大于");
        operatorTextMap.put(5, "大于等于");
        operatorTextMap.put(6, "不等于");

        return operatorTextMap;
    }

    private int checkAlertType(Double value, BuWorkMeasureItem measureItem) throws Exception {
        boolean alert1 = checkThreshold(measureItem.getOperator(), measureItem.getThreshold(), value);
        boolean alert2 = checkThreshold(measureItem.getOperator2(), measureItem.getThreshold2(), value);

        if (alert1) {
            return 1;
        } else if (alert2) {
            return 2;
        } else {
            return 0;
        }
    }

    private boolean checkThreshold(Integer operator, Double threshold, Double value) throws Exception {
        if (null == operator) {
            return false;
        }
        if (null == threshold || null == value) {
            return false;
        }

        boolean alert = true;
        String operatorString = null;
        // 操作符 1等于2小于3小于等于4大于5大于等于6不等于
        switch (operator) {
            case 1:
                operatorString = "等于";
                if (value.equals(threshold)) {
                    alert = false;
                }
                break;
            case 2:
                operatorString = "小于";
                if (value < threshold) {
                    alert = false;
                }
                break;
            case 3:
                operatorString = "小于等于";
                if (value <= threshold) {
                    alert = false;
                }
                break;
            case 4:
                operatorString = "大于";
                if (value > threshold) {
                    alert = false;
                }
                break;
            case 5:
                operatorString = "大于等于";
                if (value >= threshold) {
                    alert = false;
                }
                break;
            case 6:
                operatorString = "不等于";
                if (!value.equals(threshold)) {
                    alert = false;
                }
                break;
            default:
                break;
        }

        String alterMessage = "新的值应" + operatorString + threshold + "，请重新输入";
        if (alert) {
            throw new JeecgBootException(alterMessage);
        }

        return alert;
    }

    private void writeNewValueToDataRecord(BuPlanFormValues dataValue, Double newValue) {
        // 查询数据表实例，获取内容json
        String dataRecordId = dataValue.getFormDataRecordId();
        BuPlanFormDataRecord dataRecord = buPlanFormDataRecordGroupMapper.selectById(dataRecordId);
        String resultString = dataRecord.getResult();
        JSONArray result = JSON.parseArray(resultString);

        // 获取行、列
        String linkDomain = dataValue.getLinkDomain();
        String[] rowColumnIndex = linkDomain.split("\\$");
        String rowIndex = rowColumnIndex[1];
        String columnIndex = rowColumnIndex[2];

        // 写入新值并更新
        writeResultValue(result, Integer.valueOf(rowIndex), Integer.valueOf(columnIndex), newValue);
        dataRecord.setResult(JSON.toJSONString(result));
        buPlanFormDataRecordGroupMapper.updateById(dataRecord);
    }

    private void writeResultValue(JSONArray resultDataArray, Integer rowIndex, Integer columnIndex, Double newValue) {
        JSONArray rowData = resultDataArray.getJSONArray(rowIndex);
        if (null != rowData) {
            JSONObject columnData = rowData.getJSONObject(columnIndex);
            if (null != columnData) {
                columnData.put("v", newValue);
            }
        }
    }

}
