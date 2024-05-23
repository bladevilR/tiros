package org.jeecg.modules.basemanage.tpplan.bean;

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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 所需物料
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanMaterial对象", description = "所需物料")
@TableName("bu_tp_repair_plan_material")
public class BuTpRepairPlanMaterial extends Model<BuTpRepairPlanMaterial> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "物料类型id", required = true)
    private String materialTypeId;

    @ApiModelProperty(value = "需要数量", required = true)
    private Double amount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "用途类型  字典dictCode=bu_material_type")
    @Dict(dicCode = "bu_material_type")
    private Integer useCategory;


    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "工班id")
    @TableField(exist = false)
    private String workGroupId;

    @ApiModelProperty(value = "工班名称")
    @TableField(exist = false)
    private String workGroupName;

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
        return this.id;
    }

}
