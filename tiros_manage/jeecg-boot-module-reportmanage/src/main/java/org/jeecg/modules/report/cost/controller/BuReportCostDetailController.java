package org.jeecg.modules.report.cost.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.tiros.rpt.service.MaterialRptService;
import org.jeecg.modules.report.cost.bean.vo.BuCostDetailTotalVO;
import org.jeecg.modules.report.cost.bean.vo.BuReportCostDetailQueryVO;
import org.jeecg.modules.report.cost.service.BuWorkOrderMaterialReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

/**
 * <p>
 * 物质消耗明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
@Api(tags = "物质消耗明细")
@Slf4j
@RestController
@RequestMapping("/report/cost/detail")
public class BuReportCostDetailController {

    private final BuWorkOrderMaterialReportService buWorkOrderMaterialReportService;
    private final MaterialRptService materialRptService;

    public BuReportCostDetailController(BuWorkOrderMaterialReportService buWorkOrderMaterialReportService,
                                        MaterialRptService materialRptService) {
        this.buWorkOrderMaterialReportService = buWorkOrderMaterialReportService;
        this.materialRptService = materialRptService;
    }


    @GetMapping("/statistic-consume")
    @ApiOperation(value = "查询物质消耗明细-耗材", notes = "按系统和工位分组")
    @OperationLog()
    public Result<BuCostDetailTotalVO> getCostDetailStatisticOfConsume(@Validated BuReportCostDetailQueryVO queryVO) throws Exception {
        queryVO.setUseCategoryList(Collections.singletonList(3));
        BuCostDetailTotalVO costDetailTotalVO = buWorkOrderMaterialReportService.getCostDetailStatistic(queryVO);
        return new Result<BuCostDetailTotalVO>().successResult(costDetailTotalVO);
    }

    @GetMapping("/statistic-must-random")
    @ApiOperation(value = "查询物质消耗明细-备品备件(必换件+偶换件)", notes = "按系统和工位分组")
    @OperationLog()
    public Result<BuCostDetailTotalVO> getCostDetailStatisticOfMustAndRandom(@Validated BuReportCostDetailQueryVO queryVO) throws Exception {
        queryVO.setUseCategoryList(Arrays.asList(1, 2));
        BuCostDetailTotalVO costDetailTotalVO = buWorkOrderMaterialReportService.getCostDetailStatistic(queryVO);
        return new Result<BuCostDetailTotalVO>().successResult(costDetailTotalVO);
    }

    @GetMapping("/rebuildmaterialrpt")
    @ApiOperation(value = "重新生成物料统计中间表数据")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> rebuildMaterialRpt() {
        boolean flag = materialRptService.reBuildRptMaterial();
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/rebuild-materialRpt-byTrain")
    @ApiOperation(value = "重新生成物料统计中间表数据（单个车辆）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> reBuildRptMaterialByTrainNo(String trainNo) {
        boolean flag = materialRptService.reBuildRptMaterialByTrainNo(trainNo);
        return new Result<Boolean>().successResult(flag);
    }

}