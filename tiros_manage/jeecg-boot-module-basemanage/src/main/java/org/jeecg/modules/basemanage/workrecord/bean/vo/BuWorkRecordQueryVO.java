package org.jeecg.modules.basemanage.workrecord.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class BuWorkRecordQueryVO implements Serializable {

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属修程id")
    private String repairProId;

    @ApiModelProperty(value = "作业记录表单名称或编码")
    private String name;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "创建班组id")
    private String createGroupId;
    @ApiModelProperty(value = "规程id")
    private String reguId;

}
