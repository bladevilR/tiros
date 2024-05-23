package org.jeecg.common.tiros.constant;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 架大修业务通用常量
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/11
 */
public interface TirosConstant {

    /**
     * 工器具送检提前时间
     * 7*24*60*60*1000毫秒 = 7天
     */
    long TOOLS_CHECK_EARLIER_WARNING_TIME = 15 * 24 * 60 * 60 * 1000;

    /**
     * 委外部件质保期提前时间
     * 30*24*60*60*1000毫秒 = 30天
     */
    long OUTSOURCE_ASSET_QUALITY_EARLIER_WARNING_TIME = 2592000000L;

    /**
     * 委外任务逾期预警提前时间
     * 7*24*60*60*1000毫秒 = 7天
     */
    long OUTSOURCE_OUTIN_DETAIL_EARLIER_WARNING_TIME = 604800000L;

    /**
     * 测量预警模板替换Pattern，匹配${}
     */
    Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{\\w+\\}");

    /**
     * 班组库存：需计算占用量的工单状态
     */
    List<Integer> GROUP_STOCK_NEED_COUNT_USED_ORDER_STATUS = Arrays.asList(2, 3, 6, 7, 8);

    /**
     * 工器具送检提前时间系统配置code tool_ahead_day
     * 7*24*60*60*1000毫秒 = 7天
     */
    String TOOLS_CHECK_EARLIER_WARNING_CODE = "tool_ahead_day";

    /**
     * 物资过期预警提前时间系统配置code material_ahead_day
     * 7*24*60*60*1000毫秒 = 7天
     */
    String MATERIAL_EXPIRE_WARNING_CODE = "material_ahead_day";

    String SYS_CONFIG_UPDATE_WAREHOUSE_CACHE = "update_warehouse_cache";

}
