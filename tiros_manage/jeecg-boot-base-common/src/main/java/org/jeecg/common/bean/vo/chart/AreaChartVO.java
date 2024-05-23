package org.jeecg.common.bean.vo.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 面积图vo--前端要求数据格式
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/12
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "面积图前端数据格式")
public class AreaChartVO {

    @ApiModelProperty(value = "x")
    private String x;

    @ApiModelProperty(value = "y")
    private Double y;

}
