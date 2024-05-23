package org.jeecg.modules.basemanage.tpplan.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 维修计划模版
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "复制信息")
public class BuTpRepairPlanCopyVO implements Serializable {

    @ApiModelProperty(value = "要复制的计划id", required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "复制计划名称 不传参为:原名称-复制")
    private String name;

}
