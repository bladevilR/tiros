package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 质保期故障
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class FaultWarranty {

    @ApiModelProperty(value = "各车辆故障")
    private List<TrainFaultWarranty> trainList;

    @ApiModelProperty(value = "主要质保期故障")
    private List<DetailFault> importantFaultList;

}
