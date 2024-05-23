package org.jeecg.modules.dispatch.fault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCode;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeLevel;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCodeQueryVO;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeLevelService;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 故障代码 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-29
 */
@Api(tags = "故障管理(代码)")
@Slf4j
@RestController
@RequestMapping("/dispatch/fault/code")
public class BuFaultCodeController {

    private final BuFaultCodeService buFaultCodeService;

    private final BuFaultCodeLevelService buFaultCodeLevelService;

    public BuFaultCodeController(BuFaultCodeService buFaultCodeService, BuFaultCodeLevelService buFaultCodeLevelService) {
        this.buFaultCodeService = buFaultCodeService;
        this.buFaultCodeLevelService = buFaultCodeLevelService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<IPage<BuFaultCode>> page(@Validated BuFaultCodeQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultCode> page = buFaultCodeService.pageFaultCode(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultCode>>().successResult(page);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> save(@RequestBody @Validated BuFaultCode faultCode) throws Exception {
        boolean flag = buFaultCodeService.saveOrUpdate(faultCode);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "故障代码ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultCodeService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/faultCodeLevel")
    @ApiOperation(value = "故障码等级信息")
    @OperationLog()
    public Result<BuFaultCodeLevel> FindFaultCodeLevelById(@RequestParam @ApiParam(value = "故障码id", required = true) String id) throws Exception {
        BuFaultCodeLevel buFaultCodeLevel = buFaultCodeLevelService.FindFaultCodeLevelById(id);
        return new Result<BuFaultCodeLevel>().successResult(buFaultCodeLevel);
    }


}
