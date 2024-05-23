package org.jeecg.modules.produce.plan.bean;

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


    @ApiModelProperty(value = "编码")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String unit;

    @ApiModelProperty(value = "单价")
    @TableField(exist = false)
    private BigDecimal price;

    @ApiModelProperty(value = "总价")
    @TableField(exist = false)
    private BigDecimal sumPrice;

    @ApiModelProperty(value = "位置id")
    @TableField(exist = false)
    private String placeId;

    @ApiModelProperty(value = "位置编码")
    @TableField(exist = false)
    private String placeCode;

    @ApiModelProperty(value = "位置描述")
    @TableField(exist = false)
    private String placeDesc;

    @ApiModelProperty(value = "结构类型  字典dictCode=bu_train_asset_type")
    @TableField(exist = false)
    @Dict(dicCode = "bu_train_asset_type")
    private Integer structType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
