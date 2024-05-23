package org.jeecg.modules.basemanage.regu.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ImportTechBook {
    @ApiModelProperty(value = "目标规程作业项id",required = true)
    @NotBlank
    private String targetReguItemId;
    @ApiModelProperty(value = "要导入作业指导书的多个规程作业项id",required = true)
    @NotEmpty
    private List<String> toReguItemIds;
}
