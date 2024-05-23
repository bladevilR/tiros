package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yyg
 */
@Data
public class BuTrainTypeQueryVO {
    @ApiModelProperty(value = "名字或者编码")
    private String searchName;
    @ApiModelProperty(value = "0:禁用  1 ：启用")
    private Integer status;
}
