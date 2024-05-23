package org.jeecg.modules.dispatch.serialplan.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 列计划进度-任务vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/16
 */
@Data
@Accessors(chain = true)
public class PlanProgressTaskVO {

    @ApiModelProperty(value = "列计划任务id")
    private String id;

    @ApiModelProperty(value = "序号")
    private Integer taskNo;

    @ApiModelProperty(value = "任务WBS编码")
    private String taskWbs;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;

    @ApiModelProperty(value = "工期序号")
    private Integer dayIndex;

    @ApiModelProperty(value = "作业工班id")
    private String workGroupId;

    @ApiModelProperty(value = "任务状态  字典dictCode=bu_task_status")
    @Dict(dicCode = "bu_task_status")
    private Integer status;

    @ApiModelProperty(value = "是否生成工单  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer genOrder;

    @ApiModelProperty(value = "是否已生成工单  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer hasGen;


    @ApiModelProperty(value = "工班名称")
    private String workGroupName;

    @ApiModelProperty(value = "工单id")
    private String orderId;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单类型 字典dicCode=bu_order_type")
    @Dict(dicCode = "bu_order_type")
    private Integer orderType;

    @ApiModelProperty(value = "工单状态 字典dicCode=bu_order_status")
    @Dict(dicCode = "bu_order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "作业状态 字典dicCode=bu_work_status")
    @Dict(dicCode = "bu_work_status")
    private Integer workStatus;

    @ApiModelProperty(value = "任务序号")
    private Integer orderTaskNo;

    @ApiModelProperty(value = "任务名称")
    private String orderTaskName;

    @ApiModelProperty(value = "任务状态  字典dicCode=bu_order_task_status")
    @Dict(dicCode = "bu_order_task_status")
    private Integer orderTaskStatus;

}
