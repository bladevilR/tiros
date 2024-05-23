package org.jeecg.modules.produce.plan.bean.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 工时总和bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/22
 */
@Data
@Accessors(chain = true)
public class WorkTimeSumBO {

    @ApiModelProperty(value = "列计划id")
    private String planId;

    @ApiModelProperty(value = "班组名称")
    private String groupName;

    @ApiModelProperty(value = "工位名称")
    private String workstationName;

    @ApiModelProperty(value = "工时之和")
    private BigDecimal sumWorkTime;

}
