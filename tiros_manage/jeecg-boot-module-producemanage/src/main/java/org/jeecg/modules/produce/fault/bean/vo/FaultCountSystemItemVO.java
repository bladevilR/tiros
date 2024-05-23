package org.jeecg.modules.produce.fault.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障各系统分布统计条目VO
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/22
 */
@Data
@Accessors(chain = true)
public class FaultCountSystemItemVO {

    @ApiModelProperty(value = "系统名称")
    private String systemName;

    @ApiModelProperty(value = "故障数量")
    private Integer faultCount;

}
