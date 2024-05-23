package org.jeecg.modules.produce.summary.bean.worktime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 维修作业工时控制情况-本列车
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class WorkTimeCurrent {

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "实际工作日")
    private Integer workDays;

    @ApiModelProperty(value = "总工时")
    private Double workHours;

    @ApiModelProperty(value = "各班组工时")
    private List<GroupHours> groupHoursList;

}
