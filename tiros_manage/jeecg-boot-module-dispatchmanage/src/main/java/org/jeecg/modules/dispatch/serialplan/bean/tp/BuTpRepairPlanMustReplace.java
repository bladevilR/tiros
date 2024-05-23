package org.jeecg.modules.dispatch.serialplan.bean.tp;

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


    @ApiModelProperty(value = "列计划id", hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private String planId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
