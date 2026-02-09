package org.jeecg.modules.basemanage.productionnotice.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "生产通知单进度明细对象", description = "按列车汇总的生产通知执行进度")
public class BuProductionNoticeProgressDetailVO implements Serializable {

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "关联工单总数")
    private Integer totalOrders;

    @ApiModelProperty(value = "已完成工单数")
    private Integer completedOrders;

    @ApiModelProperty(value = "最近完成时间")
    private Date lastFinishTime;
}

