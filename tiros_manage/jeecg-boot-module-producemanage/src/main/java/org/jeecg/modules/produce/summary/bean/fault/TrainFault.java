package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 各车辆故障
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class TrainFault {

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "故障数")
    private Integer total;

}
