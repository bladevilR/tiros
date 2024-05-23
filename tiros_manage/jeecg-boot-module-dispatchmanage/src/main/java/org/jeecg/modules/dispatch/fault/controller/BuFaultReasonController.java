package org.jeecg.modules.dispatch.fault.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.fault.bean.BuFaultReason;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultReasonQueryVO;
import org.jeecg.modules.dispatch.fault.service.BuFaultReasonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 故障原因 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障管理(原因)")
@Slf4j
@RestController
@RequestMapping("/dispatch/fault/reason")
public class BuFaultReasonController {

    private final BuFaultReasonService buFaultReasonService;

    public BuFaultReasonController(BuFaultReasonService buFaultReasonService) {
        this.buFaultReasonService = buFaultReasonService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<IPage<BuFaultReason>> page(@Validated BuFaultReasonQueryVO queryVO,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultReason> page = buFaultReasonService.pageFaultReason(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultReason>>().successResult(page);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> save(@RequestBody @Validated BuFaultReason faultReason) throws Exception {
        boolean flag = buFaultReasonService.saveOrUpdate(faultReason);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "故障原因ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultReasonService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}

