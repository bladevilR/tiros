package org.jeecg.modules.basemanage.qualityvisual.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "质量策划项点", description = "从作业记录明细中抽取的质量检查项")
public class BuQualityPlanningItemVO implements Serializable {

    @ApiModelProperty(value = "工单ID")
    private String orderId;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "工单任务ID")
    private String taskId;

    @ApiModelProperty(value = "工序/工步名称")
    private String taskName;

    @ApiModelProperty(value = "表单实例ID")
    private String formInstId;

    @ApiModelProperty(value = "记录明细ID")
    private String recordDetailId;

    @ApiModelProperty(value = "作业内容")
    private String workContent;

    @ApiModelProperty(value = "技术要求")
    private String techRequire;

    @ApiModelProperty(value = "检查级别")
    private Integer checkLevel;

    @ApiModelProperty(value = "检查级别描述")
    private String checkLevelText;

    @ApiModelProperty(value = "是否无需填报：0否1是")
    private Integer noNeedFill;

    @ApiModelProperty(value = "当前已填报状态：0未填报1已填报")
    private Integer filled;
}

