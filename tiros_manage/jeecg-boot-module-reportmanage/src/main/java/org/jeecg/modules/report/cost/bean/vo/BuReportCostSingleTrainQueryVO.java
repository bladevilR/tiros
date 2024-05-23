package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.bean.TimeQuery;
import org.jeecg.common.bean.TimeQueryMonth;

/**
 * <p>
 * 物资消耗汇总-单列 查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuReportCostSingleTrainQueryVO extends TimeQueryMonth {

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

}
