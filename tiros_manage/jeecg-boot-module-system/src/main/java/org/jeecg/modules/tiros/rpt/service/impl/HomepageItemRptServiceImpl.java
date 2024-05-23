package org.jeecg.modules.tiros.rpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.rpt.constrant.HomepageItemCode;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.board.homepage.bean.BuRptBoardDataItem;
import org.jeecg.modules.board.homepage.mapper.BuRptBoardDataItemMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 生成首页统计数据项定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class HomepageItemRptServiceImpl implements HomepageItemRptService {

    @Resource
    private BuRptBoardDataItemMapper buRptBoardDataItemMapper;


    /**
     * @see HomepageItemRptService#rewriteDataItem()
     */
    @Override
    public boolean rewriteDataItem() throws RuntimeException {
        // 查询数据区数据
        LambdaQueryWrapper<BuRptBoardDataItem> wrapper = new LambdaQueryWrapper<BuRptBoardDataItem>().eq(BuRptBoardDataItem::getCategoryId, "2");
        List<BuRptBoardDataItem> itemList = buRptBoardDataItemMapper.selectList(wrapper);
        Map<String, BuRptBoardDataItem> codeItemMap = new HashMap<>();
        itemList.forEach(item -> codeItemMap.put(item.getItemCode(), item));

        // 数据区数据，查询当月数据
        // 当前架修数
        Integer repairingPlan = buRptBoardDataItemMapper.countRepairingPlanCurrentMonth();
        codeItemMap.get(HomepageItemCode.DATA_CURRENT_PLAN).setItemValue(String.valueOf(repairingPlan));

        // 当前工单
        // Integer unfinishedOrder = buRptBoardDataItemMapper.countUnfinishedOrderCurrentMonth();
        // codeItemMap.get(HomepageItemCode.DATA_CURRENT_ORDER).setItemValue(String.valueOf(unfinishedOrder));
        Integer allOrder = buRptBoardDataItemMapper.countUnfinishedOrderCurrentMonth();
        codeItemMap.get(HomepageItemCode.DATA_CURRENT_ORDER).setItemValue(String.valueOf(allOrder));
        // 作业任务
        // Integer unfinishedOrderTask = buRptBoardDataItemMapper.countUnfinishedOrderTaskCurrentMonth();
        // codeItemMap.get(HomepageItemCode.DATA_CURRENT_ORDER_TASK).setItemValue(String.valueOf(unfinishedOrderTask));
        // 作业任务因为现在和工单数量一样，改成存放已完成工单, 已关闭的工单视为已完成
        Integer finishedOrderCnt = buRptBoardDataItemMapper.countCurrentMonthOrderByStatus(4);
        codeItemMap.get(HomepageItemCode.DATA_CURRENT_ORDER_TASK).setItemValue(String.valueOf(finishedOrderCnt));

        // 任务完成比
        // Integer unfinishedOrderAllTask = buRptBoardDataItemMapper.countUnfinishedOrderAllTaskCurrentMonth();
        // int finishedOrderTask = unfinishedOrderAllTask - unfinishedOrderTask;
        // codeItemMap.get(HomepageItemCode.DATA_TASK_FINISH_PERCENT).setItemValue(String.valueOf(PercentUtils.percent(finishedOrderTask, unfinishedOrderAllTask)));
        // 任务完成比， 当月已关闭工单数 / 当前工单数
       /* float finishPrecent = 0;
        if (finishedOrderCnt != 0 && allOrder != 0) {
            finishPrecent = finishedOrderCnt / allOrder * 100;
        }*/
        codeItemMap.get(HomepageItemCode.DATA_TASK_FINISH_PERCENT).setItemValue(String.valueOf(PercentUtils.percent(finishedOrderCnt, allOrder)));


        // 消耗物料成本
        List<BuWorkOrderMaterial> orderMaterialList = buRptBoardDataItemMapper.countMaterialCostCurrentMonth();
        // 设置消耗数量价格
        setConsumeAmountAndPrice(orderMaterialList);
        BigDecimal materialCost = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        codeItemMap.get(HomepageItemCode.DATA_MATERIAL_COST).setItemValue(materialCost.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        // 发现故障数
        Integer fault = buRptBoardDataItemMapper.countFaultCurrentMonth();
        codeItemMap.get(HomepageItemCode.DATA_FOUND_FAULT).setItemValue(String.valueOf(fault));

        for (BuRptBoardDataItem dataItem : codeItemMap.values()) {
            buRptBoardDataItemMapper.updateById(dataItem);
        }

        return true;
    }

    /**
     * @see HomepageItemRptService#rewriteAlertItem()
     */
    @Override
    public boolean rewriteAlertItem() throws RuntimeException {
        // 查询预警区数据
        LambdaQueryWrapper<BuRptBoardDataItem> wrapper = new LambdaQueryWrapper<BuRptBoardDataItem>().eq(BuRptBoardDataItem::getCategoryId, "1");
        List<BuRptBoardDataItem> itemList = buRptBoardDataItemMapper.selectList(wrapper);

        // 预警类的数据，改成查预警记录表
        List<Map<String, Object>> alertTypeAndValidNumberMapList = buRptBoardDataItemMapper.countAlertTypeAndValidNumber();
        Map<Integer, Integer> alertTypeNumberMap = new HashMap<>(8);
        if (CollectionUtils.isNotEmpty(alertTypeAndValidNumberMapList)) {
            for (Map<String, Object> alertTypeAndValidNumberMap : alertTypeAndValidNumberMapList) {
                // oracle、mysql返回的数据类型不一样
                BigDecimal alertType = DataTypeCastUtil.transNumber(alertTypeAndValidNumberMap.get("alertType"));
                BigDecimal validNumber = DataTypeCastUtil.transNumber(alertTypeAndValidNumberMap.get("validNumber"));
                alertTypeNumberMap.put(alertType.intValue(), validNumber.intValue());
            }
        }

        for (BuRptBoardDataItem dataItem : itemList) {
            String itemCode = dataItem.getItemCode();
            String value = getAlertItemValue(itemCode, alertTypeNumberMap);
            dataItem.setItemValue(value);
            buRptBoardDataItemMapper.update(dataItem, Wrappers.<BuRptBoardDataItem>lambdaUpdate().eq(BuRptBoardDataItem::getItemCode, itemCode));
        }

        return true;
    }


    private String getAlertItemValue(String itemCode, Map<Integer, Integer> alertTypeNumberMap) {
        if (StringUtils.isBlank(itemCode)) {
            return "0";
        }

        String value = "0";

        switch (itemCode) {
            case HomepageItemCode.ALERT_MATERIAL_STOCK:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(1, 0));
                break;
            case HomepageItemCode.ALERT_TOOL_CHECK:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(3, 0));
                break;
            case HomepageItemCode.ALERT_MATERIAL_EXPIRE:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(2, 0));
                break;
            case HomepageItemCode.ALERT_OUTSOURCE_ASSET_EXPIRE:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(4, 0));
                break;
            case HomepageItemCode.ALERT_MEASURE:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(5, 0));
                break;
            case HomepageItemCode.ALERT_DELAY_OUTSOURCE_OUTINDETAIL:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(6, 0));
                break;
            case HomepageItemCode.ALERT_DELAY_ORDER:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(7, 0));
                break;
            case HomepageItemCode.ALERT_UN_HANDLE_FAULT:
                value = String.valueOf(alertTypeNumberMap.getOrDefault(8, 0));
                break;
            default:
                break;
        }

        return value;
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

}
