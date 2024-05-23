package org.jeecg.modules.outsource.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuyougen
 * @title: BuOutsourceQualityVO
 * @projectName tiros-manage-parent
 * @description: TODO
 * @date 2021/4/1310:26
 */
@Data
public class BuOutsourceQualityVO implements Serializable {
    @ApiModelProperty(value = "部件id")
    private String id;
    @ApiModelProperty(value = "质保日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date qualityDate;
    @ApiModelProperty(value = "质保天数")
    private Integer qualityDay;
    @ApiModelProperty(value = "备注")
    private String remark;
}
