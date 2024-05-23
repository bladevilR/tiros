package org.jeecg.modules.tiros.custom.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.tiros.custom.bean.bo.MaterialConsumeCorrect;
import org.jeecg.modules.tiros.custom.service.CustomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-04
 */
@TirosController
@Slf4j
@Api(tags = "自定义业务处理")
@RestController
@RequestMapping("/custom")
public class CustomController {

    private final CustomService customService;

    public CustomController(CustomService customService) {
        this.customService = customService;
    }


    @PostMapping("/update-orderTask-taskObjId-ifNull")
    @ApiOperation(value = "1-更新由列计划生成的工单任务的任务对象id（如果任务对象id为空）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updatePlanGenerateOrderTaskTaskObjIdIfNull(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        String resultString = customService.updatePlanGenerateOrderTaskTaskObjIdIfNull(planId);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-materials-tpPlan-tp-order")
    @ApiOperation(value = "2-更新计划模板的物料必换件到工单（如果工单物料为空）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateMaterialFromTpPlanToOrder(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                          @RequestParam(required = false) @ApiParam(value = "班组id") String groupId) throws Exception {
        String resultString = customService.updateMaterialFromTpPlanToOrder(planId, groupId);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/generate-materialConsume-correctSql")
    @ApiOperation(value = "3-生成物料实际消耗修正sql")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> generateMaterialConsumeCorrectSql(MaterialConsumeCorrect correct) throws Exception {
        String resultString = customService.generateMaterialConsumeCorrectSql(correct);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/generate-materialConsume-correctSql/excel-import")
    @ApiOperation(value = "4-生成物料实际消耗修正sql/通过excel导入")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> generateMaterialConsumeCorrectSqlByExcel(@RequestParam @ApiParam(value = "物料实际消耗修正信息excel文件", required = true) MultipartFile excelFile,
                                                                   @RequestParam(required = false) @ApiParam(value = "导出excel所在盘符，仅支持根路径,默认D") String exportDisk) throws Exception {
        String resultString = customService.generateMaterialConsumeCorrectSqlByExcel(excelFile, exportDisk);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-orderMaterial-systemAndWorkstation-ifNull")
    @ApiOperation(value = "5-更新工单物料的系统和工位（如果为空）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateOrderMaterialSystemAndWorkstation(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        String resultString = customService.updateOrderMaterialSystemAndWorkstation(planId);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-apply-errorStatus")
    @ApiOperation(value = "6-更新领用单的异常状态")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateMaterialApplyErrorStatus(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                         @RequestParam(required = false) @ApiParam(value = "是否更新状态应该为2已领用的领料单") Boolean updateApplyStatus) throws Exception {
        String resultString = customService.updateMaterialApplyErrorStatus(planId, updateApplyStatus);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/handle-repeat-materialType")
    @ApiOperation(value = "7-处理重复的物资类型（按编码去重）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> handleRepeatMaterialType(@RequestParam(required = false) @ApiParam(value = "是否删除重复的数据") Boolean deleteRepeatData) throws Exception {
        String resultString = customService.handleRepeatMaterialType(deleteRepeatData);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/handle-codeIdNotEquals-materialType")
    @ApiOperation(value = "8-处理id不等于编码的物资类型（id=编码）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> handleCodeIdNotEqualsMaterialType() throws Exception {
        String resultString = customService.handleCodeIdNotEqualsMaterialType();
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-orderMaterial-systemAndWorkstation-ifNull/by-customerData")
    @ApiOperation(value = "9-更新工单物料的系统和工位(根据车间消耗明细数据excel)（如果为空）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateOrderMaterialSystemAndWorkstationByCustomerData(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                                                @RequestParam @ApiParam(value = "车间消耗明细数据excel文件", required = true) MultipartFile excelFile) throws Exception {
        String resultString = customService.updateOrderMaterialSystemAndWorkstationByCustomerData(planId, excelFile);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-orderMaterial-price/by-customerData")
    @ApiOperation(value = "10-更新工单物料价格(根据车间消耗明细数据excel)（同步更新业务表中价格）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateOrderMaterialPriceByCustomerData(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                                 @RequestParam @ApiParam(value = "车间消耗明细数据excel文件", required = true) MultipartFile excelFile) throws Exception {
        String resultString = customService.updateOrderMaterialPriceByCustomerData(planId, excelFile);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/compare-diff-materialCostDetail/excel-import")
    @ApiOperation(value = "11-比较车间数据和系统数据消耗明细系统和工位不匹配")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> compareDifferentMaterialCostDetail(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                             @RequestParam @ApiParam(value = "车间消耗明细数据excel文件", required = true) MultipartFile excelFile,
                                                             @RequestParam(required = false) @ApiParam(value = "导出excel所在盘符，仅支持根路径,默认D") String exportDisk,
                                                             @RequestParam(required = false) @ApiParam(value = "debug物资编码") String debugMaterialCode) throws Exception {
        String resultString = customService.compareDifferentMaterialCostDetail(planId, excelFile, exportDisk, debugMaterialCode);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/flush-issueOrderMaterial-to-groupStock")
    @ApiOperation(value = "12-刷新已完成的发料工单的物料到班组库存（确认领料异常后处理）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> flushIssueOrderMaterialToGroupStock(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        String resultString = customService.flushIssueOrderMaterialToGroupStock(orderId);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-issueOrderMaterial-applyStatus-ofDeliver1")
    @ApiOperation(value = "13-更新发料工单的发料状态（发料异常时处理-发料提交但只执行了保存逻辑）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateIssueOrderMaterialApplyStatusOfDeliver1(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        String resultString = customService.updateIssueOrderMaterialApplyStatusOfDeliver1(orderId);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-issueOrderMaterial-applyStatus-ofReceive")
    @ApiOperation(value = "14-更新发料工单的领料状态（确认领料异常时后处理）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateIssueOrderMaterialApplyStatusOfReceive(@RequestParam @ApiParam(value = "工单id", required = true) String orderId,
                                                                       @RequestParam(required = false) @ApiParam(value = "确认时间 yyyy-MM-dd HH:mm:ss") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date confirmTime,
                                                                       @RequestParam @ApiParam(value = "是否同步消耗物料信息到maximo", required = true) Boolean transConsumeToMaximo,
                                                                       @RequestParam @ApiParam(value = "是否刷新分配明细的物料到班组库存", required = true) Boolean toGroupStock) throws Exception {
        String resultString = customService.updateIssueOrderMaterialApplyStatusOfReceive(orderId, confirmTime, transConsumeToMaximo, toGroupStock);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/stockUse/update-init")
    @ApiOperation(value = "15-初始化物资库存占用（根据maximo同步表中未处理的消耗记录）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateInitStockUse(Boolean forceDeleteOldUse) throws Exception {
        String resultString = customService.updateInitStockUse(forceDeleteOldUse);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/stockUse/update-maximoDataHandled-andDeleteUse")
    @ApiOperation(value = "16-设置maximo同步表消耗数据为已处理+删除对应的库存占用")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateMaximoDataHandledAndDeleteUse(@RequestParam @ApiParam(value = "maximo同步数据id列表", required = true) List<String> transDataIdList) throws Exception {
        String resultString = customService.updateMaximoDataHandledAndDeleteUse(transDataIdList);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/assignDetail/update-ebsWhCode-ifNull")
    @ApiOperation(value = "17-通过工单号设置分配明细的ebs库存信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateAssignDetailEbsWhCode(@RequestParam @ApiParam(value = "工单号，多个逗号拼接", required = true) String orderCodes) throws Exception {
        String resultString = customService.updateAssignDetailEbsWhCode(orderCodes);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/assignDetail/add-to-maximoTransDataTable")
    @ApiOperation(value = "18-通过工单号添加分配明细的到maximo同步中间表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> addAssignDetailToMaximoTransDataTable(@RequestParam @ApiParam(value = "工单号，多个逗号拼接", required = true) String orderCodes) throws Exception {
        String resultString = customService.addAssignDetailToMaximoTransDataTable(orderCodes);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/warehouse/test-regular-warehouseId")
    @ApiOperation(value = "19-测试生成有规则的仓库id")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Object> testRegularWarehouseId() throws Exception {
        Object result = customService.testRegularWarehouseId();
        return new Result<>().successResult(result);
    }

    @PostMapping("/update-orderInfo-to-maximo")
    @ApiOperation(value = "20-更新工单信息到maximo（需修改数据库工单数据后使用）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateOrderInfoToMaximo(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        String resultString = customService.updateOrderInfoToMaximo(orderId);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-orderTaskTime-by-orderActTime")
    @ApiOperation(value = "21-更新关闭工单中异常的任务开始和结束时间，并更新对应的工时同步数据（跟工单实际时间对比）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateOrderTaskTimeByOrderActTime() throws Exception {
        String resultString = customService.updateOrderTaskTimeByOrderActTime();
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-notTransferred-workTimeAndCloseOrderTransData")
    @ApiOperation(value = "22-更新未传输的工时同步数据和工单关闭同步数据（逻辑参考关闭工单时添加同步数据的逻辑）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateNotTransferredWorkTimeAndCloseOrderTransData() throws Exception {
        String resultString = customService.updateNotTransferredWorkTimeAndCloseOrderTransData();
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/import-workshopOwnSystem-fault1")
    @ApiOperation(value = "23-导入车间自己系统的检修故障")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> importWorkshopOwnSystemFault1(@RequestParam @ApiParam(value = "车间自己系统的检修故障excel文件", required = true) MultipartFile excelFile,
                                                        @RequestParam(required = false) @ApiParam(value = "用于调试的数据行") Integer debugRowNum) throws Exception {
        String resultString = customService.importWorkshopOwnSystemFault1(excelFile, debugRowNum);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/import-workshopOwnSystem-fault2")
    @ApiOperation(value = "24-导入车间自己系统的架大修故障")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> importWorkshopOwnSystemFault2(@RequestParam @ApiParam(value = "车间自己系统的架大修故障excel文件", required = true) MultipartFile excelFile,
                                                        @RequestParam(required = false) @ApiParam(value = "用于调试的数据行") Integer debugRowNum) throws Exception {
        String resultString = customService.importWorkshopOwnSystemFault2(excelFile, debugRowNum);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/trans-fault-to-maximo")
    @ApiOperation(value = "25-手动同步指定故障到maximo（仅用于maximo没有的故障）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> transFaultToMaximo(@RequestParam @ApiParam(value = "故障编号，多个逗号分隔", required = true) String faultSns) throws Exception {
        String resultString = customService.transFaultToMaximo(faultSns);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/trans-order-to-maximo")
    @ApiOperation(value = "26-手动同步指定工单到maximo（仅用于maximo没有的工单）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> transOrderToMaximo(@RequestParam @ApiParam(value = "工单号，多个逗号分隔", required = true) String orderCodes) throws Exception {
        String resultString = customService.transOrderToMaximo(orderCodes);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/error-deal/closeOrder-decrease-groupStock")
    @ApiOperation(value = "27-关闭工单扣减班组库存（用于处理异常数据）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> closeOrderDecreaseMaterialGroupStock(@RequestParam @ApiParam(value = "工单号，多个逗号分隔", required = true) String orderCodes) throws Exception {
        String resultString = customService.closeOrderDecreaseMaterialGroupStock(orderCodes);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/update-tpPLan-must-to-material")
    @ApiOperation(value = "28-刷新计划模板的必换件到物料")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> updateTpPLanMustToMaterial(@RequestParam @ApiParam(value = "计划模板必换件关联id，多个逗号分隔", required = true) String ids) throws Exception {
        String resultString = customService.updateTpPLanMustToMaterial(ids);
        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/get-error-stock-level4")
    @ApiOperation(value = "29-查询4级库库存比3级库多的情况")
    @OperationLog()
    public Result<List<Map<String, String>>> getErrorStockLevel4() throws Exception {
        List<Map<String, String>> result = customService.getErrorStockLevel4();
        return new Result<List<Map<String, String>>>().successResult(result);
    }

    @PostMapping("/delete/order-wf-andRegenerate")
    @ApiOperation(value = "30-删除计划工单的流程（删除流程实例、删除业务流程实列状态表、修改工单为未下发）")
    @OperationLog()
    public Result<String> deleteOrderAndWFAndRegenerate(@RequestParam @ApiParam(value = "工单号，多个逗号分隔", required = true) String orderCodes) throws Exception {
        String result = customService.deleteOrderAndWFAndRegenerate(orderCodes);
        return new Result<String>().successResult(result);
    }

    @PostMapping("/fault/set-workshop")
    @ApiOperation(value = "31-设置故障表车间id为空的数据（导入历史故障导致的）")
    @OperationLog()
    public Result<String> setFaultInfoWorkshopId(@RequestParam @ApiParam(value = "是否更新", required = true) Boolean needUpdate) throws Exception {
        String result = customService.setFaultInfoWorkshopId(needUpdate);
        return new Result<String>().successResult(result);
    }

}
