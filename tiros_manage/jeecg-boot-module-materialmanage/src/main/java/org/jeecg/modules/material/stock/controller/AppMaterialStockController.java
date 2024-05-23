package org.jeecg.modules.material.stock.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockDetailVO;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockQueryVO;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.service.BuMtrWarehouseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 物资库存 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-25
 */
@AppController
@Api(tags = "库存管理-物料")
@Slf4j
@RestController
@RequestMapping("/app/stock")
public class AppMaterialStockController {

    private final BuMtrWarehouseService buMtrWarehouseService;
    private final BuMaterialStockService buMaterialStockService;

    public AppMaterialStockController(BuMtrWarehouseService buMtrWarehouseService,
                                      BuMaterialStockService buMaterialStockService) {
        this.buMtrWarehouseService = buMtrWarehouseService;
        this.buMaterialStockService = buMaterialStockService;
    }


    @GetMapping("/warehouse/tree-all")
    @ApiOperation(value = "仓库-查询树")
    @OperationLog()
    public Result<List<BuMtrWarehouse>> allTrees() throws Exception {
        List<BuMtrWarehouse> buMtrWarehouseList = buMtrWarehouseService.getAllTrees(true);
        return new Result<List<BuMtrWarehouse>>().successResult(buMtrWarehouseList);
    }

    @GetMapping("/warehouse/list-child")
    @ApiOperation(value = "仓库-根据上级id获取子仓库列表")
    @OperationLog()
    public Result<List<BuMtrWarehouse>> tree(@RequestParam(required = false) @ApiParam(value = "上级仓库id") String parentId) throws Exception {
        List<BuMtrWarehouse> buMtrWarehouseList = buMtrWarehouseService.listByParentId(parentId, true);
        return new Result<List<BuMtrWarehouse>>().successResult(buMtrWarehouseList);
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询物资库存记录(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialStock>> pageStock(@Validated BuMaterialStockQueryVO queryVO,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialStock> page = buMaterialStockService.pageStock(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialStock>>().successResult(page);
    }

    @GetMapping("/list-by-material")
    @ApiOperation(value = "根据物资类型id查询库存列表")
    @OperationLog()
    public Result<List<BuMaterialStock>> listStockByMaterialTypeId(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId) throws Exception {
        List<BuMaterialStock> stockList = buMaterialStockService.listStockByMaterialTypeId(materialTypeId, new ArrayList<>(), null, null);
        return new Result<List<BuMaterialStock>>().successResult(stockList);
    }

    @GetMapping("/material/get")
    @ApiOperation(value = "查询指定物资类型的明细信息")
    @OperationLog()
    public Result<BuMaterialStockDetailVO> getMaterialStockDetail(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId) throws Exception {
        BuMaterialStockDetailVO stockDetailVO = buMaterialStockService.getMaterialStockDetail(materialTypeId);
        return new Result<BuMaterialStockDetailVO>().successResult(stockDetailVO);
    }

}

