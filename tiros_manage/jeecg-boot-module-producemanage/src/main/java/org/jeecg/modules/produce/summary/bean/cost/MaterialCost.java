package org.jeecg.modules.produce.summary.bean.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 各物料成本
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Data
@Accessors(chain = true)
public class MaterialCost {

    @ApiModelProperty(value = "物料名称")
    private String materialTypeName;

    @ApiModelProperty(value = "消耗金额")
    private BigDecimal cost;

    @ApiModelProperty(value = "消耗比例")
    private Double percent;

}
