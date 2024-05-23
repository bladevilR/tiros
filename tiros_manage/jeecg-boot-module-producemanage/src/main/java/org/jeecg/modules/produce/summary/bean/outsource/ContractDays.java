package org.jeecg.modules.produce.summary.bean.outsource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class ContractDays {

    @ApiModelProperty(value = "车号")
    private String trainNo;

    @ApiModelProperty(value = "项目")
    private String itemName;

    @ApiModelProperty(value = "天数")
    private Integer days;

}
