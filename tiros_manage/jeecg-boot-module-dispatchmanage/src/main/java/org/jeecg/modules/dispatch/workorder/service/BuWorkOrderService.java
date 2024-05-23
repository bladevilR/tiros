package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuMaterialApplyOrderCreateVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderRecordCreateTaskQRCodeVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderService extends IService<BuWorkOrder> {

    /**
     * 根据条件分页查询工单
     *
     * @param queryVO     查询条件
     * @param pageNo      页码
     * @param pageSize    页大小
     * @param isWorkGroup 是否工班查询
     * @return 工单分页结果
     * @throws Exception 异常
     */
    IPage<BuWorkOrder> page(BuWorkOrderQueryVO queryVO, Integer pageNo, Integer pageSize, boolean isWorkGroup) throws Exception;

    /**
     * 根据条件分页查询发料工单
     *
     * @param queryVO     查询条件
     * @param pageNo      页码
     * @param pageSize    页大小
     * @param isWorkGroup 是否工班查询
     * @return 工单分页结果
     * @throws Exception 异常
     */
    IPage<BuWorkOrder> applyPage(BuWorkOrderQueryVO queryVO, Integer pageNo, Integer pageSize, boolean isWorkGroup) throws Exception;

    /**
     * 根据工单id查询工单关联信息
     *
     * @param id 工单id
     * @return 工单关联信息
     * @throws Exception 异常
     */
    WorkOrderRelevanceInfo selectWorkOrderRelevanceInfo(String id) throws Exception;

    /**
     * 获取指定工单物料的发放明细，必换件则找属于同一列计划的该必换件的发放明细
     *
     * @param orderMaterialId 工单物料id
     * @return 发放明细列表
     * @throws Exception 异常
     */
    List<BuMaterialAssignDetail> listOrderMaterialAssign(String orderMaterialId) throws Exception;

    /**
     * 新增工单
     *
     * @param order 工单信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveWorkOrder(BuWorkOrder order) throws Exception;

    /**
     * 新增发料工单
     *
     * @param createVO 发料工单创建信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean addMaterialApplyOrder(BuMaterialApplyOrderCreateVO createVO) throws Exception;

    /**
     * 修改工单
     *
     * @param order 工单信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean editWorkOrder(BuWorkOrder order) throws Exception;

    /**
     * 核实工单：修改工单状态、执行核实逻辑、不操作流程
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean verifyOrder(String orderId) throws Exception;

    /**
     * app核实工单：修改工单状态、完成工单流程当前任务
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean verifyOrderForApp(String orderId) throws Exception;

    /**
     * 批量删除工单
     *
     * @param ids 工单ids，多个逗号分割
     * @return 是否成功
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 下发工单
     *
     * @param ids 工单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean issuingWorkOrderBatch(String ids) throws Exception;

    /**
     * 提交工单给调度(批量)
     *
     * @param ids   工单ids，多个逗号分隔
     * @param force 是否强制更改任务状态
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean submitOrderToDispatcherBatch(String ids, Boolean force) throws Exception;

    /**
     * 提交工单给调度(单个)，app使用
     * 修改工单状态、完成工单流程当前任务
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean submitOrderToDispatcherForApp(String orderId) throws Exception;

    /**
     * 关闭工单(批量)
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean closeOrderBatch(String ids) throws Exception;

    /**
     * 审批通过关闭工单（批量）
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean approveOrderBatch(String ids) throws Exception;

    /**
     * 启动工单流程(单个)
     *
     * @param order 工单
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean startOrderWorkflow(BuWorkOrder order) throws Exception;

    /**
     * 启动工单流程(批量)
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean startOrderWorkflowBatch(String ids) throws Exception;

    /**
     * 设置工单状态填报中，设置工单任务状态已开始（未开始的不设置）
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setOrderStatusWorking(String ids) throws Exception;

    /**
     * 工单开始
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean startOrder(String orderId) throws Exception;

    /**
     * 提交工单的临时物料申请：生成工单物料单据，并提交第一个流程任务
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean submitTempMaterialApply(String orderId) throws Exception;

    /**
     * 根据工单id和领用类型获取工单物料领用单
     *
     * @param orderId   工单id
     * @param applyType 领用类型 字典dictCode=bu_material_apply_type
     * @return 工单物料领用单列表
     * @throws Exception 异常
     */
    List<BuMaterialApply> listOrderMaterialApply(String orderId, Integer applyType) throws Exception;

    /**
     * 根据工单id获取工单退料单
     *
     * @param orderId 工单id
     * @return 工单退料单列表
     * @throws Exception 异常
     */
    List<BuMaterialReturned> listOrderMaterialReturn(String orderId) throws Exception;

    /**
     * 获取工班最后一条安全预想内容
     *
     * @param groupId 工班id
     * @return 安全预想内容
     * @throws Exception 异常
     */
    String getGroupLastSafeAssume(String groupId) throws Exception;

    /**
     * 任务二维码生成
     *
     * @param qrCodeVO 任务二维码参数
     * @return 二维码信息
     * @throws Exception 异常
     */
    Map<String, String> getTaskQRCode(BuWorkOrderRecordCreateTaskQRCodeVO qrCodeVO) throws Exception;

    /**
     * 查询二维码是否失效
     *
     * @param uuid 二维码uuid
     * @return 是否失效
     * @throws Exception 异常
     */
    Boolean getTaskQRCodeValid(String uuid) throws Exception;

    /**
     * 查询二维码是否被扫描
     *
     * @param uuid 二维码uuid
     * @return 是否被扫描
     * @throws Exception 异常
     */
    Boolean getTaskQRCodeIsScan(String uuid) throws Exception;

    /**
     * 暂停工单
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean suspendOrderBatch(String ids) throws Exception;

    /**
     * 激活工单
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean activityOrderBatch(String ids) throws Exception;

    /**
     * 取消工单
     *
     * @param ids 工单ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean cancelOrderWorkflowBatch(String ids) throws Exception;

    /**
     * 设置app按钮的权限
     *
     * @param page
     * @return
     */
    IPage<BuWorkOrder> editWorkOrderOperator(IPage<BuWorkOrder> page);

}
