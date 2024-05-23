package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务完成趋势
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuWorkshopProgressTaskTrendVO {

    @ApiModelProperty(value = "日")
    private Integer day;

    @ApiModelProperty(value = "计划完成任务数")
    private Integer plan;

    @ApiModelProperty(value = "实际完成任务数")
    private Integer actual;

}
