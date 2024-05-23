package org.jeecg.modules.report.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统故障统计
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/8
 */
@Data
@Accessors(chain = true)
public class FaultSysCountVO {

    @ApiModelProperty(value = "系统")
    private String sys;

    @ApiModelProperty(value = "架修期")
    private Integer repair;

    @ApiModelProperty(value = "质保期")
    private Integer warranty;

}
