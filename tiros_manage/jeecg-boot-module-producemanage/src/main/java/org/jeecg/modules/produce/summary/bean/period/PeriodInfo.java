package org.jeecg.modules.produce.summary.bean.period;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 维修周期控制情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/5
 */
@Data
@Accessors(chain = true)
public class PeriodInfo {

    @ApiModelProperty(value = "本列车周期")
    private PeriodDay currentPeriodDay;

    @ApiModelProperty(value = "该线路各车辆周期")
    private List<PeriodDay> linePeriodDayList;

}
