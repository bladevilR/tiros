package org.jeecg.modules.produce.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 按线路分组的列计划VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/10
 */
@Data
@Accessors(chain = true)
public class BuLineRepairPlanVO {

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "列计划列表")
    private List<BuRepairPlanVO> plans;

}
