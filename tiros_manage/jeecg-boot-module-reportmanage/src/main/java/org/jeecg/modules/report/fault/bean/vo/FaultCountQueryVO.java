package org.jeecg.modules.report.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQueryMonth;

/**
 * <p>
 * 故障汇总(单列)查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FaultCountQueryVO extends TimeQueryMonth {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

}
