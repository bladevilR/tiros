package org.jeecg.modules.dispatch.exchange.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 修改交接车记录备注 vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/3/24
 */
@Data
@Accessors(chain = true)
public class BuRepairExchangeRemarkSaveVO {

    @ApiModelProperty("交接车id")
    @NotBlank
    private String id;

    @ApiModelProperty("备注")
    private String remark;

}
