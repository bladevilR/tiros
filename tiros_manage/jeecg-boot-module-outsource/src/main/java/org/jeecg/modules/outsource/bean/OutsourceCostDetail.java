package org.jeecg.modules.outsource.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "委外成本详情")
public class OutsourceCostDetail {

    @ApiModelProperty("合同id")
    private  String id;

    @ApiModelProperty("合同编号")
    private  String contractNo;

    @ApiModelProperty("合同名称")
    private  String contractName;

    @ApiModelProperty("项目名称")
    private  String itemName;

    @ApiModelProperty("合同总价格")
    private  BigDecimal contractAmount;

    @ApiModelProperty("单列价格")
    private BigDecimal singlePrice;

    @ApiModelProperty("单节价格")
    private BigDecimal sectionPrice;

    @ApiModelProperty("单个部件价格")
    private BigDecimal partPrice;

}
