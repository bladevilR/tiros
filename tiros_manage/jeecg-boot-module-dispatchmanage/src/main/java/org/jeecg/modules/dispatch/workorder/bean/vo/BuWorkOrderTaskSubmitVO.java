package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务提交vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-09
 */
@Data
@Accessors(chain = true)
public class BuWorkOrderTaskSubmitVO {

    @ApiModelProperty(value = "工单任务id")
    private String taskId;

    @ApiModelProperty(value = "工班长填报的工时")
    private Integer reportTime;

}
