package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 年计划完工质量数据
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/2
 */
@Data
@Accessors(chain = true)
public class BuPlanYearMonthFinishVO {

    @ApiModelProperty(value = "月份")
    private String month;

    @ApiModelProperty(value = "计划剩余数")
    private Integer plan;

    @ApiModelProperty(value = "实际剩余数")
    private Integer actual;

}
