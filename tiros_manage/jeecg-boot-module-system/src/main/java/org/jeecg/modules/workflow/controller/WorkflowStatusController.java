package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.workflow.service.WfBizStatusService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TODO
 *
 * @author jakgong
 * @version V1.0
 * @date 2020/11/13
 */
@Api(tags = "流程状态")
@RequestMapping("/wf/status")
@RestController
public class WorkflowStatusController {

    private final WfBizStatusService wfBizStatusService;

    public WorkflowStatusController(WfBizStatusService wfBizStatusService) {
        this.wfBizStatusService = wfBizStatusService;
    }


    @ApiOperation(value = "通用流程状态改变接口")
    @RequestMapping("/change")
//    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> statusChange(@RequestBody Map<String, Object> requestParam) throws Exception {
        boolean flag = wfBizStatusService.changeWfBizStatus(requestParam);
        return new Result<Boolean>().successResult(flag).success("你已成功调用通用流程状态改变接口，已修改业务流程状态表的值");
    }

}
