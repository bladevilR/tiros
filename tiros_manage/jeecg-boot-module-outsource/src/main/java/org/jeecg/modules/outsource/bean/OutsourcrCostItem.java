package org.jeecg.modules.outsource.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "委外成本项")
public class OutsourcrCostItem {

    @ApiModelProperty("类型：1总成本，2单列成本，3单节成本")
    private  Integer type;

    @ApiModelProperty("成本价格")
    private BigDecimal  price;
}
