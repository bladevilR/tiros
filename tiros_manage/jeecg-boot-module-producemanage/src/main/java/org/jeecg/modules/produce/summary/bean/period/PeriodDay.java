package org.jeecg.modules.produce.summary.bean.period;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.produce.summary.bean.PlanBase;

/**
 * <p>
 * 周期控制情况-列车周期
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/5
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PeriodDay extends PlanBase {

    @ApiModelProperty(value = "计划天数")
    private Integer plannedDays;

    @ApiModelProperty(value = "实际天数")
    private Integer actualDays;

}
