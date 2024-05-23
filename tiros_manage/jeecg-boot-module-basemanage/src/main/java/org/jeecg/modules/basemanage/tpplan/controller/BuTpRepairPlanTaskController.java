package org.jeecg.modules.basemanage.tpplan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanTaskNoUpdateVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanTaskVOGantt;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanTaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 计划模板任务 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-18
 */
@Api(tags = "计划模板")
@Slf4j
@RestController
@RequestMapping("/tp-plan/task")
public class BuTpRepairPlanTaskController {

    private final BuTpRepairPlanTaskService buTpRepairPlanTaskService;

    public BuTpRepairPlanTaskController(BuTpRepairPlanTaskService buTpRepairPlanTaskService) {
        this.buTpRepairPlanTaskService = buTpRepairPlanTaskService;
    }


    @GetMapping("/detail/{id}")
    @ApiOperation(value = "任务-根据id查详情(含关联信息)")
    @OperationLog()
    public Result<BuTpRepairPlanTaskVOGantt> selectTaskDetail(@PathVariable @ApiParam(value = "任务id") String id) throws Exception {
        BuTpRepairPlanTaskVOGantt taskVOWithGantt = buTpRepairPlanTaskService.selectTaskDetail(id);
        return new Result<BuTpRepairPlanTaskVOGantt>().successResult(taskVOWithGantt);
    }

    @PostMapping("/save")
    @ApiOperation(value = "任务-保存（新增/修改）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7, saveParam = false)
    public Result<Boolean> saveOrUpdatePlanTask(@RequestBody @Validated BuTpRepairPlanTaskVOGantt taskVOWithGantt) throws Exception {
        boolean flag = buTpRepairPlanTaskService.saveOrUpdatePlanTask(taskVOWithGantt);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/update-no-wbs")
    @ApiOperation(value = "任务-更新序号和wbs")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3, saveParam = false)
    @ApiImplicitParam(name = "taskNoUpdateVOS", value = "更新信息列表", allowMultiple = true, paramType = "body", dataType = "BuTpRepairPlanTaskNoUpdateVO")
    public Result<Boolean> updateTaskNoAndWbs(@RequestBody List<BuTpRepairPlanTaskNoUpdateVO> taskNoUpdateVOS) throws Exception {
        boolean flag = buTpRepairPlanTaskService.updateTaskNoAndWbs(taskNoUpdateVOS);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "任务-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchPlanTask(@RequestParam @ApiParam(value = "任务id,多个用英文逗号隔开") String ids) throws Exception {
        boolean flag = buTpRepairPlanTaskService.deleteBatchPlanTaskByIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
