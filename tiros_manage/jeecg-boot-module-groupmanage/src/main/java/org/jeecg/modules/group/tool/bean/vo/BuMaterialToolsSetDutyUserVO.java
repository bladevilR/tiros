package org.jeecg.modules.group.tool.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 工器具设置责任人员VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/19
 */
@Data
public class BuMaterialToolsSetDutyUserVO {

    @ApiModelProperty(value = "工器具id", required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "责任人员id 接口获取/tiros/sys/user/listByGroupId  app端接口/tiros/app/user/listByGroupId", required = true)
    @NotBlank
    private String userId;

}
