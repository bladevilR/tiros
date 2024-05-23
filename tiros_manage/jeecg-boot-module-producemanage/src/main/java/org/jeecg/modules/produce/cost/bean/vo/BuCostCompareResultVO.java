package org.jeecg.modules.produce.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 成本监控
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/11
 */
@Data
@Accessors(chain = true)
public class BuCostCompareResultVO {

    @ApiModelProperty(value = "是否进行比较分析（默认false） false时不进行比较分析，只根据查询条件1返回查询结果1")
    private Boolean doCompare;

    @ApiModelProperty(value = "系统物料消耗对比-字段集合")
    private List<String> systemFieldList;

    @ApiModelProperty(value = "系统物料消耗对比-数据")
    private List<Map<String, Object>> systemDataList;

    @ApiModelProperty(value = "物料分类消耗对比-字段集合")
    private List<String> categoryFieldList;

    @ApiModelProperty(value = "物料分类消耗对比-数据")
    private List<Map<String, Object>> categoryDataList;

    @ApiModelProperty(value = "必换件系统消耗对比-字段集合")
    private List<String> mustSystemFieldList;

    @ApiModelProperty(value = "必换件系统消耗对比-数据")
    private List<Map<String, Object>> mustSystemDataList;

}
