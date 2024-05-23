package org.jeecg.modules.outsource.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "委外部件合同价格对象")
public class OutsourceCostPart {

    @ApiModelProperty("线路id")
    private String lineId;

    @ApiModelProperty("线路名称")
    private String lineName;

    @ApiModelProperty("修程id")
    private String repairProgramId;

    @ApiModelProperty("修程名称")
    private String repairProgramName;

    @ApiModelProperty("年份")
    private  Integer  year;

   /* @ApiModelProperty("具体部件价格")
    private List<OutSourceCostPartPrice>  partPriceList;*/

    @ApiModelProperty("部件名称")
    private String partName;

    @ApiModelProperty("部件价格")
    private BigDecimal price;
}
