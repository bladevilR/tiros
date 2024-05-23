package org.jeecg.modules.produce.summary.bean.worktime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位工时
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class WorkstationHours {

    @ApiModelProperty(value = "工位名称")
    private String workstationName;

    @ApiModelProperty(value = "工时")
    private Double hours;

}
