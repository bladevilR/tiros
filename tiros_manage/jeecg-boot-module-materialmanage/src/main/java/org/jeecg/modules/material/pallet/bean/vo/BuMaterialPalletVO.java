package org.jeecg.modules.material.pallet.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuMaterialPalletVO {
    private String id;
    @ApiModelProperty(value = "托盘名称")
    private String palletName;
    @ApiModelProperty(value = "托盘编码")
    private String palletCode;
    @ApiModelProperty(value = "物资列表")
    private List<BuMaterialAssignDetailVO> materialList;

}
