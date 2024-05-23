package org.jeecg.modules.report.daily.bean;

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
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 生产日报
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptDayReport对象", description = "生产日报对象")
@TableName("bu_rpt_day_report")
public class BuRptDayReport extends Model<BuRptDayReport> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "日报名称 由：车号+填报日期 自动组成", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "周期 yyyy-MM-dd - yyyy-MM-dd", required = true)
    @NotBlank
    private String period;

    @ApiModelProperty(value = "车号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "运行里程 单位为KM（千米）", required = true)
    @NotNull
    private Double mileage;

    @ApiModelProperty(value = "日报日期 yyyy.MM.dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy.MM.dd")
    private Date reportDate;

    @ApiModelProperty(value = "作业天数 整个架修计划中的第几天")
    private Integer dayIndex;

    @ApiModelProperty(value = "填报车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "填报车间名字")
    private String workshop;

    @ApiModelProperty(value = "填写人员 直接选择保存名称", required = true)
    @NotBlank
    private String userName;

    @ApiModelProperty(value = "日报内容，json数据")
    private String reportContent;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "当日生成安排列表")
    @TableField(exist = false)
    private List<BuRptDayReportWorkPlan> workPlanList;

    @ApiModelProperty(value = "次日生成安排列表")
    @TableField(exist = false)
    private List<BuRptDayReportNextWork> nextWorkList;

    @ApiModelProperty(value = "委外件送修情况列表")
    @TableField(exist = false)
    private List<BuRptDayReportOutin> outinList;

    @ApiModelProperty(value = "当日故障列表")
    @TableField(exist = false)
    private List<BuRptDayReportFault> faultList;

    @ApiModelProperty(value = "工装故障情况")
    @TableField(exist = false)
    private String toolFault;

    @ApiModelProperty(value = "设备故障情况")
    @TableField(exist = false)
    private String assetFault;

    /**
     * 生产日报-工装/设备故障情况列表，仅用于后端
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    @JsonIgnore
    private List<BuRptDayReportOtherFault> otherFaultList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
