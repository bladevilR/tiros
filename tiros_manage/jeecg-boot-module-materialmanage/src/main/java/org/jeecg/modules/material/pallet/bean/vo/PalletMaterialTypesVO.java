package org.jeecg.modules.material.pallet.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 设置托盘物资类型vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-07
 */
@Data
@Accessors(chain = true)
public class PalletMaterialTypesVO {

    @ApiModelProperty(value = "托盘ids，多个逗号分隔", required = true)
    @NotBlank
    private String palletIds;

    @ApiModelProperty(value = "物资类型ids，多个逗号分隔", required = true)
    @NotBlank
    private String materialTypes;

}
