package org.jeecg.modules.produce.summary.bean.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 维修成本情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class CostInfo {

    @ApiModelProperty(value = "本列车")
    private CostCurrent current;

    @ApiModelProperty(value = "本线路")
    private CostLine line;

}
