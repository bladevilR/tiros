package org.jeecg.common.tiros.stock.use.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;

import java.util.List;

/**
 * <p>
 * 库存占用 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-11-25
 */
public interface BuMaterialStockUseService extends IService<BuMaterialStockUse> {

    /**
     * 保存库存占用记录列表
     *
     * @param useList 库存占用记录列表
     * @return 是否成功
     */
    boolean addStockUseList(List<BuMaterialStockUse> useList);

    /**
     * 根据分配明细删除对应的分配明细的占用
     *
     * @param assignDetailIdList 分配明细id列表
     * @return 是否成功
     */
    int deleteStockUseByAssignDetailIdList(List<String> assignDetailIdList);

    /**
     * 清空所有库存占用
     *
     * @return 是否成功
     */
    int clearAllStockUse();

    /**
     * 查询库存占用列表
     *
     * @param stockUse 查询条件
     * @return 库存占用列表
     */
    List<BuMaterialStockUse> listStockUse(BuMaterialStockUse stockUse);

    /**
     * 查询库存占用列表
     *
     * @param materialTypeIdList 物资类型id列表
     * @return 库存占用列表
     */
    List<BuMaterialStockUse> listStockUseByMaterialTypeIdList(List<String> materialTypeIdList);

    /**
     * 查询库存占用列表
     *
     * @param materialTypeId 物资类型id
     * @return 库存占用列表
     */
    List<BuMaterialStockUse> listStockUseByMaterialTypeId(String materialTypeId);

}
