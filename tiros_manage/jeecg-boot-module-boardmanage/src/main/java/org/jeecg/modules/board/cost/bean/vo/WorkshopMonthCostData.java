package org.jeecg.modules.board.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 车间月份成本统计数据
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
@Data
@Accessors(chain = true)
public class WorkshopMonthCostData {

    @ApiModelProperty(value = "人工成本-人员数")
    private Integer userNum;

    @ApiModelProperty(value = "人工成本-作业工时百分比")
    private Double userTimePercent;

    @ApiModelProperty(value = "工装成本-工装数")
    private Integer toolNum;

    @ApiModelProperty(value = "工装成本-利用率百分比")
    private Double toolUsePercent;

    @ApiModelProperty(value = "委外成本-金额")
    private BigDecimal outsourceCost;

    @ApiModelProperty(value = "委外成本-部件数量")
    private Integer outsourceAssetNum;

    @ApiModelProperty(value = "物资消耗-金额")
    private BigDecimal materialCost;

    @ApiModelProperty(value = "物资消耗-数量")
    private BigDecimal materialNum;

}
