package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yyg
 */
@Data
public class FromExcelDataVO {
    @ApiModelProperty(value = "实例id",required = true)
    @NotBlank
    private String formInstId;
    @ApiModelProperty(value = "结果",required = true)
    @NotBlank
    private String xlsJson;
    @ApiModelProperty(hidden = true)
    private String createdBy;




}
