package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialAssignDetail;

import java.util.List;

/**
 * @author yyg
 */
public interface BuMaterialAssignDetailDispatchMapper extends BaseMapper<BuMaterialAssignDetail> {

    /**
     * 根据工单id删除领用明细
     *
     * @param orderId 工单id
     */
    void deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单id查询领用明细列表
     *
     * @param orderId 工单id
     * @return 领用明细列表
     */
    List<BuMaterialAssignDetail> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单物料id查询对应的已发料的分配明细
     *
     * @param orderMaterialIdList 工单物料id
     * @return 已发料的分配明细列表
     */
    List<BuMaterialAssignDetail> selectListForGroupStockCount(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

}
