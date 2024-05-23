package org.jeecg.modules.produce.summary.bean.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 维修成本情况-本线路
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class CostLine {

    @ApiModelProperty(value = "每列车")
    private BigDecimal trainAverage;

    @ApiModelProperty(value = "每节车")
    private BigDecimal carAverage;

    @ApiModelProperty(value = "线路所有车辆")
    private List<TrainCost> trainList;

    @ApiModelProperty(value = "本列和上列比较信息")
    private String currentLastCompareInfo;

}
