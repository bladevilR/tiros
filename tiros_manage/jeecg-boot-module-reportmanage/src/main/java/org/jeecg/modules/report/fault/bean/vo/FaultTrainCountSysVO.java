package org.jeecg.modules.report.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障统计(单列)系统分布vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
@Data
@Accessors(chain = true)
public class FaultTrainCountSysVO {

    @ApiModelProperty(value = "系统")
    private String sys;

    @ApiModelProperty(value = "架修期")
    private Integer repair;

    @ApiModelProperty(value = "架修期AB类")
    private Integer repairAB;

    @ApiModelProperty(value = "质保期")
    private Integer warranty;

    @ApiModelProperty(value = "质保期AB类")
    private Integer warrantyAB;

    @ApiModelProperty(value = "质保期正线")
    private Integer warrantyOnline;

    @ApiModelProperty(value = "质保期有责故障")
    private Integer warrantyHasDuty;

}
