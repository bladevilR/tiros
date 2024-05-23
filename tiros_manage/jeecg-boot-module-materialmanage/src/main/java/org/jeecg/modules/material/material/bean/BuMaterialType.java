package org.jeecg.modules.material.material.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 物资类型
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "物资类型对象", description = "物资类型")
@TableName("bu_material_type")
public class BuMaterialType extends Model<BuMaterialType> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String category2;

    @ApiModelProperty(value = "所属分类3 备用分类，来自物资分类表，一般情况下不需要使用")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String category3;

    @ApiModelProperty(value = "库存警戒 为-1表示没有设置，不需要预警，默认为-1，这里暂时不用，用物资属性表中的")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal theshold;

    @ApiModelProperty(value = "费用科目编码")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String subject;

    @ApiModelProperty(value = "是否固定资产 0否1是")
    private Integer isAsset;

    @ApiModelProperty(value = "是否总库直发 0否1是")
    private Integer fromHead;

    @ApiModelProperty(value = "列管消耗 1消耗物资 2列管物资")
    private Integer isConsume;

    @ApiModelProperty("备注")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "库存")
    @TableField(exist = false)
    private BigDecimal amount;

    @ApiModelProperty(value = "1紧固件 2备品备件 。。。 字典bu_material_attr")
    @Dict(dicCode = "bu_ext_attr")
    @TableField(exist = false)
    private Integer extAttr;

    @ApiModelProperty(value = "有效期提醒提前量 数值如25表示25%")
    @TableField(exist = false)
    private Integer lead;

    @ApiModelProperty(value = "工器具类型")
    @TableField(exist = false)
    private Integer toolType;

    @ApiModelProperty(value = "工器具类型")
    @TableField(exist = false)
    private String model;

    @ApiModelProperty(value = "可替换物资编码，多个逗号分隔")
    @TableField(exist = false)
    private String canReplace;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
