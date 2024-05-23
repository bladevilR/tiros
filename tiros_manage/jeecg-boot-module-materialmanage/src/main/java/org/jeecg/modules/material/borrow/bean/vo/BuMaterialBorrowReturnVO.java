package org.jeecg.modules.material.borrow.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuMaterialBorrowReturnVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "归还类型")
    private Integer returnType;
}
