package org.jeecg.modules.material.cost.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Set;

/**
 * <p>
 * 批次号数量差异
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/15
 */
@Data
@Accessors(chain = true)
public class TradeNoAmountDiff {

    private String tradeNo;
    private BigDecimal oldAmount;
    private BigDecimal newAmount;
    private BigDecimal newPrice;
    private BigDecimal diffAmount;
    private Set<String> relativeOrderMaterialActIdSet;

}
