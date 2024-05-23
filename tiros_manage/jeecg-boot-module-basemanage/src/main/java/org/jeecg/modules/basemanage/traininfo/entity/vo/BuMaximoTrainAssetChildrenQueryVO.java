package org.jeecg.modules.basemanage.traininfo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * <p>
 * maximo资产设备 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Data
@Accessors(chain = true)
public class BuMaximoTrainAssetChildrenQueryVO {

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "线路")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "名称或者编码")
    private String searchText;

    @ApiModelProperty(value = "状态 字典dictCode=bu_valid_status 0无效1有效")
    @Dict(dicCode = "bu_valid_status")
    private Integer status;

}
