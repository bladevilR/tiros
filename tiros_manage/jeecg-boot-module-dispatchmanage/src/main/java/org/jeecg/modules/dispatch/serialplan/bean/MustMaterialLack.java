package org.jeecg.modules.dispatch.serialplan.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author yyg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MustMaterialLack缺料对象", description = "")
public class MustMaterialLack {

    @ApiModelProperty(value = "物资编码")
    private String code;
    @ApiModelProperty(value = "物资名称")
    private String name;
    @ApiModelProperty(value = "需求量")
    private Double needAmount;
    @ApiModelProperty(value = "发放量")
    private Double receive;
    @ApiModelProperty(value = "缺少数量")
    private Double lack;
    @JsonIgnore
    private String materialTypeId;
}
