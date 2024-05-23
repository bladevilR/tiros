package org.jeecg.modules.basemanage.repair.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 检修属性查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Data
@Accessors(chain = true)
public class RepairAttributeQueryVO {

    @ApiModelProperty(value = "类型;1工作类型、2大类、3检修等级、4检修周期")
    private Integer attributeType;

    @ApiModelProperty(value = "编码或描述")
    private String searchText;

}
