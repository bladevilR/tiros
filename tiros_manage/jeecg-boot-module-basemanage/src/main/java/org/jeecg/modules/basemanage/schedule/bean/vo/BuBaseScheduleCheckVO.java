package org.jeecg.modules.basemanage.schedule.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 日程信息 是否存在vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-22
 */
@Data
@Accessors(chain = true)
public class BuBaseScheduleCheckVO {

    @ApiModelProperty(value = "日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @ApiModelProperty(value = "是否有日程")
    private Boolean hasSchedule;

    @ApiModelProperty(value = "日程数量")
    private Integer number;

}
