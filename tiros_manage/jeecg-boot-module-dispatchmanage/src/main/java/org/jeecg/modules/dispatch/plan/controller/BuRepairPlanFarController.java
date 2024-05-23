package org.jeecg.modules.dispatch.plan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFar;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearAmountVO;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanFarService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 远期规划 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Api(tags = "远期规划")
@Slf4j
@RestController
@RequestMapping("/dispatch/plan/far")
public class BuRepairPlanFarController {

    private final BuRepairPlanFarService buRepairPlanFarService;

    public BuRepairPlanFarController(BuRepairPlanFarService buRepairPlanFarService) {
        this.buRepairPlanFarService = buRepairPlanFarService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询远期规划记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairPlanFar>> page(@Validated BuRepairPlanFarYearQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairPlanFar> page = buRepairPlanFarService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairPlanFar>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询远期规划及明细")
    @OperationLog()
    public Result<BuRepairPlanFar> get(@RequestParam @ApiParam(value = "远期规划id", required = true) String id) throws Exception {
        BuRepairPlanFar repairPlanFar = buRepairPlanFarService.selectById(id);
        return new Result<BuRepairPlanFar>().successResult(repairPlanFar);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增远期规划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuRepairPlanFar buRepairPlanFar) throws Exception {
        boolean flag = buRepairPlanFarService.savePlanFar(buRepairPlanFar);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改远期规划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuRepairPlanFar buRepairPlanFar) throws Exception {
        boolean flag = buRepairPlanFarService.updatePlanFar(buRepairPlanFar);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除远期规划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "远期规划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanFarService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/getPlanAmount")
    @ApiOperation(value = "根据年份和车辆段获取修程数量", tags = "年计划管理")
    @OperationLog()
    public Result<BuRepairPlanYearAmountVO> getPlanAmount(@RequestParam @ApiParam(value = "年份，int类型", required = true) Integer year,
                                                          @RequestParam(required = false) @ApiParam(value = "车辆段id，非必需") String depotId) throws Exception {
        BuRepairPlanYearAmountVO yearAmountVO = buRepairPlanFarService.getPlanAmountByYearAndDepotId(year, depotId);
        return new Result<BuRepairPlanYearAmountVO>().successResult(yearAmountVO);
    }

}

