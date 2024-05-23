package org.jeecg.modules.tiros.custom.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 实际消耗班组库存不足-物料
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-11
 */
@Data
@Accessors(chain = true)
public class ActConsumeMaterialLack {

    private String materialTypeId;
    private String type;
    private BigDecimal lack;
    private BigDecimal price;

}
