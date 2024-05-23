package org.jeecg.modules.dispatch.plan.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFarDetail;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanFarDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 远期规划明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Api(tags = "远期规划")
@Slf4j
@RestController
@RequestMapping("/dispatch/plan/far/detail")
public class BuRepairPlanFarDetailController {

    private final BuRepairPlanFarDetailService buRepairPlanFarDetailService;

    public BuRepairPlanFarDetailController(BuRepairPlanFarDetailService buRepairPlanFarDetailService) {
        this.buRepairPlanFarDetailService = buRepairPlanFarDetailService;
    }


    @GetMapping("/listByPlanFarId")
    @ApiOperation(value = "根据远期规划id查询明细列表")
    @OperationLog()
    public Result<List<BuRepairPlanFarDetail>> listByPlanFarId(@RequestParam @ApiParam(value = "远期规划id", required = true) String planFarId) throws Exception {
        List<BuRepairPlanFarDetail> detailList = buRepairPlanFarDetailService.listByPlanFarId(planFarId);
        return new Result<List<BuRepairPlanFarDetail>>().successResult(detailList);
    }

}

