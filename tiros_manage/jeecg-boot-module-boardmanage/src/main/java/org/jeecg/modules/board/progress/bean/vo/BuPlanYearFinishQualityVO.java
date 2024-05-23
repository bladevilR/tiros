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
public class BuPlanYearFinishQualityVO {

    @ApiModelProperty(value = "总数")
    private Integer amount;

    @ApiModelProperty(value = "正常完工数")
    private Integer normal;

    @ApiModelProperty(value = "延期完工数")
    private Integer delay;

    @ApiModelProperty(value = "超前完工数")
    private Integer leading;

}
