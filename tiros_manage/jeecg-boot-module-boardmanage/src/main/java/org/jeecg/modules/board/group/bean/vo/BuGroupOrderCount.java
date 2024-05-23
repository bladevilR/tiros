package org.jeecg.modules.board.group.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 班组工单进度数量统计
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@Accessors(chain = true)
public class BuGroupOrderCount {

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "总完成数")
    private Integer finish;

    @ApiModelProperty(value = "延期数")
    private Integer delay;

    @ApiModelProperty(value = "暂停数")
    private Integer suspend;
    @ApiModelProperty(value = "班组名称")
    private String groupName;

}
