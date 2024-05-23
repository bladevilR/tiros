package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
public class BuContractBakMoneyQueryVO implements Serializable {
    @ApiModelProperty(value = "暂列金使用id")
    private  String id;
    @ApiModelProperty(value = "合同ID")
    private String contractId;
    @ApiModelProperty(value = "使用标题")
    private  String name;
    @ApiModelProperty(value = "使用时间开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(value = "使用时间结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
     private Date endTime;
}
