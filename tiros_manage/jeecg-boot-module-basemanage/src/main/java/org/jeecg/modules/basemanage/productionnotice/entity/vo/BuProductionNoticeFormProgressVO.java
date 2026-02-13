package org.jeecg.modules.basemanage.productionnotice.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "生产通知单表单填报明细", description = "生产通知单关联工单的作业记录表填报情况")
public class BuProductionNoticeFormProgressVO implements Serializable {

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "工单ID")
    private String orderId;

    @ApiModelProperty(value = "工单号")
    private String orderCode;

    @ApiModelProperty(value = "表单实例ID")
    private String formInstId;

    @ApiModelProperty(value = "作业记录表编码")
    private String formCode;

    @ApiModelProperty(value = "作业记录表名称")
    private String formTitle;

    @ApiModelProperty(value = "填报状态 0=未填写 1=已填写")
    private Integer fillStatus;

    @ApiModelProperty(value = "检查结果 0=未通过 1=通过")
    private Integer checkResult;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "检查日期")
    private Date checkDate;
}

