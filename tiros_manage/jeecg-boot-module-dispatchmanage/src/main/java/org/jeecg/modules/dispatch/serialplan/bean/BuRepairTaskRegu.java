package org.jeecg.modules.dispatch.serialplan.bean;

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

/**
 * <p>
 * 关联规程
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTaskRegu对象", description = "关联规程")
@TableName("bu_repair_task_regu")
public class BuRepairTaskRegu extends Model<BuRepairTaskRegu> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("规程id")
    private String reguId;

    @ApiModelProperty("规程详情id")
    private String reguDetailId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "规程明细--序号")
    @TableField(exist = false)
    private String no;

    @ApiModelProperty(value = "规程明细--名称")
    @TableField(exist = false)
    private String title;

    @ApiModelProperty(value = "规程明细--类型")
    @Dict(dicCode = "bu_regu_type")
    @TableField(exist = false)
    private Integer type;

    @ApiModelProperty(value = "规程明细--安全提示")
    @TableField(exist = false)
    private String safeNotice;

//    @ApiModelProperty(value = "规程明细上级id")
//    @TableField(exist = false)
//    private String parentId;

    @ApiModelProperty(value = "规程明细--是否委外  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField(exist = false)
    private Integer outsource;

    @ApiModelProperty(value = "规程明细--是否重要工序  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField(exist = false)
    private Integer important;

    @ApiModelProperty(value = "规程明细--维保手段  字典dictCode=bu_state")
    @Dict(dicCode = "bu_regu_method")
    @TableField(exist = false)
    private String method;

    @ApiModelProperty(value = "规程明细--质量等级  字典dictCode=bu_regu_quality_level")
    @Dict(dicCode = "bu_regu_quality_level")
    @TableField(exist = false)
    private String qualityLevel;

    @ApiModelProperty(value = "规程明细--设备类型id")
    @TableField(exist = false)
    private String assetTypeId;

    @ApiModelProperty(value = "规程明细--所需工时")
    @TableField(exist = false)
    private Float workTime;

    @ApiModelProperty(value = "规程明细--技术要求")
    @TableField(exist = false)
    private String requirement;

    @ApiModelProperty(value = "规程明细--是否是必换件  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField(exist = false)
    private Integer mustReplace;

    @ApiModelProperty(value = "规程明细--是否需要测量  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    @TableField(exist = false)
    private Integer measure;

    @ApiModelProperty(value = "规程明细--设备名称")
    @TableField(exist = false)
    private String assetTypeName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
