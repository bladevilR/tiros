package org.jeecg.modules.report.daily.bean;

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

/**
 * <p>
 * 生产日报-当日生产
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptDayReportWorkPlan对象", description = "生产日报-当日生产对象")
@TableName("bu_rpt_day_report_work_plan")
public class BuRptDayReportWorkPlan extends Model<BuRptDayReportWorkPlan> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属日报id")
    private String reportId;

    @ApiModelProperty(value = "班组 直接存储班组名称")
    private String groupName;

    @ApiModelProperty(value = "计划作业内容 来自该工班该天的列计划任务（工单任务）")
    private String planContent;

    @ApiModelProperty(value = "实际完成内容 来自对应的列计划任务完成情况（工单完成情况）")
    private String actContent;

    @ApiModelProperty(value = "是否按计划完成 0否1是")
    @Dict(dicCode = "bu_state")
    private Integer finished;

    @ApiModelProperty(value = "作业人数 统计工单的人数安排")
    private Integer peoples;

    @ApiModelProperty(value = "完成工时 来自工单的工时")
    private Float workTime;

    @ApiModelProperty(value = "备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
