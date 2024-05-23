package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class BuWorkOrderTaskQueryVO implements Serializable {

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "工单任务状态  字典bu_order_task_status")
    private Integer status;

    @ApiModelProperty(value = "列计划id  字典bu_repair_plan,plan_name,id")
    private String planId;

    @ApiModelProperty(value = "工单id  字典bu_work_order,order_name,id")
    private String workOrderId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "工单状态  字典dicCode=bu_order_status")
    private List<Integer> orderStatusList;

    @ApiModelProperty(value = "工单作业状态  字典dictCode=bu_work_status")
    private List<Integer> orderWorkStatusList;

    @ApiModelProperty(value = "列计划状态  字典dictCode=bu_repair_plan_status")
    private List<Integer> planStatusList;

    @ApiModelProperty(value = "列计划进度状态  字典dictCode=bu_progress_status")
    private List<Integer> planProgressStatusList;


}
