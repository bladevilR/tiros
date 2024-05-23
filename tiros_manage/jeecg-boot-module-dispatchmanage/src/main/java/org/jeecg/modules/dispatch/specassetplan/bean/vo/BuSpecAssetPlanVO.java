package org.jeecg.modules.dispatch.specassetplan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BuSpecAssetPlanVO {
    @ApiModelProperty(value = "状态类型")
    private Integer type;
    @ApiModelProperty(value = "值")
    private String value;
}
