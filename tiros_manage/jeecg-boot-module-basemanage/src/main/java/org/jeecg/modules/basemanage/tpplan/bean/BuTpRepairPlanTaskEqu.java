package org.jeecg.modules.basemanage.tpplan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 计划模板任务目标设备
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_tp_repair_plan_task_equ")
@ApiModel(value = "BuTpRepairPlanTaskEqu对象", description = "计划模板任务目标设备")
public class BuTpRepairPlanTaskEqu extends Model<BuTpRepairPlanTaskEqu> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "计划模板id")
    private String planId;

    @ApiModelProperty(value = "计划模板任务id")
    private String planTaskId;

    @ApiModelProperty(value = "系统id")
    private String systemId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structDetailId;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;


    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "工班id")
    @TableField(exist = false)
    private String workGroupId;

    @ApiModelProperty(value = "工班名称")
    @TableField(exist = false)
    private String workGroupName;

    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "系统编码")
    @TableField(exist = false)
    private String systemCode;

    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String assetTypeCode;

    @ApiModelProperty(value = "车辆结构明细名称")
    @TableField(exist = false)
    private String structDetailName;

    @ApiModelProperty(value = "车辆结构明细编码")
    @TableField(exist = false)
    private String structDetailCode;

    @ApiModelProperty(value = "车辆结构明细结构类型  字典dictCode=bu_train_asset_type")
    @Dict(dicCode = "bu_train_asset_type")
    @TableField(exist = false)
    private Integer structDetailStructType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
