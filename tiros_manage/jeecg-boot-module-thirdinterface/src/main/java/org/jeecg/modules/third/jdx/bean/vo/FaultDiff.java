package org.jeecg.modules.third.jdx.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 故障差异
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/30
 */
@Data
@Accessors(chain = true)
public class FaultDiff {

    @ApiModelProperty(value = "架大修中的故障数（已提交的）")
    private Integer jdxFaultCount;
    
    @ApiModelProperty(value = "maximo中的故障数")
    private Integer maximoFaultCount;

    @ApiModelProperty(value = "双方共有的故障数")
    private Integer bothExistFaultCount;

    @ApiModelProperty(value = "双方共有的故障号")
    private List<String> bothExistFaultSnList;

    @ApiModelProperty(value = "仅存在于架大修的故障数")
    private Integer onlyJdxExistFaultCount;

    @ApiModelProperty(value = "仅存在于架大修的故障号")
    private List<String> onlyJdxExistFaultSnList;

    @ApiModelProperty(value = "仅存在于maximo中的故障数")
    private Integer onlyMaximoExistFaultCount;

    @ApiModelProperty(value = "仅存在于maximo中的故障号")
    private List<String> onlyMaximoExistFaultSnList;

}
