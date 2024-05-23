package org.jeecg.modules.produce.summary.bean.worktime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 班组工时
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class GroupHours {

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "工时")
    private Double hours;

    @ApiModelProperty(value = "工时比率")
    private Double percent;

    @ApiModelProperty(value = "各工位工时")
    private List<WorkstationHours> workstationHoursList;

}
