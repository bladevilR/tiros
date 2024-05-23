package org.jeecg.modules.dispatch.plan.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * 年计划自动生成请求VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/27
 */
@Data
public class BuRepairPlanYearAutoGenerateVO {

    @ApiModelProperty(value = "计划年份", required = true)
    @NotNull
    private Integer year;

    @ApiModelProperty(value = "车间id", required = true)
    @NotBlank
    private String workshopId;

    @ApiModelProperty(value = "首列车架修时间 yyyy-MM-dd", required = true)
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstTime;

    @ApiModelProperty(value = "大修数", required = true)
    @NotNull
    private Integer hightAmount;

    @ApiModelProperty(value = "架修数", required = true)
    @NotNull
    private Integer middleAmount;

    @ApiModelProperty(value = "大修模版id")
    private String hightTpRepairPlanId;

    @ApiModelProperty(value = "架修模版id")
    private String middleTpRepairPlanId;

}
