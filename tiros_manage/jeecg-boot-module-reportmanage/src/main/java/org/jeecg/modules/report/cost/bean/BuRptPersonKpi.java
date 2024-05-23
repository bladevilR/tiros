package org.jeecg.modules.report.cost.bean;

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
 * 个人绩效
 * 每天自动累计个人绩效，存在记录则更新，不存在则创建一条
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuRptPersonKpi对象", description = "个人绩效 每天自动累计个人绩效，存在记录则更新，不存在则创建一条")
@TableName("bu_rpt_person_kpi")
public class BuRptPersonKpi extends Model<BuRptPersonKpi> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员id")
    private String userId;

    @ApiModelProperty(value = "人员姓名")
    private String userName;

    @ApiModelProperty(value = "工号")
    private String workNo;

    @ApiModelProperty(value = "车辆段id")
    private String depotId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "班组id")
    private String groupId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "记录日期 yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rptDate;

    @ApiModelProperty(value = "年")
    private Integer year;

    @ApiModelProperty(value = "月 这里为列计划的完成时间")
    private Integer month;

    @ApiModelProperty(value = "总工时")
    private Integer totalTime;

    @ApiModelProperty(value = "维修工时")
    private Integer repairTime;

    @ApiModelProperty(value = "故障工时")
    private Integer faultTime;

    @ApiModelProperty(value = "故障次数总计 为发现故障数+处理故障数")
    private Integer totalHappen;

    @ApiModelProperty(value = "发现故障数")
    private Integer faultAmount;

    @ApiModelProperty(value = "处理故障数")
    private Integer handleAmount;

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "公司id")
    private String companyId;


    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "车号")
    @TableField(exist = false)
    private String trainNo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
