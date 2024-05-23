package org.jeecg.modules.quality.fault.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quality.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.quality.fault.bean.BuFaultInfo;
import org.jeecg.modules.quality.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.quality.fault.service.BuFaultInfoQualityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 故障管理 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障管理")
@Slf4j
@RestController
@RequestMapping("/quality/fault/info")
public class BuFaultInfoQualityController {

    private final BuFaultInfoQualityService buFaultInfoQualityService;

    public BuFaultInfoQualityController(BuFaultInfoQualityService buFaultInfoQualityService) {
        this.buFaultInfoQualityService = buFaultInfoQualityService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询故障信息记录（分页）")
    @OperationLog()
    public Result<IPage<BuFaultInfo>> page(@Validated BuFaultInfoQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoQualityService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultInfo>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查故障信息")
    @OperationLog()
    public Result<BuFaultInfo> getById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoQualityService.getFaultById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/handles")
    @ApiOperation(value = "根据id查故障处理记录")
    @OperationLog()
    public Result<List<BuFaultHandleRecord>> listHandleRecordById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        List<BuFaultHandleRecord> handleRecordList = buFaultInfoQualityService.listHandleRecordById(id);
        return new Result<List<BuFaultHandleRecord>>().successResult(handleRecordList);
    }

}

