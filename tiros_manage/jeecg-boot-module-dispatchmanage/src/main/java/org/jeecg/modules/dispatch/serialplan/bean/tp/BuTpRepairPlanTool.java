package org.jeecg.modules.dispatch.serialplan.bean.tp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 所需工器具
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanTool对象", description = "所需工器具")
@TableName("bu_tp_repair_plan_tool")
public class BuTpRepairPlanTool extends Model<BuTpRepairPlanTool> {

    public static BuTpRepairPlanTool DO = new BuTpRepairPlanTool();
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "工具类型id  物资类型表id", required = true)
    private String toolTypeId;

    @ApiModelProperty(value = "需要数量", required = true)
    private Double amount;

    @ApiModelProperty("备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
