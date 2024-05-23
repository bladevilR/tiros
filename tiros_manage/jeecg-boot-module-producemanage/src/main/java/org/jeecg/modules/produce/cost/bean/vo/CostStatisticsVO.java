package org.jeecg.modules.produce.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.vo.chart.PieChartVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 成本监控--统计
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/26
 */
@Data
@Accessors(chain = true)
public class CostStatisticsVO {

    @ApiModelProperty(value = "系统物料消耗统计(多柱状图)-字段集合")
    private List<String> sysFieldList;

    @ApiModelProperty(value = "系统物料消耗统计(多柱状图)-数据")
    private List<Map<String, Object>> sysDataList;

    @ApiModelProperty(value = "物料消耗分类占比(饼图)")
    private List<PieChartVO> categoryDataList;

    @ApiModelProperty(value = "必换件消耗分类占比(饼图)")
    private List<PieChartVO> mustDataList;

    @ApiModelProperty(value = "偶换件消耗分类占比(饼图)")
    private List<PieChartVO> randomDataList;

    @ApiModelProperty(value = "耗材消耗分类占比(饼图)")
    private List<PieChartVO> consumeDataList;

}
