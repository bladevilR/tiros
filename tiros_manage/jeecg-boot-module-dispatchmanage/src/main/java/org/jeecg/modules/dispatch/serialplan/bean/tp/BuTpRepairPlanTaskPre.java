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

/**
 * @author yyg bu_tp_repair_plan_task_pre
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuTpRepairPlanPlace对象", description = "作业位置")
@TableName("bu_tp_repair_plan_task_pre")
public class BuTpRepairPlanTaskPre extends Model<BuTpRepairPlanTaskPre> {

    public static BuTpRepairPlanTaskPre DO = new BuTpRepairPlanTaskPre();
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "任务编号", required = true)
    private String taskNo;

    @ApiModelProperty(value = "任务名称", required = true)
    private String taskName;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "类型 FS(完成>开始) SS(开始>开始) FF(完成>完成) SF(开始完成)", required = true)
    private String preType;

    @ApiModelProperty(value = "延隔时间 单位为天")
    private Integer delay;

}
