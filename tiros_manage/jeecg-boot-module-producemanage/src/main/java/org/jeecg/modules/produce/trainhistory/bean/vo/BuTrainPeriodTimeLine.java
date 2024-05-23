package org.jeecg.modules.produce.trainhistory.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 车辆架修周期时间线vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/6
 */
@Data
@Accessors(chain = true)
public class BuTrainPeriodTimeLine {

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "日期yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @ApiModelProperty(value = "描述名称")
    private String description;

    @ApiModelProperty(value = "是否当前节点")
    private Boolean isCurrent;

}
