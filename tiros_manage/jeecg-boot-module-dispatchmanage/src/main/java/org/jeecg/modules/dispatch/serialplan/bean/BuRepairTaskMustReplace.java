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
 * 任务必换件，根据任何对应的所有规程的设备类型是否为必换件自动导入，导入后可以手动调整
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTaskMustReplace对象", description = "任务必换件，根据任何对应的所有规程的设备类型是否为必换件自动导入，导入后可以手动调整")
@TableName("bu_repair_task_must_replace")
public class BuRepairTaskMustReplace extends Model<BuRepairTaskMustReplace> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "设备类型id", required = true)
    private String assetTypeId;

    @ApiModelProperty(value = "是否更换  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer changed;

    @ApiModelProperty(value = "更换记录id")
    private String changeId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "必换件id", required = true)
    private String mustReplaceId;

    @ApiModelProperty(value = "列计划id", required = true)
    private String planId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "需要数量")
    private Double amount;


    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "物资描述")
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty(value = "物资单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "物资属性,字典bu_material_attr")
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private String materialTypeCategory;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "系统id")
    @TableField(exist = false)
    private String sysId;

    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String sysName;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "工位id")
    @TableField(exist = false)
    private String workstationId;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "安装部位")
    @TableField(exist = false)
    private String locationName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
