package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.board.progress.bean.BuRptPlanGroupStatistic;

import java.util.List;

/**
 * <p>
 * 列计划班组工单故障填写统计 结果vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/1/4
 */
@Data
@Accessors(chain = true)
public class PlanGroupStatisticVO {

    @ApiModelProperty(value = "列计划统计")
    private List<BuRptPlanGroupStatistic> planList;

    @ApiModelProperty(value = "各班组统计")
    private List<BuRptPlanGroupStatistic> groupList;

}
