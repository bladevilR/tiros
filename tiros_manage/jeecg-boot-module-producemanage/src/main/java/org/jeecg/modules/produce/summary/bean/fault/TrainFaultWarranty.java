package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 质保期各车辆故障
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TrainFaultWarranty extends TrainFault {

    @ApiModelProperty(value = "质保期AB类")
    private Integer warrantyAB;

    @ApiModelProperty(value = "质保期正线")
    private Integer warrantyOnline;

}
