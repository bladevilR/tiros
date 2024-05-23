package org.jeecg.modules.produce.summary.bean.worktime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 维修作业工时控制情况-近xx列车
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class WorkTimeLast {

    @ApiModelProperty(value = "序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "各班组工时")
    private List<GroupHours> groupHoursList;

}
