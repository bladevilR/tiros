package org.jeecg.modules.dispatch.specassetplan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 特种设备月份使用情况
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-04
 */
@Data
@Accessors(chain = true)
public class BuSpecAssetMonthUsageQueryVO {

    @ApiModelProperty(value = "年", required = true)
    @NotNull
    private Integer year;

    @ApiModelProperty(value = "月", required = true)
    @NotNull
    private Integer month;

    @ApiModelProperty(value = "特种设备id", required = true)
    @NotBlank
    private String specAssetId;

}
