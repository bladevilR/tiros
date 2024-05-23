package org.jeecg.modules.tiros.rpt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.tiros.rpt.service.FaultRptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 统计 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-14
 */
@TirosController
@Slf4j
@Api(tags = "统计接口")
@RestController
@RequestMapping("/rpt")
public class TirosRptController {

    private final FaultRptService faultRptService;

    public TirosRptController(FaultRptService faultRptService) {
        this.faultRptService = faultRptService;
    }

    @PostMapping("/rebuild-fault-rpt-data")
    @ApiOperation(value = "重新生成故障统计中间数据")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<String> rebuildFaultRptData() throws Exception {
        String resultString = faultRptService.rebuildFaultRpt(null);
        return new Result<String>().successResult(resultString);
    }

}
