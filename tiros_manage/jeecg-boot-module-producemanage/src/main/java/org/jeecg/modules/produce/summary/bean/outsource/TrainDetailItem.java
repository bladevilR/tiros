package org.jeecg.modules.produce.summary.bean.outsource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 各车辆维修详情-项目
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class TrainDetailItem {

    @ApiModelProperty(value = "项目")
    private String itemName;

    @ApiModelProperty(value = "送件日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date outDate;

    @ApiModelProperty(value = "返回日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inDate;

    @ApiModelProperty(value = "延误天数")
    private Integer delayDays;

    @ApiModelProperty(value = "是否影响进度 字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer affectProgress;

    @ApiModelProperty(value = "备注")
    private String remark;

}
