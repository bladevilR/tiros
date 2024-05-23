package org.jeecg.modules.board.quality.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;
import org.jeecg.modules.board.quality.bean.vo.BuCenterQualityFaultProgramVO;
import org.jeecg.modules.board.quality.service.BuRptBoardSysFaultService;
import org.jeecg.modules.board.quality.service.BuRptBoardTrainFaultService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 质量看板（中心） 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Api(tags = "质量看板(中心)")
@Slf4j
@RestController
@RequestMapping("/board/centerQuality")
public class BuCenterQualityController {

    private final BuRptBoardTrainFaultService buRptBoardTrainFaultService;
    private final BuRptBoardSysFaultService buRptBoardSysFaultService;

    public BuCenterQualityController(BuRptBoardTrainFaultService buRptBoardTrainFaultService,
                                     BuRptBoardSysFaultService buRptBoardSysFaultService) {
        this.buRptBoardTrainFaultService = buRptBoardTrainFaultService;
        this.buRptBoardSysFaultService = buRptBoardSysFaultService;
    }


    @GetMapping("/getDepotFault")
    @ApiOperation(value = "查询车辆段故障统计列表")
    @OperationLog()
    public Result<List<BuCenterQualityFaultProgramVO>> listDepotFault(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        List<BuCenterQualityFaultProgramVO> programVOList = buRptBoardTrainFaultService.listDepotFault(queryVO);
        return new Result<List<BuCenterQualityFaultProgramVO>>().successResult(programVOList);
    }

    @GetMapping("/getSysFault")
    @ApiOperation(value = "查询质保期故障各系统分布", notes = "x=系统名称，y=故障数")
    @OperationLog()
    public Result<List<SingleBarChartVO>> listWarrantyPeriodSysFault(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        List<SingleBarChartVO> sysFaultVOList = buRptBoardSysFaultService.listWarrantyPeriodSysFault(queryVO);
        return new Result<List<SingleBarChartVO>>().successResult(sysFaultVOList);
    }

    @GetMapping("/getFaultTrend")
    @ApiOperation(value = "查询质保期故障趋势", notes = "type=年月,jeecg=平均故障数,jeebt=实际故障数")
    @OperationLog()
    public Result<List<LineChartVO>> listWarrantyPeriodFaultTrend(@Validated BuBoardQualityQueryVO queryVO) throws Exception {
        List<LineChartVO> faultTrendVOList = buRptBoardTrainFaultService.listWarrantyPeriodFaultTrend(queryVO);
        return new Result<List<LineChartVO>>().successResult(faultTrendVOList);
    }

}
