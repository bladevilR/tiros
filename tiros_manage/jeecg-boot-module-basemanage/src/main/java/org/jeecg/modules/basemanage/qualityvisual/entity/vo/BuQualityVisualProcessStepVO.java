package org.jeecg.modules.basemanage.qualityvisual.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "质量可视化工序步骤", description = "列计划下按工单任务聚合的工序进度和质量结果")
public class BuQualityVisualProcessStepVO implements Serializable {

    @ApiModelProperty(value = "工单ID")
    private String orderId;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "工单任务ID")
    private String taskId;

    @ApiModelProperty(value = "工单任务序号")
    private Integer taskNo;

    @ApiModelProperty(value = "工序/工步名称")
    private String taskName;

    @ApiModelProperty(value = "关联检查点总数")
    private Integer totalPoints;

    @ApiModelProperty(value = "已检查点数")
    private Integer checkedPoints;

    @ApiModelProperty(value = "完成度百分比")
    private Integer progressPercent;

    @ApiModelProperty(value = "质量问题数（故障）")
    private Integer qualityIssueCount;

    @ApiModelProperty(value = "未关闭开口项数")
    private Integer openItemCount;

    @ApiModelProperty(value = "状态颜色：green/yellow/red")
    private String statusColor;

    @ApiModelProperty(value = "质量等级：A/B/C/D")
    private String qualityLevel;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始时间")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划结束时间")
    private Date finishTime;
}

