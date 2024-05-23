package org.jeecg.modules.basemanage.tpplan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.bean.PlusProjectGanttTask;
import org.jeecg.modules.basemanage.tpplan.bean.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 计划任务明细--VO,加入甘特图信息
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BuTpRepairPlanTaskVOGantt extends PlusProjectGanttTask {

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "所属计划模版id")
    private String planId;

    @ApiModelProperty(value = "为null表示顶级任务")
    private String taskParentId;

    @ApiModelProperty(value = "序号")
    private Integer taskNo;

    @ApiModelProperty(value = "任务WBS编码")
    private String taskWbs;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "0 否  1 是，只有当关键作业任务节点未按计划完成并影响后续整体列计划实施进度时，调度发起修改")
    @Dict(dicCode = "bu_state")
    private Integer important;

    @ApiModelProperty(value = "0 否 1 是,委外")
    @Dict(dicCode = "bu_state")
    private Integer outsource;

    @ApiModelProperty(value = "1 目测 2 清洁 3 操作  4 检测 5 测量")
    @Dict(dicCode = "bu_regu_method")
    private Integer method;

    @ApiModelProperty(value = "作业所属系统 ,设备类型结构表中的系统或者子系统id")
    private String systemId;

    @ApiModelProperty(value = "作业设备类型,来自设备类型结构表的部件或者子部件id")
    private String assetTypeId;

    @ApiModelProperty(value = "工时,单位小时，1天按8小时算")
    private Double workTime;

    @ApiModelProperty(value = "工期,单位天")
    private Double taskDuration;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "任务的开始时间和结束时间是基于计划的基准时间计算出来的，在转成具体的列计划时，将根据时间开始日期进行替换")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间")
    private Date finishTime;

    @ApiModelProperty(value = "工期序号,相对于基准日期是第几天，如基准日期是2020-01-01，那么工期2020-01-01则表示计划第1天 day_index 为 1，2020-01-02 表示计划第2天 day_index 为 2")
    private Integer dayIndex;

    @ApiModelProperty(value = "来自组织里的工班ID")
    private String workGroupId;

    @ApiModelProperty(value = "任务描述")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "是否生成工单")
    @Dict(dicCode = "bu_state")
    private Integer genOrder;

    @ApiModelProperty(value = "任务安全预想")
    private String safeNotice;


    @ApiModelProperty(value = "作业所属系统名称")
    private String systemName;

    @ApiModelProperty(value = "作业设备类型名称")
    private String assetTypeName;

    @ApiModelProperty(value = "工班名称")
    private String workGroupName;

    @ApiModelProperty(value = "停靠轨道id")
    private String trackId;

    @ApiModelProperty(value = "停靠轨道名称")
    private String trackName;

    @ApiModelProperty(value = "是否有子节点")
    private Boolean hasChildren; 

    @ApiModelProperty(value = "关联规程")
    private List<BuTpRepairPlanReguInfo> repairPlanReguInfo;

    @ApiModelProperty(value = "作业工位")
    private List<BuTpRepairPlanWorkstation> workstations;

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

    @ApiModelProperty(value = "前置任务")
    private List<BuTpRepairPlanTaskPre> taskPres;

    @ApiModelProperty(value = "目标设备")
    private List<BuTpRepairPlanTaskEqu> equipments;

    @ApiModelProperty(value = "任务关联表单")
    private List<BuTpRepairPlanTaskForms> forms;

}
