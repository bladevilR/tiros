package org.jeecg.modules.report.daily.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 生产日报-当日故障
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptDayReportFault对象", description = "生产日报-当日故障对象")
@TableName("bu_rpt_day_report_fault")
public class BuRptDayReportFault extends Model<BuRptDayReportFault> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属日报id")
    private String reportId;

    @ApiModelProperty(value = "报告时间")
    private Date faultTime;

    @ApiModelProperty(value = "报告人员 故障报告人员，直接存储用户名称")
    private String reportUser;

    @ApiModelProperty(value = "故障描述")
    private String faultDesc;

    @ApiModelProperty(value = "是否处理 0否1是")
    private Integer isHandle;

    @ApiModelProperty(value = "责任工程师 直接存储名称")
    private String dutyEngineer;

    @ApiModelProperty(value = "处理详细情况")
    private String handleDesc;

    @ApiModelProperty(value = "备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
