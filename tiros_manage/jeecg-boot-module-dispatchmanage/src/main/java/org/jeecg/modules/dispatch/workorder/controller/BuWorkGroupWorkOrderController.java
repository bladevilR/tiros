package org.jeecg.modules.dispatch.workorder.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialApply;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialReturned;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.bean.WorkOrderRelevanceInfo;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderQueryVO;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-09
 */
@Api(tags = "工班管理——工单管理")
@Slf4j
@RestController
@RequestMapping("/workGroup/workOrder")
public class BuWorkGroupWorkOrderController {

    private final BuWorkOrderService buWorkOrderService;

    public BuWorkGroupWorkOrderController(BuWorkOrderService buWorkOrderService) {
        this.buWorkOrderService = buWorkOrderService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工单（分页）")
    @OperationLog()
    public Result<IPage<BuWorkOrder>> page(@Validated BuWorkOrderQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkOrder> page = buWorkOrderService.page(queryVO, pageNo, pageSize, true);
        return new Result<IPage<BuWorkOrder>>().successResult(page);
    }

    @GetMapping("/apply/page")
    @ApiOperation(value = "查询领用单（分页）")
    @OperationLog()
    public Result<IPage<BuWorkOrder>> applyPage(@Validated BuWorkOrderQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkOrder> page = buWorkOrderService.applyPage(queryVO, pageNo, pageSize, true);
        return new Result<IPage<BuWorkOrder>>().successResult(page);
    }

    @GetMapping("/relevanceInfo/{id}")
    @ApiOperation(value = "工单关联的信息详情")
    @OperationLog()
    public Result<WorkOrderRelevanceInfo> relevanceInfo(@ApiParam(value = "工单id", name = "id") @PathVariable String id) throws Exception {
        WorkOrderRelevanceInfo relevanceInfo = buWorkOrderService.selectWorkOrderRelevanceInfo(id);
        return new Result<WorkOrderRelevanceInfo>().successResult(relevanceInfo);
    }

    @PostMapping("/start")
    @ApiOperation(value = "工单开始")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> startOrder(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        boolean flag = buWorkOrderService.startOrder(orderId);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/submit-to-dispatcher")
    @ApiOperation(value = "提交工单给调度(批量)", notes = "success=true表示成功；success=false，code=9001表示有工单任务未完成")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submitOrderToDispatchBatch(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids,
                                                      @RequestParam(required = false) @ApiParam(value = "是否强制提交") Boolean force) throws Exception {
        boolean flag = buWorkOrderService.submitOrderToDispatcherBatch(ids, force);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/material/commit-temp-apply")
    @ApiOperation(value = "物料领用-提交临时物料申请")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> commitTempMaterialApply(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        boolean flag = buWorkOrderService.submitTempMaterialApply(orderId);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/material/apply/list")
    @ApiOperation(value = "物料领用-根据工单id获取")
    @OperationLog()
    public Result<List<BuMaterialApply>> listOrderMaterialApply(@RequestParam @ApiParam(value = "工单id", required = true) String orderId,
                                                                @RequestParam(required = false) @ApiParam(value = "领用类型 字典dictCode=bu_material_apply_type") Integer applyType) throws Exception {
        List<BuMaterialApply> applyList = buWorkOrderService.listOrderMaterialApply(orderId, applyType);
        return new Result<List<BuMaterialApply>>().successResult(applyList);
    }

    @GetMapping("/material/return/list")
    @ApiOperation(value = "物料退料-根据工单id获取")
    @OperationLog()
    public Result<List<BuMaterialReturned>> listOrderMaterialReturn(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        List<BuMaterialReturned> returnedList = buWorkOrderService.listOrderMaterialReturn(orderId);
        return new Result<List<BuMaterialReturned>>().successResult(returnedList);
    }

}

