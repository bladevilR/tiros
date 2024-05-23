package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class BuRepairPlanFormInstDeleteVO {

    @ApiModelProperty(value = "表单实例id", required = true)
    @NotBlank
    private String formInstId;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type", required = true)
    @NotNull
    private Integer formType;

}
