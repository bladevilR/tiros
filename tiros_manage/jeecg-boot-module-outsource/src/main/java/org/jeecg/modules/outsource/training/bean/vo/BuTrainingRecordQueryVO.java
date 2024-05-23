package org.jeecg.modules.outsource.training.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
public class BuTrainingRecordQueryVO implements Serializable {

    @ApiModelProperty(value = "培训开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(value = "培训结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    @ApiModelProperty(value = "合同名称或编码")
    private String searchText;
    @ApiModelProperty(value = "线路id")
    private String lineId;
    @ApiModelProperty(value = "厂商名称")
    private String supplier;
    @ApiModelProperty(value = "培训人员Id")
    private String userId;
}

