package org.jeecg.modules.board.cost.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OutsourceAssetPrice {
    private BigDecimal amount;
    private BigDecimal price;
}
