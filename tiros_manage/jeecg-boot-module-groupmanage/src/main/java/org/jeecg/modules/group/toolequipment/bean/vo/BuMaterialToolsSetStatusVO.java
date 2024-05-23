package org.jeecg.modules.group.toolequipment.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 工装设置状态VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/19
 */
@Data
public class BuMaterialToolsSetStatusVO {

    @ApiModelProperty(value = "工装id", required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "状态 字典dictCode=bu_tools_status", required = true)
    @NotNull
    private Integer status;

}
