package org.jeecg.modules.board.progress.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticQueryVO;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticVO;
import org.jeecg.modules.board.progress.service.BuRptPlanGroupStatisticService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 列计划班组工单故障填写统计 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-04
 */
@Api(tags = "列计划班组工单故障填写统计")
@Slf4j
@RestController
@RequestMapping("/board/plan-group-statistic")
public class BuRptPlanGroupStatisticController {

    private final BuRptPlanGroupStatisticService buRptPlanGroupStatisticService;

    public BuRptPlanGroupStatisticController(BuRptPlanGroupStatisticService buRptPlanGroupStatisticService) {
        this.buRptPlanGroupStatisticService = buRptPlanGroupStatisticService;
    }


    @GetMapping("/get-last-working-plan")
    @ApiOperation(value = "查询最近一个作业中的列计划")
    @OperationLog()
    public Result<BuRepairPlan> getLastWorkingPlan() throws Exception {
        BuRepairPlan plan = buRptPlanGroupStatisticService.getLastWorkingPlan();
        return new Result<BuRepairPlan>().successResult(plan);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询列计划工单故障填写统计")
    @OperationLog()
    public Result<PlanGroupStatisticVO> listPlanGroupStatistic(@Validated PlanGroupStatisticQueryVO queryVO) throws Exception {
        PlanGroupStatisticVO statisticVO = buRptPlanGroupStatisticService.listPlanGroupStatistic(queryVO);
        return new Result<PlanGroupStatisticVO>().successResult(statisticVO);
    }

}

