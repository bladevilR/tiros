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

import java.io.Serializable;

/**
 * <p>
 * 生产日报-次日生产安排
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptDayReportNextWork对象", description = "生产日报-次日生产安排对象")
@TableName("bu_rpt_day_report_next_work")
public class BuRptDayReportNextWork extends Model<BuRptDayReportNextWork> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属日报id")
    private String reportId;

    @ApiModelProperty(value = "工班 直接存储工班名称")
    @TableField("group_name")
    private String groupName;

    @ApiModelProperty(value = "计划作业内容")
    private String workContent;

    @ApiModelProperty(value = "临时作业")
    private String tempWork;

    @ApiModelProperty(value = "跟踪事宜")
    private String trackContent;

    @ApiModelProperty(value = "备注")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
