package org.jeecg.modules.dispatch.workorder.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class JobRequirement implements Serializable {

    @ApiModelProperty(value = "安全提示")
    private String safeNotice;

    @ApiModelProperty(value = "技术要求")
    private String requirement;

    @ApiModelProperty(value = "安全预想id")
    private String safetyExpectationId;

    @ApiModelProperty(value = "安全预想")
    private String safetyExpectation;

}
