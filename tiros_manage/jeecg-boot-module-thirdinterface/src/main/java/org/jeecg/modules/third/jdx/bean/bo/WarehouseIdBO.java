package org.jeecg.modules.third.jdx.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/1/20
 */
@Data
@Accessors(chain = true)
public class WarehouseIdBO {

    private String warehouseId;
    private Boolean needUpdateWarehouseCache;

}
