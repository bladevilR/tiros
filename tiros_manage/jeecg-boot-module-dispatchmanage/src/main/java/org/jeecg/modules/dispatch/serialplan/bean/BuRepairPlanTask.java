package org.jeecg.modules.dispatch.serialplan.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanForms;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanTaskForms;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划任务
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRepairPlanTask对象", description = "列计划任务")
@TableName("bu_repair_plan_task")
public class BuRepairPlanTask extends Model<BuRepairPlanTask> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属列计划id", required = true)
    @NotBlank
    private String planId;

    @ApiModelProperty(value = "上级任务id 为null表示顶级任务")
    private String parentId;

    @ApiModelProperty(value = "序号")
    private Integer taskNo;

    @ApiModelProperty(value = "任务WBS编码")
    private String taskWbs;

    @ApiModelProperty(value = "任务名称", required = true)
    @NotBlank
    private String taskName;

    @ApiModelProperty(value = "关键任务 0否1是  字典dicCode=bu_state", required = true, notes = "只有当关键作业任务节点未按计划完成并影响后续整体列计划实施进度时，调度发起修改")
    @Dict(dicCode = "bu_state")
    @NotEmpty
    private Integer important;

    @ApiModelProperty(value = "委外任务 0否1是  字典dicCode=bu_state", required = true)
    @Dict(dicCode = "bu_state")
    @NotEmpty
    private Integer outsource;

    @ApiModelProperty(value = "作业手段 1目测2清洁3操作4检测5测量  字典dicCode=bu_regu_method", required = true)
    @Dict(dicCode = "bu_regu_method")
    @NotEmpty
    private Integer method;

    @ApiModelProperty(value = "作业所属系统id", notes = "设备类型结构表中的系统或者子系统id")
    private String systemId;

    @ApiModelProperty(value = "作业设备类型", notes = "来自设备类型结构表的部件或者子部件id")
    private String assetTypeId;

    @ApiModelProperty(value = "工时", required = true, notes = "单位小时，1天按8小时算")
    @NotEmpty
    private Double workTime;

    @ApiModelProperty(value = "进度")
    private Double progress;

    @ApiModelProperty(value = "工期", notes = "单位天")
    private Double duration;

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

    @ApiModelProperty(value = "前置任务ids", notes = "前置任务id，可以多个，逗号分隔")
    private String predecessor;

    @ApiModelProperty(value = "工期序号", required = true, notes = "相对于基准日期是第几天，如基准日期是2020-01-01，那么工期2020-01-01则表示计划第1天 day_index 为 1，2020-01-02 表示计划第2天 day_index 为 2")
    @NotEmpty
    private Integer dayIndex;

    @ApiModelProperty(value = "作业工班id")
    private String workGroupId;

    @ApiModelProperty(value = "任务状态  字典dictCode=bu_task_status")
    @Dict(dicCode = "bu_task_status")
    private Integer status;

    @ApiModelProperty(value = "备注描述")
    private String remark;

    @ApiModelProperty(value = "是否生成工单  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer genOrder;

    @ApiModelProperty(value = "是否已生成工单  字典dictCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer hasGen;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

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
    @TableField(exist = false)
    private String systemName;

    @ApiModelProperty(value = "作业设备类型名称")
    @TableField(exist = false)
    private String assetTypeName;

    @ApiModelProperty(value = "工班名称")
    @TableField(exist = false)
    private String workGroupName;

    @ApiModelProperty(value = "停靠轨道id")
    @TableField(exist = false)
    private String trackId;

    @ApiModelProperty(value = "停靠轨道名称")
    @TableField(exist = false)
    private String trackName;

    @ApiModelProperty(value = "是否有子节点")
    @TableField(exist = false)
    private Boolean hasChildren;

    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    private List<BuRepairPlanTask> children;

    @ApiModelProperty(value = "父节点wbs")
    @TableField(exist = false)
    @JsonIgnore
    private String parentWbs;

    @ApiModelProperty(value = "关联规程")
    @TableField(exist = false)
    private List<BuRepairTaskRegu> repairPlanReguInfo;

    @ApiModelProperty(value = "作业指导书明细")
    @TableField(exist = false)
    private List<BuRepairTaskBookStep> bookSteps;

    @ApiModelProperty(value = "特种设备需求")
    @TableField(exist = false)
    private List<BuRepairPlanSpeEq> specAssets;

    @ApiModelProperty(value = "作业工位")
    @TableField(exist = false)
    private List<BuRepairTaskWorkstation> workstations;

    @ApiModelProperty(value = "物料")
    @TableField(exist = false)
    private List<BuRepairTaskMaterial> materials;

    @ApiModelProperty(value = "工器具")
    @TableField(exist = false)
    private List<BuRepairTaskTool> tools;

    @ApiModelProperty(value = "所需人员")
    @TableField(exist = false)
    private List<BuRepairTaskStaffRequire> persons;

    @ApiModelProperty(value = "必换件")
    @TableField(exist = false)
    private List<BuRepairTaskMustReplace> mustReplaces;

    @ApiModelProperty(value = "前置任务")
    @TableField(exist = false)
    private List<BuRepairPlanTaskPre> taskPres;

    @ApiModelProperty(value = "目标设备")
    @TableField(exist = false)
    private List<BuRepairPlanTaskEqu> equipments;

    @ApiModelProperty(value = "工艺文件-来自关联的规程明细的工艺文件")
    @TableField(exist = false)
    private List<BuRepairReguTechFile> techFiles;

    @ApiModelProperty(value = "任务关联表单")
    @TableField(exist = false)
    private List<BuRepairPlanTaskForms> forms;

//    @ApiModelProperty(value = "任务停靠")
//    @TableField(exist = false)
//    @JsonIgnore
//    private List<BuRepairTaskTrack> tracks;

    /**
     * 用于更新状态
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Integer orderTaskStatus;

    /**
     * 用于更新状态
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Date orderTaskFinishTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
