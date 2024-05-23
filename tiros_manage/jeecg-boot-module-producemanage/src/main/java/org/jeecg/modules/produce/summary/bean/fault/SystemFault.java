package org.jeecg.modules.produce.summary.bean.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 各系统故障
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class SystemFault {

    @ApiModelProperty(value = "系统")
    private String systemName;

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "A/B类数")
    private Integer levelAB;

    @ApiModelProperty(value = "未处理数")
    private Integer unhandled;

}
