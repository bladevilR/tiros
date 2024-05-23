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

/**
 * <p>
 * 必换件清单，根据任何对应的所有规程的设备类型是否为必换件自动导入，导入后可以手动调整
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanMustReplace对象", description = "必换件清单，根据任何对应的所有规程的设备类型是否为必换件自动导入，导入后可以手动调整")
@TableName("bu_tp_repair_plan_must_replace")
public class BuTpRepairPlanMustReplace extends Model<BuTpRepairPlanMustReplace> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "必换件id", required = true)
    private String mustReplaceId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "物资类型id")
    private String materialTypeId;

    @ApiModelProperty(value = "需要数量")
    private Double amount;


    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;

    @ApiModelProperty(value = "物资名称", hidden = true)
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "物资编码", hidden = true)
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "物资描述", hidden = true)
    @TableField(exist = false)
    private String spec;

    @ApiModelProperty(value = "物资单位", hidden = true)
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "物资属性,字典bu_material_attr", hidden = true)
    @TableField(exist = false)
    @Dict(dicCode = "bu_material_attr")
    private String materialTypeCategory;

    @ApiModelProperty(value = "线路名称", hidden = true)
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程名称", hidden = true)
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "系统", hidden = true)
    @TableField(exist = false)
    private String sysName;

    @ApiModelProperty(value = "班组id", hidden = true)
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名称", hidden = true)
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "工位名称", hidden = true)
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "安装部位", hidden = true)
    @TableField(exist = false)
    private String locationName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
