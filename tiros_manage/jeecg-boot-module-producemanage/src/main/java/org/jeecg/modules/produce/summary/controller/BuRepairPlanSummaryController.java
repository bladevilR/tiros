package org.jeecg.modules.produce.summary.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.produce.summary.bean.TotalInfo;
import org.jeecg.modules.produce.summary.bean.cost.CostInfo;
import org.jeecg.modules.produce.summary.bean.fault.FaultInfo;
import org.jeecg.modules.produce.summary.bean.outsource.OutsourceInfo;
import org.jeecg.modules.produce.summary.bean.period.PeriodInfo;
import org.jeecg.modules.produce.summary.bean.problem.RepairProblem;
import org.jeecg.modules.produce.summary.bean.progress.ProgressInfo;
import org.jeecg.modules.produce.summary.bean.worktime.WorkTimeInfo;
import org.jeecg.modules.produce.summary.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 维修列计划总结 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/4
 */
@Api(tags = "维修列计划总结")
@Slf4j
@RestController
@RequestMapping("/produce/plan/summary")
public class BuRepairPlanSummaryController {

    private final PlanSummaryTotalService planSummaryTotalService;
    private final PlanSummaryProgressService planSummaryProgressService;
    private final PlanSummaryPeriodService planSummaryPeriodService;
    private final PlanSummaryCostService planSummaryCostService;
    private final PlanSummaryFaultService planSummaryFaultService;
    private final PlanSummaryOutsourceService planSummaryOutsourceService;
    private final PlanSummaryWorkTimeService planSummaryWorkTimeService;
    private final PlanSummaryProblemService planSummaryProblemService;

    public BuRepairPlanSummaryController(PlanSummaryTotalService planSummaryTotalService,
                                         PlanSummaryProgressService planSummaryProgressService,
                                         PlanSummaryPeriodService planSummaryPeriodService,
                                         PlanSummaryCostService planSummaryCostService,
                                         PlanSummaryFaultService planSummaryFaultService,
                                         PlanSummaryOutsourceService planSummaryOutsourceService,
                                         PlanSummaryWorkTimeService planSummaryWorkTimeService,
                                         PlanSummaryProblemService planSummaryProblemService) {
        this.planSummaryTotalService = planSummaryTotalService;
        this.planSummaryProgressService = planSummaryProgressService;
        this.planSummaryPeriodService = planSummaryPeriodService;
        this.planSummaryCostService = planSummaryCostService;
        this.planSummaryFaultService = planSummaryFaultService;
        this.planSummaryOutsourceService = planSummaryOutsourceService;
        this.planSummaryWorkTimeService = planSummaryWorkTimeService;
        this.planSummaryProblemService = planSummaryProblemService;
    }


    @GetMapping("/total")
    @ApiOperation(value = "总体情况")
    @OperationLog()
    public Result<TotalInfo> getTotalInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        TotalInfo totalInfo = planSummaryTotalService.getTotalInfo(planId);
        return new Result<TotalInfo>().successResult(totalInfo);
    }

    @GetMapping("/progress")
    @ApiOperation(value = "生产进度控制情况")
    @OperationLog()
    public Result<ProgressInfo> getProgressInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        ProgressInfo progressInfo = planSummaryProgressService.getProgressInfo(planId);
        return new Result<ProgressInfo>().successResult(progressInfo);
    }

    @GetMapping("/period")
    @ApiOperation(value = "维修周期控制情况")
    @OperationLog()
    public Result<PeriodInfo> getPeriodInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        PeriodInfo periodInfo = planSummaryPeriodService.getPeriodInfo(planId);
        return new Result<PeriodInfo>().successResult(periodInfo);
    }

    @GetMapping("/cost")
    @ApiOperation(value = "维修成本情况")
    @OperationLog()
    public Result<CostInfo> getCostInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        CostInfo costInfo = planSummaryCostService.getCostInfo(planId);
        return new Result<CostInfo>().successResult(costInfo);
    }

    @GetMapping("/fault")
    @ApiOperation(value = "维修质量控制情况")
    @OperationLog()
    public Result<FaultInfo> getFaultInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        FaultInfo faultInfo = planSummaryFaultService.getFaultInfo(planId);
        return new Result<FaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/outsource")
    @ApiOperation(value = "委外维修部件维修进度控制情况")
    @OperationLog()
    public Result<OutsourceInfo> getOutsourceInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        OutsourceInfo outsourceInfo = planSummaryOutsourceService.getOutsourceInfo(planId);
        return new Result<OutsourceInfo>().successResult(outsourceInfo);
    }

    @GetMapping("/work-time")
    @ApiOperation(value = "维修作业工时控制情况")
    @OperationLog()
    public Result<WorkTimeInfo> getWorkTimeInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        WorkTimeInfo workTimeInfo = planSummaryWorkTimeService.getWorkTimeInfo(planId);
        return new Result<WorkTimeInfo>().successResult(workTimeInfo);
    }

    @GetMapping("/problem")
    @ApiOperation(value = "维修过程中存在的问题及处理措施")
    @OperationLog()
    public Result<List<RepairProblem>> getRepairProblem(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        List<RepairProblem> repairProblem = planSummaryProblemService.getRepairProblem(planId);
        return new Result<List<RepairProblem>>().successResult(repairProblem);
    }

}
