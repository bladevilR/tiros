package org.jeecg.modules.board.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 成本看板（车间）物料消耗统计vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/17
 */
@Data
@Accessors(chain = true)
public class BuWorkshopCostItemVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "金额")
    private Double cost;

    @ApiModelProperty(value = "个数")
    private Double quantity;

}
