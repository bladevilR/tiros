package org.jeecg.modules.material.warehouse.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 仓库查询条件
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
@Data
public class BuMtrWarehouseQueryVO implements Serializable {

    @ApiModelProperty(value = "上级仓库id，为null表示顶级库")
    private String parentId;

    @ApiModelProperty(value = "仓库编码或者名称", example = "NO.18")
    private String searchText;

    @ApiModelProperty(value = "仓库类别 0 虚拟库  1 实体库  2 库位", example = "1")
    private Integer type;
}
