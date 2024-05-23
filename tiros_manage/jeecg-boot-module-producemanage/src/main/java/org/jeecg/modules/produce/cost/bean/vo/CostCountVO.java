package org.jeecg.modules.produce.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.vo.chart.PieChartVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 成本监控--消耗明细--金额统计
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/26
 */
@Data
@Accessors(chain = true)
public class CostCountVO {

    @ApiModelProperty(value = "物料消耗总金额")
    private BigDecimal total;

    @ApiModelProperty(value = "必换件消耗金额")
    private BigDecimal must;

    @ApiModelProperty(value = "偶换件消耗金额")
    private BigDecimal random;

    @ApiModelProperty(value = "耗材消耗金额")
    private BigDecimal consume;

}
