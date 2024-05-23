package org.jeecg.modules.tiros.rpt.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障统计bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
@Data
@Accessors(chain = true)
public class BuFaultRptBO {

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "车间id")
    private String workshopId;

    @ApiModelProperty(value = "公司id")
    private String companyId;

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

    @ApiModelProperty(value = "系统id")
    private String sysId;

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

}
