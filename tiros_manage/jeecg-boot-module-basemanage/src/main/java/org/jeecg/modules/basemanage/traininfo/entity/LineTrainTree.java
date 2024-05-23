package org.jeecg.modules.basemanage.traininfo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 线路-车辆树型结构
 *
 * @author zhaiyantao
 * @since 2021-01-12
 */
@Data
@Accessors(chain = true)
public class LineTrainTree {

    @ApiModelProperty(value = "类型 1线路 2车辆")
    private Integer type;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "车型id")
    private String trainTypeId;

    @ApiModelProperty(value = "是否禁用选择")
    private boolean disabled;

    @ApiModelProperty(value = "子节点列表")
    private List<LineTrainTree> children;

}
