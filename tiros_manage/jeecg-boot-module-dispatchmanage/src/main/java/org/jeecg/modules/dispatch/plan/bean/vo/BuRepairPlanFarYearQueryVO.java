package org.jeecg.modules.dispatch.plan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 远期规划/年计划 查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/22
 */
@Data
public class BuRepairPlanFarYearQueryVO {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "状态 字典dictCode=bu_repair_plan_status")
    private Integer status;

    @ApiModelProperty(value = "主键id")
    private String id;

}
