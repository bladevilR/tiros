package org.jeecg.modules.tiros.wbs.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * wbs更新 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-06
 */
@TirosController
@Api(tags = "wbs更新")
@Slf4j
@RestController
@RequestMapping("/wbs")
public class WbsController {

    private final WbsService wbsService;

    public WbsController(WbsService wbsService) {
        this.wbsService = wbsService;
    }


    @PostMapping("/update")
    @ApiOperation(value = "更新wbs")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateWbs(WbsConf wbsConf) {
        boolean flag = wbsService.updateWbs(wbsConf);
        return new Result<Boolean>().successResult(flag);
    }

}
