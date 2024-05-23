package org.jeecg.modules.third.jdx.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 工单物料价格为0信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-02-28
 */
@Data
@Accessors(chain = true)
public class PriceZero {

    private String orderId;
    private String orderCode;
    private String orderName;
    private Integer orderStatus;
    private String orderMaterialActId;
    private String groupStockId;
    private String assignDetailId;
    private String materialTypeId;
    private String tradeNo;
    private String ebsWhCode;
    private String ebsWhChildCode;

    private BigDecimal currentMaterialTypePrice;

    private BigDecimal oldOrderMaterialActPrice;
    private BigDecimal oldGroupStockPrice;
    private BigDecimal oldAssignDetailPrice;

    private BigDecimal newOrderMaterialActPrice;
    private BigDecimal newGroupStockPrice;
    private BigDecimal newAssignDetailPrice;

    private BigDecimal currentMaximoPrice;

    private String moreStockPriceZeroInfo;
    private String moreGroupPriceZeroInfo;

}
