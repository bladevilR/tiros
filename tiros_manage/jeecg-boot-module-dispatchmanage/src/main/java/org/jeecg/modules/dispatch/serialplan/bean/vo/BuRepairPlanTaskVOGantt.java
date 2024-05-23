package org.jeecg.modules.dispatch.serialplan.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.bean.PlusProjectGanttTask;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanForms;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanTaskForms;
import org.jeecg.modules.dispatch.serialplan.bean.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划任务--VO,加入甘特图信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BuRepairPlanTaskVOGantt extends PlusProjectGanttTask {

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "所属列计划id")
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

    @ApiModelProperty(value = "进度")
    private Double progress;

    @ApiModelProperty(value = "工期,单位天")
    private Double taskDuration;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd  任务的开始时间和结束时间是基于计划的基准时间计算出来的，在转成具体的列计划时，将根据时间开始日期进行替换")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;

    @ApiModelProperty(value = "实际开始 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actStart;

    @ApiModelProperty(value = "实际结束 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actFinish;

    @ApiModelProperty(value = "为前置任务id，可以多个，逗号分隔")
    private String predecessor;

    @ApiModelProperty(value = "工期序号,相对于基准日期是第几天，如基准日期是2020-01-01，那么工期2020-01-01则表示计划第1天 day_index 为 1，2020-01-02 表示计划第2天 day_index 为 2")
    private Integer dayIndex;

    @ApiModelProperty(value = "来自组织里的工班ID")
    private String workGroupId;

    @ApiModelProperty(value = " 任务状态：1 未开始 2 已完成 3 已暂停")
    @Dict(dicCode = "bu_task_status")
    private Integer status;

    @ApiModelProperty(value = "任务描述")
    private String remark;

    @ApiModelProperty(value = "是否生成工单")
    @Dict(dicCode = "bu_state")
    private Integer genOrder;

    @ApiModelProperty(value = "已生成工单？0 否 1 是")
    @Dict(dicCode = "bu_state")
    private Integer hasGen;

    @ApiModelProperty(value = "暂停前状态 字典dicCode=bu_task_status")
    @Dict(dicCode = "bu_task_status")
    private Integer suspendStatus;

    @ApiModelProperty(value = "暂停时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date suspendTime;

    @ApiModelProperty(value = "激活时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date activeTime;

    @ApiModelProperty(value = "任务安全预想")
    private String safeNotice;


    @ApiModelProperty(value = "作业所属系统名称")
    private String systemName;

    @ApiModelProperty(value = "作业设备类型名称")
    private String assetTypeName;

    @ApiModelProperty(value = "工班名称")
    private String workGroupName;

    @ApiModelProperty(value = "是否有子节点")
    private Boolean hasChildren; 

//    @ApiModelProperty(value = "子节点")
//    private List<BuRepairPlanTask> children;

    @ApiModelProperty(value = "停靠轨道id")
    private String trackId;

    @ApiModelProperty(value = "停靠轨道名称")
    private String trackName;

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

    @ApiModelProperty(value = "前置任务")
    private List<BuRepairPlanTaskPre> taskPres;

    @ApiModelProperty(value = "目标设备")
    private List<BuRepairPlanTaskEqu> equipments;

    @ApiModelProperty(value = "任务关联表单")
    @TableField(exist = false)
    private List<BuRepairPlanTaskForms> forms;

//    @ApiModelProperty(value = "任务停靠")
//    @JsonIgnore
//    private List<BuRepairTaskTrack> tracks;

}
