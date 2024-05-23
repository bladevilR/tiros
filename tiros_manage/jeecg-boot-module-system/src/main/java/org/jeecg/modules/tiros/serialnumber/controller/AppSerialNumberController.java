package org.jeecg.modules.tiros.serialnumber.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.modules.tiros.serialnumber.bean.SysSerialNumber;
import org.jeecg.modules.tiros.serialnumber.mapper.SysSerialNumberMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 流水号 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/21
 */
@AppController
@Api(tags = "流水号生成-公共")
@Slf4j
@RestController
@RequestMapping("/app/serialNumber")
public class AppSerialNumberController {

    private final SerialNumberGenerate serialNumberGenerate;

    public AppSerialNumberController(SerialNumberGenerate serialNumberGenerate) {
        this.serialNumberGenerate = serialNumberGenerate;
    }

    @GetMapping("/generate")
    @ApiOperation(value = "根据模块编码生成流水号")
    @OperationLog()
    public Result<String> get(@RequestParam(name = "moduleCode") @ApiParam(name = "moduleCode", value = "moduleCode", required = true) String moduleCode) {
        String serialNumber = serialNumberGenerate.generateSerialNumberByCode(moduleCode);
        return new Result<String>().successResult(serialNumber);
    }

}
