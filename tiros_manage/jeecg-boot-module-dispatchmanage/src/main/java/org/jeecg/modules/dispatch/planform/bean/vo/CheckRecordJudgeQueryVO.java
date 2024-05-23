package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 检查表单质量评定查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Data
@Accessors(chain = true)
public class CheckRecordJudgeQueryVO {

    @ApiModelProperty(value = "检查表实例id", required = true)
    @NotBlank
    private String checkInstId;

    @ApiModelProperty(value = "评定人员id")
    private String jdUserId;

}
