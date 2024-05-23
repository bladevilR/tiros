package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderMaterialActs;

import java.util.List;

public interface BuWorkOrderMaterialActsMapper extends BaseMapper<BuWorkOrderMaterialActs> {

    /**
     * 批量插入
     *
     * @param list 工单物料实际消耗列表
     */
    void insertList(@Param("list") List<BuWorkOrderMaterialActs> list);

    /**
     * 批量更新
     *
     * @param list 工单物料实际消耗列表
     */
    void updateList(@Param("list") List<BuWorkOrderMaterialActs> list);

    /**
     * 批量更新价格字段
     *
     * @param list 工单物料实际消耗列表
     */
    void updatePriceBatch(@Param("list") List<BuWorkOrderMaterialActs> list);

    /**
     * 根据工单id删除工单物料实际消耗
     *
     * @param orderIdList 工单id列表
     */
    int deleteByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 查询没有分配明细的工单物料实际消耗
     *
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectListOfNoGroupStockId();

    /**
     * 查询非发料、已提交已关闭、的工单的实际消耗数量大于0的工单物料实际消耗
     *
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectListOfSubmitOrCloseOrder();

    /**
     * 根据工单id查询工单物料实际消耗
     *
     * @param orderId 工单id
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单id查询工单物料实际消耗
     *
     * @param orderIdList 工单id列表
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectListByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据工单物料id列表查询工单物料实际消耗
     *
     * @param orderMaterialIdList 工单物料id列表
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectListByOrderMaterialIdList(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

}
