package org.jeecg.modules.produce.cost.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.produce.cost.bean.vo.*;
import org.jeecg.modules.produce.cost.service.BuProduceCostMonitorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 物资消耗 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-22
 */
@Api(tags = "成本监控")
@Slf4j
@RestController
@RequestMapping("/produce/cost")
public class BuProduceCostMonitorController {

    private final BuProduceCostMonitorService buProduceCostMonitorService;

    public BuProduceCostMonitorController(BuProduceCostMonitorService buProduceCostMonitorService) {
        this.buProduceCostMonitorService = buProduceCostMonitorService;
    }


    @GetMapping("/detail/page")
    @ApiOperation(value = "明细-查询物资消耗记录(分页)")
    @OperationLog()
    public Result<IPage<BuWorkOrderMaterial>> page(@Validated BuCostQueryVO queryVO,
                                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkOrderMaterial> page = buProduceCostMonitorService.pageOrderMaterial(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkOrderMaterial>>().successResult(page);
    }

    @GetMapping("/detail/count")
    @ApiOperation(value = "明细-查询总金额和分类金额")
    @OperationLog()
    public Result<CostCountVO> countCost(@Validated BuCostQueryVO queryVO) throws Exception {
        CostCountVO costCountVO = buProduceCostMonitorService.countCost(queryVO);
        return new Result<CostCountVO>().successResult(costCountVO);
    }

    @PostMapping("/statistics")
    @ApiOperation(value = "统计-统计数据")
    @OperationLog()
    public Result<CostStatisticsVO> getCostStatistics(@RequestBody @Validated BuCostQueryVO queryVO) throws Exception {
        CostStatisticsVO costStatisticsVO = buProduceCostMonitorService.getCostStatistics(queryVO);
        return new Result<CostStatisticsVO>().successResult(costStatisticsVO);
    }

    @GetMapping("/compare/count-6year")
    @ApiOperation(value = "比较分析-车辆架大修成本趋势(近6年物资消耗)", notes = "趋势没有过滤条件，查询6年内所有消耗")
    @OperationLog()
    public Result<CostCompareYearTrendVO> getCostYearTrend() throws Exception {
        CostCompareYearTrendVO yearTrendVO = buProduceCostMonitorService.getCostYearTrend();
        return new Result<CostCompareYearTrendVO>().successResult(yearTrendVO);
    }

    @PostMapping("/compare")
    @ApiOperation(value = "比较分析-比较数据")
    @OperationLog()
    public Result<BuCostCompareResultVO> compareCostCount(@RequestBody @Validated BuCostCompareQueryVO compareQueryVO) throws Exception {
        BuCostCompareResultVO compareResultVO = buProduceCostMonitorService.compareCostCount(compareQueryVO);
        return new Result<BuCostCompareResultVO>().successResult(compareResultVO);
    }

}

