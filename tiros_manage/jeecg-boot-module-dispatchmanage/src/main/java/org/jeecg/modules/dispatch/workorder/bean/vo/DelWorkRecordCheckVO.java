package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author yyg
 */
@Data
public class DelWorkRecordCheckVO {

    @ApiModelProperty(value = "实例id", required = true)
    @NotBlank
    private String formInstId;

    @ApiModelProperty(value = "检查项id", required = true)
    @NotEmpty
    private List<String> ids;

    @ApiModelProperty(value = "检查类型", required = true)
    @NotNull
    private Integer checkType;

    @ApiModelProperty(value = "作业记录表类型 0=不是作业记录表 1=老版作业记录表 2=新版excel作业记录表", required = true)
    @NotNull
    private Integer workRecordType;

}
