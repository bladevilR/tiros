package org.jeecg.modules.basemanage.regu.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 规程额定工器具，包括：工具、器具、工装
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("bu_repair_regu_tools")
public class BuRepairReguTools extends Model<BuRepairReguTools> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "规程明细id")
    private String reguDetailId;

    @ApiModelProperty(value = "工具类型id 物资类型id")
    private String toolTypeId;

    @ApiModelProperty(value = "数量")
    private Double amount;


    @ApiModelProperty("物资类型--编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty("物资类型--名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty("物资类型--规格描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty("物资类型--单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "物资类型--单价：单位元")
    @TableField(exist = false)
    private BigDecimal price;

    @ApiModelProperty(value = "物资类型--种类 字典dictCode=bu_material_kind")
    @Dict(dicCode = "bu_material_kind")
    @TableField(exist = false)
    private Integer kind;

    @ApiModelProperty(value = "工器具分类  字典dictCode=bu_tools_type")
    @Dict(dicCode = "bu_tools_type")
    @TableField(exist = false)
    private Integer category1;

    @ApiModelProperty(value = "物资类型--属性 字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    @TableField(exist = false)
    private String category2;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
