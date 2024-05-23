package org.jeecg.modules.quality.measureanalyse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.quality.measureanalyse.bean.vo.BuMeasureAnalyseQueryVO;
import org.jeecg.modules.quality.measureanalyse.service.BuMeasureAnalyseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 测量值分析 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/20
 */
@Api(tags = "测量值分析")
@Slf4j
@RestController
@RequestMapping("/quality/measureAnalyse")
public class BuMeasureAnalyseController {

    private final BuMeasureAnalyseService buMeasureAnalyseService;

    public BuMeasureAnalyseController(BuMeasureAnalyseService buMeasureAnalyseService) {
        this.buMeasureAnalyseService = buMeasureAnalyseService;
    }


    @GetMapping("/getTrend")
    @ApiOperation(value = "查询指定条件测量项值趋势")
    @OperationLog()
    public Result<List<Map<String, Object>>> getMeasureTrend(@Validated BuMeasureAnalyseQueryVO queryVO) throws Exception {
        List<Map<String, Object>> measureTrend = buMeasureAnalyseService.getMeasureTrend(queryVO);
        return new Result<List<Map<String, Object>>>().successResult(measureTrend);
    }

}
