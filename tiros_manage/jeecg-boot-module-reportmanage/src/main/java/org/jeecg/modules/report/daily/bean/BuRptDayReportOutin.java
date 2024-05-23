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
 * 生产日报-委外件送修情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptDayReportOutin对象", description = "生产日报-委外件送修情况对象")
@TableName("bu_rpt_day_report_outin")
public class BuRptDayReportOutin extends Model<BuRptDayReportOutin> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属日报id")
    private String reportId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "设备类型名称")
    private String assetTypeName;

    @ApiModelProperty(value = "送修情况 填写设备的送修日期和数量，来自对应的出厂单")
    private String sendDesc;

    @ApiModelProperty(value = "返厂情况 填写设备的返厂日期和数量，来自对应的返厂单")
    private String receiveDesc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
