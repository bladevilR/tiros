package org.jeecg.modules.board.workstation.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位任务信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationTaskVO {

    @ApiModelProperty(value = "任务完成进度 -1表示无作业")
    private Double progress;

    @ApiModelProperty(value = "任务总数")
    private Integer total;

    @ApiModelProperty(value = "完成任务数")
    private Integer finish;

    @ApiModelProperty(value = "逾期任务数")
    private Integer delay;

}
