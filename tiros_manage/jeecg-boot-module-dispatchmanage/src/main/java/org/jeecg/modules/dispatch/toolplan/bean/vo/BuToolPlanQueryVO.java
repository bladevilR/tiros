package org.jeecg.modules.dispatch.toolplan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 工装运用/保养计划查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/9
 */
@Data
public class BuToolPlanQueryVO {

    @ApiModelProperty(value = "计划类型 1运用计划2保养计划", required = true)
    @NotNull
    private Integer planType;

    @ApiModelProperty(value = "工装名称")
    private String name;

    @ApiModelProperty(value = "工装编码(对应业务中的物资编码)")
    private String code;

    @ApiModelProperty(value = "工装状态 1正常 2维修 3报废 4送检 字典dictCode=bu_tools_status")
    private Integer status;

}
