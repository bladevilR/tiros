package org.jeecg.modules.material.stock.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 3级库位对应批次号选择vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-17
 */
@Data
@Accessors(chain = true)
public class BuMaterialStockTradeNo {

    @ApiModelProperty(value = "3级库存id")
    private String Level3StockId;

    @ApiModelProperty(value = "3级库位id")
    private String Level3WarehouseId;

    @ApiModelProperty(value = "3级库位名称")
    private String Level3WarehouseName;

    @ApiModelProperty(value = "批次号")
    private String tradeNo;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

}
