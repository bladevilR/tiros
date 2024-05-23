package org.jeecg.modules.material.apply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.pallet.bean.vo.BuMaterialPalletVO;

import java.util.List;

/**
 * <p>
 * 领用明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
public interface BuMaterialApplyDetailService extends IService<BuMaterialApplyDetail> {

    /**
     * 根据领用单id查询领用明细列表，包含领用明细下的分配明细
     *
     * @param applyId 领用单id
     * @param status  领用明细状态
     * @return 明细列表
     * @throws Exception 异常
     */
    List<BuMaterialApplyDetail> listByApplyId(String applyId, List<Integer> status) throws Exception;

    /**
     * 根据工单id查询领用明细列表，包含领用明细下的分配明细
     *
     * @param orderId 工单id
     * @param status  领用明细状态
     * @return 明细列表
     * @throws Exception 异常
     */
    List<BuMaterialApplyDetail> listByOrderId(String orderId, List<Integer> status) throws Exception;

    /**
     * 根据托盘id查询分配明细中的物料
     *
     * @param palletId 托盘id
     * @return 托盘的分配明细中的物料
     * @throws Exception 异常
     */
    BuMaterialPalletVO getMaterialByPalletId(String palletId) throws Exception;

}
