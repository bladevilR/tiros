package org.jeecg.modules.board.group.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 班组缺料
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@Accessors(chain = true)
public class BuGroupMaterialLack {

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "物资类型编码")
    private String materialTypeCode;

    @ApiModelProperty(value = "物资类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "车间库存")
    private BigDecimal workshopStockAmount;

    @ApiModelProperty(value = "班组库存")
    private BigDecimal groupStockAmount;

    @ApiModelProperty(value = "需求数量")
    private BigDecimal needAmount;

    @ApiModelProperty(value = "差量")
    private BigDecimal diffAmount;

}
