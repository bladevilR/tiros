package org.jeecg.modules.produce.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 按车辆段分组的列计划VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-09
 */
@Data
@Accessors(chain = true)
public class BuDepotRepairPlanVO {

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "车辆段名称")
    private String depotName;

    @ApiModelProperty(value = "列计划列表")
    private List<BuRepairPlanVO> plans;

}
