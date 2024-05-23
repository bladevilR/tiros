package org.jeecg.modules.produce.plan.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.DictIgnore;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.produce.plan.bean.vo.*;
import org.jeecg.modules.produce.plan.service.BuRepairPlanProduceService;
import org.jeecg.modules.produce.plan.service.BuRepairPlanTaskProduceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 列计划 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Api(tags = "计划监控")
@Slf4j
@RestController
@RequestMapping("/produce/plan")
public class BuRepairPlanProduceController {

    private final BuRepairPlanProduceService buRepairPlanProduceService;
    private final BuRepairPlanTaskProduceService buRepairPlanTaskProduceService;

    public BuRepairPlanProduceController(BuRepairPlanProduceService buRepairPlanProduceService,
                                         BuRepairPlanTaskProduceService buRepairPlanTaskProduceService) {
        this.buRepairPlanProduceService = buRepairPlanProduceService;
        this.buRepairPlanTaskProduceService = buRepairPlanTaskProduceService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询列计划列表(按线路分组)", notes = "返回列计划列表(按线路分组)")
    @OperationLog()
    public Result<List<BuLineRepairPlanVO>> list() throws Exception {
        List<BuLineRepairPlanVO> repairPlanVOList = buRepairPlanProduceService.listAllGroupByLine();
        return new Result<List<BuLineRepairPlanVO>>().successResult(repairPlanVOList);
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "查询指定列计划详情")
    @OperationLog()
    public Result<BuRepairPlanVO> get(@PathVariable @ApiParam(value = "计划id") String id) throws Exception {
        BuRepairPlanVO repairPlanVO = buRepairPlanProduceService.getRepairPlanVOById(id, true);
        return new Result<BuRepairPlanVO>().successResult(repairPlanVO);
    }

    @GetMapping("/task/listByPlanId/{planId}")
    @ApiOperation(value = "列计划方式-查询列计划任务列表")
    @OperationLog()
    public Result<List<BuRepairPlanTaskVOGantt>> listTaskByPlanId(@PathVariable @ApiParam(value = "计划id") String planId) throws Exception {
        List<BuRepairPlanTaskVOGantt> taskVOWithGanttList = buRepairPlanTaskProduceService.listByPlanId(planId);
        return new Result<List<BuRepairPlanTaskVOGantt>>().successResult(taskVOWithGanttList);
    }

    @GetMapping("/task/get/{id}")
    @ApiOperation(value = "列计划方式-查询指定任务详情")
    @OperationLog()
    public Result<BuRepairPlanTaskVOGantt> getTask(@PathVariable @ApiParam(value = "任务id") String id) throws Exception {
        BuRepairPlanTaskVOGantt taskVOWithGantt = buRepairPlanTaskProduceService.selectTaskDetail(id);
        return new Result<BuRepairPlanTaskVOGantt>().successResult(taskVOWithGantt);
    }

    @GetMapping("/regu/list/{id}")
    @ApiOperation(value = "规程结构方式-查询规程进度列表")
    @OperationLog()
    public Result<List<BuRepairPlanReguVO>> listReguVOByPlanId(@PathVariable @ApiParam(value = "计划id") String id) throws Exception {
        List<BuRepairPlanReguVO> reguVOList = buRepairPlanProduceService.listReguVOByPlanId(id);
        return new Result<List<BuRepairPlanReguVO>>().successResult(reguVOList);
    }

    @GetMapping("/train-struct/list/{id}")
    @ApiOperation(value = "车辆结构方式-查询车辆结构进度列表")
    @DictIgnore
    @OperationLog()
    public Result<List<BuRepairPlanTrainStructVO>> listTrainStructVOByPlanId(@PathVariable @ApiParam(value = "计划id") String id) throws Exception {
        List<BuRepairPlanTrainStructVO> trainStructVOList = buRepairPlanProduceService.listTrainStructVOByPlanId(id);
        return new Result<List<BuRepairPlanTrainStructVO>>().successResult(trainStructVOList);
    }

    @GetMapping("/train-struct/orders/{orderIds}")
    @ApiOperation(value = "车辆结构方式-查询工单列表", notes = "此处orderIds取自“查询车辆结构进度列表”返回对象的orderIds")
    @OperationLog()
    public Result<List<BuWorkOrderVO>> listWorkOrderVOByOrderIds(@PathVariable @ApiParam(value = "工单ids，查询车辆结构进度列表返回对象的orderIds") String orderIds) throws Exception {
        List<BuWorkOrderVO> workOrderBOList = buRepairPlanProduceService.listWorkOrderVOByOrderIds(orderIds);
        return new Result<List<BuWorkOrderVO>>().successResult(workOrderBOList);
    }

//    @GetMapping("/relevanceInfo/{id}")
//    @ApiOperation(value = "查询指定任务关联的信息详情")
//    public Result<TaskRelevanceInfo> relevanceInfo(@ApiParam(value = "任务id", name = "id") @PathVariable String id) {
//        Result<TaskRelevanceInfo> result = new Result<>();
//        try {
//            TaskRelevanceInfo relevanceInfo = buRepairPlanTaskProduceService.selectTaskRelevanceInfo(id);
//            result.success(CommonConstant.RESULT_OK).setResult(relevanceInfo);
//        } catch (JeecgBootException je) {
//            log.error(je.getMessage(), je);
//            result.error500(je.getMessage());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            result.error500(CommonConstant.RESULT_ERROR);
//        }
//        return result;
//    }

//    @GetMapping("/relevanceInfo/{id}")
//    @ApiOperation(value = "查询指定列计划关联的信息详情")
//    public Result<PlanRelevanceInfo> relevanceInfo(@ApiParam(value = "计划id", name = "id") @PathVariable String id) {
//        Result<PlanRelevanceInfo> result = new Result<>();
//        try {
//            PlanRelevanceInfo relevanceInfo = buRepairPlanProduceService.selectPlanRelevanceInfo(id);
//            result.success(CommonConstant.RESULT_OK).setResult(relevanceInfo);
//        } catch (JeecgBootException je) {
//            log.error(je.getMessage(), je);
//            result.error500(je.getMessage());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            result.error500(CommonConstant.RESULT_ERROR);
//        }
//        return result;
//    }

}

