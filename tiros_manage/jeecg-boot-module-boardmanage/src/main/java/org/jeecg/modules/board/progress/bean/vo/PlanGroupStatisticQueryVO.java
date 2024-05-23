package org.jeecg.modules.board.progress.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 列计划班组工单故障填写统计 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/1/4
 */
@Data
@Accessors(chain = true)
public class PlanGroupStatisticQueryVO {

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

}
