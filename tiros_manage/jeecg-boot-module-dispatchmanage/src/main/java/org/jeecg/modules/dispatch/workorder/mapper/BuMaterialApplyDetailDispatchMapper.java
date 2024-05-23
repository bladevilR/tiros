package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialApplyDetail;

import java.util.List;

/**
 * <p>
 * 领用明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
public interface BuMaterialApplyDetailDispatchMapper extends BaseMapper<BuMaterialApplyDetail> {

    /**
     * 批量插入
     *
     * @param list 领用明细列表
     */
    void insertList(@Param("list") List<BuMaterialApplyDetail> list);

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
    List<BuMaterialApplyDetail> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单物料id列表查询领用明细及分配明细
     *
     * @param orderMaterialIdList 工单物料id列表
     * @return 领用明细及分配明细
     */
    List<BuMaterialApplyDetail> selectListWithAssignByOrderMaterialIdList(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

    /**
     * 根据工单id和物料id查询已领用的领用明细及分配明细
     *
     * @param orderId        工单id
     * @param materialTypeId 物料id
     * @return 领用明细及分配明细
     */
    List<BuMaterialApplyDetail> selectReceivedListWithAssignByOrderIdAndMaterialTypeId(@Param("orderId") String orderId, @Param("materialTypeId") String materialTypeId);

    /**
     * 查询指定列计划、工班、物料的必换件领用工单的领用明细
     *
     * @param planId         列计划id
     * @param groupId        班组id
     * @param materialTypeId 物资类型id
     * @return 领用明细列表
     */
    List<BuMaterialApplyDetail> selectMaterialOrderApplyDetailListWithAssign(@Param("planId") String planId, @Param("groupId") String groupId, @Param("materialTypeId") String materialTypeId);

}
