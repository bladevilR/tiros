package org.jeecg.modules.material.apply.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 自动分配物料vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-24
 */
@Data
@Accessors(chain = true)
public class BuMaterialAutoAssignVO {

    @ApiModelProperty(value = "领用明细id")
    private List<String> applyDetailIdList;

    @ApiModelProperty(value = "自动分配类型：1优先消耗库存量多的库存（默认） 2优先消耗库存量少的库存")
    private Integer type;

}
