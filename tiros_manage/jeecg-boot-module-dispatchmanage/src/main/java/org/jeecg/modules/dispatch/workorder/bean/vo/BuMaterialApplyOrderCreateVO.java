package org.jeecg.modules.dispatch.workorder.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 发料工单创建vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-15
 */
@Data
@Accessors(chain = true)
public class BuMaterialApplyOrderCreateVO {

    @ApiModelProperty(value = "列计划id", required = true)
    @NotBlank
    private String planId;

    @ApiModelProperty(value = "班组id列表", required = true)
    @NotNull
    private List<String> groupIdList;

}
