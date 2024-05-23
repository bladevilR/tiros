package org.jeecg.modules.outsource.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "委外成本部件价格")
public class OutSourceCostPartPrice {

    @ApiModelProperty("部件名称")
    private String partName;

    @ApiModelProperty("部件价格")
    private BigDecimal price;

}
