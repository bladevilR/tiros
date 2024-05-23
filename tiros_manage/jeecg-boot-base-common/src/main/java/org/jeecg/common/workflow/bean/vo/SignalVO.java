package org.jeecg.common.workflow.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 信号vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-07
 */
@Data
@Accessors(chain = true)
public class SignalVO {

    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    @ApiModelProperty(value = "activityId")
    private String activityId;

}
