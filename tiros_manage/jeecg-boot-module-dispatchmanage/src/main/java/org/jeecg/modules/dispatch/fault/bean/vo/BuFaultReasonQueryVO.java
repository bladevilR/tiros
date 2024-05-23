package org.jeecg.modules.dispatch.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障原因查询vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-29
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BuFaultReasonQueryVO extends BuFaultCodeQueryVO {

    @ApiModelProperty(value = "所属故障代码id")
    private String faultCodeId;

}
