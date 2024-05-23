package org.jeecg.modules.report.cost.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 物料消耗明细--总计
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/11
 */
@Data
@Accessors(chain = true)
public class BuCostDetailTotalVO {

    @ApiModelProperty(value = "系统数据列表")
    private List<BuCostDetailSystemVO> systemList;

    @ApiModelProperty(value = "合计")
    private BigDecimal totalCost;

}
