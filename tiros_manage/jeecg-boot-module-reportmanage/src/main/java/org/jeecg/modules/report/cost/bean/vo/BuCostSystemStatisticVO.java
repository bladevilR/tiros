package org.jeecg.modules.report.cost.bean.vo;

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
public class BuCostSystemStatisticVO {

    @ApiModelProperty(value = "系统-字段集合")
    private List<String> systemFieldList;

    @ApiModelProperty(value = "系统-数据")
    private List<Map<String, Object>> systemDataList;

}
