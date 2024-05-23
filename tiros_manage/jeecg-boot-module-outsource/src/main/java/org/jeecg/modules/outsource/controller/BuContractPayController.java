package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuContractPay;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;
import org.jeecg.modules.outsource.service.BuContractPayService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 支付记录 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "支付记录")
@Slf4j
@RestController
@RequestMapping("/outsource/contractPay/")
public class BuContractPayController {

    private final BuContractPayService payService;

    public BuContractPayController(BuContractPayService payService) {
        this.payService = payService;
    }


    @GetMapping("page")
    @ApiOperation(value = "查询支付记录（分页）")
    @OperationLog()
    public Result<IPage<BuContractPay>> page(@Validated BuContractPayQueryVO queryVO,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuContractPay> page = payService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuContractPay>>().successResult(page);
    }

    @PostMapping("add")
    @ApiOperation(value = "新增支付记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuContractPay contractPay) throws Exception {
        boolean flag = payService.saveContractPay(contractPay);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改支付记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuContractPay contractPay) {
        boolean flag = payService.editContractPay(contractPay);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除支付记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") @ApiParam(name = "ids", value = "支付记录ids，多个逗号分隔", required = true) String ids) {
        boolean deleteFlag = payService.removeByIdsAndRestore(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(deleteFlag);
    }


}

