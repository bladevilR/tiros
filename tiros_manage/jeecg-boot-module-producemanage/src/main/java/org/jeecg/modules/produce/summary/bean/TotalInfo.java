package org.jeecg.modules.produce.summary.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 总体情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/4
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TotalInfo extends PlanBase {

    @ApiModelProperty(value = "计划接车时间")
    private Date receiveTime;

    @ApiModelProperty(value = "计划交车时间")
    private Date deliverTime;

    @ApiModelProperty(value = "实际接车时间")
    private Date actualReceiveTime;

    @ApiModelProperty(value = "实际交车时间")
    private Date actualDeliverTime;

    @ApiModelProperty(value = "接车提前天数")
    private Integer receiveEarlierDays;

    @ApiModelProperty(value = "交车提前天数")
    private Integer deliverEarlierDays;


}
