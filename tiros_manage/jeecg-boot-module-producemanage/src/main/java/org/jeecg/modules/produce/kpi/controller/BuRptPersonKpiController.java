package org.jeecg.modules.produce.kpi.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.produce.kpi.bean.vo.BuKpiVO;
import org.jeecg.modules.produce.kpi.bean.vo.KpiQueryVO;
import org.jeecg.modules.produce.kpi.service.BuRptPersonKpiService;
import org.jeecg.modules.produce.plan.bean.vo.BuDepotRepairPlanVO;
import org.jeecg.modules.produce.plan.bean.vo.BuRepairPlanVO;
import org.jeecg.modules.produce.plan.service.BuRepairPlanProduceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 个人绩效 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
@Api(tags = "组织贡献")
@Slf4j
@RestController
@RequestMapping("/produce/kpi")
public class BuRptPersonKpiController {

    private final BuRepairPlanProduceService buRepairPlanProduceService;
    private final BuRptPersonKpiService buRptPersonKpiService;

    public BuRptPersonKpiController(BuRepairPlanProduceService buRepairPlanProduceService,
                                    BuRptPersonKpiService buRptPersonKpiService) {
        this.buRepairPlanProduceService = buRepairPlanProduceService;
        this.buRptPersonKpiService = buRptPersonKpiService;
    }


    @GetMapping("/plan/list")
    @ApiOperation(value = "查询列计划列表(按车辆段分组)", notes = "返回列计划列表(按车辆段分组)")
    @OperationLog()
    public Result<List<BuDepotRepairPlanVO>> list() throws Exception {
        List<BuDepotRepairPlanVO> repairPlanVOList = buRepairPlanProduceService.listAllGroupByDepot();
        return new Result<List<BuDepotRepairPlanVO>>().successResult(repairPlanVOList);
    }

    @GetMapping("/plan/get")
    @ApiOperation(value = "查询指定列计划详情")
    @OperationLog()
    public Result<BuRepairPlanVO> get(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        BuRepairPlanVO repairPlanVO = buRepairPlanProduceService.getRepairPlanVOById(planId, false);
        return new Result<BuRepairPlanVO>().successResult(repairPlanVO);
    }

    @GetMapping("/user")
    @ApiOperation(value = "查询个人贡献")
    @OperationLog()
    public Result<BuKpiVO> getUserKpi(@Validated KpiQueryVO queryVO) throws Exception {
        BuKpiVO kpiVO = buRptPersonKpiService.getUserKpi(queryVO);
        return new Result<BuKpiVO>().successResult(kpiVO);
    }

    @GetMapping("/group")
    @ApiOperation(value = "查询班组贡献")
    @OperationLog()
    public Result<BuKpiVO> getGroupKpi(@Validated KpiQueryVO queryVO) throws Exception {
        BuKpiVO kpiVO = buRptPersonKpiService.getGroupKpi(queryVO);
        return new Result<BuKpiVO>().successResult(kpiVO);
    }

}

