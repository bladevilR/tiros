package org.jeecg.modules.dispatch.workorder.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 班组库存占用bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/1
 */
@Data
@Accessors(chain = true)
public class GroupStockUsed {
    private String materialTypeId;
    private String trainNo;
    private String tradeNo;
    private String orderCode;
    private BigDecimal amount;
    /**
     * 1预占用 2实际消耗占用
     */
    private Integer usedType;
}
