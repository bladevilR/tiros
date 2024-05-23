package org.jeecg.modules.material.pallet.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuMaterialAssignDetailVO {
    @ApiModelProperty(value = "物资编码")
    private  String materialCode;
    @ApiModelProperty(value = "物资名称")
    private  String materialName;
    @ApiModelProperty(value = "物资数量")
    private  Double amount;

}
