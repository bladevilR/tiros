package org.jeecg.modules.board.progress.bean;

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
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "列计划对象", description = "列计划")
@TableName("bu_repair_plan")
public class BuRepairPlan extends Model<BuRepairPlan> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "修程类型id")
    private String repairProgramId;

    @ApiModelProperty(value = "接车记录id 交接车记录表中的id")
    private String exchangeId;

    @ApiModelProperty(value = "维修车辆号 从选择的交接车记录中获取")
    private String trainNo;

    @ApiModelProperty(value = "当前里程")
    private Double mileage;

    @ApiModelProperty(value = "计划模版id")
    private String planTemplateId;

    @ApiModelProperty(value = "开始日期")
    private Date startDate;

    @ApiModelProperty(value = "完成日期 用于作为计划模版的的任务开始日期")
    private Date finishDate;

    @ApiModelProperty(value = "实际开始 每次下发工单时，如果工单时自动生成的，每次都检查对应的列计划是否有实际开始日期，如果没有则将该工单的开始日期填入该列计划的实际开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actStart;

    @ApiModelProperty(value = "实际完成")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actFinish;

    @ApiModelProperty(value = "工期")
    private Integer duration;

    @ApiModelProperty(value = "状态  字典：bu_repair_plan_status")
    @Dict(dicCode = "bu_repair_plan_status")
    private Integer status;

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

    @ApiModelProperty(value = "进度状态 字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    private Integer progressStatus;

    @ApiModelProperty(value = "当前进度：为1~100的数量，列计划中当前已完成任务的工期，占所有任务工期的比例，后端程序自动计算")
    private Integer progress;

    @ApiModelProperty(value = "实际工期")
    private Integer actDuration;

    @ApiModelProperty(value = "车辆序号，来自交接车记录明细中的序号")
    private Integer trainIndex;

    @ApiModelProperty(value = "规程id")
    private String reguId;

    @ApiModelProperty(value = "所属财务项目")
    private String fdProject;

    @ApiModelProperty(value = "所属财务任务")
    private String fdTask;

    @ApiModelProperty(value = "所属财务开支编码")
    private String fdCostType;

    @ApiModelProperty(value = "车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "暂停时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date suspendTime;

    @ApiModelProperty(value = "激活时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date activateTime;

    @ApiModelProperty(value = "暂停前进度状态")
    @Dict(dicCode = "bu_progress_status")
    private Integer suspendedProgressStatus;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "是否历史数据")
    @Dict(dicCode = "bu_state")
    private Integer historyData;


    @ApiModelProperty(value = "审批流程状态")
    @TableField(exist = false)
    private String wfStatus;

    @ApiModelProperty(value = "流程当前处理人")
    @TableField(exist = false)
    private String processCandidate;

    @ApiModelProperty(value = "车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "车间名称")
    @TableField(exist = false)
    private String workshopName;

    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "修程类型名称")
    @TableField(exist = false)
    private String repairProgramName;

    @ApiModelProperty(value = "计划模板名称")
    @TableField(exist = false)
    private String planTemplateName;

    @ApiModelProperty(value = "规程名称")
    @TableField(exist = false)
    private String reguName;

    @ApiModelProperty(value = "任务集合")
    @TableField(exist = false)
    private List<BuRepairPlanTask> tasks;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
