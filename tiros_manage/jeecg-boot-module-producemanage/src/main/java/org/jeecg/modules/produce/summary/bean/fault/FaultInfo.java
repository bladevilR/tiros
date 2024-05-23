package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 维修质量控制情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class FaultInfo {

    @ApiModelProperty(value = "本列车")
    private FaultCurrent current;

    @ApiModelProperty(value = "本线路惯性故障")
    private List<FaultLineOften> lineOftenList;

    @ApiModelProperty(value = "本线路各车辆故障数量")
    private List<TrainFault> lineTrainList;

    @ApiModelProperty(value = "质保期故障情况")
    private FaultWarranty warranty;

}
