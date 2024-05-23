package org.jeecg.modules.board.progress.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 列计划班组工单故障填写统计
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bu_rpt_plan_group_statistic")
@ApiModel(value = "BuRptPlanGroupStatistic对象", description = "")
public class BuRptPlanGroupStatistic extends Model<BuRptPlanGroupStatistic> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "总任务数")
    private Integer taskTotal;

    @ApiModelProperty(value = "已下发任务数")
    private Integer taskIssued;

    @ApiModelProperty(value = "已暂停任务数")
    private Integer taskSuspended;

    @ApiModelProperty(value = "已关闭任务数")
    private Integer taskClosed;

    @ApiModelProperty(value = "已提交任务数")
    private Integer taskSubmitted;

    @ApiModelProperty(value = "完成比率")
    private Double taskFinishPercent;

    @ApiModelProperty(value = "班组应填任务数")
    private Integer groupTaskNeedWrite;

    @ApiModelProperty(value = "班组已填任务数")
    private Integer groupTaskWrote;

    @ApiModelProperty(value = "班组已暂停任务数")
    private Integer groupTaskSuspended;

    @ApiModelProperty(value = "班组已关闭任务数")
    private Integer groupTaskClosed;

    @ApiModelProperty(value = "班组完成比率")
    private Double groupTaskFinishPercent;

    @ApiModelProperty(value = "发现故障数")
    private Integer faultFound;

    @ApiModelProperty(value = "解决故障数")
    private Integer faultHandled;

    @ApiModelProperty(value = "班组发现故障数")
    private Integer groupFaultFound;

    @ApiModelProperty(value = "班组解决故障数")
    private Integer groupFaultHandled;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "列计划名称")
    @TableField(exist = false)
    private String planName;

    @ApiModelProperty(value = "列计划开始时间")
    @TableField(exist = false)
    private Date planStartDate;

    @ApiModelProperty(value = "列计划进度状态 字典dictCode=bu_progress_status")
    @Dict(dicCode = "bu_progress_status")
    @TableField(exist = false)
    private Integer planProgressStatus;

    @ApiModelProperty(value = "列计划当前进度：为1~100的数量，列计划中当前已完成任务的工期，占所有任务工期的比例，后端程序自动计算")
    @TableField(exist = false)
    private Integer planProgress;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String groupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
