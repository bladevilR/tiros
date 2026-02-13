package org.jeecg.modules.quality.exceptiontransfer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quality.exceptiontransfer.bean.BuWorkExceptionTransfer;
import org.jeecg.modules.quality.exceptiontransfer.bean.vo.BuWorkExceptionTransferQueryVO;
import org.jeecg.modules.quality.exceptiontransfer.service.BuWorkExceptionTransferService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Api(tags = "例外转序")
@Slf4j
@RestController
@RequestMapping("/quality/exception-transfer")
public class BuWorkExceptionTransferController {

    @Resource
    private BuWorkExceptionTransferService service;

    @GetMapping("/page")
    @ApiOperation(value = "例外转序分页查询")
    @OperationLog()
    public Result<IPage<BuWorkExceptionTransfer>> page(@Validated BuWorkExceptionTransferQueryVO queryVO,
                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkExceptionTransfer> page = service.pageTransfer(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkExceptionTransfer>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据ID查询例外转序")
    @OperationLog()
    public Result<BuWorkExceptionTransfer> get(@RequestParam @ApiParam(value = "例外转序ID", required = true) String id) throws Exception {
        BuWorkExceptionTransfer transfer = service.getByIdWithDetail(id);
        return new Result<BuWorkExceptionTransfer>().successResult(transfer);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增例外转序")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuWorkExceptionTransfer transfer) throws Exception {
        boolean flag = service.createByFault(transfer);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑例外转序")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuWorkExceptionTransfer transfer) throws Exception {
        boolean flag = service.updateTransfer(transfer);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除例外转序(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> delete(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = service.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/decide")
    @ApiOperation(value = "例外转序放行/驳回")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> decide(@RequestBody @Validated DecideDTO dto) throws Exception {
        boolean flag = service.decideTransfer(dto.getId(), dto.getStatus(), dto.getDecisionType(), dto.getDecisionRemark());
        return new Result<Boolean>().successResult(flag);
    }

    @Data
    public static class DecideDTO {
        @NotBlank
        @ApiParam(value = "例外转序ID", required = true)
        private String id;
        @NotNull
        @ApiParam(value = "状态 1放行 2驳回", required = true)
        private Integer status;
        @ApiParam(value = "完成方式 1审批流 2一键放行")
        private Integer decisionType;
        @ApiParam(value = "备注")
        private String decisionRemark;
    }
}

