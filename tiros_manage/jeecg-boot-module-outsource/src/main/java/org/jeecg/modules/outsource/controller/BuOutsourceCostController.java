package org.jeecg.modules.outsource.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.outsource.bean.OutsourceAsset;
import org.jeecg.modules.outsource.bean.OutsourceCost;
import org.jeecg.modules.outsource.bean.OutsourceCostDetail;
import org.jeecg.modules.outsource.bean.OutsourceCostPart;
import org.jeecg.modules.outsource.service.BuOutSourceCostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 委外成本分析 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2021-11-
 */
@Api(tags = "委外成本分析")
@Slf4j
@RestController
@RequestMapping("/outsource/cost/")
public class BuOutsourceCostController {

    private final BuOutSourceCostService outSourceCostService;

    public BuOutsourceCostController(BuOutSourceCostService outSourceCostService) {
        this.outSourceCostService = outSourceCostService;
    }


    /**
     * 所有线路修程的不同成本
     */
    @GetMapping("/listCost")
    @ApiOperation(value = "委外成本")
    @OperationLog()
    public Result<List<OutsourceCost>> listCost() throws Exception {
        List<OutsourceCost> outsourceCostList = outSourceCostService.listCost();
        return new Result<List<OutsourceCost>>().successResult(outsourceCostList);
    }

    /**
     * xxx线路xx修程项目成本明细
     */
    @GetMapping("/detail")
    @ApiOperation(value = "根据线路查委外成本明细")
    @OperationLog()
    public Result<List<OutsourceCostDetail>> outsourceCostDetailByLineId(@RequestParam @ApiParam(value = "线路id", required = true) String lineId,
                                                                         @RequestParam @ApiParam(value = "修程id", required = true) String repairProgramId) throws Exception {
        List<OutsourceCostDetail> outsourceCostDetailList = outSourceCostService.outsourceCostDetail(lineId, repairProgramId);
        return new Result<List<OutsourceCostDetail>>().successResult(outsourceCostDetailList);
    }

    @GetMapping("/asset")
    @ApiOperation(value = "查询所有合同设备")
    @OperationLog()
    public Result<List<OutsourceAsset>> outsourceAssetList() throws Exception {
        List<OutsourceAsset> outsourceAssetList = outSourceCostService.outsourceAssetList();
        return new Result<List<OutsourceAsset>>().successResult(outsourceAssetList);
    }

    /**
     * 根据合同设备查合同价格趋势
     */
    @GetMapping("/part")
    @ApiOperation(value = "根据合同设备查合同价格趋势")
    @OperationLog()
    public Result<List<OutsourceCostPart>> outsourceCostPartByLineId(@RequestParam @ApiParam(value = "合同设备id", required = true) String assetId, @RequestParam @ApiParam(value = "类型：1总成本，2单列成本，3单节成本,4单部件成本", required = true, defaultValue = "1") Integer type) throws Exception {
        List<OutsourceCostPart> outsourceCostPartList = outSourceCostService.outsourceCostPartByAssetId(assetId, type);
        return new Result<List<OutsourceCostPart>>().successResult(outsourceCostPartList);
    }


}
