package org.jeecg.modules.dispatch.workorder.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrder对象", description = "")
@TableName("bu_work_order")
public class BuWorkOrder extends Model<BuWorkOrder> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "工单编号")
    private String orderCode;

    @ApiModelProperty(value = "工单名称", required = true)
    @NotBlank
    private String orderName;

    @ApiModelProperty(value = "工单类型 字典dicCode=bu_order_type", required = true)
    @Dict(dicCode = "bu_order_type")
    @NotNull
    private Integer orderType;

    @ApiModelProperty(value = "是否自动生成 字典dicCode=bu_state")
    @Dict(dicCode = "bu_state")
    private Integer generate;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "计划开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "计划结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;

    @ApiModelProperty(value = "工单工期")
    private Integer duration;

    @ApiModelProperty(value = "作业班组id")
    private String groupId;

    @ApiModelProperty(value = "班长id")
    private String monitor;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "作业车号")
    private String trainNo;

    @ApiModelProperty(value = "实际开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actStart;

    @ApiModelProperty(value = "实际结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actFinish;

    @ApiModelProperty(value = "工单状态 字典dicCode=bu_order_status")
    @Dict(dicCode = "bu_order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "作业状态 字典dicCode=bu_work_status")
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

    @ApiModelProperty(value = "下发时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date issuingTime;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支编码")
    private String fdCostType;

    @ApiModelProperty(value = "工单创建来源 1自动生成 2调度创建 3班组创建")
    private Integer fromType;

    @ApiModelProperty(value = "工单暂停前状态 字典dicCode=bu_order_status")
    @Dict(dicCode = "bu_order_status")
    private Integer suspendStatus;

    @ApiModelProperty(value = "暂停时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date suspendTime;

    @ApiModelProperty(value = "激活时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date activeTime;

    @ApiModelProperty(value = "maximo工单id 工单同步后由maximo设置，用于设备更换跳转")
    private Long maximoWorkOrderId;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "列计划名称")
    @TableField(exist = false)
    private String planName;

    @ApiModelProperty(value = "车辆段id")
    @TableField(exist = false)
    private String depotId;

    @ApiModelProperty(value = "审批流程状态")
    @TableField(exist = false)
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    @TableField(exist = false)
    private String processCandidate;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "所属财务项目编码")
    @TableField(exist = false)
    private String fdProjectCode;

    @ApiModelProperty(value = "所属财务项目名称")
    @TableField(exist = false)
    private String fdProjectName;

    @ApiModelProperty(value = "所属财务任务编码")
    @TableField(exist = false)
    private String fdTaskCode;

    @ApiModelProperty(value = "所属财务任务名称")
    @TableField(exist = false)
    private String fdTaskName;

    @ApiModelProperty(value = "作业任务")
    @TableField(exist = false)
    private List<BuWorkOrderTask> tasks;

    @ApiModelProperty(value = "作业工位")
    @TableField(exist = false)
    private List<BuWorkOrderTaskWorkstation> workstations;

    @ApiModelProperty(value = "作业物资")
    @TableField(exist = false)
    private List<BuWorkOrderMaterial> materials;

    @ApiModelProperty(value = "作业工具")
    @TableField(exist = false)
    private List<BuWorkOrderTool> tools;

    @ApiModelProperty(value = "工单表单")
    @TableField(exist = false)
    private List<BuWorkOrderTaskFormInst> forms;

    @ApiModelProperty(value = "作业工艺文件")
    @TableField(exist = false)
    private List<BuWorkOrderTechFile> techFiles;

    @ApiModelProperty(value = "作业指导书明细")
    @TableField(exist = false)
    private List<BuWorkOrderBookStep> bookSteps;

    @ApiModelProperty(value = "工单附件列表")
    @TableField(exist = false)
    private List<BuWorkOrderAnnex> annexList;

    @ApiModelProperty(value = "目标设备")
    @TableField(exist = false)
    private List<BuWorkOrderTaskEqu> equipments;

    @ApiModelProperty(value = "是否启动流程 true/false 默认false")
    @TableField(exist = false)
    private Boolean startFlow;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "线路编号")
    @TableField(exist = false)
    private String lineNum;

    @ApiModelProperty(value = "班长工号")
    @TableField(exist = false)
    private String monitorWorkNo;

    @ApiModelProperty(value = "作业资产设备编码")
    @TableField(exist = false)
    private String assetCode;

    @ApiModelProperty(value = "是否逾期,bu_state")
    @TableField(exist = false)
    @Dict(dicCode = "bu_state")
    private Integer overdue;

    @ApiModelProperty(value = "按钮操作")
    @TableField(exist = false)
    private Integer operator;

    @ApiModelProperty(value = "物料消耗发送时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date syncTime;

    @ApiModelProperty(value = "物料消耗返回状态")
    @TableField(exist = false)
    private String syncResult;

    @ApiModelProperty(value = "物料消耗返回状态时间 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date syncResultTime;

    @ApiModelProperty(value = "流程操作属性")
    @TableField(exist = false)
    private String wfAttrKey;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
