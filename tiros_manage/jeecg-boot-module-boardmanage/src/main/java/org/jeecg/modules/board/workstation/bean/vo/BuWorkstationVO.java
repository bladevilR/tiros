package org.jeecg.modules.board.workstation.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位看板vo--工位信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationVO {

    @ApiModelProperty(value = "工班id")
    private String groupId;

    @ApiModelProperty(value = "工班名称")
    private String groupName;

    @ApiModelProperty(value = "工位id")
    private String workstationId;

    @ApiModelProperty(value = "工位名称")
    private String workstationName;

    @ApiModelProperty(value = "任务信息")
    private BuWorkstationTaskVO task;

    @ApiModelProperty(value = "必换件信息")
    private BuWorkstationMustReplaceVO mustReplace;

    @ApiModelProperty(value = "故障信息")
    private BuWorkstationFaultVO fault;

    @ApiModelProperty(value = "作业人数信息")
    private BuWorkstationWorkerVO worker;

}
