package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialApplyDetail;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderMaterial;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOrderMaterialQueryVO;

import java.util.List;

/**
 * <p>
 * 工单物料 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderMaterialService extends IService<BuWorkOrderMaterial> {

    /**
     * 查询工单物料列表
     *
     * @param queryVO 查询条件
     * @return 工单物料列表
     * @throws Exception 异常
     */
    List<BuWorkOrderMaterial> listOrderMaterial(BuOrderMaterialQueryVO queryVO) throws Exception;

    /**
     * 保存工单物料，新增或更新
     *
     * @param orderMaterial 工单物料
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOrderMaterial(BuWorkOrderMaterial orderMaterial) throws Exception;

    /**
     * 保存物资实际消耗记录
     *
     * @param orderMaterial 工单物料及实际消耗信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOrderMaterialAct(BuWorkOrderMaterial orderMaterial) throws Exception;

    /**
     * 更新物资消耗(批量更新工单物料)
     *
     * @param orderMaterialList 工单物料
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateOrderMaterialList(List<BuWorkOrderMaterial> orderMaterialList) throws Exception;

    /**
     * 批量删除工单物料
     *
     * @param ids 工单物料ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 根据工单id查询必换件物料（包装成领用明细）
     *
     * @param orderId 工单id
     * @return 必换件物料-领用明细
     * @throws Exception 异常
     */
    List<BuMaterialApplyDetail> listOrderMustMaterialAsApplyDetail(String orderId) throws Exception;

}
