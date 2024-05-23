package org.jeecg.modules.tiros.uuv.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.tiros.uuv.service.UUVService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuyougen
 * @title: UUVController
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/2717:02
 */
@TirosController
@Api(tags = "UUV同步")
@Slf4j
@RestController
@RequestMapping("/uuv")
public class UUVController {

    private final UUVService uuvService;

    public UUVController(UUVService uuvService) {
        this.uuvService = uuvService;
    }


    @GetMapping(value = "/sync")
    @ApiOperation(value = "同步组织人员")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> syncDeptAndUser(@RequestParam(required = false) @ApiParam(value = "是否同步人员") Boolean syncUser,
                                          @RequestParam(required = false) @ApiParam(value = "同步的车间id") String workshopId) throws Exception {
        String syncDeptAndUserInfo = uuvService.syncDeptAndUser((null == syncUser || syncUser), null, workshopId);
        return new Result<String>().successResult(syncDeptAndUserInfo);
    }

}
