package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 车辆累计消耗金额--系统
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-07
 */
@Data
@Accessors(chain = true)
public class BuCostSystemTotalCountVO {

    @ApiModelProperty(value = "系统")
    private String sysName;

    @ApiModelProperty(value = "备品备件-必换件")
    private BigDecimal mustCost;

    @ApiModelProperty(value = "备品备件-故障件（偶换件）")
    private BigDecimal randomCost;

    @ApiModelProperty(value = "备品备件-合计")
    private BigDecimal mustRandomCost;

    @ApiModelProperty(value = "耗材")
    private BigDecimal consumeCost;

}
