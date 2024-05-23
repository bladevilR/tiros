package org.jeecg.modules.produce.fault.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.SingleBarChartVO;
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.bean.vo.*;
import org.jeecg.modules.produce.fault.service.BuFaultInfoProduceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 故障管理 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障监控")
@Slf4j
@RestController
@RequestMapping("/produce/fault")
public class BuFaultInfoProduceController {

    private final BuFaultInfoProduceService buFaultInfoProduceService;

    public BuFaultInfoProduceController(BuFaultInfoProduceService buFaultInfoProduceService) {
        this.buFaultInfoProduceService = buFaultInfoProduceService;
    }

    @GetMapping("/detail/page")
    @ApiOperation(value = "明细-查询故障信息记录(分页)")
    @OperationLog()
    public Result<IPage<BuFaultInfo>> page(@Validated BuFaultInfoQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoProduceService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultInfo>>().successResult(page);
    }

    @GetMapping("/detail/get")
    @ApiOperation(value = "明细-根据id查故障信息")
    @OperationLog()
    public Result<BuFaultInfo> get(@RequestParam @ApiParam(value = "故障id", required = true) String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoProduceService.getFaultById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/detail/handles")
    @ApiOperation(value = "明细-根据id查故障处理记录")
    @OperationLog()
    public Result<List<BuFaultHandleRecord>> listHandleRecordById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        List<BuFaultHandleRecord> handleRecordList = buFaultInfoProduceService.listHandleRecordById(id);
        return new Result<List<BuFaultHandleRecord>>().successResult(handleRecordList);
    }

    @GetMapping("/detail/count")
    @ApiOperation(value = "明细-查询故障总数、处理数、未处理数")
    @OperationLog()
    public Result<FaultCountInfoVO> countHandling(@Validated BuFaultInfoQueryVO queryVO) throws Exception {
        FaultCountInfoVO countInfoVO = buFaultInfoProduceService.countFault(queryVO);
        return new Result<FaultCountInfoVO>().successResult(countInfoVO);
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "统计-统计数据")
    @OperationLog()
    public Result<FaultCountInfoVO> countBySystem(@Validated BuFaultInfoQueryVO queryVO) throws Exception {
        FaultCountInfoVO countInfoVO = buFaultInfoProduceService.countFaultGroupBySystem(queryVO);
        return new Result<FaultCountInfoVO>().successResult(countInfoVO);
    }

    @PostMapping("/compare")
    @ApiOperation(value = "比较分析-比较数据")
    @OperationLog()
    public Result<BuFaultInfoCompareResultVO> compareCountByPhaseAndSystem(@RequestBody @Validated BuFaultInfoCompareQueryVO compareQueryVO) throws Exception {
        BuFaultInfoCompareResultVO compareResultVO = buFaultInfoProduceService.compareCountFaultGroupByPhaseAndSystem(compareQueryVO);
        return new Result<BuFaultInfoCompareResultVO>().successResult(compareResultVO);
    }

//    @GetMapping("/time-effect/chart")
//    @ApiOperation(value = "实效分析-对比图形", notes = "x=时间，y=故障数")
//    public Result<List<SingleBarChartVO>> getRepairBeforeAndAfterFaultDiffChart(@Validated BuFaultTimeEffectQueryVO queryVO) throws Exception {
//        List<SingleBarChartVO> faultDiffChart = buFaultInfoProduceService.getRepairBeforeAndAfterFaultDiffChart(queryVO);
//        return new Result<List<SingleBarChartVO>>().successResult(faultDiffChart);
//    }

    @GetMapping("/time-effect/data")
    @ApiOperation(value = "实效分析-表格数据")
    @OperationLog()
    public Result<List<BuFaultTimeEffectResultVO>> listRepairBeforeAndAfterFaultDiffData(@Validated BuFaultTimeEffectQueryVO queryVO) throws Exception {
        List<BuFaultTimeEffectResultVO> faultDiffData = buFaultInfoProduceService.listRepairBeforeAndAfterFaultDiffData(queryVO);
        return new Result<List<BuFaultTimeEffectResultVO>>().successResult(faultDiffData);
    }

}
