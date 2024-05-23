package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 工单任务app查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-24
 */
@Data
public class BuWorkOrderTaskQueryVOForApp implements Serializable {

    @ApiModelProperty(value = "工单id")
    private String workOrderId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "是否未完成 0否1是")
    private Integer unfinished;

    @ApiModelProperty(value = "是否包含表单记录 0否1是")
    private Integer hasWorkRecord;

    @ApiModelProperty(value = "是否重要工序 0否1是")
    private Integer important;

}
