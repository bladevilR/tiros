package org.jeecg.modules.produce.summary.bean.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 各车辆成本
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class TrainCost {

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "自主修")
    private BigDecimal selfRepair;

    @ApiModelProperty(value = "委外修")
    private BigDecimal outsourceRepair;

    @ApiModelProperty(value = "每列车")
    private BigDecimal trainAverage;

    @ApiModelProperty(value = "每节车")
    private BigDecimal carAverage;


}
