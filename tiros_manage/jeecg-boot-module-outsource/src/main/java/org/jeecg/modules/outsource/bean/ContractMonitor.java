package org.jeecg.modules.outsource.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class ContractMonitor implements Serializable {
    @ApiModelProperty(value = "已支付")
    private Double  payed;
    @ApiModelProperty(value = "暂列金")
    private Double  bakMoney;
    @ApiModelProperty(value = "评分")
    private Double score;
    @ApiModelProperty(value = "合同进度")
    private Double contractProcess;
    @ApiModelProperty(value = "质保期限")
    private Double quality;
}
