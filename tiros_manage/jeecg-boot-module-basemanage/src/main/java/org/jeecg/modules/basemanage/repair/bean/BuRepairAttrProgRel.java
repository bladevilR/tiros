package org.jeecg.modules.basemanage.repair.bean;

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
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 检修属性修程关联
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_repair_attr_prog_rel")
@ApiModel(value = "BuRepairAttrProgRel对象", description = "检修属性修程关联")
public class BuRepairAttrProgRel extends Model<BuRepairAttrProgRel> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类型;1工作类型、2大类、3检修等级、4检修周期")
    @TableField("attribute_type")
    @Dict(dicCode = "repair_attribute_type")
    private Integer attributeType;

    @ApiModelProperty(value = "检修属性id")
    @TableField("attribute_id")
    @NotBlank
    private String attributeId;

    @ApiModelProperty(value = "修程类型id")
    @TableField("program_id")
    @NotBlank
    private String programId;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;


    @ApiModelProperty(value = "检修属性编码")
    @TableField(exist = false)
    private String attributeCode;

    @ApiModelProperty(value = "检修属性名称")
    @TableField(exist = false)
    private String attributeName;

    @ApiModelProperty(value = "修程类型名称")
    @TableField(exist = false)
    private String programName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
