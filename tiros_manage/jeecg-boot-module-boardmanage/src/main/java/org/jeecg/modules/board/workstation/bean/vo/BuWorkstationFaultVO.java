package org.jeecg.modules.board.workstation.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工位故障信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuWorkstationFaultVO {

    @ApiModelProperty(value = "故障数量")
    private Integer total;

    @ApiModelProperty(value = "已处理故障数量")
    private Integer handled;

    @ApiModelProperty(value = "未处理故障数量")
    private Integer unhandled;

}
