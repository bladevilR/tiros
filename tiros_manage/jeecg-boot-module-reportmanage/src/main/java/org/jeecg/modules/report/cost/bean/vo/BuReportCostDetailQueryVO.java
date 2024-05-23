package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;
import org.jeecg.common.bean.TimeQueryMonth;

import java.util.List;

/**
 * <p>
 * 物质消耗明细 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuReportCostDetailQueryVO extends TimeQueryMonth {

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "物资使用类型列表 字典dictCode=bu_material_type")
    private List<Integer> useCategoryList;

    @ApiModelProperty(value = "物资属性 字典dictCode=bu_material_attr")
    private String category2;

}
