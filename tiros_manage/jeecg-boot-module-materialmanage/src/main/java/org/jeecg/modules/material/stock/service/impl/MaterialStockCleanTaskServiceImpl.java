package org.jeecg.modules.material.stock.service.impl;

import org.jeecg.common.tiros.task.service.MaterialStockCleanTaskService;
import org.jeecg.modules.material.stock.mapper.BuMaterialStockMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 清理物资库存表定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class MaterialStockCleanTaskServiceImpl implements MaterialStockCleanTaskService {

    @Resource
    private BuMaterialStockMapper buMaterialStockMapper;


    /**
     * @see MaterialStockCleanTaskService#clearInvalidStock()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearInvalidStock() throws RuntimeException {
        // 清理物资库存表中的数量为0的数据 bu_material_stock
        buMaterialStockMapper.deleteZeroAmount();
        // 清理物质类型表中没有，库存中有的物质类型的数据
        buMaterialStockMapper.deleteNotExistMaterialType();

        return true;
    }

}
