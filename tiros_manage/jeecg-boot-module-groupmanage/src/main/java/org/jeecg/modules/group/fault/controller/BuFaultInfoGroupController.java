package org.jeecg.modules.group.fault.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.group.fault.bean.BuFaultInfo;
import org.jeecg.modules.group.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.group.fault.service.BuFaultInfoGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 故障管理 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/12
 */
@Api(tags = "已报故障")
@Slf4j
@RestController
@RequestMapping("/group/fault/info")
public class BuFaultInfoGroupController {

    private final BuFaultInfoGroupService buFaultInfoGroupService;

    public BuFaultInfoGroupController(BuFaultInfoGroupService buFaultInfoGroupService) {
        this.buFaultInfoGroupService = buFaultInfoGroupService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询故障记录（分页）")
    @OperationLog()
    public Result<IPage<BuFaultInfo>> page(BuFaultInfoQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoGroupService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultInfo>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查故障信息")
    @OperationLog()
    public Result<BuFaultInfo> getById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoGroupService.getFaultById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/handles")
    @ApiOperation(value = "根据id查故障处理记录")
    @OperationLog()
    public Result<List<BuFaultHandleRecord>> listHandleRecordById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        List<BuFaultHandleRecord> handleRecordList = buFaultInfoGroupService.listHandleRecordById(id);
        return new Result<List<BuFaultHandleRecord>>().successResult(handleRecordList);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改故障信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuFaultInfo buFaultInfo) throws Exception {
        boolean flag = buFaultInfoGroupService.updateFaultInfo(buFaultInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除故障信息（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "故障信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultInfoGroupService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "取消故障（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> cancelBatch(@RequestParam @ApiParam(value = "故障信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultInfoGroupService.cancelBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "提交故障")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submitFault(@RequestParam @ApiParam(value = "故障信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultInfoGroupService.submitFault(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
