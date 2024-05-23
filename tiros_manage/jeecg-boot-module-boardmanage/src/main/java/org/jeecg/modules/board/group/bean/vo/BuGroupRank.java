package org.jeecg.modules.board.group.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 班组排名
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Data
@Accessors(chain = true)
public class BuGroupRank {

    @ApiModelProperty(value = "故障提报排名")
    private List<BuGroupRankItem> reportFaultList;

    @ApiModelProperty(value = "工单提交排名")
    private List<BuGroupRankItem> orderSubmitList;

    @ApiModelProperty(value = "工单关闭排名")
    private List<BuGroupRankItem> orderCloseList;

}
