package org.jeecg.modules.quality.check.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quality.check.bean.BuWorkOrderRecordCheck;
import org.jeecg.modules.quality.check.service.BuWorkOrderRecordCheckQualityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 作业记录明细检查信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Api(tags = "质量检查")
@Slf4j
@RestController
@RequestMapping("/quality/check")
public class BuWorkOrderRecordCheckQualityController {

    private final BuWorkOrderRecordCheckQualityService buWorkOrderRecordCheckQualityService;

    public BuWorkOrderRecordCheckQualityController(BuWorkOrderRecordCheckQualityService buWorkOrderRecordCheckQualityService) {
        this.buWorkOrderRecordCheckQualityService = buWorkOrderRecordCheckQualityService;
    }


    @PostMapping("/submitCheck")
    @ApiOperation(value = "提交检查信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submitCheck(@RequestBody @Validated BuWorkOrderRecordCheck buWorkOrderRecordCheck) throws Exception {
        boolean flag = buWorkOrderRecordCheckQualityService.submitCheck(buWorkOrderRecordCheck);
        return new Result<Boolean>().successResult(flag);
    }

}

