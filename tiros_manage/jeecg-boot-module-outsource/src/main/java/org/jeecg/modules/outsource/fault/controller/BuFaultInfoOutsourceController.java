package org.jeecg.modules.outsource.fault.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.outsource.fault.bean.BuFaultInfo;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoCompareQueryVO;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoCompareResultVO;
import org.jeecg.modules.outsource.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.outsource.fault.bean.vo.FaultCountInfoVO;
import org.jeecg.modules.outsource.fault.service.BuFaultInfoOutsourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-19
 */
@Api(tags = "委外故障")
@Slf4j
@RestController
@RequestMapping("/outsource/fault")
public class BuFaultInfoOutsourceController {

    private final BuFaultInfoOutsourceService buFaultInfoOutsourceService;

    public BuFaultInfoOutsourceController(BuFaultInfoOutsourceService buFaultInfoOutsourceService) {
        this.buFaultInfoOutsourceService = buFaultInfoOutsourceService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询委外故障记录（分页）")
    @OperationLog()
    public Result<IPage<BuFaultInfo>> page(@Validated BuFaultInfoQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoOutsourceService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultInfo>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查故障信息")
    @OperationLog()
    public Result<BuFaultInfo> getById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoOutsourceService.getFaultById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/handles")
    @ApiOperation(value = "根据id查故障处理记录")
    @OperationLog()
    public Result<List<BuFaultHandleRecord>> listHandleRecordById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        List<BuFaultHandleRecord> handleRecordList = buFaultInfoOutsourceService.listHandleRecordById(id);
        return new Result<List<BuFaultHandleRecord>>().successResult(handleRecordList);
    }

    @GetMapping("/countFault")
    @ApiOperation(value = "查询故障总数、处理数、未处理数")
    @OperationLog()
    public Result<FaultCountInfoVO> countHandling(@Validated BuFaultInfoQueryVO queryVO) throws Exception {
        FaultCountInfoVO countInfoVO = buFaultInfoOutsourceService.countFault(queryVO);
        return new Result<FaultCountInfoVO>().successResult(countInfoVO);
    }

    @GetMapping("/countFaultGroupBySystem")
    @ApiOperation(value = "查询故障系统分布（统计）")
    @OperationLog()
    public Result<FaultCountInfoVO> countBySystem(@Validated BuFaultInfoQueryVO queryVO) throws Exception {
        FaultCountInfoVO countInfoVO = buFaultInfoOutsourceService.countFaultGroupBySystem(queryVO);
        return new Result<FaultCountInfoVO>().successResult(countInfoVO);
    }

    @PostMapping("/countFaultGroupByPhaseAndSystem")
    @ApiOperation(value = "查询故障阶段和系统分布（比较分析）")
    @OperationLog()
    public Result<BuFaultInfoCompareResultVO> compareCountByPhaseAndSystem(@RequestBody @Validated BuFaultInfoCompareQueryVO compareQueryVO) throws Exception {
        BuFaultInfoCompareResultVO compareResultVO = buFaultInfoOutsourceService.compareCountFaultGroupByPhaseAndSystem(compareQueryVO);
        return new Result<BuFaultInfoCompareResultVO>().successResult(compareResultVO);
    }

}

