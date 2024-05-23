package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 年计划进度--车辆段
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/2
 */
@Data
@Accessors(chain = true)
public class BuPlanYearProgressDepotVO {

//    @ApiModelProperty(value = "车辆段id")
//    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "整体进度")
    private String overallProgress;

    @ApiModelProperty(value = "修程数据")
    private List<BuPlanYearProgressProgramVO> programs;

}
