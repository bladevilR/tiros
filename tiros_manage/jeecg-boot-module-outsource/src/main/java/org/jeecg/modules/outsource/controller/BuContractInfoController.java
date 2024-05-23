package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.bean.ContractMonitor;
import org.jeecg.modules.outsource.bean.ContractPrice;
import org.jeecg.modules.outsource.bean.vo.BuContractInfoQueryVO;
import org.jeecg.modules.outsource.service.BuContractInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 合同信息 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "合同管理")
@Slf4j
@RestController
@RequestMapping("/outsource/contractInfo")
public class BuContractInfoController {

    private final BuContractInfoService contractInfoService;

    public BuContractInfoController(BuContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询合同（分页）")
    @OperationLog()
    public Result<IPage<BuContractInfo>> page(@Validated BuContractInfoQueryVO queryVO,
                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuContractInfo> page = contractInfoService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuContractInfo>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增合同")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuContractInfo contractInfo) throws Exception {
        boolean flag = contractInfoService.saveContractInfo(contractInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改合同")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuContractInfo contractInfo) throws Exception {
        boolean flag = contractInfoService.editContractInfo(contractInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除合同（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "合同ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = contractInfoService.removeBachByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/monitor")
    @ApiOperation(value = "合同统计数据")
    @OperationLog()
    public Result<ContractMonitor> contractMonitor(@RequestParam @ApiParam(value = "合同id", required = true) String id) {
        ContractMonitor contractMonitor = contractInfoService.contractMonitor(id);
        return new Result<ContractMonitor>().successResult(contractMonitor);
    }

    @GetMapping("/assetType/needDay/{assetTypeId}")
    @ApiOperation(value = "查询指定设备类型的维修天数")
    @OperationLog()
    public Result<Integer> getAssetTypeNeedDay(@PathVariable("assetTypeId") String assetTypeId) {
        return new Result<Integer>().successResult(contractInfoService.getAssetTypeNeedDay(assetTypeId));
    }

    @GetMapping("/price")
    @ApiOperation(value = "查询合同价格")
    @OperationLog()
    public Result<List<ContractPrice>> getContractPrice(@RequestParam @ApiParam(value = "合同ids，多个逗号分隔", required = true) String ids) throws Exception {
        List<ContractPrice> priceList = contractInfoService.getContractPrice(Arrays.asList(ids.split(",")));
        return new Result<List<ContractPrice>>().successResult(priceList);
    }

}

