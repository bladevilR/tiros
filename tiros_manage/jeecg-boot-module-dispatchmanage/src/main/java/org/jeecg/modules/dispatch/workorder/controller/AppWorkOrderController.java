package org.jeecg.modules.dispatch.workorder.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormWorkRecord;
import org.jeecg.modules.dispatch.planform.bean.vo.BuPlanWorkRecordFormQueryVO;
import org.jeecg.modules.dispatch.planform.service.BuRepairPlanFormsService;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.vo.*;
import org.jeecg.modules.dispatch.workorder.service.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工单 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-24
 */
@AppController
@Api(tags = "工单-工班")
@Slf4j
@RestController
@RequestMapping("/app/order")
public class AppWorkOrderController {

    private final BuWorkOrderService buWorkOrderService;
    private final BuWorkOrderTaskService buWorkOrderTaskService;
    private final BuRepairPlanFormsService buRepairPlanFormsService;
    private final BuWorkOrderMaterialService buWorkOrderMaterialService;
    private final BuWorkOrderToolService buWorkOrderToolService;
    private final BuWorkSafeAssumeReadService buWorkSafeAssumeReadService;
    private final BuWorkOrderAnnexService buWorkOrderAnnexService;
    private final BuRepairTaskStaffArrangeService buRepairTaskStaffArrangeService;
    private final WorkflowForwardService workflowForwardService;

    public AppWorkOrderController(BuWorkOrderService buWorkOrderService,
                                  BuWorkOrderTaskService buWorkOrderTaskService,
                                  BuRepairPlanFormsService buRepairPlanFormsService,
                                  BuWorkOrderMaterialService buWorkOrderMaterialService,
                                  BuWorkOrderToolService buWorkOrderToolService,
                                  BuWorkSafeAssumeReadService buWorkSafeAssumeReadService,
                                  BuWorkOrderAnnexService buWorkOrderAnnexService,
                                  BuRepairTaskStaffArrangeService buRepairTaskStaffArrangeService,
                                  WorkflowForwardService workflowForwardService) {
        this.buWorkOrderService = buWorkOrderService;
        this.buWorkOrderTaskService = buWorkOrderTaskService;
        this.buRepairPlanFormsService = buRepairPlanFormsService;
        this.buWorkOrderMaterialService = buWorkOrderMaterialService;
        this.buWorkOrderToolService = buWorkOrderToolService;
        this.buWorkSafeAssumeReadService = buWorkSafeAssumeReadService;
        this.buWorkOrderAnnexService = buWorkOrderAnnexService;
        this.buRepairTaskStaffArrangeService = buRepairTaskStaffArrangeService;
        this.workflowForwardService = workflowForwardService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工单(分页)")
    @OperationLog()
    public Result<IPage<BuWorkOrder>> page(@Validated BuWorkOrderQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkOrder> page = buWorkOrderService.page(queryVO, pageNo, pageSize, true);
        //设置按钮
        page = buWorkOrderService.editWorkOrderOperator(page);
        return new Result<IPage<BuWorkOrder>>().successResult(page);
    }

    @PostMapping("/verify")
    @ApiOperation(value = "核实工单", notes = "修改工单状态、完成工单流程当前任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> verifyOrder(@RequestParam @ApiParam(value = "工单id", required = true) String id) throws Exception {
        boolean flag = buWorkOrderService.verifyOrderForApp(id);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/task/list")
    @ApiOperation(value = "查询工单任务(列表)")
    @OperationLog()
    public Result<List<BuWorkOrderTask>> listTask(@Validated BuWorkOrderTaskQueryVOForApp queryVO) throws Exception {
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskService.listTaskForApp(queryVO);
        return new Result<List<BuWorkOrderTask>>().successResult(orderTaskList);
    }

    @GetMapping("/safe-assume")
    @ApiOperation(value = "查询安全预想", notes = "取工班最后一条安全预想记录")
    @OperationLog()
    public Result<String> getGroupLastSafeAssume(@RequestParam @ApiParam(value = "工班id", required = true) String groupId) throws Exception {
        String safeAssume = buWorkOrderService.getGroupLastSafeAssume(groupId);
        return new Result<String>().successResult(safeAssume);
    }

    @GetMapping("/relation")
    @ApiOperation(value = "查询工单关联信息详情")
    @OperationLog()
    public Result<WorkOrderRelevanceInfo> getOrderRelation(@RequestParam @ApiParam(value = "工单id", required = true) String id) throws Exception {
        WorkOrderRelevanceInfo relevanceInfo = buWorkOrderService.selectWorkOrderRelevanceInfo(id);
        return new Result<WorkOrderRelevanceInfo>().successResult(relevanceInfo);
    }

    @GetMapping("/work-records")
    @ApiOperation(value = "作业记录表-查询列表")
    @OperationLog()
    public Result<List<BuWorkOrderTaskFormInst>> listOrderWorkRecord(@RequestParam @ApiParam(value = "工单id", required = true) String id) throws Exception {
        List<BuWorkOrderTaskFormInst> planFormWorkRecordInstList = buRepairPlanFormsService.listOrderWorkRecord(id);
        return new Result<List<BuWorkOrderTaskFormInst>>().successResult(planFormWorkRecordInstList);
    }

    @GetMapping("/work-record/get")
    @ApiOperation(value = "作业记录表-查询明细")
    @OperationLog()
    public Result<BuPlanFormWorkRecord> getFormWorkRecordById(@Validated BuPlanWorkRecordFormQueryVO queryVO) throws Exception {
        BuPlanFormWorkRecord planFormWorkRecord = buRepairPlanFormsService.getFormWorkRecordById(queryVO);
        return new Result<BuPlanFormWorkRecord>().successResult(planFormWorkRecord);
    }

    @PostMapping("/work-record/check")
    @ApiOperation(value = "作业记录表-提交检查结果信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Map<String, String>> checkFormWorkRecordDetail(@RequestBody @Validated BuWorkOrderRecordCheckVO checkVO) throws Exception {
        return buRepairPlanFormsService.checkFormWorkRecordDetail(checkVO);
    }

    @GetMapping("/task/relation")
    @ApiOperation(value = "查询工单任务关联信息详情")
    @OperationLog()
    public Result<BuWorkOrderTaskRelatedInfo> getOrderTaskRelation(@RequestParam @ApiParam(value = "工单任务id", required = true) String id) throws Exception {
        BuWorkOrderTaskRelatedInfo taskRelatedInfo = buWorkOrderTaskService.selectTaskRelatedInfo(id);
        return new Result<BuWorkOrderTaskRelatedInfo>().successResult(taskRelatedInfo);
    }

    @PostMapping("/submit-working-order")
    @ApiOperation(value = "提交工单(作业填报后给工班长)", notes = "工单workStatus=1作业中时，调用，修改工单状态、完成工单流程当前任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submitOrder(@RequestParam @ApiParam(value = "工单id", required = true) String id) throws Exception {
        boolean flag = buWorkOrderTaskService.submitWorkingOrderForApp(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/submit-to-dispatcher")
    @ApiOperation(value = "提交工单(作业完成后给调度)", notes = "工单workStatus=2作业完成时，调用，修改工单状态、完成工单流程当前任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submitOrderToDispatcher(@RequestParam @ApiParam(value = "工单id", required = true) String id) throws Exception {
        boolean flag = buWorkOrderService.submitOrderToDispatcherForApp(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/safeassume/read/add")
    @ApiOperation(value = "新增安全预想阅读记录", notes = "增加安全预想阅读记录后，更新工单任务开始，列计划任务开始，更新车辆当前停放股道;\r\n" +
            "如果安全预想id为空，则不会增加安全预想阅读记录，仅更新任务等信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestParam(required = false) @ApiParam(value = "安全预想id") String safeAssumeId,
                               @RequestParam @ApiParam(value = "工单任务id", required = true) String orderTaskId) throws Exception {
        boolean flag = true;
        if (StringUtils.isNotBlank(safeAssumeId)) {
            flag = buWorkSafeAssumeReadService.addSafeAssumeRead(safeAssumeId);
        }
        if (flag) {
            flag = buWorkOrderTaskService.startOrderTask(orderTaskId);
        }
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/material/save")
    @ApiOperation(value = "物资消耗-保存(新增或修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> addWorkOrderMaterial(@RequestBody @Validated BuWorkOrderMaterial workOrderMaterial) {
        boolean flag = buWorkOrderMaterialService.saveOrUpdate(workOrderMaterial);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/material/delete")
    @ApiOperation(value = "物资消耗-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteWorkOrderMaterial(@RequestParam @ApiParam(value = "工单物料ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderMaterialService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/material/consume")
    @ApiOperation(value = "物资消耗-保存任务实际消耗", notes = "批量更新工单物料")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> updateOrderMaterialList(@RequestBody @Validated List<BuWorkOrderMaterial> materials) throws Exception {
        boolean flag = buWorkOrderMaterialService.updateOrderMaterialList(materials);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/tool/save")
    @ApiOperation(value = "工器具-保存(新增或修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> addTool(@RequestBody @Validated BuWorkOrderTool tool) {
        boolean flag = buWorkOrderToolService.saveOrUpdate(tool);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/tool/delete")
    @ApiOperation(value = "工器具-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteToolBatch(@RequestParam @ApiParam(value = "工器具ids，多个逗号分隔", required = true) String ids) {
        boolean flag = buWorkOrderToolService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/annex/list")
    @ApiOperation(value = "查询工单附件列表")
    @OperationLog()
    public Result<List<BuWorkOrderAnnex>> listAnnex(@RequestParam @ApiParam(value = "工单id", required = true) String orderId,
                                                    @RequestParam(required = false) @ApiParam(value = "工单任务id") String taskId) throws Exception {
        List<BuWorkOrderAnnex> annexList = buWorkOrderAnnexService.listAnnex(orderId, taskId);
        return new Result<List<BuWorkOrderAnnex>>().successResult(annexList);
    }

    @PostMapping("/annex/save")
    @ApiOperation(value = "增加工单附件列表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuWorkOrderAnnexSaveVO orderAnnexSaveVO) throws Exception {
        boolean flag = buWorkOrderAnnexService.saveAnnex(orderAnnexSaveVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/annex/delete")
    @ApiOperation(value = "删除工单附件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工单附件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderAnnexService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/save/work-time")
    @ApiOperation(value = "任务人员安排-保存工时", notes = "任务人员安排为新增的时候，orderTaskId、workstationId、userId、planWorkTime是必填的")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> updateTaskStaffArrangeWorkTime(@RequestBody @Validated List<BuRepairTaskStaffArrange> staffArrangeList) throws Exception {
        Boolean flag = buRepairTaskStaffArrangeService.saveStaffArrangeList(staffArrangeList);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete/work-time/{id}")
    @ApiOperation(value = "任务人员安排-删除工时")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> updateTaskStaffArrangeWorkTime(@ApiParam(value = "id", required = true) @PathVariable("id") String id) throws Exception {
        Boolean flag = buRepairTaskStaffArrangeService.removeById(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/getTaskQRCode")
    @ApiOperation(value = "任务二维码生成")
    @OperationLog()
    public Result<Map<String, String>> getTaskQRCode(@RequestBody @Validated BuWorkOrderRecordCreateTaskQRCodeVO workOrderRecordCreateTaskQRCodeVO) throws Exception {
        Map<String, String> taskQRCode = buWorkOrderService.getTaskQRCode(workOrderRecordCreateTaskQRCodeVO);
        return new Result<Map<String, String>>().successResult(taskQRCode);
    }

    @GetMapping("/getMyCodeValid")
    @ApiOperation(value = "查询二维码是否失效")
    @OperationLog()
    public Result<Boolean> getTaskQRCodeValid(@RequestParam @ApiParam(value = "任务二维码UUID", required = true) String uuid) throws Exception {
        Boolean qrCodeValid = buWorkOrderService.getTaskQRCodeValid(uuid);
        return new Result<Boolean>().successResult(qrCodeValid);
    }

    @GetMapping("/getTaskQRCodeIsScan")
    @ApiOperation(value = "查询二维码是否被扫描")
    @OperationLog()
    public Result<Boolean> getTaskQRCodeIsScan(@RequestParam @ApiParam(value = "任务二维码UUID", required = true) String uuid) throws Exception {
        Boolean qrCodeIsScan = buWorkOrderService.getTaskQRCodeIsScan(uuid);
        return new Result<Boolean>().successResult(qrCodeIsScan);
    }

    @PostMapping("/check/scanTask")
    @ApiOperation(value = "提交检查结果信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> checkScanTaskResult(@RequestBody @Validated BuWorkOrderRecordScanTaskCheckVO checkVO) throws Exception {
        boolean flag = buRepairPlanFormsService.scanCheckFormWorkRecordDetail(checkVO);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping(value = "/getDataJson/{id}")
    @ApiOperation("根据表单id获取dataJson")
    @OperationLog()
    public Result<String> getDataJsonByFormId(@PathVariable @ApiParam(value = "表单id") String id) throws Exception {
        String dataJson = buRepairPlanFormsService.selectFormTemplateDataJsonByFormTemplateId(id);
        return new Result<String>().successResult(dataJson);
    }

}
