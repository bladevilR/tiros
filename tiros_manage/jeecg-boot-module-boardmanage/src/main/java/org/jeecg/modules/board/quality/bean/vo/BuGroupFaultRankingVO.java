package org.jeecg.modules.board.quality.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 班组发现故障排名
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class BuGroupFaultRankingVO {

    @ApiModelProperty("序号")
    private Integer order;

    @ApiModelProperty("工班")
    private String groupName;

    @ApiModelProperty("上月数")
    private Integer latestMonth;

    @ApiModelProperty("本月数")
    private Integer currentMonth;

    @ApiModelProperty("提升数")
    private Integer increase;

}
