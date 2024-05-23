package org.jeecg.modules.dispatch.specassetplan.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * 特种设备运用/保养计划查询VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/9
 */
@Data
public class BuSpecAssetPlanQueryVO {

    @ApiModelProperty(value = "计划类型 1运用计划2保养计划", required = true)
    @NotNull
    private Integer planType;

    @ApiModelProperty(value = "特种设备名称/编码")
    private String searchText;

    @ApiModelProperty(value = "特种设备状态 1正常 2维修 3报废 4送检 字典dictCode=bu_tools_status")
    private Integer status;

    @ApiModelProperty(value = "查询日期-开始")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty(value = "查询日期-结束")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

}
