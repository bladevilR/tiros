package org.jeecg.modules.board.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 物料消耗统计--修程
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
public class BuRptBoardTrainMaterialProgramVO {

//    @ApiModelProperty(value = "修程id")
//    private String programId;

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "修程名称")
    private String programName;

    @ApiModelProperty(value = "总成本")
    private BigDecimal totalCost;

    @ApiModelProperty(value = "必换件成本")
    private BigDecimal mustCost;

    @ApiModelProperty(value = "偶换件成本")
    private BigDecimal randomCost;

    @ApiModelProperty(value = "耗材成本")
    private BigDecimal consumeCost;

    @ApiModelProperty(value = "自主修成本")
    private BigDecimal selfRepairCost;

    @ApiModelProperty(value = "委外修成本")
    private BigDecimal outsourceRepairCost;

    @ApiModelProperty(value = "单列车平均总成本")
    private BigDecimal singleTrainAverageCost;

}
