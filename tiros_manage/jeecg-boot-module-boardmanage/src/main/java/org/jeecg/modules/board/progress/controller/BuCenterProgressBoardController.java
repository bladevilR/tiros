package org.jeecg.modules.board.progress.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;
import org.jeecg.modules.board.progress.bean.vo.BuPlanYearProgressProgramVO;
import org.jeecg.modules.board.progress.service.BuRepairPlanYearBoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 进度看板（中心） 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-02
 */
@Api(tags = "进度看板（中心）")
@Slf4j
@RestController
@RequestMapping("/board/centerProgress")
public class BuCenterProgressBoardController {

    private final BuRepairPlanYearBoardService buRepairPlanYearBoardService;

    public BuCenterProgressBoardController(BuRepairPlanYearBoardService buRepairPlanYearBoardService) {
        this.buRepairPlanYearBoardService = buRepairPlanYearBoardService;
    }


    @GetMapping("/listProgress")
    @ApiOperation(value = "查询架大修完成数据统计列表")
    @OperationLog()
    public Result<List<BuPlanYearProgressProgramVO>> listPlanYearProgress(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<BuPlanYearProgressProgramVO> programVOList = buRepairPlanYearBoardService.listPlanYearProgress(queryVO);
        return new Result<List<BuPlanYearProgressProgramVO>>().successResult(programVOList);
    }

    @GetMapping("/finishQuality")
    @ApiOperation(value = "查询架大修完工质量比例", notes = "item=完工类型，count=数量")
    @OperationLog()
    public Result<List<PieChartVO>> getFinishQuality(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<PieChartVO> pieChartVOList = buRepairPlanYearBoardService.getFinishQuality(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/burnDownChartData")
    @ApiOperation(value = "查询架大修年计划燃尽数据", notes = "type=月份，jeecg=计划剩余数，jeebt=实际剩余数")
    @OperationLog()
    public Result<List<LineChartVO>> getBurnDownChartData(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<LineChartVO> lineChartVOList = buRepairPlanYearBoardService.getBurnDownChartData(queryVO);
        return new Result<List<LineChartVO>>().successResult(lineChartVOList);
    }

}

