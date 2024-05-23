package org.jeecg.modules.third.jdx.service;


import org.jeecg.modules.third.jdx.bean.BuMaterialStockChange;

import java.util.List;

/**
 * <p>
 * 库存变动记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-10-23
 */
public interface BuMaterialStockChangeThirdService {

    /**
     * 保存库存变动记录列表
     *
     * @param changeList 库存变动记录列表
     * @return 是否成功
     */
    boolean addChangeList(List<BuMaterialStockChange> changeList);

}
