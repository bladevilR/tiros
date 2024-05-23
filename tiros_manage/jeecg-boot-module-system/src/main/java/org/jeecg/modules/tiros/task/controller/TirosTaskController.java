package org.jeecg.modules.tiros.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.tiros.task.service.WorkOrderGenerateTaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 * 定时任务 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@TirosController
@Slf4j
@Api(tags = "定时任务")
@RestController
@RequestMapping("/task")
public class TirosTaskController {

    private final WorkOrderGenerateTaskService workOrderGenerateTaskService;

    public TirosTaskController(WorkOrderGenerateTaskService workOrderGenerateTaskService) {
        this.workOrderGenerateTaskService = workOrderGenerateTaskService;
    }

    @PostMapping("/order-generate")
    @ApiOperation(value = "自动生成工单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<String> generateOrder(@RequestParam(required = false) @ApiParam(value = "日期，生成哪一天的工单 默认当天") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                        @RequestParam(required = false) @ApiParam(value = "是否生成日期之前应生成但未生成的工单 默认否") Boolean generateEarlierOrder,
                                        @RequestParam(required = false) @ApiParam(value = "列计划id 默认所有") String planId,
                                        @RequestParam(required = false) @ApiParam(value = "是否启动流程 默认否") Boolean startFlow) throws Exception {
        String resultString = workOrderGenerateTaskService.generateOrder(date, generateEarlierOrder, planId, null != startFlow && startFlow);
        return new Result<String>().successResult(resultString);
    }

}
