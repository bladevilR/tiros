package org.jeecg.modules.quality.exceptiontransfer.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "例外转序查询对象", description = "例外转序分页查询条件")
public class BuWorkExceptionTransferQueryVO implements Serializable {

    @ApiModelProperty(value = "列计划ID")
    private String planId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "线路ID")
    private String lineId;

    @ApiModelProperty(value = "故障ID")
    private String faultId;

    @ApiModelProperty(value = "工单ID")
    private String orderId;

    @ApiModelProperty(value = "工单任务ID")
    private String orderTaskId;

    @ApiModelProperty(value = "状态 0待处理 1已放行 2已驳回")
    private Integer status;

    @ApiModelProperty(value = "关键字（编号/描述）")
    private String searchText;
}

