package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.workflow.bean.WorkFlowGroup;
import org.jeecg.common.workflow.service.WorkflowSyncService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 工作流 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/28
 */
@Api(tags = "流程同步")
@Slf4j
@RestController
@RequestMapping("/sys/workflow")
public class WorkflowSyncController {

    private final WorkflowSyncService workflowSyncService;

    public WorkflowSyncController(WorkflowSyncService workflowSyncService) {
        this.workflowSyncService = workflowSyncService;
    }

    @PostMapping("/sync-depart")
    @ApiOperation(value = "同步所有部门信息(含子部门和人员)到工作流"
            , notes = "不请求工作流接口，返回数据到前端，前端发送请求到工作流同步接口")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<WorkFlowGroup> syncDepartInfoToWorkflow() throws Exception {
        WorkFlowGroup topGroup = workflowSyncService.syncDepartInfoToWorkflow();
        return new Result<WorkFlowGroup>().successResult(topGroup);
    }

    @PostMapping("/sync-role")
    @ApiOperation(value = "同步所有角色信息(含人员)到工作流"
            , notes = "不请求工作流接口，返回数据到前端，前端发送请求到工作流同步接口")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<WorkFlowGroup> syncRoleInfoToWorkflow() throws Exception {
        WorkFlowGroup topGroup = workflowSyncService.syncRoleInfoToWorkflow();
        return new Result<WorkFlowGroup>().successResult(topGroup);
    }

}
