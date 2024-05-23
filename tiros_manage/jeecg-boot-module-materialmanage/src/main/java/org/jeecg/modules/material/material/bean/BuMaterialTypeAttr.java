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

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 物资属性
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "物资属性对象", description = "物资属性")
@TableName("bu_material_type_attr")
public class BuMaterialTypeAttr extends Model<BuMaterialTypeAttr> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "物资类型id", required = true)
    @NotBlank
    private String materialTypeId;

    @ApiModelProperty(value = "所属系统id")
    private Integer systemId;

    @ApiModelProperty(value = "有效期提醒提前量 数值如25表示25%")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer lead;

    @ApiModelProperty(value = "安全库存量 默认3列车的架修需求量, -1表示没有")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double theshold;

    @ApiModelProperty(value = "一列需求")
    private Double oneRequire;

    @ApiModelProperty(value = "三列需求")
    private Double twoRequire;

    @ApiModelProperty(value = "十五列需求")
    private Double threeRequire;


    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialTypeCode;

    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "物料属性 1紧固件 2备品备件 3车体 4车上电气 5辅助 6牵引 7制定 8转向架   字典bu_material_attr")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private String materialAttr;

    @ApiModelProperty(value = "所属系统名称")
    @TableField(exist = false)
    private String systemName;

}
