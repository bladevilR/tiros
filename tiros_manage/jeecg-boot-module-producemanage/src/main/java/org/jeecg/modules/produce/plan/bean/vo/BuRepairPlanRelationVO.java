package org.jeecg.modules.produce.plan.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.produce.plan.bean.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
@ApiModel(value = "列计划关联信息对象", description = "列计划关联的信息")
public class BuRepairPlanRelationVO implements Serializable {

    @ApiModelProperty(value = "计划id")
    private String id;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "修程类型id")
    private String repairProgramId;

    @ApiModelProperty(value = "关联规程")
    private List<BuRepairTaskRegu> repairPlanReguInfo;

    @ApiModelProperty(value = "作业工位")
    private List<BuRepairTaskWorkstation> workstations;

    @ApiModelProperty(value = "物料")
    private List<BuRepairTaskMaterial> materials;

    @ApiModelProperty(value = "工器具")
    private List<BuRepairTaskTool> tools;

    @ApiModelProperty(value = "所需人员")
    private List<BuRepairTaskStaffRequire> persons;

    @ApiModelProperty(value = "作业表单")
    private List<BuRepairPlanForms> forms;

    @ApiModelProperty(value = "必换件")
    private List<BuRepairTaskMustReplace> mustReplaces;

}
