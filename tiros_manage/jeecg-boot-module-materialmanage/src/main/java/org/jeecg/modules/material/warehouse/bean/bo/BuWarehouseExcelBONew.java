package org.jeecg.modules.material.warehouse.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 仓库excel导入信息bo，新，含4级库信息
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-07
 */
@Data
@Accessors(chain = true)
public class BuWarehouseExcelBONew {
    String level2;
    String level3;
    String level4;
    Double amount;
    String materialTypeCode;
}
