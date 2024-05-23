package org.jeecg.modules.produce.summary.bean.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.vo.chart.PieChartVO;

import java.util.List;

/**
 * <p>
 * 维修成本情况-本列车
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class CostCurrent {

    @ApiModelProperty(value = "各系统物资消耗")
    private List<SystemCost> systemList;

    @ApiModelProperty(value = "物资主要消耗占比")
    private List<MaterialCost> materialTopList;

}
