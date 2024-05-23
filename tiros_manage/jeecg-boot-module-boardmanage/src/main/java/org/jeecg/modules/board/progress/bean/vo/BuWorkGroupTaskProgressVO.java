package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工班任务进度vo--进度看板(车间)
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/16
 */
@Data
@Accessors(chain = true)
public class BuWorkGroupTaskProgressVO {

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称(带车辆段名称)")
    private String groupName;

    @ApiModelProperty(value = "工班长名称")
    private String groupMonitorName;

    @ApiModelProperty(value = "作业人数")
    private Integer workerQuantity;

    @ApiModelProperty(value = "进度")
    private Double progress;

}
