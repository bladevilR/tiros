package org.jeecg.common.tiros.holiday.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

/**
 * <p>
 * 特殊休息日/工作日
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-12
 */
@Data
@Accessors(chain = true)
public class SpecialHoliday {

    @ApiModelProperty(value = "特殊休息日")
    private Set<Date> specialHolidayDaySet;

    @ApiModelProperty(value = "特殊工作日")
    private Set<Date> specialWorkDaySet;

}
