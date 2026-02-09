package org.jeecg.modules.dispatch.workorder.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yyg
 */
@Data
@ApiModel(value = "WorkOrderRelevanceInfo对象", description = "工单关联的信息")
public class WorkOrderRelevanceInfo implements Serializable {

    @ApiModelProperty(value = "工单id")
    private String id;

    @ApiModelProperty(value = "工单编号 ")
    private String orderCode;

    @ApiModelProperty(value = "工单名称")
    private String orderName;

    @ApiModelProperty(value = "1 计划工单 2 故障工单 3 临时工单,字典bu_order_type", required = true)
    @Dict(dicCode = "bu_order_type")
    @NotNull
    private Integer orderType;

    @ApiModelProperty(value = " 自动生成 0 否 1 是 字典bu_state")
    @Dict(dicCode = "bu_state")
    private Integer generate;

    @ApiModelProperty(value = "工单创建来源 1  自动生成  2 调度创建   3   班组创建")
    private Integer fromType;

    @ApiModelProperty(value = "关联生产通知单ID")
    private String productionNoticeId;

    @ApiModelProperty(value = "生产通知单号")
    private String productionNoticeNo;

    @ApiModelProperty(value = "生产通知单标题")
    private String productionNoticeTitle;

    @ApiModelProperty(value = "列计划id 根据列计划自动生成的工单填写")
    private String planId;

    @ApiModelProperty(value = "计划开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "计划结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date finishTime;

    @ApiModelProperty(value = "工单工期")
    private Integer duration;

    @ApiModelProperty(value = "作业班组 字典 bu_mtr_workshop_group,group_name,id", required = true)
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "班长", required = true)
    @NotBlank
    private String monitor;

    @ApiModelProperty(value = "所属车间,字典bu_mtr_workshop,name,id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "所属线路，字典bu_mtr_line,line_name,line_id", required = true)
    @NotBlank
    private String lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "作业车号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "实际开始时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actStart;

    @ApiModelProperty(value = "实际结束时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actFinish;

    @ApiModelProperty(value = "工单状态")
    @Dict(dicCode = "bu_order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "作业状态 0 未作业 1 作业中 2 作业完成 ,字典 bu_work_status")
    @Dict(dicCode = "bu_work_status")
    private Integer workStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "审批流程状态")
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    private String processCandidate;

    @ApiModelProperty(value = "下发时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date issuingTime;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务项目编码")
    private String fdProjectCode;

    @ApiModelProperty(value = "所属财务项目名称")
    private String fdProjectName;

    @ApiModelProperty(value = "所属财务任务编码")
    private String fdTaskCode;

    @ApiModelProperty(value = "所属财务任务名称")
    private String fdTaskName;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支编码")
    private String fdCostType;


    @ApiModelProperty(value = "列计划名称")
    private String planName;
    @ApiModelProperty(value = "修程id")
    private String repairProgramId;

    @ApiModelProperty(value = "任务")
    private List<BuWorkOrderTask> tasks;

    @ApiModelProperty(value = "物料")
    private List<BuWorkOrderMaterial> materials;

    @ApiModelProperty(value = "工器具")
    private List<BuWorkOrderTool> tools;

    @ApiModelProperty(value = "工位")
    private List<BuWorkOrderTaskWorkstation> workstations;

    @ApiModelProperty(value = "表单")
    private List<BuWorkOrderTaskFormInst> forms;

    @ApiModelProperty(value = "作业要求")
    private JobRequirement jobRequirement;

    @ApiModelProperty(value = "工艺文件")
    private List<BuWorkOrderTechFile> techFiles;

    @ApiModelProperty(value = "作业指导书明细")
    private List<BuWorkOrderBookStep> bookSteps;

    @ApiModelProperty(value = "工单附件列表")
    private List<BuWorkOrderAnnex> annexList;

    @ApiModelProperty(value = "目标设备")
    private List<BuWorkOrderTaskEqu> equipments;

}
