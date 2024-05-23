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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 检修属性
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_repair_attribute")
@ApiModel(value = "BuRepairAttribute对象", description = "检修属性")
public class BuRepairAttribute extends Model<BuRepairAttribute> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类型;1工作类型、2大类、3检修等级、4检修周期")
    @TableField("attribute_type")
    @Dict(dicCode = "repair_attribute_type")
    @NotNull
    private Integer attributeType;

    @ApiModelProperty(value = "编码")
    @TableField("attribute_code")
    @NotBlank
    private String attributeCode;

    @ApiModelProperty(value = "名称")
    @TableField("attribute_name")
    @NotBlank
    private String attributeName;

    @ApiModelProperty(value = "上级id")
    @TableField("parent_id")
    private String parentId;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

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



    @ApiModelProperty(value = "上级类型")
    @TableField(exist = false)
    @Dict(dicCode = "repair_attribute_type")
    private Integer parentType;

    @ApiModelProperty(value = "上级名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "关联的修程类型名称，多个逗号分割")
    @TableField(exist = false)
    private String programNames;

    @ApiModelProperty(value = "检修属性修程关联列表")
    @TableField(exist = false)
    private List<BuRepairAttrProgRel> attrProgRelList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
