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

/**
 * <p>
 * 生产日报-工装/设备故障情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptDayReportOtherFault对象", description = "生产日报-工装/设备故障情况对象")
@TableName("bu_rpt_day_report_other_fault")
public class BuRptDayReportOtherFault extends Model<BuRptDayReportOtherFault> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属日报id")
    private String reportId;

    @ApiModelProperty(value = "故障类型 1工装故障情况2设备故障情况")
    private Integer faultType;

    @ApiModelProperty(value = "故障描述")
    private String faultDesc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
