package org.jeecg.modules.material.entry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;

/**
 * <p>
 * 入库单 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryOrderService extends IService<BuMaterialEntryOrder> {

    /**
     * 根据入库单id查询入库单及明细信息
     *
     * @param entryOrderId 入库单id
     * @return 入库单及明细信息
     * @throws Exception 异常
     */
    BuMaterialEntryOrder getEntryOrderById(String entryOrderId) throws Exception;

    /**
     * 新增/修改入库单
     *
     * @param entryOrder 入库单
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveEntryOrder(BuMaterialEntryOrder entryOrder) throws Exception;

}
