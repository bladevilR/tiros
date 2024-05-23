package org.jeecg.modules.produce.trainhistory.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 车辆履历树--基本结构，统一字段
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/4
 */
@Data
@Accessors(chain = true)
public class BuTrainHistoryTreeBaseVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "显示标题")
    private String title;

    @ApiModelProperty(value = "显示长标题")
    private String longTitle;

    @ApiModelProperty(value = "节点数据类型：1线路 2车辆 3资产结构")
    private Integer type;

    @ApiModelProperty(value = "禁用标志  前端使用")
    private Boolean disabled;

    @ApiModelProperty(value = "子节点")
    private List<BuTrainHistoryTreeBaseVO> children;

}
