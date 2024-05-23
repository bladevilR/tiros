package org.jeecg.modules.board.quality.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障系统分布
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Data
@Accessors(chain = true)
public class BuCenterQualitySysFaultVO {

    @ApiModelProperty(value = "系统名称")
    private String sysName;

    @ApiModelProperty(value = "故障数")
    private Integer sum;

}
