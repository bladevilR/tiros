package org.jeecg.modules.report.fault.bean;

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
 * 故障统计-中间表-车辆维度
 * 以车辆为基本统计单位，后台程序，每天自动统计所有的故障数据写入对应车辆
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptBoardTrainFault对象", description = "以车辆为基本统计单位，后台程序，每天自动统计所有的故障数据写入对应车辆")
@TableName("bu_rpt_board_train_fault")
public class BuRptBoardTrainFault extends Model<BuRptBoardTrainFault> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属周期id 来自架修周期记录表的id，获取对应车辆的最后一条记录id")
    private String repairPeriod;

    @ApiModelProperty(value = "所属车辆id")
    private String trainId;

    @ApiModelProperty(value = "车辆号")
    private String trainNo;

    @ApiModelProperty(value = "架修序号")
    private Integer repairIndex;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "月份 这里为列计划的完成时间")
    private Integer month;

    @ApiModelProperty(value = "修程id")
    private String programId;

    @ApiModelProperty(value = "架修期故障数")
    private Integer sum1;

    @ApiModelProperty(value = "架修期AB故障")
    private Integer sum2;

    @ApiModelProperty(value = "质保期故障")
    private Integer sum3;

    @ApiModelProperty(value = "质保期正线故障")
    private Integer sum4;

    @ApiModelProperty(value = "质保期AB故障")
    private Integer sum5;

    @ApiModelProperty(value = "质保期有责故障")
    private Integer sum6;

    @ApiModelProperty(value = "出保期故障")
    private Integer sum7;

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;


    @ApiModelProperty(value = "所属车辆段名称")
    @TableField(exist = false)
    private String depotName;

    @ApiModelProperty(value = "修程名称")
    @TableField(exist = false)
    private String programName;

    @ApiModelProperty(value = "修程类型")
    @TableField(exist = false)
    private Integer programType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
