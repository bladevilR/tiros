package org.jeecg.modules.board.quality.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障趋势
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuCenterQualityFaultTrendVO {

    @ApiModelProperty(value = "年月")
    private String yearMonth;

    @ApiModelProperty(value = "平均故障数")
    private Integer average;

    @ApiModelProperty(value = "实际故障数")
    private Integer actual;

}