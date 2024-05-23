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

import java.io.Serializable;

/**
 * <p>
 * 车辆的停放计划，子任务没有设置的话，就继承父任务的设置，如果有设置则覆盖父任务的设置
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanTrainPark对象", description = "车辆的停放计划，子任务没有设置的话，就继承父任务的设置，如果有设置则覆盖父任务的设置")
@TableName("bu_tp_repair_plan_train_park")
public class BuTpRepairPlanTrainPark extends Model<BuTpRepairPlanTrainPark> {

    public static BuTpRepairPlanTrainPark DO = new BuTpRepairPlanTrainPark();
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "轨道id")
    private String trackId;

    @ApiModelProperty(value = "轨道名称")
    @TableField(exist = false)
    private String trackName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
