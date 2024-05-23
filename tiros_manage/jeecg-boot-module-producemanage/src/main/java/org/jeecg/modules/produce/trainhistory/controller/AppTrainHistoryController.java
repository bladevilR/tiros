package org.jeecg.modules.produce.trainhistory.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.produce.trainhistory.bean.*;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainAssetUseRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainHistoryTreeBaseVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainPeriodTimeLine;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.*;
import org.jeecg.modules.produce.trainhistory.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆结构明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
@AppController
@Api(tags = "车辆履历-公共")
@Slf4j
@RestController
@RequestMapping("/app/train/history")
public class AppTrainHistoryController {

    private final BuTrainStructureDetailProduceService buTrainStructureDetailProduceService;
    private final BuTrainInfoProduceService buTrainInfoProduceService;
    private final BuTrainAssetProduceService buTrainAssetProduceService;
    private final BuTrainHistoryWorkService buTrainHistoryWorkService;
    private final BuTrainHistoryChangeService buTrainHistoryChangeService;
    private final BuTrainHistoryFaultService buTrainHistoryFaultService;
    private final BuTrainHistoryMeasureService buTrainHistoryMeasureService;
    private final BuMaximoTrainAssetProduceService buMaximoTrainAssetProduceService;

    public AppTrainHistoryController(BuTrainStructureDetailProduceService buTrainStructureDetailProduceService,
                                     BuTrainInfoProduceService buTrainInfoProduceService,
                                     BuTrainAssetProduceService buTrainAssetProduceService,
                                     BuTrainHistoryWorkService buTrainHistoryWorkService,
                                     BuTrainHistoryChangeService buTrainHistoryChangeService,
                                     BuTrainHistoryFaultService buTrainHistoryFaultService,
                                     BuTrainHistoryMeasureService buTrainHistoryMeasureService,
                                     BuMaximoTrainAssetProduceService buMaximoTrainAssetProduceService) {
        this.buTrainStructureDetailProduceService = buTrainStructureDetailProduceService;
        this.buTrainInfoProduceService = buTrainInfoProduceService;
        this.buTrainAssetProduceService = buTrainAssetProduceService;
        this.buTrainHistoryWorkService = buTrainHistoryWorkService;
        this.buTrainHistoryChangeService = buTrainHistoryChangeService;
        this.buTrainHistoryFaultService = buTrainHistoryFaultService;
        this.buTrainHistoryMeasureService = buTrainHistoryMeasureService;
        this.buMaximoTrainAssetProduceService = buMaximoTrainAssetProduceService;
    }


//    @GetMapping("/tree")
//    @ApiOperation(value = "履历树-查询树(线路+车辆+结构明细树)")
//    public Result<List<BuTrainHistoryTreeBaseVO>> tree() throws Exception {
//        List<BuTrainHistoryTreeBaseVO> treeLineVOList = buTrainStructureDetailProduceService.tree();
//        return new Result<List<BuTrainHistoryTreeBaseVO>>().successResult(treeLineVOList);
//    }

    @GetMapping("/tree/line-train")
    @ApiOperation(value = "履历树-查询树(线路+车辆)")
    @OperationLog()
    public Result<List<BuTrainHistoryTreeBaseVO>> treeLineAndTrain() throws Exception {
        List<BuTrainHistoryTreeBaseVO> treeLineVOList = buTrainStructureDetailProduceService.treeLineAndTrain();
        return new Result<List<BuTrainHistoryTreeBaseVO>>().successResult(treeLineVOList);
    }

//    @GetMapping("/tree/structure-all")
//    @ApiOperation(value = "履历树-查询车辆结构明细(树)", notes = "根据车辆号查询车辆结构明细树，全部数据")
//    public Result<List<BuTrainStructureDetailTreeVO>> treeAllStructure(@RequestParam @ApiParam(value = "车辆号", required = true) String trainNo) throws Exception {
//        List<BuTrainStructureDetailTreeVO> structureDetailTree = buTrainStructureDetailProduceService.treeAllStructure(trainNo);
//        return new Result<List<BuTrainStructureDetailTreeVO>>().successResult(structureDetailTree);
//    }
//
//    @GetMapping("/tree/structure")
//    @ApiOperation(value = "履历树-查询车辆结构明细(列表)", notes = "根据车辆号和上级车辆结构明细id，查询下一级结构明细")
//    public Result<List<BuTrainStructureDetail>> treeStructure(@Validated BuTrainHistoryTreeStructQueryVO queryVO) throws Exception {
//        List<BuTrainStructureDetail> structureDetailList = buTrainStructureDetailProduceService.listStructureDetail(queryVO);
//        return new Result<List<BuTrainStructureDetail>>().successResult(structureDetailList);
//    }

    @GetMapping("/train/get")
    @ApiOperation(value = "根据车辆号查询车辆信息")
    @OperationLog()
    public Result<BuTrainInfo> getTrainInfo(@RequestParam @ApiParam(value = "车辆号", required = true) String trainNo) throws Exception {
        BuTrainInfo trainInfo = buTrainInfoProduceService.getTrainInfoByTrainNo(trainNo);
        return new Result<BuTrainInfo>().successResult(trainInfo);
    }

    @GetMapping("/train/period")
    @ApiOperation(value = "根据车辆号查询架修周期时间线")
    @OperationLog()
    public Result<List<BuTrainPeriodTimeLine>> listRepairPeriod(@RequestParam @ApiParam(value = "车辆号", required = true) String trainNo) throws Exception {
        List<BuTrainPeriodTimeLine> periodVOList = buTrainInfoProduceService.listTrainPeriodTimeLine(trainNo);
        return new Result<List<BuTrainPeriodTimeLine>>().successResult(periodVOList);
    }

    @GetMapping("/asset/list/child")
    @ApiOperation(value = "查询子设备", notes = "根据车辆号和车辆结构明细id，找到对应的车辆设备，找出此设备的下一层子设备列表")
    @OperationLog()
    public Result<List<BuTrainAsset>> listChildAsset(@RequestParam @ApiParam(value = "车辆号", required = true) String trainNo,
                                                     @RequestParam @ApiParam(value = "车辆结构明细id", required = true) String structureDetailId) throws Exception {
        List<BuTrainAsset> trainAssetList = buTrainAssetProduceService.listChildAsset(trainNo, structureDetailId);
        return new Result<List<BuTrainAsset>>().successResult(trainAssetList);
    }

    @GetMapping("/asset/query")
    @ApiOperation(value = "查询车辆设备")
    @OperationLog()
    public Result<IPage<BuTrainAsset>> queryAsset(@Validated BuTrainAssetQueryVO queryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        List<BuTrainAsset> trainAssetList = buTrainAssetProduceService.queryAsset(queryVO);

        //按parentAssetId过滤
        if (StringUtils.isEmpty(queryVO.getParentAssetId())) {
            trainAssetList = trainAssetList.stream().filter(p -> StringUtils.isEmpty(p.getParentId())).collect(Collectors.toList());
        } else {
            trainAssetList = trainAssetList.stream().filter(p -> queryVO.getParentAssetId().equalsIgnoreCase(p.getParentId())).collect(Collectors.toList());
        }

        List<BuTrainAsset> trainAssetListVo = trainAssetList.stream()
                .skip(pageSize * (pageNo - 1))
                .limit(pageSize).collect(Collectors.toList());
        IPage<BuTrainAsset> page = new Page();
        page.setRecords(trainAssetListVo);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(trainAssetList.size());

        return new Result<IPage<BuTrainAsset>>().successResult(page);
    }

    @GetMapping("/asset/get")
    @ApiOperation(value = "根据设备id查询设备详情")
    @OperationLog()
    public Result<BuTrainAsset> getAssetById(@RequestParam @ApiParam(value = "设备id", required = true) String assetId) throws Exception {
        BuTrainAsset trainAsset = buTrainAssetProduceService.getAssetById(assetId);
        return new Result<BuTrainAsset>().successResult(trainAsset);
    }

    @GetMapping("/work/page")
    @ApiOperation(value = "查询作业记录(分页)")
    @OperationLog()
    public Result<IPage<BuTrainHistoryWork>> pageWorkRecord(@Validated BuTrainHistoryWorkQueryVO queryVO,
                                                            @RequestParam(defaultValue = "1") Integer pageNo,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryWork> page = buTrainHistoryWorkService.pageWorkRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryWork>>().successResult(page);
    }

    @GetMapping("/change/page")
    @ApiOperation(value = "查询更换记录(分页)")
    @OperationLog()
    public Result<IPage<BuTrainHistoryChange>> pageChangeRecord(@Validated BuTrainHistoryChangeQueryVO queryVO,
                                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryChange> page = buTrainHistoryChangeService.pageChangeRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryChange>>().successResult(page);
    }

    @GetMapping("/change/count/time")
    @ApiOperation(value = "查询更换记录同比分析")
    @OperationLog()
    public Result<List<LineChartVO>> countChangeRecordByTime(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<LineChartVO> lineChartVOList = buTrainHistoryChangeService.countChangeRecordByTime(queryVO);
        return new Result<List<LineChartVO>>().successResult(lineChartVOList);
    }

    @GetMapping("/change/count/distribution")
    @ApiOperation(value = "查询更换记录分布分析")
    @OperationLog()
    public Result<List<PieChartVO>> countChangeRecordByDistribution(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<PieChartVO> pieChartVOList = buTrainHistoryChangeService.countChangeRecordByDistribution(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/fault/page")
    @ApiOperation(value = "查询故障记录(分页)")
    @OperationLog()
    public Result<IPage<BuTrainHistoryFault>> pageFaultRecord(@Validated BuTrainHistoryFaultQueryVO queryVO,
                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryFault> page = buTrainHistoryFaultService.pageFaultRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryFault>>().successResult(page);
    }

    @GetMapping("/fault/count/time")
    @ApiOperation(value = "查询故障记录同比分析", notes = "type=月份，jeecg=当年，jeebt=上一年")
    @OperationLog()
    public Result<List<LineChartVO>> countFaultRecordByTime(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<LineChartVO> lineChartVOList = buTrainHistoryFaultService.countFaultRecordByTime(queryVO);
        return new Result<List<LineChartVO>>().successResult(lineChartVOList);
    }

    @GetMapping("/fault/count/distribution")
    @ApiOperation(value = "查询故障记录分布分析")
    @OperationLog()
    public Result<List<PieChartVO>> countFaultRecordByDistribution(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<PieChartVO> pieChartVOList = buTrainHistoryFaultService.countFaultRecordByDistribution(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/measure/page")
    @ApiOperation(value = "查询测量记录(分页)")
    @OperationLog()
    public Result<IPage<BuTrainHistoryMeasure>> pageMeasureRecord(BuTrainHistoryMeasureQueryVO queryVO,
                                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryMeasure> page = buTrainHistoryMeasureService.pageMeasureRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryMeasure>>().successResult(page);
    }

    @GetMapping("/asset/use/page")
    @ApiOperation(value = "查询部件使用记录(分页)")
    @OperationLog()
    public Result<IPage<BuTrainAssetUseRecordVO>> pageAssetUseRecordVO(@RequestParam @ApiParam(value = "设备id", required = true) String assetId,
                                                                       @RequestParam @ApiParam(value = "车号", required = false) String trainNo,
                                                                       @RequestParam(required = false) @ApiParam(value = "更换日期 yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd") Date changeDate,
                                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                                       @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTrainAssetUseRecordVO> page = buTrainHistoryChangeService.pageAssetUseRecordVO(assetId, trainNo, changeDate, pageNo, pageSize);
        return new Result<IPage<BuTrainAssetUseRecordVO>>().successResult(page);
    }


    private void preTreatRecordsQueryVO(HistoryRecordsQueryVO queryVO) throws Exception {
        if (null != queryVO.getQueryType() && 2 == queryVO.getQueryType()) {
            // 查询类型：1车辆履历 2部件履历；为2时只会根据assetId查询车辆设备自身数据
            if (StringUtils.isBlank(queryVO.getAssetId())) {
                throw new JeecgBootException("请选择车辆设备");
            }
//            queryVO.setAssetIdList(Collections.singletonList(queryVO.getAssetId()));
        } else {
//            // 求出所有子车辆设备id
//            BuTrainAssetQueryVO buTrainAssetQueryVO = new BuTrainAssetQueryVO()
//                    .setTrainNo(queryVO.getTrainNo())
//                    .setStructureDetailId(queryVO.getStructureDetailId())
//                    .setParentAssetId(queryVO.getAssetId())
//                    .setRecurseStructDetail(true)
//                    .setRecurseAsset(true);
//            List<String> assetIdList = buTrainAssetProduceService.listAssetId(buTrainAssetQueryVO);
//            queryVO.setAssetIdList(assetIdList);
            if (StringUtils.isBlank(queryVO.getAssetId()) && StringUtils.isNotBlank(queryVO.getStructureDetailId())) {
                String assetId = buTrainAssetProduceService.getAssetIdByTrainNoAndStructDetailId(queryVO.getTrainNo(), queryVO.getStructureDetailId());
                queryVO.setAssetId(assetId);
            }
        }
    }

    @GetMapping(value = "/maximoTrainAsset/list-child")
    @ApiOperation("查询资产设备子节点")
    @OperationLog()
    public Result<List<BuMaximoTrainAsset>> listTrainAssetTypeChildren(@Validated BuMaximoTrainAssetChildrenQueryVO queryVO) throws Exception {
        List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetProduceService.listMaximoTrainAssetChild(queryVO);
        return new Result<List<BuMaximoTrainAsset>>().successResult(assetList);
    }
}