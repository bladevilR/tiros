package org.jeecg.modules.dispatch.serialplan.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanTaskNoUpdateVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanTaskVOGantt;
import org.jeecg.modules.dispatch.serialplan.service.BuRepairPlanTaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 列计划任务 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Api(tags = "列计划任务")
@Slf4j
@RestController
@RequestMapping("/serialplan/task")
public class BuRepairPlanTaskController {

    private final BuRepairPlanTaskService buRepairPlanTaskService;

    public BuRepairPlanTaskController(BuRepairPlanTaskService buRepairPlanTaskService) {
        this.buRepairPlanTaskService = buRepairPlanTaskService;
    }


    @GetMapping("/get/{id}")
    @ApiOperation(value = "根据id查详情(含关联信息)")
    @OperationLog()
    public Result<BuRepairPlanTaskVOGantt> selectTaskDetail(@PathVariable @ApiParam(value = "任务id") String id) throws Exception {
        BuRepairPlanTaskVOGantt taskVOWithGantt = buRepairPlanTaskService.selectTaskDetail(id);
        return new Result<BuRepairPlanTaskVOGantt>().successResult(taskVOWithGantt);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增或者更新")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdatePlanTask(@RequestBody @Validated BuRepairPlanTaskVOGantt taskVOWithGantt) throws Exception {
        boolean flag = buRepairPlanTaskService.saveOrUpdatePlanTask(taskVOWithGantt);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/updateTaskNoAndWbs")
    @ApiOperation(value = "更新序号和wbs")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3, saveParam = false)
    @ApiImplicitParam(name = "taskNoUpdateVOS", value = "更新信息列表", allowMultiple = true, paramType = "body", dataType = "BuRepairPlanTaskNoUpdateVO")
    public Result<Boolean> updateTaskNoAndWbs(@RequestBody List<BuRepairPlanTaskNoUpdateVO> taskNoUpdateVOS) throws Exception {
        boolean flag = buRepairPlanTaskService.updateTaskNoAndWbs(taskNoUpdateVOS);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除列计划任务（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "任务ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanTaskService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

