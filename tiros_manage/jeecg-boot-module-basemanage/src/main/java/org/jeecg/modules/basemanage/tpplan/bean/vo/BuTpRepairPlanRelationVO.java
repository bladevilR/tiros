package org.jeecg.modules.basemanage.tpplan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.basemanage.tpplan.bean.*;

import java.util.List;

/**
 * <p>
 * 计划模板关联信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/5
 */
@Data
@Accessors(chain = true)
public class BuTpRepairPlanRelationVO {

    @ApiModelProperty(value = "计划id")
    private String id;

    @ApiModelProperty(value = "关联规程")
    private List<BuTpRepairPlanReguInfo> repairPlanReguInfo;

//    @ApiModelProperty(value = "作业工位")
//    private List<BuTpRepairPlanWorkstation> workstations;

    @ApiModelProperty(value = "物料")
    private List<BuTpRepairPlanMaterial> materials;

    @ApiModelProperty(value = "工器具")
    private List<BuTpRepairPlanTool> tools;

    @ApiModelProperty(value = "所需人员")
    private List<BuTpRepairPlanPerson> persons;

    @ApiModelProperty(value = "必换件")
    private List<BuTpRepairPlanMustReplace> mustReplaces;

    @ApiModelProperty(value = "作业指导书明细")
    private List<BuTpRepairPlanBookStep> bookSteps;

    @ApiModelProperty(value = "特种设备需求")
    private List<BuTpRepairPlanSpeEq> specAssets;

    @ApiModelProperty(value = "目标设备")
    private List<BuTpRepairPlanTaskEqu> equipments;

}
