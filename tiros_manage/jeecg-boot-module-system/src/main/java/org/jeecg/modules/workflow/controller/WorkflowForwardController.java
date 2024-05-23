package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.workflow.bean.vo.QueryCandidateVO;
import org.jeecg.common.workflow.bean.vo.StartVO;
import org.jeecg.common.workflow.bean.vo.TaskCompleteVO;
import org.jeecg.common.workflow.bean.vo.TaskInfoVO;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 流程接口转发 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Slf4j
@Api(tags = "流程转发")
@RestController
@RequestMapping("/wf/forward")
public class WorkflowForwardController {

    private final WorkflowForwardService workflowForwardService;

    public WorkflowForwardController(WorkflowForwardService workflowForwardService) {
        this.workflowForwardService = workflowForwardService;
    }


    @PostMapping("/instance/start/solution")
    @ApiOperation(value = "启动指定业务的流程")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> clearInvalidStock(@RequestBody @Validated StartVO startVO) throws Exception {
        boolean flag = workflowForwardService.startSolution(startVO);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/instance/queryCandidate")
    @ApiOperation(value = "获取当前用户指定流程的可办理任务")
    @OperationLog()
    public Result<List<TaskInfoVO>> listCurrentUserProcessCanHandleTask(@Validated QueryCandidateVO queryCandidateVO) throws Exception {
        List<TaskInfoVO> objectList = workflowForwardService.listCurrentUserProcessCanHandleTask(queryCandidateVO);
        return new Result<List<TaskInfoVO>>().successResult(objectList);
    }

    @PostMapping("/task/complete")
    @ApiOperation(value = "完成指定任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> clearInvalidStock(@RequestBody @Validated TaskCompleteVO taskCompleteVO) throws Exception {
        boolean flag = workflowForwardService.completeTask(taskCompleteVO);
        return new Result<Boolean>().successResult(flag);
    }

}
