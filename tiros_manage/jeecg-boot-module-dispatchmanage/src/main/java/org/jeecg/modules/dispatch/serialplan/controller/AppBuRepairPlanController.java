package org.jeecg.modules.dispatch.serialplan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairReguDetail;
import org.jeecg.modules.dispatch.serialplan.bean.MustMaterialLack;
import org.jeecg.modules.dispatch.serialplan.bean.tp.BuTpRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanQueryVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanRelationVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanVOGantt;
import org.jeecg.modules.dispatch.serialplan.service.BuRepairPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Api(tags = "APP列计划管理")
@Slf4j
@RestController
@AppController
@RequestMapping("/app/serialPlan/plan")
public class AppBuRepairPlanController {

    private final BuRepairPlanService buRepairPlanService;

    public AppBuRepairPlanController(BuRepairPlanService buRepairPlanService) {
        this.buRepairPlanService = buRepairPlanService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询列计划记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairPlan>> page(@Validated BuRepairPlanQueryVO queryVO,
                                            @RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairPlan> page = buRepairPlanService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairPlan>>().successResult(page);
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "根据id查详情(含任务)")
    @OperationLog()
    public Result<BuRepairPlanVOGantt> selectPlanAndTask(@ApiParam(value = "列计划id") @PathVariable String id) throws Exception {
        BuRepairPlanVOGantt planGantt = buRepairPlanService.selectPlanAndTask(id);
        return new Result<BuRepairPlanVOGantt>().successResult(planGantt);
    }



}

