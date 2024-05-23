package org.jeecg.modules.dispatch.workorder.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormDataRecord;
import org.jeecg.modules.dispatch.planform.service.BuRepairPlanFormsService;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.vo.*;
import org.jeecg.modules.dispatch.workorder.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-09
 */
@Api(tags = "工班管理——工单作业")
@Slf4j
@RestController
@RequestMapping("/workGroup/workRecord")
public class BuWorkGroupWorkRecordController {

    private final BuWorkOrderTaskService buWorkOrderTaskService;
    private final BuRepairPlanFormsService buRepairPlanFormsService;
    private final BuWorkOrderMaterialService buWorkOrderMaterialService;
    private final BuWorkOrderToolService buWorkOrderToolService;
    private final BuOutsourceOutinService buOutsourceOutinService;
    private final BuRepairTaskStaffArrangeService buRepairTaskStaffArrangeService;
    private final BuWorkOrderService buWorkOrderService;

    public BuWorkGroupWorkRecordController(BuWorkOrderTaskService buWorkOrderTaskService,
                                           BuRepairPlanFormsService buRepairPlanFormsService,
                                           BuWorkOrderMaterialService buWorkOrderMaterialService,
                                           BuWorkOrderToolService buWorkOrderToolService,
                                           BuOutsourceOutinService buOutsourceOutinService,
                                           BuRepairTaskStaffArrangeService buRepairTaskStaffArrangeService,
                                           BuWorkOrderService buWorkOrderService) {
        this.buWorkOrderTaskService = buWorkOrderTaskService;
        this.buRepairPlanFormsService = buRepairPlanFormsService;
        this.buWorkOrderMaterialService = buWorkOrderMaterialService;
        this.buWorkOrderToolService = buWorkOrderToolService;
        this.buOutsourceOutinService = buOutsourceOutinService;
        this.buRepairTaskStaffArrangeService = buRepairTaskStaffArrangeService;
        this.buWorkOrderService = buWorkOrderService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "任务-查询列表")
    @OperationLog()
    public Result<List<BuWorkOrderTask>> list(@Validated BuWorkOrderTaskQueryVO queryVO) throws Exception {
        List<BuWorkOrderTask> workOrderTasks = buWorkOrderTaskService.listTask(queryVO);
        return new Result<List<BuWorkOrderTask>>().successResult(workOrderTasks);
    }

    @PostMapping("/save/work-time")
    @ApiOperation(value = "任务人员安排-保存工时", notes = "任务人员安排为新增的时候，orderTaskId、workstationId、userId、planWorkTime是必填的")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> updateTaskStaffArrangeWorkTime(@RequestBody @Validated List<BuRepairTaskStaffArrange> staffArrangeList) throws Exception {
        Boolean flag = buRepairTaskStaffArrangeService.saveStaffArrangeList(staffArrangeList);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/arrange/delete")
    @ApiOperation(value = "任务人员安排-删除（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "任务人员安排ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairTaskStaffArrangeService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/submit-task")
    @ApiOperation(value = "任务-提交")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submitTask(@RequestBody @Validated BuWorkOrderTaskSubmitVO submitVO) throws Exception {
        boolean flag = buWorkOrderTaskService.submitTask(submitVO);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/recordForm")
    @ApiOperation(value = "表单-查询工单任务表单")
    @OperationLog()
    public Result<List<BuWorkOrderTaskFormInst>> recordForm(@RequestParam @ApiParam(value = "任务id", required = true) String taskId) throws Exception {
        List<BuWorkOrderTaskFormInst> planFormsList = buWorkOrderTaskService.listPlanFormsByTaskId(taskId);
        return new Result<List<BuWorkOrderTaskFormInst>>().successResult(planFormsList);
    }

    @PostMapping("/save/form-result")
    @ApiOperation(value = "表单-在线表单(数据记录表)填写", notes = "后端更新填写状态，前端需更新“填写人员id”和“填写日期”")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2, saveParam = false)
    public Result<Boolean> saveFormDataRecordResult(@RequestBody @Validated BuPlanFormDataRecord buPlanFormDataRecord) throws Exception {
        boolean flag = buRepairPlanFormsService.saveFormDataRecordResult(buPlanFormDataRecord);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/check")
    @ApiOperation(value = "表单-保存作业记录表明细检查")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Map<String, String>> checkFormWorkRecordDetail(@RequestBody @Validated BuWorkOrderRecordCheckVO checkVO) throws Exception {
        return buRepairPlanFormsService.checkFormWorkRecordDetail(checkVO);
    }

    @PostMapping("/check/saveExcelData")
    @ApiOperation(value = "表单-保存作业记录表明细检查exceldata结果")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7, saveParam = false)
    public Result<Boolean> saveFormWorkRecordExcelData(@RequestBody @Validated FromExcelDataVO excelDataVO) throws Exception {
        boolean flag = buRepairPlanFormsService.saveFormWorkRecordExcelData(excelDataVO);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/get/excelData/{id}")
    @ApiOperation(value = "获取作业记录表exceldata结果")
    @OperationLog()
    public Result<String> getFormWorkRecordExcelData(@PathVariable @ApiParam(value = "实例id") String id) throws Exception {
        String dataJson = buRepairPlanFormsService.getFormWorkRecordExcelData(id);
        return new Result<String>().successResult(dataJson);
    }

    @PostMapping("/work-record/save-manufNo")
    @ApiOperation(value = "表单-保存作业记录表的原/新部件序列号")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> saveFormWorkRecordManufNo(@RequestParam @ApiParam(value = "作业记录表实例id", required = true) String id,
                                                     @RequestParam @ApiParam(value = "原部件序列号") String manufNo,
                                                     @RequestParam @ApiParam(value = "现部件序列号") String manufNoUp) throws Exception {
        boolean flag = buRepairPlanFormsService.saveFormWorkRecordManufNo(id, manufNo, manufNoUp);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/check-record/set-time")
    @ApiOperation(value = "表单-设置检查记录表作业时间")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setCheckRecordItemWorkTime(@RequestParam @ApiParam(value = "检查记录表(实例)检查项明细ids，多个逗号分隔", required = true) String itemIds,
                                                      @RequestParam(required = false) @ApiParam(value = "作业时间 yyyy-MM-dd HH:mm:ss") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date workTime) throws Exception {
        boolean flag = buRepairPlanFormsService.setCheckRecordItemWorkTime(itemIds, workTime);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/check-record/set-check")
    @ApiOperation(value = "表单-保存检查记录表检查项结果")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> setCheckRecordItemCheckResult(@RequestBody @Validated FormCheckRecordCheckVO checkVO) throws Exception {
        boolean flag = buRepairPlanFormsService.checkCheckRecordItem(checkVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/check-record/delete")
    @ApiOperation(value = "删除自检互检专检(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteWorkOrderMaterial(@RequestBody @Validated DelWorkRecordCheckVO delWorkRecordCheckVO) throws Exception {
        boolean flag = buRepairPlanFormsService.deleteBatchCheckResult(delWorkRecordCheckVO);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/list/material")
    @ApiOperation(value = "物资-根据工单/任务/类型查询")
    @OperationLog()
    public Result<List<BuWorkOrderMaterial>> listOrderMaterial(@Validated BuOrderMaterialQueryVO queryVO) throws Exception {
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialService.listOrderMaterial(queryVO);
        return new Result<List<BuWorkOrderMaterial>>().successResult(orderMaterialList);
    }

    @PostMapping("/save/material")
    @ApiOperation(value = "物资-保存（新增或修改）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveWorkOrderMaterial(@RequestBody @Validated BuWorkOrderMaterial workOrderMaterial) throws Exception {
        boolean flag = buWorkOrderMaterialService.saveOrderMaterial(workOrderMaterial);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/save/actMaterial")
    @ApiOperation(value = "物资实际消耗-保存（新增或修改）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrderMaterialAct(@RequestBody @Validated BuWorkOrderMaterial workOrderMaterial) throws Exception {
        boolean flag = buWorkOrderMaterialService.saveOrderMaterialAct(workOrderMaterial);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete/material")
    @ApiOperation(value = "物资-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteWorkOrderMaterial(@RequestParam @ApiParam(value = "工单物料ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderMaterialService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/save/tool")
    @ApiOperation(value = "工器具-保存")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> addTool(@RequestBody @Validated BuWorkOrderTool tool) throws Exception {
        boolean flag = buWorkOrderToolService.saveOrderTool(tool);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete/tool")
    @ApiOperation(value = "工器具-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteToolBatch(@RequestParam @ApiParam(value = "工单工器具ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderToolService.deleteOrderTool(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/relevanceInfo/{id}")
    @ApiOperation(value = "关联信息-查询任务关联的信息详情")
    @OperationLog()
    public Result<BuWorkOrderTaskRelatedInfo> relevanceInfo(@PathVariable @ApiParam(value = "任务id", required = true) String id) throws Exception {
        BuWorkOrderTaskRelatedInfo taskRelatedInfo = buWorkOrderTaskService.selectTaskRelatedInfo(id);
        return new Result<BuWorkOrderTaskRelatedInfo>().successResult(taskRelatedInfo);
    }

    @GetMapping("/outsourceOutin")
    @ApiOperation(value = "委外-查询交接信息")
    @OperationLog()
    public Result<BuOutsourceOutin> outsourceOutin(@RequestParam @ApiParam(value = "工单任务Id", required = true) String id) throws Exception {
        BuOutsourceOutin outsourceOutin = buOutsourceOutinService.selectOutsourceOutinByTaskId(id);
        return new Result<BuOutsourceOutin>().successResult(outsourceOutin);
    }

    @PostMapping("/save/outsourceOutin")
    @ApiOperation(value = "委外-保存交接信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOutsourceOutin(@RequestBody @Validated BuOutsourceOutin outin) throws Exception {
        Boolean flag = buOutsourceOutinService.saveOutsourceOutin(outin);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/outin/not-returns")
    @ApiOperation(value = "委外-查询未返厂设备", notes = "查询未生成入库明细的出库明细，转成入库明细(未返厂状态)返回")
    @OperationLog()
    public Result<List<BuOutsourceOutinDetail>> listNotReturnAssetAsOutinDetail(@RequestParam @ApiParam(value = "车号", required = true) String trainNo,
                                                                                @RequestParam(required = false) @ApiParam(value = "设备类型id") String assetTypeId,
                                                                                @RequestParam(required = false) @ApiParam(value = "设备名称") String assetName) throws Exception {
        List<BuOutsourceOutinDetail> outinDetailList = buOutsourceOutinService.listNotReturnAssetAsOutinDetail(trainNo, assetTypeId, assetName);
        return new Result<List<BuOutsourceOutinDetail>>().successResult(outinDetailList);
    }

    @GetMapping("/relevanceInfo/bookSteps/{id}")
    @ApiOperation(value = "关联信息-查询任务关联的作业指导书")
    @OperationLog()
    public Result<List<BuWorkOrderBookStep>> relevanceInfoOfBookSteps(@PathVariable @ApiParam(value = "任务id", required = true) String id) throws Exception {
        List<BuWorkOrderBookStep> bookSteps = buWorkOrderTaskService.selectTaskRelatedInfoOfBookSteps(id);
        return new Result<List<BuWorkOrderBookStep>>().successResult(bookSteps);
    }

    @PostMapping("/getTaskQRCode")
    @ApiOperation(value = "任务二维码生成")
    @OperationLog()
    public Result<Map<String, String>> getTaskQRCode(@RequestBody @Validated BuWorkOrderRecordCreateTaskQRCodeVO workOrderRecordCreateTaskQRCodeVO) throws Exception {
        Map<String, String> taskQRCode = buWorkOrderService.getTaskQRCode(workOrderRecordCreateTaskQRCodeVO);
        return new Result<Map<String, String>>().successResult(taskQRCode);
    }

}
