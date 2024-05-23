package org.jeecg.modules.dispatch.fault.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeNew;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeNewService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 090102003, 09 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码
 * -& 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2021-06-29
 */
@Api(tags = "故障编码（新的）")
@Slf4j
@RestController
@RequestMapping("/fault/faultCodeNew")
public class BuFaultCodeNewController {
    private final BuFaultCodeNewService faultCodeNewService;

    public BuFaultCodeNewController(BuFaultCodeNewService faultCodeNewService) {
        this.faultCodeNewService = faultCodeNewService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询故障编码")
    @OperationLog()
    public Result<List<BuFaultCodeNew>> page(@ApiParam(value = "编码或名称") @RequestParam String searchText) throws Exception {
        return new Result<List<BuFaultCodeNew>>().successResult(faultCodeNewService.selectList(searchText));
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据上级故障编码，查询故障编码列表", notes = "上级编码为空表示为获取顶级")
    @OperationLog()
    public Result<List<BuFaultCodeNew>> get(@ApiParam(value = "上级编码") @RequestParam String code) throws Exception {
        return new Result<List<BuFaultCodeNew>>().successResult(faultCodeNewService.selectByParentCode(code));
    }

    @GetMapping("/parent/get")
    @ApiOperation(value = "根据故障编码，查询故障所有上级编码列表")
    @OperationLog()
    public Result<List<BuFaultCodeNew>> getParentCode(@ApiParam(value = "编码") @RequestParam String code) throws Exception {
        return new Result<List<BuFaultCodeNew>>().successResult(faultCodeNewService.selectParentCodeByCode(code));
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增故障编码")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuFaultCodeNew faultCodeNew) throws Exception {
        boolean flag = faultCodeNewService.save(faultCodeNew);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改故障编码")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> update(@RequestBody @Validated BuFaultCodeNew faultCodeNew) throws Exception {
        boolean flag = faultCodeNewService.updateById(faultCodeNew);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除故障编码(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "故障编码ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = faultCodeNewService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }


}

