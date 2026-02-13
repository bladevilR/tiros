package org.jeecg.modules.basemanage.qualityvisual.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "质量策划抽取结果", description = "按列计划/车号抽取的质量策划检查单数据")
public class BuQualityPlanningSummaryVO implements Serializable {

    @ApiModelProperty(value = "列计划ID")
    private String planId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "总项点数")
    private Integer totalItems;

    @ApiModelProperty(value = "无需填报项点数")
    private Integer noNeedFillItems;

    @ApiModelProperty(value = "已填报项点数")
    private Integer filledItems;

    @ApiModelProperty(value = "待填报项点数")
    private Integer pendingItems;

    @ApiModelProperty(value = "检查项明细")
    private List<BuQualityPlanningItemVO> items;
}

