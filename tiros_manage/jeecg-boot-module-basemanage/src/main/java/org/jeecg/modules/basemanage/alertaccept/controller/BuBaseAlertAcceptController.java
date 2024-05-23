package org.jeecg.modules.basemanage.alertaccept.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlertAccept;
import org.jeecg.modules.basemanage.alertaccept.entity.vo.BuBaseAlertAcceptVO;
import org.jeecg.modules.basemanage.alertaccept.service.IBuBaseAlertAcceptService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 预警信息 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-09
 */
@Api(tags = "预警接收管理")
@Slf4j
@RestController
@RequestMapping("/alertAccept")
public class BuBaseAlertAcceptController {

    private final IBuBaseAlertAcceptService alertAcceptService;

    public BuBaseAlertAcceptController(IBuBaseAlertAcceptService alertAcceptService) {
        this.alertAcceptService = alertAcceptService;
    }


    @GetMapping(value = "/list")
    @ApiOperation("查询预警接收信息")
    @OperationLog()
    public Result<List<BuBaseAlertAccept>> queryPageList(@Validated BuBaseAlertAcceptVO baseAlertAcceptVO) {
        List<BuBaseAlertAccept> alertAcceptList = alertAcceptService.listAll(baseAlertAcceptVO);
        return new Result<List<BuBaseAlertAccept>>().successResult(alertAcceptList);
    }

    @PostMapping("/add")
    @ApiOperation("添加接受对象")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuBaseAlertAccept alertAccept) {
        alertAcceptService.saveList(alertAccept);
        return new Result<Boolean>().successResult(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量刪除接受对象")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> alertAcceptDelete(@RequestParam @ApiParam(value = "接受对象ids，多个逗号分割") String ids) {
        boolean flag = alertAcceptService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}
