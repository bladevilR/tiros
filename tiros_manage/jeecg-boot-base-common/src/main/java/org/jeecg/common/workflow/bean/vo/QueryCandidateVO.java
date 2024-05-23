package org.jeecg.common.workflow.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 获取当前用户指定流程的可办理任务参数对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Data
@Accessors(chain = true)
public class QueryCandidateVO {

    @ApiModelProperty(value = "业务主键", required = true)
    @NotBlank
    private String businessKey;

    @ApiModelProperty(value = "业务与流程映射编码", required = true)
    @NotBlank
    private String solutionCode;

    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

}
