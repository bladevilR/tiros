package org.jeecg.modules.produce.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 物料成本比较分析查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/26
 */
@Data
@Accessors(chain = true)
public class BuCostCompareQueryVO {

    @ApiModelProperty(value = "是否进行比较分析（默认false） false时不进行比较分析，只根据查询条件1返回查询结果1")
    private Boolean doCompare;

    @ApiModelProperty(value = "查询条件1")
    private BuCostQueryVO query1;

    @ApiModelProperty(value = "查询条件2")
    private BuCostQueryVO query2;

}
