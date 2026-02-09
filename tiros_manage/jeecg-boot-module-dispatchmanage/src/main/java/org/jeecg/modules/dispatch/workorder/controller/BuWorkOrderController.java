package org.jeecg.modules.dispatch.workorder.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialApplyDetail;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialAssignDetail;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.bean.WorkOrderRelevanceInfo;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuMaterialApplyOrderCreateVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderQueryVO;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderMaterialService;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderService;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 工单 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Api(tags = "调度管理——工单管理")
@Slf4j
@RestController
@RequestMapping("/workorder/order")
public class BuWorkOrderController {

    private final BuWorkOrderService buWorkOrderService;
    private final BuWorkOrderMaterialService buWorkOrderMaterialService;
    private final IBuProductionNoticeService productionNoticeService;


    public BuWorkOrderController(BuWorkOrderService buWorkOrderService,
                                 BuWorkOrderMaterialService buWorkOrderMaterialService,
                                 IBuProductionNoticeService productionNoticeService) {
        this.buWorkOrderService = buWorkOrderService;
        this.buWorkOrderMaterialService = buWorkOrderMaterialService;
        this.productionNoticeService = productionNoticeService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工单(分页)")
    @OperationLog()
    public Result<IPage<BuWorkOrder>> page(BuWorkOrderQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkOrder> page = buWorkOrderService.page(queryVO, pageNo, pageSize, false);
        return new Result<IPage<BuWorkOrder>>().successResult(page);
    }

    @GetMapping("/relevanceInfo/{id}")
    @ApiOperation(value = "工单关联的信息详情")
    @OperationLog()
    public Result<WorkOrderRelevanceInfo> relevanceInfo(@ApiParam(value = "工单id") @PathVariable String id) throws Exception {
        WorkOrderRelevanceInfo relevanceInfo = buWorkOrderService.selectWorkOrderRelevanceInfo(id);
        return new Result<WorkOrderRelevanceInfo>().successResult(relevanceInfo);
    }

    @GetMapping("/list-pending-production-notice")
    @ApiOperation(value = "查询可关联的待执行生产通知单")
    @OperationLog()
    public Result<List<BuProductionNotice>> listPendingProductionNotice(@RequestParam(required = false) String lineId,
                                                                        @RequestParam(required = false) String trainNo) {
        List<BuProductionNotice> notices = productionNoticeService.listPendingTechnicalNotices(lineId, trainNo);
        return new Result<List<BuProductionNotice>>().successResult(notices);
    }

    @GetMapping("/list-material-assign")
    @ApiOperation(value = "查询工单物料的发放明细")
    @OperationLog()
    public Result<List<BuMaterialAssignDetail>> listOrderMaterialAssign(@RequestParam @ApiParam(value = "工单物料id", required = true) String orderMaterialId) throws Exception {
        List<BuMaterialAssignDetail> assignDetailList = buWorkOrderService.listOrderMaterialAssign(orderMaterialId);
        return new Result<List<BuMaterialAssignDetail>>().successResult(assignDetailList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增工单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuWorkOrder order) throws Exception {
        boolean flag = buWorkOrderService.saveWorkOrder(order);
        if (flag) {
            buWorkOrderService.startOrderWorkflow(order);
        }
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/add-material-apply-order")
    @ApiOperation(value = "新增发料工单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> addMaterialApplyOrder(@RequestBody @Validated BuMaterialApplyOrderCreateVO createVO) throws Exception {
        boolean flag = buWorkOrderService.addMaterialApplyOrder(createVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改工单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuWorkOrder order) throws Exception {
        boolean flag = buWorkOrderService.editWorkOrder(order);
        if (flag) {
            buWorkOrderService.startOrderWorkflow(order);
        }
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/verify")
    @ApiOperation(value = "核实工单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> verify(@RequestParam @ApiParam(value = "工单id", required = true) String id) throws Exception {
        boolean flag = buWorkOrderService.verifyOrder(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除工单(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/issuing")
    @ApiOperation(value = "下发工单(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> issuing(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔,未下发的才可以", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.issuingWorkOrderBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/close")
    @ApiOperation(value = "关闭工单(批量完成流程默认审批通过)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> closeOrder(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.closeOrderBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/suspend")
    @ApiOperation(value = "暂停工单（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> suspendOrder(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.suspendOrderBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/activity")
    @ApiOperation(value = "激活工单（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> activityOrder(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.activityOrderBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/approveClose")
    @ApiOperation(value = "审批关闭工单(批量完成流程默认审批通过)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> approveCloseOrder(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.approveOrderBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/submit-wf")
    @ApiOperation(value = "启动工单(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> startOrderWorkflow(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.startOrderWorkflowBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/set-order-working")
    @ApiOperation(value = "设置工单填报中(批量)", notes = "设置工单状态填报中，设置工单任务状态已开始（未开始的不设置）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setOrderStatusWorking(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.setOrderStatusWorking(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/material/list-must-as-applyDetail")
    @ApiOperation(value = "根据工单id查询必换件物料（包装成领用明细）")
    @OperationLog()
    public Result<List<BuMaterialApplyDetail>> listOrderMustMaterialAsApplyDetail(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        List<BuMaterialApplyDetail> detailList = buWorkOrderMaterialService.listOrderMustMaterialAsApplyDetail(orderId);
        return new Result<List<BuMaterialApplyDetail>>().successResult(detailList);
    }

//    @GetMapping("/forms")
//    @ApiOperation(value = "表单-查询列计划和工单表单-根据工单id")
//    public Result<List<BuRepairPlanForms>> recordFormByOrderId(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
//        List<BuRepairPlanForms> planFormsList = buWorkOrderService.listPlanFormsByOrderId(orderId);
//        return new Result<List<BuRepairPlanForms>>().successResult(planFormsList);
//    }

    @PostMapping("/cancel")
    @ApiOperation(value = "取消工单(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> cancelOrderWorkflow(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderService.cancelOrderWorkflowBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

