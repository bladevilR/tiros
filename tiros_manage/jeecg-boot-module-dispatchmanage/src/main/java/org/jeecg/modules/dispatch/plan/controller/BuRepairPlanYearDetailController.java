package org.jeecg.modules.dispatch.plan.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearAutoGenerateVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearDetailVOWithTaskGantt;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanYearDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 年计划明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Api(tags = "年计划管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/plan/year/detail")
public class BuRepairPlanYearDetailController {

    private final BuRepairPlanYearDetailService buRepairPlanYearDetailService;

    public BuRepairPlanYearDetailController(BuRepairPlanYearDetailService buRepairPlanYearDetailService) {
        this.buRepairPlanYearDetailService = buRepairPlanYearDetailService;
    }


    @GetMapping("/list-by-year")
    @ApiOperation(value = "查询指定年份的年计划明细列表")
    @OperationLog()
    public Result<List<BuRepairPlanYearDetail>> listByYearAndStatus(@RequestParam @ApiParam(value = "年份", required = true) Integer year,
                                                                    @RequestParam(required = false) @ApiParam(value = "年计划明细状态") Integer status) throws Exception {
        List<BuRepairPlanYearDetail> planYearDetailList = buRepairPlanYearDetailService.listByYearAndStatus(year, status);
        return new Result<List<BuRepairPlanYearDetail>>().successResult(planYearDetailList);
    }

    @GetMapping("/autoGenerate")
    @ApiOperation(value = "自动生成年计划详情")
    @OperationLog()
    public Result<List<BuRepairPlanYearDetail>> autoGenerate(@Validated BuRepairPlanYearAutoGenerateVO generateVO) throws Exception {
        List<BuRepairPlanYearDetail> planYearDetailList = buRepairPlanYearDetailService.autoGenerate(generateVO);
        return new Result<List<BuRepairPlanYearDetail>>().successResult(planYearDetailList);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存年计划明细列表(甘特)", notes = "删除年计划下的所有原明细信息，重新插入")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> saveGanttList(@RequestBody List<BuRepairPlanYearDetailVOWithTaskGantt> ganttList) throws Exception {
        boolean flag = buRepairPlanYearDetailService.saveGanttList(ganttList);
        return new Result<Boolean>().successResult(flag);
    }

}

