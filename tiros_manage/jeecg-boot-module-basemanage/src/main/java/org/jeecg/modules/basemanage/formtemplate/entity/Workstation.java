package org.jeecg.modules.basemanage.formtemplate.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class Workstation implements Serializable {
    @ApiModelProperty(value = "工位号")
    private String id;
    @ApiModelProperty(value = "工位名称")
    @NotBlank
    private String name;

}
