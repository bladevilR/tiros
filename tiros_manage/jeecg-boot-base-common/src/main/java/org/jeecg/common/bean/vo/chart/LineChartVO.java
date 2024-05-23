package org.jeecg.common.bean.vo.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 折线图vo--前端要求数据格式
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/10
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "折线图前端数据格式")
public class LineChartVO {

    @ApiModelProperty(value = "type")
    private String type;

    @ApiModelProperty(value = "jeecg")
    private Double jeecg;

    @ApiModelProperty(value = "jeebt")
    private Double jeebt;

}
