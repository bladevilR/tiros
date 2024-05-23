package org.jeecg.modules.dispatch.workorder.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuOtherData implements Serializable {

    @ApiModelProperty(value = "工单任务id")
    private String taskId;

    @ApiModelProperty(value = "文件id")
    private String fileId;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "存放地址")
    private String savePath;

}
