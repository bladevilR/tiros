package org.jeecg.modules.produce.trainhistory.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 架修周期
 * 表示车辆的架修周期，在接车流程审批完成后，自动创建，如果已存在同样的车辆了，则将填写该记录的结束日期，并创建一条新的记录。
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuBaseTrainRepairPeriod对象", description = "架修周期")
@TableName("bu_base_train_repair_period")
public class BuBaseTrainRepairPeriod extends Model<BuBaseTrainRepairPeriod> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车辆id")
    private String trainId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "开始日期yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "结束日期yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end;

    @ApiModelProperty(value = "质保结束日期yyyy-MM-dd 根据架大修质保期限自动计算")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lifeDate;

    @ApiModelProperty(value = "架修年份")
    private Integer year;

    @ApiModelProperty(value = "架修序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "修程id")
    private String repairProgram;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;


    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String repairProgramName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
