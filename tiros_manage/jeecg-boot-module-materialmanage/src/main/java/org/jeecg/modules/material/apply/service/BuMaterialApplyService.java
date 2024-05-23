package org.jeecg.modules.material.apply.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.apply.bean.BuMaterialApply;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderQueryVO;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyConfirmVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyQueryVO;

/**
 * <p>
 * 物料申请(领用) 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
public interface BuMaterialApplyService extends IService<BuMaterialApply> {

    /**
     * 分页查询物资领用记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuMaterialApply> page(BuMaterialApplyQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 查询领用工单列表，用于app
     *
     * @param queryVO 查询条件
     * @return 领用工单vo
     * @throws Exception 异常
     */
    IPage<AppApplyOrderVO> pageApplyOrderForApp(AppApplyOrderQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 更新物资领用记录
     *
     * @param buMaterialApply 物资领用信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveMaterialApply(BuMaterialApply buMaterialApply) throws Exception;

    /**
     * 保存物资领用记录
     *
     * @param buMaterialApply 物资领用信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateMaterialApply(BuMaterialApply buMaterialApply) throws Exception;

    /**
     * 批量删除物资领用记录
     *
     * @param ids 物资领用记录ids 多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 备料确认
     *
     * @param confirmVO 确认信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean readyConfirm(BuMaterialApplyConfirmVO confirmVO) throws Exception;

    /**
     * 备料确认
     *
     * @param confirmVO 确认信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean readyConfirmForApp(BuMaterialApplyConfirmVO confirmVO) throws Exception;

    /**
     * 领料确认
     *
     * @param fromType ids类型 1托盘id 2领用明细id
     * @param ids      领用明细/托盘ids，多个逗号分隔
     * @param onlySave 是否只保存 true=只保存（只保存时不进行修改班组库存、消耗到maximo、更新统计数据等操作），false=保存加提交
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean receiveConfirm(Integer fromType, String ids, Boolean onlySave) throws Exception;

    /**
     * app领料确认：修改对应的领用单状态及明细状态、完成工单流程当前任务
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean receiveConfirmForApp(String orderId) throws Exception;

}
