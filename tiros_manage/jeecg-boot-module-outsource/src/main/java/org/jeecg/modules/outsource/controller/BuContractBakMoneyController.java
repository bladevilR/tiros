package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuContractBakMoney;
import org.jeecg.modules.outsource.bean.vo.BuContractBakMoneyQueryVO;
import org.jeecg.modules.outsource.service.BuContractBakMoneyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 暂列金使用记录 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "暂列金使用记录")
@Slf4j
@RestController
@RequestMapping("/outsource/contractBakMoney/")
public class BuContractBakMoneyController {

    private final BuContractBakMoneyService contractBakMoneyService;

    public BuContractBakMoneyController(BuContractBakMoneyService contractBakMoneyService) {
        this.contractBakMoneyService = contractBakMoneyService;
    }


    @GetMapping("page")
    @ApiOperation(value = "查询暂列金记录（分页）")
    @OperationLog()
    public Result<IPage<BuContractBakMoney>> page(@Validated BuContractBakMoneyQueryVO queryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuContractBakMoney> page = contractBakMoneyService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuContractBakMoney>>().successResult(page);
    }

    @PostMapping("add")
    @ApiOperation(value = "新增暂列金记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuContractBakMoney bakMoney) {
        boolean flag = contractBakMoneyService.saveBakMoney(bakMoney);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改暂列金记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuContractBakMoney bakMoney) {
        boolean flag = contractBakMoneyService.editBakMoney(bakMoney);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除暂列金记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") @ApiParam(name = "ids", value = "暂列金记录ids，多个逗号分隔", required = true) String ids) {
        boolean deleteFlag = contractBakMoneyService.removeByIdsAndRestore(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(deleteFlag);
    }

}

