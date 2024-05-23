package org.jeecg.modules.third.jdx.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 工单差异
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/30
 */
@Data
@Accessors(chain = true)
public class OrderDiff {

    @ApiModelProperty(value = "架大修中的工单数（已核实的）")
    private Integer jdxOrderCount;

    @ApiModelProperty(value = "maximo中的工单数")
    private Integer maximoOrderCount;

    @ApiModelProperty(value = "双方共有的工单数")
    private Integer bothExistOrderCount;

    @ApiModelProperty(value = "双方共有的工单号")
    private List<String> bothExistOrderCodeList;

    @ApiModelProperty(value = "仅存在于架大修的工单数")
    private Integer onlyJdxExistOrderCount;

    @ApiModelProperty(value = "仅存在于架大修的工单号")
    private List<String> onlyJdxExistOrderCodeList;

    @ApiModelProperty(value = "仅存在于maximo中的工单数")
    private Integer onlyMaximoExistOrderCount;

    @ApiModelProperty(value = "仅存在于maximo中的工单号")
    private List<String> onlyMaximoExistOrderCodeList;

}
