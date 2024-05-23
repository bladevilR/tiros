package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 年计划进度--修程
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/2
 */
@Data
@Accessors(chain = true)
public class BuPlanYearProgressProgramVO {

//    @ApiModelProperty(value = "修程id")
//    private String programId;

    @ApiModelProperty(value = "修程名称")
    private String programName;

    @ApiModelProperty(value = "年计划数量")
    private Integer amount;

    @ApiModelProperty(value = "已完成数量")
    private Integer finish;

    @ApiModelProperty(value = "完成比例")
    private String finishProgress;

    @ApiModelProperty(value = "正在检修数量")
    private Integer repairing;

//    @ApiModelProperty(value = "车辆段id")
//    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "车辆段整体进度")
    private String overallProgress;

}
