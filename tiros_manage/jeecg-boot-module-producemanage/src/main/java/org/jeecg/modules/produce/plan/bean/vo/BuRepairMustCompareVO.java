package org.jeecg.modules.produce.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 列计划必换件对比vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/3
 */
@Data
@Accessors(chain = true)
public class BuRepairMustCompareVO {

    @ApiModelProperty(value = "物料id")
    private String materialTypeId;

    @ApiModelProperty(value = "物料编码")
    private String materialTypeCode;

    @ApiModelProperty(value = "物料名称")
    private String materialTypeName;

    @ApiModelProperty(value = "物料规格")
    private String materialTypeSpec;

    @ApiModelProperty(value = "需求量")
    private BigDecimal needAmount;

    @ApiModelProperty(value = "消耗量")
    private BigDecimal consumeAmount;

}
