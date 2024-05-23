package org.jeecg.modules.outsource.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
public class BuOutsourceQuality implements Serializable {
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "线路")
    private String lineName;

    @ApiModelProperty(value = "部件编号")
    private String assetNo;

    @ApiModelProperty(value = "部件编号")
    private String assetId;

    @ApiModelProperty(value = "部件")
    private String assetName;

    @ApiModelProperty(value = "厂商")
    private String supplier;

    @ApiModelProperty(value = "验收班组")
    private String acceptanceGroup;

    @ApiModelProperty(value = "当前位置")
    private String currentPosition;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "剩余天数")
    private Integer surplusDays;

    @ApiModelProperty(value = "质保天数")
    private Integer dayCount;

    @ApiModelProperty(value = "验收日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "质保开始日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "质保结束日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "质保天数(格式：剩余天数/质保天数)")
    private String  qualityDayStr;

    @JsonIgnore
    private Integer billType;

    @JsonIgnore
    private String outDetailId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "备注")
    private String remark;
}
