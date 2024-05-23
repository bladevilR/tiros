package org.jeecg.modules.quality.fault.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.quality.fault.bean.BuFaultReason;
import org.jeecg.modules.quality.fault.service.BuFaultReasonQualityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 故障原因 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障管理")
@Slf4j
@RestController
@RequestMapping("/quality/fault/reason")
public class BuFaultReasonQualityController {

    private final BuFaultReasonQualityService buFaultReasonQualityService;

    public BuFaultReasonQualityController(BuFaultReasonQualityService buFaultReasonQualityService) {
        this.buFaultReasonQualityService = buFaultReasonQualityService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询故障原因列表")
    @OperationLog()
    public Result<List<BuFaultReason>> list(@RequestParam(required = false) @ApiParam(value = "所属分类id") String categoryId,
                                            @RequestParam(required = false) @ApiParam(value = "所属故障代码id") String faultCodeId) throws Exception {
        List<BuFaultReason> reasonList = buFaultReasonQualityService.list(categoryId, faultCodeId);
        return new Result<List<BuFaultReason>>().successResult(reasonList);
    }

}

