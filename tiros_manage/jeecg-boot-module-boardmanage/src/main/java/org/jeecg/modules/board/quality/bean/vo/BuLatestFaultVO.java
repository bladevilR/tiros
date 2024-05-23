package org.jeecg.modules.board.quality.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 最新故障及统计信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/17
 */
@Data
@Accessors(chain = true)
public class BuLatestFaultVO {

    @ApiModelProperty(value = "当天新增数")
    private Integer todayAdded;

    @ApiModelProperty(value = "当前共发现数")
    private Integer total;

    @ApiModelProperty(value = "已解决数")
    private Integer resolved;

    @ApiModelProperty(value = "未解决数")
    private Integer unresolved;

    @ApiModelProperty(value = "最新提报的故障信息（4条）")
    private List<BuLatestFaultItemVO> latestFaults;

}
