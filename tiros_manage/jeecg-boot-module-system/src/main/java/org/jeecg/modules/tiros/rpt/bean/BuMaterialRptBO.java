package org.jeecg.modules.tiros.rpt.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 物料统计bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
@Data
@Accessors(chain = true)
public class BuMaterialRptBO {

    @ApiModelProperty(value = "公司id")
    private String companyId;

    @ApiModelProperty(value = "所属车辆段id")
    private String depotId;

    @ApiModelProperty(value = "所属车间id")
    private String workshopId;

    @ApiModelProperty(value = "所属线路id")
    private String lineId;

    @ApiModelProperty(value = "所属周期id  来自架修周期记录表的id，获取对应车辆的最后一条记录id")
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

    @ApiModelProperty(value = "系统id")
    private String sysId;

    @ApiModelProperty(value = "修程id")
    private String programId;

    @ApiModelProperty(value = "必换件成本")
    private BigDecimal mustCost;

    @ApiModelProperty(value = "偶换件成本")
    private BigDecimal randomCost;

    @ApiModelProperty(value = "耗材成本")
    private BigDecimal consumeCost;

}
