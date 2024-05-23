package org.jeecg.modules.board.quality.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuGroupFaultRankingVO;
import org.jeecg.modules.board.quality.bean.vo.BuLatestFaultVO;
import org.jeecg.modules.board.quality.service.BuFaultInfoBoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 质量看板(车间) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-17
 */
@Api(tags = "质量看板(车间)")
@Slf4j
@RestController
@RequestMapping("/board/workshopQuality")
public class BuWorkshopQualityBoardController {

    private final BuFaultInfoBoardService buFaultInfoBoardService;

    public BuWorkshopQualityBoardController(BuFaultInfoBoardService buFaultInfoBoardService) {
        this.buFaultInfoBoardService = buFaultInfoBoardService;
    }


    @GetMapping("/getLatestFault")
    @ApiOperation(value = "查询最新故障信息")
    @OperationLog()
    public Result<BuLatestFaultVO> getLatestFaultInfo(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        BuLatestFaultVO latestFaultVO = buFaultInfoBoardService.getLatestFaultInfo(queryVO);
        return new Result<BuLatestFaultVO>().successResult(latestFaultVO);
    }

    @GetMapping("/getFaultRanking")
    @ApiOperation(value = "查询工班发现故障排名")
    @OperationLog()
    public Result<List<BuGroupFaultRankingVO>> getGroupFaultRanking(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        List<BuGroupFaultRankingVO> groupFaultRankingVOList = buFaultInfoBoardService.getGroupFaultRanking(queryVO);
        return new Result<List<BuGroupFaultRankingVO>>().successResult(groupFaultRankingVOList);
    }

    @GetMapping("/getLatestOutsourceFault")
    @ApiOperation(value = "查询最新委外故障信息")
    @OperationLog()
    public Result<BuLatestFaultVO> getLatestOutsourceFaultInfo(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        BuLatestFaultVO latestFaultVO = buFaultInfoBoardService.getLatestOutsourceFaultInfo(queryVO);
        return new Result<BuLatestFaultVO>().successResult(latestFaultVO);
    }

    @GetMapping("/getFaultSystemCount")
    @ApiOperation(value = "查询故障数量系统分布", notes = "item=系统名称，count=故障数")
    @OperationLog()
    public Result<List<PieChartVO>> getFaultSystemCount(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        List<PieChartVO> pieChartVOList = buFaultInfoBoardService.getFaultSystemCount(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/getDayFaultTrend")
    @ApiOperation(value = "查询故障数量趋势(按天)", notes = "x=日期，y=故障数")
    @OperationLog()
    public Result<List<SingleBarChartVO>> getDayFaultTrend(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        List<SingleBarChartVO> singleBarChartVOList = buFaultInfoBoardService.getDayFaultTrend(queryVO);
        return new Result<List<SingleBarChartVO>>().successResult(singleBarChartVOList);
    }

}

