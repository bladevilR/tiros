package org.jeecg.modules.material.stock.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.material.service.BuMaterialTypeAttrService;
import org.jeecg.modules.material.stock.bean.BuMaterialEntryAttr;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockQueryVO;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Api(tags = "库存管理")
@Slf4j
@RestController
@RequestMapping("/material/stock")
public class BuMaterialStockController {

    private final BuMaterialStockService buMaterialStockService;
    private final BuMaterialTypeAttrService buMaterialTypeAttrService;

    public BuMaterialStockController(BuMaterialStockService buMaterialStockService,
                                     BuMaterialTypeAttrService buMaterialTypeAttrService) {
        this.buMaterialStockService = buMaterialStockService;
        this.buMaterialTypeAttrService = buMaterialTypeAttrService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询物资库存(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialStock>> pageStock(@Validated BuMaterialStockQueryVO queryVO,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialStock> page = buMaterialStockService.pageStock(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialStock>>().successResult(page);
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据物资类型id查询库存列表")
    @OperationLog()
    public Result<List<BuMaterialStock>> listStockByMaterialTypeId(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId,
                                                                   @RequestParam(required = false) @ApiParam(value = "仓库级别列表") List<Integer> warehouseLevelList,
                                                                   @RequestParam(required = false) @ApiParam(value = "发放明细id") String applyDetailId,
                                                                   @RequestParam(required = false) @ApiParam(value = "分配明细id") String assignDetailId) throws Exception {
        List<BuMaterialStock> stockList = buMaterialStockService.listStockByMaterialTypeId(materialTypeId, warehouseLevelList, applyDetailId, assignDetailId);
        return new Result<List<BuMaterialStock>>().successResult(stockList);
    }

    @GetMapping("/attr/get")
    @ApiOperation(value = "查询物资属性")
    @OperationLog()
    public Result<BuMaterialTypeAttr> getByMaterialTypeId(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId) throws Exception {
        BuMaterialTypeAttr buMaterialTypeAttr = buMaterialTypeAttrService.getByMaterialTypeId(materialTypeId);
        return new Result<BuMaterialTypeAttr>().successResult(buMaterialTypeAttr);
    }

    @PostMapping("/attr/set")
    @ApiOperation(value = "设置物资属性")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setMaterialAttr(@RequestBody @Validated BuMaterialTypeAttr buMaterialTypeAttr) throws Exception {
        boolean flag = buMaterialTypeAttrService.setMaterialAttr(buMaterialTypeAttr);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/trade-attr/get")
    @ApiOperation(value = "查询物资批次属性")
    @OperationLog()
    public Result<BuMaterialEntryAttr> getMaterialTradeAttr(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId,
                                                            @RequestParam @ApiParam(value = "批次", required = true) String tradeNo) throws Exception {
        BuMaterialEntryAttr entryAttr = buMaterialStockService.getMaterialTradeAttr(materialTypeId, tradeNo);
        return new Result<BuMaterialEntryAttr>().successResult(entryAttr);
    }

    @PostMapping("/trade-attr/set")
    @ApiOperation(value = "设置物资批次属性")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setMaterialTradeAttr(@RequestBody @Validated BuMaterialEntryAttr tradeAttr) throws Exception {
        boolean flag = buMaterialStockService.setMaterialTradeAttr(tradeAttr);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/sum/get-amount")
    @ApiOperation(value = "查询物资总库存可用量")
    @OperationLog()
    public Result<Double> getMaterialSumStockAvailableAmount(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId) throws Exception {
        Double amount = buMaterialStockService.getMaterialSumStockAvailableAmount(materialTypeId);
        return new Result<Double>().successResult(amount);
    }

    @PostMapping("/import-level4-stock")
    @ApiOperation(value = "导入4级库库存数量（excel）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_5, saveParam = false)
    public Result<Boolean> importExcel(@RequestParam @ApiParam(value = "4级库库存数量excel文件=库位信息excel文件", required = true) MultipartFile excelFile) throws Exception {
        boolean flag = buMaterialStockService.importLevel4Stock(excelFile);
        return new Result<Boolean>().successResult(flag);
    }

}
