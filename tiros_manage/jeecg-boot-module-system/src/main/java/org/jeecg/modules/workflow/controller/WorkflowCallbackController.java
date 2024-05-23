package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.workflow.service.WorkflowCallbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 流程接口转发 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Slf4j
@Api(tags = "流程回调")
@RestController
@RequestMapping("/wf/callback")
public class WorkflowCallbackController {

    private final WorkflowCallbackService workflowCallbackService;

    public WorkflowCallbackController(WorkflowCallbackService workflowCallbackService) {
        this.workflowCallbackService = workflowCallbackService;
    }


    @PostMapping("/complete/change-status")
    @ApiOperation(value = "流程结束改变对应业务状态及处理相关逻辑")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> statusChange(@RequestBody Map<String, Object> requestParam) throws Exception {
        boolean flag = workflowCallbackService.completeChangeStatus(requestParam);
        return new Result<Boolean>().successResult(flag).success("成功回调接口：流程结束改变对应业务状态及处理相关逻辑");
    }

}
