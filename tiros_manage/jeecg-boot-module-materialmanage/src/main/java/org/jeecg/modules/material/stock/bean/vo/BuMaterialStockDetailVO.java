package org.jeecg.modules.material.stock.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 物资库存明细信息vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-25
 */
@Data
@Accessors(chain = true)
public class BuMaterialStockDetailVO {

    @ApiModelProperty("物资类型编码")
    private String materialTypeId;

    @ApiModelProperty("物资类型编码")
    private String code;

    @ApiModelProperty("物资类型名称")
    private String name;

    @ApiModelProperty("规格描述")
    private String spec;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty(value = "单价：单位元")
    private BigDecimal price;

    @ApiModelProperty(value = "状态", notes = "0无效 1 有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

    @ApiModelProperty(value = "种类 字典dictCode=bu_material_kind", notes = "1物料 2工器具")
    @Dict(dicCode = "bu_material_kind")
    private Integer kind;

    @ApiModelProperty(value = "物料分类id")
    private String category;

    @ApiModelProperty(value = "物料分类  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer category1;

    @ApiModelProperty(value = "物料属性  字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    private String category2;

    @ApiModelProperty(value = "所属分类3 备用分类，来自物资分类表，一般情况下不需要使用")
    private String category3;

    @ApiModelProperty(value = "库存警戒 为-1表示没有设置，不需要预警，默认为-1，这里暂时不用，用物资属性表中的")
    private BigDecimal theshold;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "当前库存数量")
    private BigDecimal currentAmount;

    @ApiModelProperty(value = "入库明细")
    private List<BuMaterialEntryDetail> entryDetailList;


}
