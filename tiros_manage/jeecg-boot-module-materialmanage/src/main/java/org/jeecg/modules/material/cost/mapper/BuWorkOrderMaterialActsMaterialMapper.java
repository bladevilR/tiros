package org.jeecg.modules.material.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterialActs;

import java.util.List;

public interface BuWorkOrderMaterialActsMaterialMapper extends BaseMapper<BuWorkOrderMaterialActs> {

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
    void updateListAmount(@Param("list") List<BuWorkOrderMaterialActs> list);

    /**
     * 删除工单物料实际消耗
     *
     * @param orderMaterialId 工单物料id
     */
    void deleteByOrderMaterialId(@Param("orderMaterialId") String orderMaterialId);

    /**
     * 查询班组的指定状态的工单的物料实际消耗
     *
     * @param groupId         班组id
     * @param materialTypeId  物资id
     * @param orderStatusList 工单状态
     * @return 工单物料实际消耗列表
     */
    List<BuWorkOrderMaterialActs> selectListForGroupStockCount(@Param("groupId") String groupId, @Param("materialTypeId") String materialTypeId, @Param("orderStatusList") List<Integer> orderStatusList);

    /**
     * 根据工单物料id列表查询工单物料实际消耗
     *
     * @param orderMaterialIdList 工单物料id列表
     * @return 工单物料实际消耗
     */
    List<BuWorkOrderMaterialActs> selectListByOrderMaterialIdList(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

}
