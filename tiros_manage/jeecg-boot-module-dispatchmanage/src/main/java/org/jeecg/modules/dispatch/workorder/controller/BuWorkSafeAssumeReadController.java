package org.jeecg.modules.dispatch.workorder.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderTaskService;
import org.jeecg.modules.dispatch.workorder.service.BuWorkSafeAssumeReadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 安全预想阅读记录 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Api(tags = "工班管理——工单作业")
@Slf4j
@RestController
@RequestMapping("/group/safeassume/read")
public class BuWorkSafeAssumeReadController {

    private final BuWorkSafeAssumeReadService buWorkSafeAssumeReadService;
    private final BuWorkOrderTaskService buWorkOrderTaskService;

    public BuWorkSafeAssumeReadController(BuWorkSafeAssumeReadService buWorkSafeAssumeReadService,
                                          BuWorkOrderTaskService buWorkOrderTaskService) {
        this.buWorkSafeAssumeReadService = buWorkSafeAssumeReadService;
        this.buWorkOrderTaskService = buWorkOrderTaskService;
    }


    @PostMapping("/add")
    @ApiOperation(value = "新增安全预想阅读记录", notes = "增加安全预想阅读记录后，更新工单任务开始，列计划任务开始，更新车辆当前停放股道;\r\n" +
            "如果安全预想id为空，则不会增加安全预想阅读记录，仅更新任务等信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestParam(required = false) @ApiParam(value = "安全预想id") String safeAssumeId,
                               @RequestParam @ApiParam(value = "工单任务id", required = true) String orderTaskId) throws Exception {
        boolean flag = true;
        if (StringUtils.isNotBlank(safeAssumeId)) {
            flag = buWorkSafeAssumeReadService.addSafeAssumeRead(safeAssumeId);
        }
        if (flag) {
            flag = buWorkOrderTaskService.startOrderTask(orderTaskId);
        }
        return new Result<Boolean>().successResult(flag);
    }

}

