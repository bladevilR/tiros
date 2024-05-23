package org.jeecg.modules.third.jdx.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.third.common.api.vo.Result;
import org.jeecg.modules.third.common.bean.bo.FaultCompareBO;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.jdx.bean.vo.FaultDiff;
import org.jeecg.modules.third.jdx.bean.vo.OrderDiff;
import org.jeecg.modules.third.jdx.service.*;
import org.jeecg.modules.third.maximo.bean.*;
import org.jeecg.modules.third.maximo.service.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 第三方接口 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-09
 */
@Api(tags = "从maximo获取接口")
@Slf4j
@RestController
@RequestMapping("/maximo")
public class ThirdMaximoController {

    private final JdxInvbalancesOutService jdxInvbalancesOutService;
    private final BuMaterialTypeThirdService buMaterialTypeThirdService;
    private final BuMtrWarehouseThirdService buMtrWarehouseThirdService;
    private final JdxAssetOutService jdxAssetOutService;
    private final JdxLocationsOutService jdxLocationsOutService;
    private final BuTrainInfoThirdService buTrainInfoThirdService;
    private final JdxRealassetOutService jdxRealassetOutService;
    private final BuMaterialToolsThirdService buMaterialToolsThirdService;
    private final JdxFailurelistOutService jdxFailurelistOutService;
    private final BuFaultCodeNewThirdService buFaultCodeNewThirdService;
    private final BuMaximoLocationThirdService buMaximoLocationThirdService;
    private final BuMaximoTrainAssetThirdService buMaximoTrainAssetThirdService;
    private final JdxProjectOutService jdxProjectOutService;
    private final BuMaximoFinanceItemThirdService buMaximoFinanceItemThirdService;
    private final JdxSrOutService jdxSrOutService;
    private final BuTrainHistoryFaultThirdService buTrainHistoryFaultThirdService;
    private final JdxWoOutService jdxWoOutService;
    private final BuTrainHistoryWorkThirdService buTrainHistoryWorkThirdService;
    private final JdxRealassettransOutService jdxRealassettransOutService;
    private final BuTrainHistoryChangeThirdService buTrainHistoryChangeThirdService;
    private final MxoutInterTransService mxoutInterTransService;
    private final JdxWoInService jdxWoInService;
    private final BuWorkOrderThirdService buWorkOrderThirdService;
    private final JdxSrInService jdxSrInService;
    private final BuFaultInfoThirdService buFaultInfoThirdService;

    private final int PAGE_SIZE = 100000;

    public ThirdMaximoController(JdxInvbalancesOutService jdxInvbalancesOutService,
                                 BuMaterialTypeThirdService buMaterialTypeThirdService,
                                 BuMtrWarehouseThirdService buMtrWarehouseThirdService,
                                 JdxAssetOutService jdxAssetOutService,
                                 JdxLocationsOutService jdxLocationsOutService,
                                 BuTrainInfoThirdService buTrainInfoThirdService,
                                 JdxRealassetOutService jdxRealassetOutService,
                                 BuMaterialToolsThirdService buMaterialToolsThirdService,
                                 JdxFailurelistOutService jdxFailurelistOutService,
                                 BuFaultCodeNewThirdService buFaultCodeNewThirdService,
                                 BuMaximoLocationThirdService buMaximoLocationThirdService,
                                 BuMaximoTrainAssetThirdService buMaximoTrainAssetThirdService,
                                 JdxProjectOutService jdxProjectOutService,
                                 BuMaximoFinanceItemThirdService buMaximoFinanceItemThirdService,
                                 JdxSrOutService jdxSrOutService,
                                 BuTrainHistoryFaultThirdService buTrainHistoryFaultThirdService,
                                 JdxWoOutService jdxWoOutService,
                                 BuTrainHistoryWorkThirdService buTrainHistoryWorkThirdService,
                                 JdxRealassettransOutService jdxRealassettransOutService,
                                 BuTrainHistoryChangeThirdService buTrainHistoryChangeThirdService,
                                 MxoutInterTransService mxoutInterTransService,
                                 JdxWoInService jdxWoInService,
                                 BuWorkOrderThirdService buWorkOrderThirdService,
                                 JdxSrInService jdxSrInService,
                                 BuFaultInfoThirdService buFaultInfoThirdService) {
        this.jdxInvbalancesOutService = jdxInvbalancesOutService;
        this.buMaterialTypeThirdService = buMaterialTypeThirdService;
        this.buMtrWarehouseThirdService = buMtrWarehouseThirdService;
        this.jdxAssetOutService = jdxAssetOutService;
        this.jdxLocationsOutService = jdxLocationsOutService;
        this.buTrainInfoThirdService = buTrainInfoThirdService;
        this.jdxRealassetOutService = jdxRealassetOutService;
        this.buMaterialToolsThirdService = buMaterialToolsThirdService;
        this.jdxFailurelistOutService = jdxFailurelistOutService;
        this.buFaultCodeNewThirdService = buFaultCodeNewThirdService;
        this.buMaximoLocationThirdService = buMaximoLocationThirdService;
        this.buMaximoTrainAssetThirdService = buMaximoTrainAssetThirdService;
        this.jdxProjectOutService = jdxProjectOutService;
        this.buMaximoFinanceItemThirdService = buMaximoFinanceItemThirdService;
        this.jdxSrOutService = jdxSrOutService;
        this.buTrainHistoryFaultThirdService = buTrainHistoryFaultThirdService;
        this.jdxWoOutService = jdxWoOutService;
        this.buTrainHistoryWorkThirdService = buTrainHistoryWorkThirdService;
        this.jdxRealassettransOutService = jdxRealassettransOutService;
        this.buTrainHistoryChangeThirdService = buTrainHistoryChangeThirdService;
        this.mxoutInterTransService = mxoutInterTransService;
        this.jdxWoInService = jdxWoInService;
        this.buWorkOrderThirdService = buWorkOrderThirdService;
        this.jdxSrInService = jdxSrInService;
        this.buFaultInfoThirdService = buFaultInfoThirdService;
    }


    @GetMapping("/read/material-stock")
    @ApiOperation(value = "初始化-从maximo读取物资类型和库存（慎用！）（会清空旧数据）")
    public Result<Boolean> readMaterialAndStock() throws Exception {
        List<JdxInvbalancesOut> maximoStockList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxInvbalancesOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxInvbalancesOut> maximoPage = jdxInvbalancesOutService.pageMaterial(currentPage, pageSize);
            List<JdxInvbalancesOut> maximoPageList = maximoPage.getRecords();
            maximoStockList.addAll(maximoPageList);
            currentPage++;
        }

        buMaterialTypeThirdService.initConsumeMaximoStockData(maximoStockList);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/material-stock/by-location")
    @ApiOperation(value = "初始化-从maximo读取【指定库房】的物资类型和库存（慎用！）（会清空旧数据）")
    public Result<Boolean> readMaterialAndStockByLocation(@RequestParam @ApiParam(value = "库房 如JDX01,JDX04...", required = true) String location) throws Exception {
        List<JdxInvbalancesOut> maximoStockList = jdxInvbalancesOutService.listMaterialStockByLocation(location);

        buMaterialTypeThirdService.initConsumeMaximoStockDataByTopWarehouse(maximoStockList, location);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/warehouse")
    @ApiOperation(value = "初始化-从maximo读取所有仓库（慎用！）（会清空旧数据）")
    public Result<Boolean> readWarehouse() throws Exception {
        Map<String, List<String>> warehouseMap = jdxInvbalancesOutService.getAllWarehouse();
        // 导入到二级(架大修)
        buMtrWarehouseThirdService.insertWarehouseFromMaximoData(warehouseMap);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/warehouse/getShouldNotExistWarehouseIds")
    @ApiOperation(value = "获取不应该存在的仓库信息（跟maximo对比后，获取3级库及以上）")
    public Result<String> getShouldNotExistWarehouseIds() throws Exception {
        Map<String, List<String>> warehouseMap = jdxInvbalancesOutService.getAllWarehouse();
        // 跟maximo对比后，获取不应该存在的仓库id
        String notExistWarehouseIds = buMtrWarehouseThirdService.getShouldNotExistWarehouseIds(warehouseMap);

        return new Result<String>().successResult(notExistWarehouseIds);
    }

    @GetMapping("/read/init/train")
    @ApiOperation(value = "初始化-从maximo读取车辆（慎用！）（会清空旧数据）")
    public Result<Boolean> readTrain(@RequestParam @ApiParam(value = "线路 1号线=1,2号线=2...", required = true) String lineId,
                                     @RequestParam @ApiParam(value = "公司id", required = true) String companyId,
                                     @RequestParam @ApiParam(value = "车间id", required = true) String workshopId) throws Exception {
        buTrainInfoThirdService.deleteByLineId(lineId);

        int pageSize = PAGE_SIZE;
        int total = jdxAssetOutService.countTrainTotal(lineId);
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;

        while (currentPage <= totalPage) {
            IPage<JdxAssetOut> maximoPage = jdxAssetOutService.pageTrain(currentPage, pageSize, lineId);
            List<JdxAssetOut> maximoTrainList = maximoPage.getRecords();
            buTrainInfoThirdService.insertTrainFromMaximoData(maximoTrainList, lineId, companyId, workshopId);

            currentPage++;
        }

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/asset")
    @ApiOperation(value = "初始化-从maximo读取所有资产（慎用！）（会清空旧数据）")
    public Result<Boolean> readAsset(@RequestParam(required = false) @ApiParam(value = "是否需要更新业务表中的旧资产设备id 默认是") Boolean needUpdateOldBusinessTableData) throws Exception {
        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAll();
        buMaximoTrainAssetThirdService.insertAllAssetFromMaximoData(maximoAssetList, needUpdateOldBusinessTableData);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/location")
    @ApiOperation(value = "初始化-从maximo读取所有位置（慎用！）（会清空旧数据）")
    public Result<Boolean> readLocation() throws Exception {
        List<JdxLocationsOut> maximoLocationsList = jdxLocationsOutService.listAll();
        buMaximoLocationThirdService.insertAllLocationFromMaximoData(maximoLocationsList, true);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/finance")
    @ApiOperation(value = "初始化-从maximo读取所有财务项目（慎用！）（会清空旧数据）")
    public Result<Boolean> readFinance() throws Exception {
        List<JdxProjectOut> maximoFinanceList = jdxProjectOutService.listAll();
        buMaximoFinanceItemThirdService.insertAllFinanceFromMaximoData(maximoFinanceList, true);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/tool")
    @ApiOperation(value = "初始化-从maximo读取工器具（慎用！）（会清空旧数据）")
    public Result<Boolean> readTools() throws Exception {
        List<JdxRealassetOut> maximoToolList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxRealassetOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxRealassetOut> maximoPage = jdxRealassetOutService.pageTool(currentPage, pageSize);
            List<JdxRealassetOut> maximoPageList = maximoPage.getRecords();
            maximoToolList.addAll(maximoPageList);
            currentPage++;
        }

        buMaterialToolsThirdService.insertToolFromMaximoData(maximoToolList);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/faultCode")
    @ApiOperation(value = "初始化-从maximo读取所有故障代码（慎用！）（会清空旧数据）")
    public Result<Boolean> readAllFaultCode() throws Exception {
        List<JdxFailurelistOut> maximoFaultCodeList = jdxFailurelistOutService.listAll();
        buFaultCodeNewThirdService.insertAllFaultCodeFromMaximoData(maximoFaultCodeList);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/fault")
    @ApiOperation(value = "初始化-从maximo读取所有故障（慎用！）（会清空旧数据）")
    public Result<Boolean> readAllFault() throws Exception {
        List<JdxSrOut> maximoFaultList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxSrOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxSrOut> maximoPage = jdxSrOutService.pageFault(currentPage, pageSize);
            List<JdxSrOut> maximoPageList = maximoPage.getRecords();
            maximoFaultList.addAll(maximoPageList);
            currentPage++;
        }

        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        buTrainHistoryFaultThirdService.initConsumeMaximoFaultData(maximoFaultList, maximoAssetList);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/order")
    @ApiOperation(value = "初始化-从maximo读取所有工单（慎用！）（会清空旧数据）")
    public Result<Boolean> readAllOrder() throws Exception {
        List<JdxWoOut> maximoOrderList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxWoOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxWoOut> maximoPage = jdxWoOutService.pageOrder(currentPage, pageSize);
            List<JdxWoOut> maximoPageList = maximoPage.getRecords();
            maximoOrderList.addAll(maximoPageList);
            currentPage++;
        }

        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        buTrainHistoryWorkThirdService.initConsumeMaximoOrderData(maximoOrderList, maximoAssetList);

        return new Result<Boolean>().successResult(true);
    }

    @GetMapping("/read/init/change")
    @ApiOperation(value = "初始化-从maximo读取所有更换（慎用！）（会清空旧数据）")
    public Result<Boolean> readAllChange() throws Exception {
        List<JdxRealassettransOut> maximoChangeList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxRealassettransOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxRealassettransOut> maximoPage = jdxRealassettransOutService.pageChange(currentPage, pageSize);
            List<JdxRealassettransOut> maximoPageList = maximoPage.getRecords();
            maximoChangeList.addAll(maximoPageList);
            currentPage++;
        }

        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        buTrainHistoryChangeThirdService.initConsumeMaximoChangeData(maximoChangeList, maximoAssetList);

        return new Result<Boolean>().successResult(true);
    }

    @PostMapping("/read/task/stock-by-stockTransIds")
    @ApiOperation(value = "手动执行定时任务-根据transIds从maximo读取库存变动（用于手动处理消耗成功后库存变动时的设置消耗已处理及删除占用等逻辑）")
    public Result<String> readTaskStockChangeByTransIds(@RequestParam @ApiParam(value = "库存变动transIds", required = true) String stockChangeTransIds) throws Exception {
        String[] split = stockChangeTransIds.split(",");
        List<Long> transIdList = new ArrayList<>();
        for (String transIdString : split) {
            transIdList.add(Long.valueOf(transIdString));
        }
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByTransIdList(transIdList);

        // 获取变动数据
        List<JdxInvbalancesOut> maximoStockList = jdxInvbalancesOutService.listByOutTransList(outTransBakList);
        // 处理变动数据
        buMaterialTypeThirdService.taskConsumeMaximoStockData(maximoStockList);

        String resultString = String.format("transId%s个，获取对应的队列数据%s条，获取对应的库存记录%s条。", split.length, outTransBakList.size(), maximoStockList.size());

        return new Result<String>().successResult(resultString);
    }

    @PostMapping("/getDiff/order")
    @ApiOperation(value = "核对差异-工单-架大修系统和maximo系统的不同")
    public Result<OrderDiff> getOrderDiffOfJdxAndMaximo() throws Exception {
        // 获取maximo系统中的所有工单
        List<String> maximoOrderCodeList = jdxWoInService.listOrderCode();
        // 与架大修对比获取工单差异
        OrderDiff orderDiff = buWorkOrderThirdService.getOrderDiffOfJdxAndMaximo(maximoOrderCodeList);

        return new Result<OrderDiff>().successResult(orderDiff);
    }

    @PostMapping("/getDiff/fault")
    @ApiOperation(value = "核对差异-故障-架大修系统和maximo系统的不同")
    public Result<FaultDiff> getFaultDiffOfJdxAndMaximo() throws Exception {
        // 获取maximo系统中的所有故障
        List<String> maximoFaultSnList = jdxSrInService.listFaultSn();
        // 与架大修对比获取故障差异
        FaultDiff faultDiff = buFaultInfoThirdService.getFaultDiffOfJdxAndMaximo(maximoFaultSnList);

        return new Result<FaultDiff>().successResult(faultDiff);
    }

    @PostMapping("/getDiff/fault/export")
    @ApiOperation(value = "核对差异-故障-导出架大修系统和maximo系统的不同")
    public Result<Boolean> exportFaultDiffOfJdxAndMaximo(HttpServletResponse httpServletResponse) throws Exception {
        try {
            // 获取maximo系统中的所有故障
            Map<String, FaultCompareBO> faultSnBOMap = jdxSrInService.mapFaultSnStatus();
            // 与架大修对比获取故障差异
            HSSFWorkbook workbook = buFaultInfoThirdService.exportFaultDiffOfJdxAndMaximo(faultSnBOMap);
            String fileName = String.format("物料消耗核实%s", workbook.getSheetAt(0).getSheetName());
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-Disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/handlePriceZero")
    @ApiOperation(value = "业务操作-工单关闭时物料价格为0的处理")
    public Result<String> handleNeedCloseOrderMaterialPriceZero(@RequestParam(required = false) @ApiParam(value = "是否修正数据") Boolean rewritePrice) throws Exception {
        // 查询架大修中价格为0的
        List<PriceZero> priceZeroList = buWorkOrderThirdService.getNeedCloseOrderMaterialPriceZero();
        // 查询maximo中对应价格
        priceZeroList = jdxInvbalancesOutService.selectPriceOfPriceZeroList(priceZeroList);
        if (null != rewritePrice && rewritePrice) {
            priceZeroList = buWorkOrderThirdService.rewriteNeedCloseOrderMaterialPriceZero(priceZeroList);

            long currentMaximoPriceNotZeroCount = priceZeroList.stream()
                    .filter(item -> null != item.getCurrentMaximoPrice() && !item.getCurrentMaximoPrice().equals(BigDecimal.ZERO))
                    .count();
            long rewritePriceCount = priceZeroList.stream()
                    .filter(item -> (null != item.getNewOrderMaterialActPrice() && !item.getNewOrderMaterialActPrice().equals(BigDecimal.ZERO))
                            && (null != item.getNewGroupStockPrice() && !item.getNewGroupStockPrice().equals(BigDecimal.ZERO))
                            && (null != item.getNewAssignDetailPrice() && !item.getNewAssignDetailPrice().equals(BigDecimal.ZERO))
                    )
                    .count();
            return new Result<String>().successResult(String.format("查询到价格为0的数据%s条，其中%s条可修正，%s条已修正。数据详情：%s",
                    priceZeroList.size(),
                    currentMaximoPriceNotZeroCount,
                    rewritePriceCount,
                    JSON.toJSONString(priceZeroList)));
        } else {
            long currentMaximoPriceNotZeroCount = priceZeroList.stream()
                    .filter(item -> null != item.getCurrentMaximoPrice() && !item.getCurrentMaximoPrice().equals(BigDecimal.ZERO))
                    .count();
            return new Result<String>().successResult(String.format("查询到价格为0的数据%s条，其中%s条可修正。数据详情：%s",
                    priceZeroList.size(),
                    currentMaximoPriceNotZeroCount,
                    JSON.toJSONString(priceZeroList)));
        }
    }

}
