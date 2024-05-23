package org.jeecg.modules.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 用户系统访问统计 vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-11-13
 */
@Data
@Accessors(chain = true)
public class LoginStatisticVO {

    @ApiModelProperty(value = "年")
    private Integer year;

    @ApiModelProperty(value = "月")
    private Integer month;

    @ApiModelProperty(value = "周")
    private Integer weekOfMonth;

    @ApiModelProperty(value = "周描述")
    private String weekOfMonthDesc;

    @ApiModelProperty(value = "日期列表")
    private List<String> dateList;

    @ApiModelProperty(value = "日期范围")
    private String dateRange;

    @ApiModelProperty(value = "用户数")
    private Long userCount;

    @ApiModelProperty(value = "登录次数")
    private Long loginCount;

    @ApiModelProperty(value = "使用次数")
    private Long useTimeCount;

}
