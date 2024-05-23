package org.jeecg.modules.tiros.custom.service;

import org.jeecg.modules.tiros.custom.bean.bo.MaterialConsumeCorrect;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-04
 */
public interface CustomService {

    /**
     * 更新由列计划生成的工单任务的任务对象id（如果为空）
     *
     * @param planId 列计划id
     * @return 执行结果
     * @throws Exception 异常
     */
    String updatePlanGenerateOrderTaskTaskObjIdIfNull(String planId) throws Exception;

    /**
     * 没有物料的工单，查找对应的计划模板物料，如果有更新到工单物料上
     *
     * @param planId  列计划id
     * @param groupId 班组id
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateMaterialFromTpPlanToOrder(String planId, String groupId) throws Exception;

    /**
     * 生成物料实际消耗修正sql
     *
     * @param correct 修正物料实际消耗
     * @return sql语句
     * @throws Exception 异常
     */
    String generateMaterialConsumeCorrectSql(MaterialConsumeCorrect correct) throws Exception;

    /**
     * 通过excel导入，生成物料实际消耗修正sql
     *
     * @param excelFile  修正物料实际消耗excel统计信息
     * @param exportDisk 导出文件所在盘符
     * @return sql语句
     * @throws Exception 异常
     */
    String generateMaterialConsumeCorrectSqlByExcel(MultipartFile excelFile, String exportDisk) throws Exception;

    /**
     * 更新工单物料的系统和工位
     *
     * @param planId 列计划id
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateOrderMaterialSystemAndWorkstation(String planId) throws Exception;

    /**
     * 更新领用单的异常状态
     *
     * @param planId                      列计划id
     * @param updateApplyStatusToReceived 是否更新状态应该为2已领用的领料单
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateMaterialApplyErrorStatus(String planId, Boolean updateApplyStatusToReceived) throws Exception;

    /**
     * 处理重复的物资类型（按编码去重）
     *
     * @param deleteRepeatData 是否删除重复的数据
     * @return 执行结果
     * @throws Exception 异常
     */
    String handleRepeatMaterialType(Boolean deleteRepeatData) throws Exception;

    /**
     * 处理id不等于编码的物资类型
     *
     * @return 执行结果
     * @throws Exception 异常
     */
    String handleCodeIdNotEqualsMaterialType() throws Exception;

    /**
     * 根据车间消耗明细数据更新工单物料的系统和工位
     *
     * @param planId    列计划id
     * @param excelFile 车间消耗明细数据excel文件
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateOrderMaterialSystemAndWorkstationByCustomerData(String planId, MultipartFile excelFile) throws Exception;

    /**
     * 根据车间消耗明细数据更新工单物料的价格，同时更新相关业务表中价格
     *
     * @param planId    列计划idBU_WORK_ORDER_MATERIAL_ACTS
     * @param excelFile 车间消耗明细数据excel文件
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateOrderMaterialPriceByCustomerData(String planId, MultipartFile excelFile) throws Exception;

    /**
     * 通过excel导入，比较车间消耗明细数据和架大修系统明细数据中系统和工位不匹配
     *
     * @param planId     列计划id
     * @param excelFile  车间消耗明细数据excel文件
     * @param exportDisk 导出文件所在盘符
     * @return 比较结果
     * @throws Exception 异常
     */
    String compareDifferentMaterialCostDetail(String planId, MultipartFile excelFile, String exportDisk, String debugMaterialCode) throws Exception;

    /**
     * 刷新已完成的发料工单的物料到班组库存（确认领料异常后处理）
     *
     * @param orderId 工单id
     * @return 执行结果
     * @throws Exception 异常
     */
    String flushIssueOrderMaterialToGroupStock(String orderId) throws Exception;

    /**
     * 更新发料工单的发料状态（发料异常时处理-发料提交但只执行了保存逻辑）
     *
     * @param orderId 工单id
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateIssueOrderMaterialApplyStatusOfDeliver1(String orderId) throws Exception;

    /**
     * 更新发料工单的领料状态（确认领料异常时后处理）
     *
     * @param orderId              工单id
     * @param confirmTime          确认时间
     * @param transConsumeToMaximo 是否同步消耗物料信息到maximo
     * @param toGroupStock         是否刷新分配明细的物料到班组库存
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateIssueOrderMaterialApplyStatusOfReceive(String orderId, Date confirmTime, Boolean transConsumeToMaximo, Boolean toGroupStock) throws Exception;

    /**
     * 初始化物资库存占用（根据maximo同步表中未处理的消耗记录）
     *
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateInitStockUse(Boolean forceDeleteOldUse) throws Exception;

    /**
     * 设置maximo同步表消耗数据为已处理并删除对应的库存占用
     *
     * @param transDataIdList maximo同步数据id列表
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateMaximoDataHandledAndDeleteUse(List<String> transDataIdList) throws Exception;

    /**
     * 通过工单号设置分配明细的ebs库存信息
     *
     * @param orderCodes 工单号，多个逗号拼接
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateAssignDetailEbsWhCode(String orderCodes) throws Exception;

    /**
     * 通过工单号添加分配明细的到maximo同步中间表
     *
     * @param orderCodes 工单号，多个逗号拼接
     * @return 执行结果
     * @throws Exception 异常
     */
    String addAssignDetailToMaximoTransDataTable(String orderCodes) throws Exception;

    /**
     * 测试生成有规则的仓库id
     *
     * @return 结果
     * @throws Exception 异常
     */
    Object testRegularWarehouseId() throws Exception;

    /**
     * 更新工单信息到maximo（需修改数据库工单数据后使用）
     *
     * @param orderId 工单id
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateOrderInfoToMaximo(String orderId) throws Exception;

    /**
     * 更新关闭工单任务的开始结束时间并更新对应的工时同步数据（跟工单实际时间对比）
     *
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateOrderTaskTimeByOrderActTime() throws Exception;

    /**
     * 更新未传输的工时同步数据和工单关闭同步数据
     *
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateNotTransferredWorkTimeAndCloseOrderTransData() throws Exception;

    /**
     * 导入车间自己系统的检修故障
     *
     * @param excelFile   车间自己系统的检修故障excel文件
     * @param debugRowNum 用于调试的数据行
     * @return 执行结果
     * @throws Exception 异常
     */
    String importWorkshopOwnSystemFault1(MultipartFile excelFile, Integer debugRowNum) throws Exception;

    /**
     * 导入车间自己系统的架大修故障
     *
     * @param excelFile   车间自己系统的架大修故障excel文件
     * @param debugRowNum 用于调试的数据行
     * @return 执行结果
     * @throws Exception 异常
     */
    String importWorkshopOwnSystemFault2(MultipartFile excelFile, Integer debugRowNum) throws Exception;

    /**
     * 同步故障到maximo
     *
     * @param faultSns 故障编号，多个逗号分隔
     * @return 执行结果
     * @throws Exception 异常
     */
    String transFaultToMaximo(String faultSns) throws Exception;

    /**
     * 同步工单到maximo
     *
     * @param orderCodes 工单号，多个逗号分隔
     * @return 执行结果
     * @throws Exception 异常
     */
    String transOrderToMaximo(String orderCodes) throws Exception;

    /**
     * 关闭工单扣减班组库存（用于处理异常数据）
     *
     * @param orderCodes 工单号，多个逗号分隔
     * @return 执行结果
     * @throws Exception 异常
     */
    String closeOrderDecreaseMaterialGroupStock(String orderCodes) throws Exception;

    /**
     * 刷新计划模板的必换件到物料
     *
     * @param ids 计划模板必换件关联id，多个逗号分隔
     * @return 执行结果
     * @throws Exception 异常
     */
    String updateTpPLanMustToMaterial(String ids) throws Exception;

    /**
     * 查询4级库库存比3级库多的情况
     *
     * @return 执行结果
     * @throws Exception 异常
     */
    List<Map<String, String>> getErrorStockLevel4() throws Exception;

    /**
     * 删除计划工单的流程（删除流程实例、删除业务流程实列状态表、修改工单为未下发）
     *
     * @param orderCodes 工单号，多个逗号分隔
     * @return 执行结果
     */
    String deleteOrderAndWFAndRegenerate(String orderCodes);

    /**
     * 设置故障表车间id为空的数据
     *
     * @return 执行结果
     */
    String setFaultInfoWorkshopId(Boolean needUpdate);

}
