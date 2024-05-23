package org.jeecg.common.workflow.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * <p>
 * 任务完成参数对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Data
@Accessors(chain = true)
public class TaskCompleteVO {

    @ApiModelProperty(value = "activityId")
    private String activityId;

    @ApiModelProperty(value = "activityName")
    private String activityName;

    @ApiModelProperty(value = "完成任务时的意见")
    private String message;

    @ApiModelProperty(value = "任务id", required = true)
    @NotBlank
    private String taskId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "流程变量")
    private Map<String, Object> vars;

}
