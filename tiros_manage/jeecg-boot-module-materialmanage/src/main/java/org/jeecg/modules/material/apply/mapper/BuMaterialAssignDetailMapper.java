package org.jeecg.modules.material.apply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;

import java.util.List;

/**
 * <p>
 * 物料分配明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuMaterialAssignDetailMapper extends BaseMapper<BuMaterialAssignDetail> {

    /**
     * 批量插入
     *
     * @param list 物料分配明细列表
     */
    void insertList(@Param("list") List<BuMaterialAssignDetail> list);

    /**
     * 批量更新价格字段
     *
     * @param list 物料分配明细列表
     */
    void updatePriceBatch(@Param("list") List<BuMaterialAssignDetail> list);

    /**
     * 批量更新EBS库位编码
     *
     * @param list 物料分配明细列表
     */
    void updateListEbsWhCode(@Param("list") List<BuMaterialAssignDetail> list);

    /**
     * 根据领用明细id查询物料分配明细
     *
     * @param applyDetailId 领用明细id
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByApplyDetailId(@Param("applyDetailId") String applyDetailId);

    /**
     * 根据领用明细id列表查询物料分配明细
     *
     * @param applyDetailIdList 领用明细id列表
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByApplyDetailIdList(@Param("applyDetailIdList") List<String> applyDetailIdList);

    /**
     * 根据分配明细id列表查询物料分配明细
     *
     * @param idList 分配明细id列表
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByIdList(@Param("idList") List<String> idList);

    /**
     * 根据领用明细id查询物料分配明细的分配数量之和
     *
     * @param applyDetailId 领用明细id
     * @return 分配明细的分配数量之和
     */
    Double sumAmountByApplyDetailId(@Param("applyDetailId") String applyDetailId);

    /**
     * 根据分配明细id列表查询物料分配明细
     *
     * @param idList 分配明细id列表
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListForMaximoByIdList(@Param("idList") List<String> idList);

    /**
     * 查询所有未提交的工单的发放明细中的托盘id
     *
     * @return 托盘id列表
     */
    List<String> selectUnCommitOrderAssignDetailPalletIdList();

    /**
     * 根据托盘id找发放信息
     *
     * @param id
     * @return
     */
    List<BuMaterialAssignDetail> selectListByPalletId(@Param("id") String id);

    /**
     * 根据工单id查询物料分配明细
     *
     * @param orderId 工单id
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单id列表查询物料分配明细
     *
     * @param orderIdList 工单id列表
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据工单物料id查询对应的已发料的分配明细
     *
     * @param orderMaterialIdList 工单物料id
     * @return 已发料的分配明细列表
     */
    List<BuMaterialAssignDetail> selectListForGroupStockCount(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

}
