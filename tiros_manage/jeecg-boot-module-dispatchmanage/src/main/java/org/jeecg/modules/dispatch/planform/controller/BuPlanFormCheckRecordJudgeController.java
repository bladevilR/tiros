package org.jeecg.modules.dispatch.planform.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormCheckRecordJudge;
import org.jeecg.modules.dispatch.planform.bean.vo.CheckRecordJudgeQueryVO;
import org.jeecg.modules.dispatch.planform.service.BuPlanFormCheckRecordJudgeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 质量评定 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Api(tags = "表单-实例")
@Slf4j
@RestController
@RequestMapping("/serialPlan/plan/forms/judge")
public class BuPlanFormCheckRecordJudgeController {

    private final BuPlanFormCheckRecordJudgeService buPlanFormCheckRecordJudgeService;

    public BuPlanFormCheckRecordJudgeController(BuPlanFormCheckRecordJudgeService buPlanFormCheckRecordJudgeService) {
        this.buPlanFormCheckRecordJudgeService = buPlanFormCheckRecordJudgeService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "检查表单质量评定-根据检查表单实例id和检查人查询")
    @OperationLog()
    public Result<List<BuPlanFormCheckRecordJudge>> listJudge(@Validated CheckRecordJudgeQueryVO queryVO) throws Exception {
        List<BuPlanFormCheckRecordJudge> judgeList = buPlanFormCheckRecordJudgeService.listJudge(queryVO);
        return new Result<List<BuPlanFormCheckRecordJudge>>().successResult(judgeList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "检查表单质量评定-根据id查询")
    @OperationLog()
    public Result<BuPlanFormCheckRecordJudge> getJudgeById(@RequestParam @ApiParam(value = "检查表单质量评定id", required = true) String id) throws Exception {
        BuPlanFormCheckRecordJudge checkRecordJudge = buPlanFormCheckRecordJudgeService.getJudgeById(id);
        return new Result<BuPlanFormCheckRecordJudge>().successResult(checkRecordJudge);
    }

    @PostMapping("/save")
    @ApiOperation(value = "检查表单质量评定-新增/修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdateJudge(@RequestBody @Validated BuPlanFormCheckRecordJudge checkRecordJudge) throws Exception {
        boolean flag = buPlanFormCheckRecordJudgeService.saveOrUpdateJudge(checkRecordJudge);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "检查表单质量评定-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchForms(@RequestParam @ApiParam(value = "检查表单质量评定ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buPlanFormCheckRecordJudgeService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
