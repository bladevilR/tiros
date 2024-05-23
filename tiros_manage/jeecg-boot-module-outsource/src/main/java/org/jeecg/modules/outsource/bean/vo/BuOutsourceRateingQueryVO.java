package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
public class BuOutsourceRateingQueryVO implements Serializable {
    @ApiModelProperty(value = "交接明细id")
    private String outinDetailId;
    @ApiModelProperty(value = "合同id")
    private String contractId;
    @ApiModelProperty(value = "评分项")
    private Integer rateingItem;
    @ApiModelProperty(value = "评分开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "评分结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date  endTime;
}
