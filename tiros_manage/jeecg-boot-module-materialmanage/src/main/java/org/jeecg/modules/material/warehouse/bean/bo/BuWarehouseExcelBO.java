package org.jeecg.modules.material.warehouse.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 仓库excel导入信息bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-11
 */
@Data
@Accessors(chain = true)
public class BuWarehouseExcelBO {
    private String room;
    private String self;
    private String system;
}
