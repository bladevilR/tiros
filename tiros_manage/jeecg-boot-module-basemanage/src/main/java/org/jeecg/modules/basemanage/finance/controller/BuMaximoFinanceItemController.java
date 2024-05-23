package org.jeecg.modules.basemanage.finance.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.basemanage.finance.bean.BuMaximoFinanceItem;
import org.jeecg.modules.basemanage.finance.service.BuMaximoFinanceItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 财务项目 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
@Api(tags = "财务项目")
@Slf4j
@RestController
@RequestMapping("/base/finance")
public class BuMaximoFinanceItemController {

    private final BuMaximoFinanceItemService buMaximoFinanceItemService;

    public BuMaximoFinanceItemController(BuMaximoFinanceItemService buMaximoFinanceItemService) {
        this.buMaximoFinanceItemService = buMaximoFinanceItemService;
    }

    @GetMapping("/project/list")
    @ApiOperation("查询项目列表")
    @OperationLog()
    public Result<List<BuMaximoFinanceItem>> listProjectItem() throws Exception {
        List<BuMaximoFinanceItem> projectItemList = buMaximoFinanceItemService.listProjectItem();
        return new Result<List<BuMaximoFinanceItem>>().successResult(projectItemList);
    }

    @GetMapping("/task/list")
    @ApiOperation("查询任务列表")
    @OperationLog()
    public Result<List<BuMaximoFinanceItem>> listTaskItem(@RequestParam @ApiParam(value = "项目id", required = true) String projectId) throws Exception {
        List<BuMaximoFinanceItem> taskItemList = buMaximoFinanceItemService.listTaskItemByProjectId(projectId);
        return new Result<List<BuMaximoFinanceItem>>().successResult(taskItemList);
    }

}

