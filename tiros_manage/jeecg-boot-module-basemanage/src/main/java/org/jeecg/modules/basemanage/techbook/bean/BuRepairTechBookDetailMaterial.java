package org.jeecg.modules.basemanage.techbook.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 作业指导书(工艺)明细步骤物资
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTechBookDetailMaterial对象", description = "作业指导书(工艺)明细步骤物资")
@TableName("bu_repair_tech_book_material ")
public class BuRepairTechBookDetailMaterial extends Model<BuRepairTechBookDetailMaterial> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "指导书明细id")
    private String bookDetailId;

    @ApiModelProperty(value = "物料类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "需要数量")
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

    @ApiModelProperty(value = "物料分类  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    @TableField(exist = false)
    private Integer category1;

    @ApiModelProperty(value = "物资类型--属性 字典dictCode=bu_material_attr")
    @Dict(dicCode = "bu_material_attr")
    @TableField(exist = false)
    private String category2;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
