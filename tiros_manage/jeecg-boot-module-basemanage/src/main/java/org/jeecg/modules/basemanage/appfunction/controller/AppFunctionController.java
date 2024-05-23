package org.jeecg.modules.basemanage.appfunction.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.basemanage.appfunction.bean.AppFunction;
import org.jeecg.modules.basemanage.appfunction.service.AppFunctionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * APP功能模块 用于设置工作台功能 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@AppController
@Api(tags = "功能权限-公共")
@RestController
@RequestMapping("/app/function")
public class AppFunctionController {

    private final AppFunctionService appFunctionService;

    public AppFunctionController(AppFunctionService appFunctionService) {
        this.appFunctionService = appFunctionService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询当前用户的app功能菜单(列表)")
    @OperationLog()
    public Result<List<AppFunction>> listCurrentUserFunction() throws Exception {
        List<AppFunction> scheduleList = appFunctionService.listCurrentUserFunction();
        return new Result<List<AppFunction>>().successResult(scheduleList);
    }

}

