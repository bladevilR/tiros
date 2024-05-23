package org.jeecg.modules.produce.summary.bean.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 各用途类型成本
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/22
 */
@Data
@Accessors(chain = true)
public class UseCategoryCost {

    @ApiModelProperty(value = "总计")
    private BigDecimal totalCost;

    @ApiModelProperty(value = "备品备件-必换件")
    private BigDecimal mustCost;

    @ApiModelProperty(value = "备品备件-故障件（偶换件）")
    private BigDecimal randomCost;

    @ApiModelProperty(value = "备品备件-合计")
    private BigDecimal mustRandomCost;

    @ApiModelProperty(value = "耗材")
    private BigDecimal consumeCost;

    @ApiModelProperty(value = "其他（未归类）")
    private BigDecimal otherCost;

}
