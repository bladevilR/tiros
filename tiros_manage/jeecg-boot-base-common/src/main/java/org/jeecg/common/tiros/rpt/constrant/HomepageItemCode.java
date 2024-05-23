package org.jeecg.common.tiros.rpt.constrant;

/**
 * <p>
 * 首页数据去数据item_code
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-25
 */
public interface HomepageItemCode {

    /**
     * 当前架修数
     */
    String DATA_CURRENT_PLAN = "dqjxs";
    /**
     * 当前工单
     */
    String DATA_CURRENT_ORDER = "dqgd";
    /**
     * 作业任务
     */
    String DATA_CURRENT_ORDER_TASK = "zyrw";
    /**
     * 任务完成比
     */
    String DATA_TASK_FINISH_PERCENT = "rwwcb";
    /**
     * 消耗物料成本
     */
    String DATA_MATERIAL_COST = "xhwlcb";
    /**
     * 发现故障数
     */
    String DATA_FOUND_FAULT = "fxgzs";

    /**
     * 物料库存预警
     */
    String ALERT_MATERIAL_STOCK = "wlkcyj";
    /**
     * 器具送检预警
     */
    String ALERT_TOOL_CHECK = "qjsjyj";
    /**
     * 物资有效期预警
     */
    String ALERT_MATERIAL_EXPIRE = "wzyxyj";
    /**
     * 部件质保期预警
     */
    String ALERT_OUTSOURCE_ASSET_EXPIRE = "bjzbqyj";
    /**
     * 测量数据预警
     */
    String ALERT_MEASURE = "clsjyj";
    /**
     * 委外逾期预警
     */
    String ALERT_DELAY_OUTSOURCE_OUTINDETAIL = "wwyqyj";
    /**
     * 逾期工单预警
     */
    String ALERT_DELAY_ORDER = "yqgdyj";
    /**
     * 未处理故障预警
     */
    String ALERT_UN_HANDLE_FAULT = "wclgzyj";

}