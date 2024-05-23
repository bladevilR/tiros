package org.jeecg.common.workflow.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * <p>
 * 流程方案启动参数对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Data
@Accessors(chain = true)
public class StartVO {

    @ApiModelProperty(value = "关联的业务主键", required = true)
    @NotBlank
    private String businessKey;

    @ApiModelProperty(value = "流程方案编码", required = true)
    @NotBlank
    private String solutionCode;

    @ApiModelProperty(value = "启动的流程标题，可为空，为空则为方案中配置标题，如果流程方案配置为空，则为流程名称")
    private String title;

    @ApiModelProperty(value = "流程变量")
    private Map<String, Object> variables;

}
