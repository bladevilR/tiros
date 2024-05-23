package org.jeecg.modules.quality.fault.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.quality.fault.bean.BuFaultSolution;
import org.jeecg.modules.quality.fault.service.BuFaultSolutionQualityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 故障处理措施 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障管理")
@Slf4j
@RestController
@RequestMapping("/quality/fault/solution")
public class BuFaultSolutionQualityController {

    private final BuFaultSolutionQualityService buFaultSolutionQualityService;

    public BuFaultSolutionQualityController(BuFaultSolutionQualityService buFaultSolutionQualityService) {
        this.buFaultSolutionQualityService = buFaultSolutionQualityService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询故障处理措施列表")
    @OperationLog()
    public Result<List<BuFaultSolution>> list(@RequestParam(required = false) @ApiParam(value = "所属分类id") String categoryId,
                                              @RequestParam(required = false) @ApiParam(value = "所属故障代码id") String faultCodeId,
                                              @RequestParam(required = false) @ApiParam(value = "所属故障原因id") String faultReasonId) throws Exception {
        List<BuFaultSolution> solutionList = buFaultSolutionQualityService.list(categoryId, faultCodeId, faultReasonId);
        return new Result<List<BuFaultSolution>>().successResult(solutionList);
    }

}

