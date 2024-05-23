package org.jeecg.modules.produce.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 比较分析-车辆架大修成本趋势(近6年物资消耗)
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/26
 */
@Data
@Accessors(chain = true)
public class CostCompareYearTrendVO {

    @ApiModelProperty(value = "字段集合")
    private List<String> fieldList;

    @ApiModelProperty(value = "数据")
    private List<Map<String, Object>> dataList;

}
