package org.jeecg.modules.produce.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 架大修实效分析查询vo
 * 查询架修点前后故障数量对比
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
@Data
@Accessors(chain = true)
public class BuFaultTimeEffectQueryVO {

    @ApiModelProperty(value = "系统id")
    private String sysId;

    @ApiModelProperty(value = "故障等级 字典dictCode=bu_fault_level", notes = "1A 2B 3C 4D")
    @Dict(dicCode = "bu_fault_level")
    private Integer level;

    @ApiModelProperty(value = "线路id")
    private String lineId;

    @ApiModelProperty(value = "车号", required = true)
    @NotBlank
    private String trainNo;

    @ApiModelProperty(value = "周期类型 1=年 2=月 不传默认月")
    private Integer periodType;

    /**
     * 后端使用，获取到车辆架修周期中最近的时间点，左右12个月的时间
     */
    @ApiModelProperty(hidden = true)
    private Date startTime;

    /**
     * 后端使用，获取到车辆架修周期中最近的时间点，左右12个月的时间
     */
    @ApiModelProperty(hidden = true)
    private Date endTime;

}
