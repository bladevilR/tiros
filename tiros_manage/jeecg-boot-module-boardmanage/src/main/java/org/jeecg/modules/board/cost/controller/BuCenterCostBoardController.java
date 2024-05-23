package org.jeecg.modules.board.cost.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.AreaChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuRptBoardTrainMaterialProgramVO;
import org.jeecg.modules.board.cost.service.BuWorkOrderMaterialBoardService;
import org.jeecg.modules.board.cost.service.BuRptBoardTrainMaterialService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 成本看板（中心）（物料统计） 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
@Api(tags = "成本看板(中心)")
@Slf4j
@RestController
@RequestMapping("/board/centerCost")
public class BuCenterCostBoardController {

    private final BuRptBoardTrainMaterialService buRptBoardTrainMaterialService;
    private final BuWorkOrderMaterialBoardService buWorkOrderMaterialBoardService;

    public BuCenterCostBoardController(BuRptBoardTrainMaterialService buRptBoardTrainMaterialService,
                                       BuWorkOrderMaterialBoardService buWorkOrderMaterialBoardService) {
        this.buRptBoardTrainMaterialService = buRptBoardTrainMaterialService;
        this.buWorkOrderMaterialBoardService = buWorkOrderMaterialBoardService;
    }


    @GetMapping("/getAreaChart")
    @ApiOperation(value = "查询当年总金额面积图", notes = "x=年月，y=总消耗")
    @OperationLog()
    public Result<List<AreaChartVO>> getYearTotalCostAreaChart(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<AreaChartVO> areaChartVOList = buRptBoardTrainMaterialService.getYearTotalCostAreaChart(queryVO);
        return new Result<List<AreaChartVO>>().successResult(areaChartVOList);
    }

    @GetMapping("/getDepotCost")
    @ApiOperation(value = "查询车辆段物资成本统计列表")
    @OperationLog()
    public Result<List<BuRptBoardTrainMaterialProgramVO>> listMaterialCost(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<BuRptBoardTrainMaterialProgramVO> programVOList = buRptBoardTrainMaterialService.listMaterialCost(queryVO);
        return new Result<List<BuRptBoardTrainMaterialProgramVO>>().successResult(programVOList);
    }

    @GetMapping("/getMaterialCostTopTen")
    @ApiOperation(value = "查询物资消耗成本前10", notes = "item=物资名称，count=总消耗")
    @OperationLog()
    public Result<List<PieChartVO>> getMaterialCostTopTen(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<PieChartVO> pieChartVOList = buWorkOrderMaterialBoardService.getMaterialCostTopTen(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/getLastTenTrainNo")
    @ApiOperation(value = "查询最近的10列车车号")
    @OperationLog()
    public Result<List<String>> getLastTenTrainNo(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<String> trainNoList = buRptBoardTrainMaterialService.getLastTenTrainNo(queryVO);
        return new Result<List<String>>().successResult(trainNoList);
    }

    @GetMapping("/getLastTenTrainCost")
    @ApiOperation(value = "查询最近的10列车物资消耗成本")
    @OperationLog()
    public Result<List<Map<String, Object>>> getLastTenTrainCost(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<Map<String, Object>> rptBoardTrainMaterialList = buRptBoardTrainMaterialService.getLastTenTrainCost(queryVO);
        return new Result<List<Map<String, Object>>>().successResult(rptBoardTrainMaterialList);
    }

}