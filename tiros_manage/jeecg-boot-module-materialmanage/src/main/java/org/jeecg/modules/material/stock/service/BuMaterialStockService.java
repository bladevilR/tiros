package org.jeecg.modules.material.stock.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.modules.material.stock.bean.BuMaterialEntryAttr;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockDetailVO;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockQueryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialStockService extends IService<BuMaterialStock> {

    /**
     * 分页查询物资库存记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页面
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuMaterialStock> pageStock(BuMaterialStockQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据物资类型id查询库存列表
     *
     * @param materialTypeId     物资类型id
     * @param warehouseLevelList 仓库级别列表
     * @param applyDetailId      领用明细id（旧的分配明细不占用库存：可重复分配）
     * @param assignDetailId     分配明细id（旧的分配明细不占用库存：可重复分配）
     * @return 库存列表
     * @throws Exception 异常
     */
    List<BuMaterialStock> listStockByMaterialTypeId(String materialTypeId, List<Integer> warehouseLevelList, String applyDetailId, String assignDetailId) throws Exception;

    /**
     * 根据物资类型id查询物质库存明细信息
     *
     * @param materialTypeId 物资类型id
     * @return 物质库存明细信息
     * @throws Exception 异常
     */
    BuMaterialStockDetailVO getMaterialStockDetail(String materialTypeId) throws Exception;

    /**
     * 根据物资类型id查询库存列表
     *
     * @param materialTypeIdList    物资类型id
     * @param filterWarehouseLevel4 真实库存=不统计3级库以下的
     * @return 库存列表
     */
    List<BuMaterialStock> listStockByMaterialTypeIdList(List<String> materialTypeIdList, boolean filterWarehouseLevel4);

    /**
     * 根据占用量计算库存
     *
     * @param stockList                       库存
     * @param warehouseBOMap                  仓库信息
     * @param notNeedDeleteAssignDetailIdList 不需要扣减占用的分配明细id
     */
    void deleteStockUsedAmount(List<BuMaterialStock> stockList, Map<String, CacheWarehouseBO> warehouseBOMap, List<String> notNeedDeleteAssignDetailIdList);

    /**
     * 设置批次选择
     *
     * @param resultStockList   目标库存
     * @param materialStockList 目标库存下的物资的所有库存记录
     * @param warehouseBOMap    所有仓库
     */
    void setTradeNoChoice(List<BuMaterialStock> resultStockList, List<BuMaterialStock> materialStockList, Map<String, CacheWarehouseBO> warehouseBOMap, List<String> notNeedDeleteAssignDetailIdList);

    /**
     * 查询物资批次属性
     *
     * @param materialTypeId 物资id
     * @param tradeNo        批次
     * @return 物资批次属性
     * @throws Exception 异常
     */
    BuMaterialEntryAttr getMaterialTradeAttr(String materialTypeId, String tradeNo) throws Exception;

    /**
     * 设置物资批次属性
     *
     * @param tradeAttr 物资批次属性
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setMaterialTradeAttr(BuMaterialEntryAttr tradeAttr) throws Exception;

    /**
     * 根据物资类型id查询物资总库存可用量
     *
     * @param materialTypeId 物资类型id
     * @return 可用量
     * @throws Exception 异常
     */
    double getMaterialSumStockAvailableAmount(String materialTypeId) throws Exception;

    /**
     * excel导入4级库库存数量
     *
     * @param excelFile excel文件
     * @return 是否导入成功
     * @throws Exception 异常信息
     */
    boolean importLevel4Stock(MultipartFile excelFile) throws Exception;

}
