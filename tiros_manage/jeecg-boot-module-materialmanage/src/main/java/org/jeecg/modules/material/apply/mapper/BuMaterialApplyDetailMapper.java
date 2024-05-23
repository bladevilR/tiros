package org.jeecg.modules.material.apply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.cost.bean.vo.CompareQueryVO;
import org.jeecg.modules.material.cost.bean.vo.CompareResultVO;

import java.util.List;

/**
 * <p>
 * 领用明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
public interface BuMaterialApplyDetailMapper extends BaseMapper<BuMaterialApplyDetail> {

    /**
     * 批量更新价格
     *
     * @param list 领用明细列表
     */
    void updatePriceBatch(@Param("list") List<BuMaterialApplyDetail> list);

    /**
     * 批量更新领料确认属性
     *
     * @param list 领用明细列表
     */
    void updateListForReceive(@Param("list") List<BuMaterialApplyDetail> list);

    /**
     * 根据领用明细id列表查询领用明细
     *
     * @param detailIdList 领用明细id列表
     * @return 领用明细列表
     */
    List<BuMaterialApplyDetail> selectListByIdList(@Param("detailIdList") List<String> detailIdList);

    /**
     * 根据领用单id查询领用明细
     *
     * @param applyId 领用单id
     * @return 领用明细
     */
    List<BuMaterialApplyDetail> selectListByApplyId(@Param("applyId") String applyId, @Param("status") List<Integer> status);

    /**
     * 根据工单id查询领用明细列表
     *
     * @param orderId 工单id
     * @return 明细列表
     */
    List<BuMaterialApplyDetail> selectListByOrderId(@Param("orderId") String orderId, @Param("status") List<Integer> status);

    /**
     * 根据工单id列表查询领用明细
     *
     * @param orderIdList 工单id列表
     * @return 领用明细列表
     */
    List<BuMaterialApplyDetail> selectListByOrderIdList(@Param("orderIdList") List<String> orderIdList, @Param("status") List<Integer> status);

    /**
     * 根据托盘id列表查询领用明细列表
     *
     * @param palletIdList 托盘id列表
     * @return 明细列表
     */
    List<BuMaterialApplyDetail> selectListByPalletIdList(@Param("palletIdList") List<String> palletIdList);

    /**
     * 根据消耗核实条件查询领用明细列表
     *
     * @param queryVO 查询条件
     * @return 领用明细列表
     */
    List<CompareResultVO> selectMaterialCostList(@Param("queryVO") CompareQueryVO queryVO);

    /**
     * 通过所有领用明细查询班组物资价格信息
     *
     * @return 班组物资价格信息
     */
    List<BuMaterialApplyDetail> selectGroupMaterialPriceInfo();

    /**
     * 查询所有已提交已关闭工单、已发料、的领用明细列表
     *
     * @return 领用明细列表
     */
    List<BuMaterialApplyDetail> selectListOfSubmitOrCloseOrder();

    /**
     * 根据条件查询领用明细，用于消耗核实
     *
     * @param queryVO 查询条件
     * @return 领用明细
     */
    List<BuMaterialApplyDetail> selectListForCostCompare(@Param("queryVO") CompareQueryVO queryVO);

}
