package org.jeecg.common.bean.vo.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 饼状图vo--前端要求数据格式
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/10
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "饼状图前端数据格式")
public class PieChartVO {

    @ApiModelProperty(value = "item")
    private String item;

    @ApiModelProperty(value = "count")
    private Double count;

}
