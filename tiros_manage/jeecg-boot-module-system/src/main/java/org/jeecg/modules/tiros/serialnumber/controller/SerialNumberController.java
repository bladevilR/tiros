package org.jeecg.modules.tiros.serialnumber.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.TirosController;
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
@TirosController
@Api(tags = "流水号生成")
@Slf4j
@RestController
@RequestMapping("/sys/serialNumber")
public class SerialNumberController {

    private final SerialNumberGenerate serialNumberGenerate;
    private final SysSerialNumberMapper sysSerialNumberMapper;

    public SerialNumberController(SerialNumberGenerate serialNumberGenerate,
                                  SysSerialNumberMapper sysSerialNumberMapper) {
        this.serialNumberGenerate = serialNumberGenerate;
        this.sysSerialNumberMapper = sysSerialNumberMapper;
    }

    @GetMapping("/generate")
    @ApiOperation(value = "根据模块编码生成流水号")
    @OperationLog()
    public Result<String> get(@RequestParam(name = "moduleCode") @ApiParam(name = "moduleCode", value = "moduleCode", required = true) String moduleCode) {
        String serialNumber = serialNumberGenerate.generateSerialNumberByCode(moduleCode);
        return new Result<String>().successResult(serialNumber);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询流水号生成配置")
    @OperationLog()
    public Result<List<SysSerialNumber>> list() {
        List<SysSerialNumber> sysSerialNumberList = sysSerialNumberMapper.selectList(Wrappers.emptyWrapper());
        return new Result<List<SysSerialNumber>>().successResult(sysSerialNumberList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增流水号生成配置")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated SysSerialNumber sysSerialNumber) {
        sysSerialNumberMapper.insert(sysSerialNumber);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改流水号生成配置")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated SysSerialNumber sysSerialNumber) {
        sysSerialNumberMapper.updateById(sysSerialNumber);
        return new Result<Boolean>().successResult(true);
    }

}
