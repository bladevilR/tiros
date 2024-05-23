package org.jeecg.modules.produce.summary.bean.worktime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 维修作业工时控制情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class WorkTimeInfo {

    @ApiModelProperty(value = "本列车")
    private WorkTimeCurrent current;

    @ApiModelProperty(value = "近三列车")
    private List<WorkTimeLast> lastThree;

    @ApiModelProperty(value = "本线路各车辆工时")
    private List<WorkTimeLine> lineTrainList;

}
