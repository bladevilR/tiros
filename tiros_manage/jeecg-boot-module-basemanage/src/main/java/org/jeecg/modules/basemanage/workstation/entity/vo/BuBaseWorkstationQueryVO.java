package org.jeecg.modules.basemanage.workstation.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yyg
 */
@Data
public class BuBaseWorkstationQueryVO {

    @ApiModelProperty(value = "工位名称")
    private String name;
    @ApiModelProperty(value = "位置")
    private String location;
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "班组id")
    private String groupId;


}
