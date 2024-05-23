package org.jeecg.modules.report.cost.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.cost.bean.vo.BuCostSystemTotalCountVO;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostSingleTrainQueryVO;
import org.jeecg.modules.report.cost.service.BuRptBoardSysMaterialService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 物质消耗明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-07
 */
@Api(tags = "物质消耗汇总(单列)")
@Slf4j
@RestController
@RequestMapping("/report/cost/train")
public class BuReportCostSingleTrainController {

    private final BuRptBoardSysMaterialService buRptBoardSysMaterialService;

    public BuReportCostSingleTrainController(BuRptBoardSysMaterialService buRptBoardSysMaterialService) {
        this.buRptBoardSysMaterialService = buRptBoardSysMaterialService;
    }


    @GetMapping("/get-cars-number")
    @ApiOperation(value = "查询车辆车厢数量")
    @OperationLog()
    public Result<Integer> getTrainCarsNumber(@RequestParam @ApiParam(required = true, value = "线路id") String lineId,
                                              @RequestParam @ApiParam(required = true, value = "车号") String trainNo) throws Exception {
        Integer carsNumber = buRptBoardSysMaterialService.getTrainCarsNumber(lineId, trainNo);
        return new Result<Integer>().successResult(carsNumber);
    }

    @GetMapping("/statistic")
    @ApiOperation(value = "查询车辆各系统消耗金额")
    @OperationLog()
    public Result<List<BuCostSystemTotalCountVO>> getCostSystemStatistic(@Validated BuReportCostSingleTrainQueryVO queryVO) throws Exception {
        List<BuCostSystemTotalCountVO> systemTotalCountVOList = buRptBoardSysMaterialService.getCostSystemStatistic(queryVO);
        return new Result<List<BuCostSystemTotalCountVO>>().successResult(systemTotalCountVOList);
    }

//    @GetMapping("/total-statistic")
//    @ApiOperation(value = "查询车辆累计消耗金额")
//    public Result<List<BuCostSystemTotalCountVO>> getCostSystemTotalStatistic(@Validated BuReportCostSingleTrainQueryVO queryVO) throws Exception {
//        List<BuCostSystemTotalCountVO> systemTotalCountVOList = buRptBoardSysMaterialService.getCostSystemTotalStatistic(queryVO);
//        return new Result<List<BuCostSystemTotalCountVO>>().successResult(systemTotalCountVOList);
//    }

}