package org.jeecg.modules.dispatch.planform.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class BuRepairPlanFormsInstQueryVO {

    @ApiModelProperty(value = "列计划id", required = true)
    @NotBlank
    private String planId;

    @ApiModelProperty(value = "表单名称或编码")
    private String searchText;

    @ApiModelProperty(value = "表单类型 字典dictCode=bu_work_form_type")
    private Integer formType;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

}
