package org.jeecg.modules.dispatch.serialplan.bean;

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
 * <p>
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairPlanTaskPre对象", description = "")
@TableName("bu_repair_plan_task_pre")
public class BuRepairPlanTaskPre extends Model<BuRepairPlanTaskPre> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务编号")
    private String taskNo;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "类型 FS (完成>开始) SS (开始>开始) FF (完成>完成) SF(开始完成)")
    private String preType;

    @ApiModelProperty(value = "单位为天")
    private Integer delay;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
