package org.jeecg.modules.produce.trainhistory.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 车辆结构明细vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-03
 */
@Data
@Accessors(chain = true)
public class BuTrainStructureDetailTreeVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "上级结构id")
    private String parentId;

    @ApiModelProperty(value = "子节点")
    private List<BuTrainStructureDetailTreeVO> children;

}
