package org.jeecg.modules.board.cost.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.AreaChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuMaterialAlertVO;
import org.jeecg.modules.board.cost.bean.vo.BuWorkshopCostItemVO;
import org.jeecg.modules.board.cost.bean.vo.WorkshopMonthCostData;
import org.jeecg.modules.board.cost.service.BuRptBoardTrainMaterialService;
import org.jeecg.modules.board.cost.service.BuWorkOrderMaterialBoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 成本看板(车间) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-17
 */
@Api(tags = "成本看板(车间)")
@Slf4j
@RestController
@RequestMapping("/board/workshopCost")
public class BuWorkshopCostBoardController {

    private final BuWorkOrderMaterialBoardService buWorkOrderMaterialBoardService;
    private final BuRptBoardTrainMaterialService buRptBoardTrainMaterialService;

    public BuWorkshopCostBoardController(BuWorkOrderMaterialBoardService buWorkOrderMaterialBoardService,
                                         BuRptBoardTrainMaterialService buRptBoardTrainMaterialService) {
        this.buWorkOrderMaterialBoardService = buWorkOrderMaterialBoardService;
        this.buRptBoardTrainMaterialService = buRptBoardTrainMaterialService;
    }


    @GetMapping("/getAreaChart")
    @ApiOperation(value = "查询当年总金额面积图", notes = "x=年月，y=总消耗")
    @OperationLog()
    public Result<List<AreaChartVO>> getYearTotalCostAreaChart(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<AreaChartVO> areaChartVOList = buRptBoardTrainMaterialService.getYearTotalCostAreaChart(queryVO);
        return new Result<List<AreaChartVO>>().successResult(areaChartVOList);
    }

    @GetMapping("/get-month-data")
    @ApiOperation(value = "查询车间成本数据（指定月）")
    @OperationLog()
    public Result<WorkshopMonthCostData> getWorkshopMonthCostData(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        WorkshopMonthCostData monthCostData = buWorkOrderMaterialBoardService.getWorkshopMonthCostData(queryVO);
        return new Result<WorkshopMonthCostData>().successResult(monthCostData);
    }

    @GetMapping("/getCostItem")
    @ApiOperation(value = "查询物料消耗统计", notes = "总物料消耗、必换件消耗、偶换件消耗、耗材消耗")
    @OperationLog()
    public Result<List<BuWorkshopCostItemVO>> listMaterialCostItem(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<BuWorkshopCostItemVO> costItemVOList = buWorkOrderMaterialBoardService.listMaterialCostItem(queryVO);
        return new Result<List<BuWorkshopCostItemVO>>().successResult(costItemVOList);
    }

    @GetMapping("/getCostProportion")
    @ApiOperation(value = "查询物料消耗占比", notes = "item=物料类型，count=总消耗")
    @OperationLog()
    public Result<List<PieChartVO>> listMaterialCostProportion(@Validated BuBoardCostQueryVO queryVO) throws Exception {
        List<PieChartVO> pieChartVOList = buWorkOrderMaterialBoardService.listMaterialCostProportion(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/getThresholdAlert")
    @ApiOperation(value = "查询物料安全库存预警")
    @OperationLog()
    public Result<List<BuMaterialAlertVO>> listMaterialThresholdAlertTopTen() throws Exception {
        List<BuMaterialAlertVO> materialAlertVOList = buWorkOrderMaterialBoardService.listMaterialThresholdAlertTopTen();
        return new Result<List<BuMaterialAlertVO>>().successResult(materialAlertVOList);
    }

}

