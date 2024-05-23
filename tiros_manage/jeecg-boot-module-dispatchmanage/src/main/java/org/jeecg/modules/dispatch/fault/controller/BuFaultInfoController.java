package org.jeecg.modules.dispatch.fault.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.workflow.constant.WfConstant;
import org.jeecg.common.workflow.service.WfBizStatusService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.dispatch.fault.bean.BuFaultInfo;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.dispatch.fault.service.BuFaultInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 故障管理 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/fault/info")
public class BuFaultInfoController {

    private final BuFaultInfoService buFaultInfoService;
    private final WfBizStatusService wfBizStatusService;

    public BuFaultInfoController(BuFaultInfoService buFaultInfoService,
                                 WfBizStatusService wfBizStatusService) {
        this.buFaultInfoService = buFaultInfoService;
        this.wfBizStatusService = wfBizStatusService;
    }


    @GetMapping("/list-fault-wfStatus")
    @ApiOperation(value = "查询故障流程审批节点（即审批流程状态）")
    @OperationLog()
    public Result<List<String>> listFaultWfStatus() throws Exception {
        List<String> processStatusList = wfBizStatusService.listProcessStatusBySolutionCode(WfConstant.SOLUTION_CODE_FAULT);
        return new Result<List<String>>().successResult(processStatusList);
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询故障信息记录(分页)")
    @OperationLog()
    public Result<IPage<BuFaultInfo>> page(@Validated BuFaultInfoQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultInfo>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查故障信息")
    @OperationLog()
    public Result<BuFaultInfo> getById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoService.getFaultById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/handles")
    @ApiOperation(value = "根据id查故障处理记录")
    @OperationLog()
    public Result<List<BuFaultHandleRecord>> listHandleRecordById(@RequestParam @ApiParam(value = "故障信息id", required = true) String id) throws Exception {
        List<BuFaultHandleRecord> handleRecordList = buFaultInfoService.listHandleRecordById(id);
        return new Result<List<BuFaultHandleRecord>>().successResult(handleRecordList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增故障信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuFaultInfo buFaultInfo) throws Exception {
        boolean flag = buFaultInfoService.saveFaultInfo(buFaultInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改故障信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuFaultInfo buFaultInfo) throws Exception {
        boolean flag = buFaultInfoService.updateFaultInfo(buFaultInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除故障(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "故障信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultInfoService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "取消故障(批量)", notes = "已处理/已关闭的不能取消")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> cancelBatch(@RequestParam @ApiParam(value = "故障信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultInfoService.cancelBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/close")
    @ApiOperation(value = "关闭故障(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> closeBatch(@RequestParam @ApiParam(value = "故障信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultInfoService.closeBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping(value = "/import/fault/history")
    @ApiOperation(value = "导入历史故障", notes = "需要用request传参数type：1架修 2大修")
    public Result<String> importFaultHistory(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        int type = StringUtils.isBlank(request.getParameter("type")) ? 1 : Integer.parseInt(request.getParameter("type"));
        String resultString = buFaultInfoService.importFaultHistory(file, type);
        return new Result<String>().successResult(resultString);
    }

}
