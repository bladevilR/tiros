package org.jeecg.modules.outsource.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuContractAnnex;
import org.jeecg.modules.outsource.bean.vo.BuContractAnnexSaveVO;
import org.jeecg.modules.outsource.service.BuContractAnnexService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 合同附件 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Api(tags = "合同附件")
@Slf4j
@RestController
@RequestMapping("/outsource/contract/annex")
public class BuContractAnnexController {

    private final BuContractAnnexService buContractAnnexService;

    public BuContractAnnexController(BuContractAnnexService buContractAnnexService) {
        this.buContractAnnexService = buContractAnnexService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询合同附件列表")
    @OperationLog()
    public Result<List<BuContractAnnex>> listAnnex(@RequestParam @ApiParam(value = "合同id", required = true) String contractId) throws Exception {
        List<BuContractAnnex> annexList = buContractAnnexService.listAnnex(contractId);
        return new Result<List<BuContractAnnex>>().successResult(annexList);
    }

    @PostMapping("/save")
    @ApiOperation(value = "增加合同附件列表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> add(@RequestBody @Validated BuContractAnnexSaveVO contractAnnexSaveVO) throws Exception {
        boolean flag = buContractAnnexService.saveAnnex(contractAnnexSaveVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除合同附件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "合同附件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buContractAnnexService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

