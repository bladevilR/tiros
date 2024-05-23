package org.jeecg.modules.outsource.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "委外合同设备对象")
public class OutsourceAsset {

    @ApiModelProperty("合同设备id")
    private String  id;

    @ApiModelProperty("合同设备名称")
    private String assetName;
}
