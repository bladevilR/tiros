package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 维修质量控制情况-本列车
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class FaultCurrent {

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "A/B类数")
    private Integer levelAB;

    @ApiModelProperty(value = "已处理数")
    private Integer handled;

    @ApiModelProperty(value = "未处理数")
    private Integer unhandled;

    @ApiModelProperty(value = "跟踪数")
    private Integer tracking;

    @ApiModelProperty(value = "各系统故障")
    private List<SystemFault> systemList;

    @ApiModelProperty(value = "主要维修期故障")
    private List<DetailFault> importantFaultList;

}
