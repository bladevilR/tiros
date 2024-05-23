package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;

import java.util.List;

/**
 * <p>
 * 工班工单信息vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/16
 */
@Data
@Accessors(chain = true)
public class BuWorkGroupOrderInfoVO {

    @ApiModelProperty(value = "工班名称")
    private String groupName;

    @ApiModelProperty(value = "工单列表")
    private List<BuWorkOrder> orderList;

    @ApiModelProperty(value = "应完成任务数")
    private Integer taskCount;

    @ApiModelProperty(value = "已完成任务数")
    private Integer finishedTaskCount;

    @ApiModelProperty(value = "未完成任务数")
    private Integer unfinishedTaskCount;

    @ApiModelProperty(value = "完成情况饼图")
    private List<PieChartVO> pieChartVOList;

}
