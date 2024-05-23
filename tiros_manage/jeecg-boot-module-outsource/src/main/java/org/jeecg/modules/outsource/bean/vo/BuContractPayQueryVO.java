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
public class BuContractPayQueryVO implements Serializable {
    @ApiModelProperty(value = "支付id")
    private String id;
    @ApiModelProperty(value = "支付时间开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(value = "支付时间结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    @ApiModelProperty(value = "支付标题")
    private String searchText;
    @ApiModelProperty(value = "合同id")
    private String contractId;
    @ApiModelProperty(value = "线路id")
    private String lineId;
    @ApiModelProperty(value = "厂商Id")
    private String supplierId;
}

