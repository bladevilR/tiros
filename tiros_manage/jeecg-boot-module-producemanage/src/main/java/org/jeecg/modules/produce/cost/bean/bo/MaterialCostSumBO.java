package org.jeecg.modules.produce.cost.bean.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.math.BigDecimal;

/**
 * <p>
 * 物料消耗总和bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/22
 */
@Data
@Accessors(chain = true)
public class MaterialCostSumBO {

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "物资类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "用途类型 字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;

    @ApiModelProperty(value = "消耗金额之和")
    private BigDecimal totalCost;

}
