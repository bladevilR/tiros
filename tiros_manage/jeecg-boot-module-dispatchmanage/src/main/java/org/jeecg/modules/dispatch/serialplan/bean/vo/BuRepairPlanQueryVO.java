package org.jeecg.modules.dispatch.serialplan.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 列计划
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairPlanQueryVO对象", description = "列计划查询条件")
public class BuRepairPlanQueryVO implements Serializable {

    private String id;

    @ApiModelProperty(value = "计划名或列车序号")
    private String searchText;

    @ApiModelProperty(value = "车辆段id ,字典bu_mtr_depot,name,id")
    private String depotId;

    @ApiModelProperty(value = "线路id,字典bu_mtr_line,line_name,line_id")
    private String lineId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "进度状态  字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    private Integer progressStatus;

    @ApiModelProperty(value = "审批状态  字典：bu_repair_plan_status")
    private Integer status;

    @ApiModelProperty(value = "进度状态列表")
    private List<Integer> progressStatusList;

}
