package org.jeecg.modules.dispatch.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 指定年份修程计划数量
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/24
 */
@Data
@Accessors(chain = true)
public class BuRepairPlanYearAmountVO {

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "架修数")
    private Integer middleRepair;

    @ApiModelProperty(value = "大修数")
    private Integer hightRepair;

}
