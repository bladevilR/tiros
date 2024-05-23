package org.jeecg.modules.produce.summary.bean.outsource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 委外部件验收问题-明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/18
 */
@Data
@Accessors(chain = true)
public class InProblem {

    @ApiModelProperty(value = "项目")
    private String itemName;

    @ApiModelProperty(value = "厂商短名称")
    private String supplierShortName;

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "已处理数")
    private Integer handled;

    @ApiModelProperty(value = "跟踪数")
    private Integer tracking;

}
