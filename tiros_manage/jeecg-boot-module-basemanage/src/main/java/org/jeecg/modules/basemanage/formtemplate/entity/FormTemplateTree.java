package org.jeecg.modules.basemanage.formtemplate.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class FormTemplateTree implements Serializable {
    @ApiModelProperty(value = "表单类型：1 测量表单  2 记录表单  3 其他表单")
    @Dict(dicCode = "bu_form_type")
    private Integer type;
    private List<Workstation> children;
}
