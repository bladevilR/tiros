package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuOutsourceResourceQueryVO implements Serializable {
    @ApiModelProperty(value = "交接明细id")
    private String outinDetailId;
    @ApiModelProperty(value = "合同id")
    private String contractId;
    @ApiModelProperty(value = "文件类型 字典 bu_outin_file_type ")
    private Integer fileType;
    @ApiModelProperty(value = "文件名")
    private String fileName;

}
