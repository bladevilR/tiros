package org.jeecg.modules.basemanage.holiday.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "节日查询对象")
public class BuBaseHolidayQueryVO {
    @ApiModelProperty(value = "年份")
    private Integer year;
    @ApiModelProperty(value = "节日名称")
    private String name;


}
