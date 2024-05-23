package org.jeecg.modules.third.jdx.bean;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 任务人员安排
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairTaskStaffArrange对象", description = "任务人员安排")
@TableName("bu_repair_task_staff_arrange")
public class BuRepairTaskStaffArrange extends Model<BuRepairTaskStaffArrange> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单作业工位id")
    private String ordTskWkStationId;

    @ApiModelProperty(value = "人员id 作业填报新增时必填")
    private String userId;

    @ApiModelProperty(value = "作业工时")
    private BigDecimal workTime;


    @ApiModelProperty(value = "工单号")
    @TableField(exist = false)
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    @TableField(exist = false)
    private String orderName;

    @ApiModelProperty(value = "工单任务id 作业填报新增时必填")
    @TableField(exist = false)
    private String orderTaskId;

    @ApiModelProperty(value = "工单任务名称")
    @TableField(exist = false)
    private String orderTaskName;

    @ApiModelProperty(value = "工位id 作业填报新增时必填")
    @TableField(exist = false)
    private String workstationId;

    @ApiModelProperty(value = "工位号")
    @TableField(exist = false)
    private String workstationNo;

    @ApiModelProperty(value = "工位名称")
    @TableField(exist = false)
    private String workstationName;

    @ApiModelProperty(value = "人员名称")
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty(value = "计划工时 来自工单工位中work_time")
    @TableField(exist = false)
    private Integer planWorkTime;

    @ApiModelProperty(value = "工号")
    @TableField(exist = false)
    private String workNo;

    @ApiModelProperty(value = "车辆段id")
    @TableField(exist = false)
    private String depotId;

    @ApiModelProperty(value = "车间id")
    @TableField(exist = false)
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    @TableField(exist = false)
    private String lineId;

    @ApiModelProperty(value = "班组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "列计划id")
    @TableField(exist = false)
    private String planId;

    @ApiModelProperty(value = "工单任务开始时间")
    @TableField(exist = false)
    private Date taskStart;

    @ApiModelProperty(value = "工单任务结束时间")
    @TableField(exist = false)
    private Date taskFinish;

    @ApiModelProperty(value = " 任务类型 字典dictCode=bu_task_type")
    @TableField(exist = false)
    private Integer taskType;

    @ApiModelProperty(value = "所属财务项目")
    @TableField(exist = false)
    private String fdProject;

    @ApiModelProperty(value = "所属财务项目编码")
    @TableField(exist = false)
    private String fdProjectCode;

    @ApiModelProperty(value = "所属财务项目名称")
    @TableField(exist = false)
    private String fdProjectName;

    @ApiModelProperty(value = "所属财务任务")
    @TableField(exist = false)
    private String fdTask;

    @ApiModelProperty(value = "所属财务任务编码")
    @TableField(exist = false)
    private String fdTaskCode;

    @ApiModelProperty(value = "所属财务任务名称")
    @TableField(exist = false)
    private String fdTaskName;

    @ApiModelProperty(value = "所属财务开支编码")
    @TableField(exist = false)
    private String fdCostType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
