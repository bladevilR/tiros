package org.jeecg.modules.group.measurealert.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 测量预警修复关闭VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/13
 */
@Data
public class BuWorkMeasureAlertCloseVO {

    @ApiModelProperty(value = "测量预警id",required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "新的测量值",required = true)
    @NotNull
    private Double newValue;

    @ApiModelProperty(value = "备注")
    private String remark;

}
