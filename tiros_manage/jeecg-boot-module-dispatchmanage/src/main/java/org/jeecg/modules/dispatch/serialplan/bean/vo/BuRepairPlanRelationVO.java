package org.jeecg.modules.dispatch.serialplan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanForms;
import org.jeecg.modules.dispatch.serialplan.bean.*;
import org.jeecg.modules.dispatch.serialplan.bean.tp.BuTpRepairPlanSpeEq;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
@Accessors(chain = true)
public class BuRepairPlanRelationVO implements Serializable {

    @ApiModelProperty(value = "计划id")
    private String id;

    @ApiModelProperty(value = "关联规程")
    private List<BuRepairTaskRegu> repairPlanReguInfo;

    @ApiModelProperty(value = "作业指导书明细")
    private List<BuRepairTaskBookStep> bookSteps;

    @ApiModelProperty(value = "特种设备需求")
    private List<BuRepairPlanSpeEq> specAssets;

    @ApiModelProperty(value = "作业工位")
    private List<BuRepairTaskWorkstation> workstations;

    @ApiModelProperty(value = "物料")
    private List<BuRepairTaskMaterial> materials;

    @ApiModelProperty(value = "工器具")
    private List<BuRepairTaskTool> tools;

    @ApiModelProperty(value = "所需人员")
    private List<BuRepairTaskStaffRequire> persons;

    @ApiModelProperty(value = "必换件")
    private List<BuRepairTaskMustReplace> mustReplaces;

    @ApiModelProperty(value = "目标设备")
    private List<BuRepairPlanTaskEqu> equipments;

    @ApiModelProperty(value = "作业表单")
    private List<BuRepairPlanForms> forms;

}
