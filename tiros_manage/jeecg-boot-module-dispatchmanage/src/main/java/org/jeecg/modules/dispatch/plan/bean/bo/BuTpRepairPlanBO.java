package org.jeecg.modules.dispatch.plan.bean.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 计划模板BO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/28
 */
@Data
public class BuTpRepairPlanBO {

    @ApiModelProperty(value = "计划模板id")
    private String id;

    @ApiModelProperty(value = "计划模板名称")
    private String tpName;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "修程类型id")
    private String repairProgramId;

    @ApiModelProperty(value = "计划工期")
    private Integer duration;

    @ApiModelProperty(value = "所属线路名称")
    private String lineName;

    @ApiModelProperty(value = "修程类型名称")
    private String repairProgramName;

    @ApiModelProperty(value = "工期序号和对应作业班组")
    private Map<Integer, Set<String>> dayIndexWorkGroupIdSetMap;

}
