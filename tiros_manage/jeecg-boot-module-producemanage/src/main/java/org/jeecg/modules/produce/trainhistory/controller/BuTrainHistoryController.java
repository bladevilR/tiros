package org.jeecg.modules.produce.trainhistory.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.produce.trainhistory.bean.*;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainAssetUseRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainHistoryFormRecordVO;
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

/**
 * <p>
 * 车辆结构明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
@Api(tags = "车辆履历")
@Slf4j
@RestController
@RequestMapping("/produce/train/history")
public class BuTrainHistoryController {

    private final BuTrainStructureDetailProduceService buTrainStructureDetailProduceService;
    private final BuTrainInfoProduceService buTrainInfoProduceService;
    private final BuTrainHistoryWorkService buTrainHistoryWorkService;
    private final BuTrainHistoryChangeService buTrainHistoryChangeService;
    private final BuTrainHistoryFaultService buTrainHistoryFaultService;
    private final BuTrainHistoryMeasureService buTrainHistoryMeasureService;
    private final BuTrainHistoryFormService buTrainHistoryFormService;
    private final BuMaximoTrainAssetProduceService buMaximoTrainAssetProduceService;

    public BuTrainHistoryController(BuTrainStructureDetailProduceService buTrainStructureDetailProduceService,
                                    BuTrainInfoProduceService buTrainInfoProduceService,
                                    BuTrainHistoryWorkService buTrainHistoryWorkService,
                                    BuTrainHistoryChangeService buTrainHistoryChangeService,
                                    BuTrainHistoryFaultService buTrainHistoryFaultService,
                                    BuTrainHistoryMeasureService buTrainHistoryMeasureService,
                                    BuTrainHistoryFormService buTrainHistoryFormService,
                                    BuMaximoTrainAssetProduceService buMaximoTrainAssetProduceService) {
        this.buTrainStructureDetailProduceService = buTrainStructureDetailProduceService;
        this.buTrainInfoProduceService = buTrainInfoProduceService;
        this.buTrainHistoryWorkService = buTrainHistoryWorkService;
        this.buTrainHistoryChangeService = buTrainHistoryChangeService;
        this.buTrainHistoryFaultService = buTrainHistoryFaultService;
        this.buTrainHistoryMeasureService = buTrainHistoryMeasureService;
        this.buTrainHistoryFormService = buTrainHistoryFormService;
        this.buMaximoTrainAssetProduceService = buMaximoTrainAssetProduceService;
    }


    @GetMapping("/tree/line-train")
    @ApiOperation(value = "履历树-查询树(线路+车辆)")
    public Result<List<BuTrainHistoryTreeBaseVO>> treeLineAndTrain() throws Exception {
        List<BuTrainHistoryTreeBaseVO> treeLineVOList = buTrainStructureDetailProduceService.treeLineAndTrain();
        return new Result<List<BuTrainHistoryTreeBaseVO>>().successResult(treeLineVOList);
    }

    @GetMapping(value = "/maximo-asset/list-child")
    @ApiOperation("资产-查询资产设备子节点")
    public Result<List<BuMaximoTrainAsset>> listTrainAssetTypeChildren(@Validated BuMaximoTrainAssetChildrenQueryVO queryVO) throws Exception {
        List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetProduceService.listMaximoTrainAssetChild(queryVO);
        return new Result<List<BuMaximoTrainAsset>>().successResult(assetList);
    }

    @GetMapping("/train/get")
    @ApiOperation(value = "车辆-根据车号查询车辆信息")
    public Result<BuTrainInfo> getTrainInfo(@RequestParam @ApiParam(value = "车号", required = true) String trainNo) throws Exception {
        BuTrainInfo trainInfo = buTrainInfoProduceService.getTrainInfoByTrainNo(trainNo);
        return new Result<BuTrainInfo>().successResult(trainInfo);
    }

    @GetMapping("/train/period")
    @ApiOperation(value = "架修周期-根据车号查询架修周期时间线")
    public Result<List<BuTrainPeriodTimeLine>> listRepairPeriod(@RequestParam @ApiParam(value = "车号", required = true) String trainNo) throws Exception {
        List<BuTrainPeriodTimeLine> periodVOList = buTrainInfoProduceService.listTrainPeriodTimeLine(trainNo);
        return new Result<List<BuTrainPeriodTimeLine>>().successResult(periodVOList);
    }

    @GetMapping(value = "/maximo-asset/get")
    @ApiOperation("资产-根据id查询资产设备")
    public Result<BuMaximoTrainAsset> listTrainAssetTypeChildren(@RequestParam @ApiParam(value = "资产设备id", required = true) String maximoAssetId) throws Exception {
        BuMaximoTrainAsset asset = buMaximoTrainAssetProduceService.getTrainAssetById(maximoAssetId);
        return new Result<BuMaximoTrainAsset>().successResult(asset);
    }

    @GetMapping("/work/page")
    @ApiOperation(value = "作业记录-查询(分页)")
    public Result<IPage<BuTrainHistoryWork>> pageWorkRecord(@Validated BuTrainHistoryWorkQueryVO queryVO,
                                                            @RequestParam(defaultValue = "1") Integer pageNo,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryWork> page = buTrainHistoryWorkService.pageWorkRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryWork>>().successResult(page);
    }

    @GetMapping("/change/page")
    @ApiOperation(value = "更换记录-查询(分页)")
    public Result<IPage<BuTrainHistoryChange>> pageChangeRecord(@Validated BuTrainHistoryChangeQueryVO queryVO,
                                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryChange> page = buTrainHistoryChangeService.pageChangeRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryChange>>().successResult(page);
    }

    @GetMapping("/change/count/time")
    @ApiOperation(value = "更换记录-查询同比分析")
    public Result<List<LineChartVO>> countChangeRecordByTime(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<LineChartVO> lineChartVOList = buTrainHistoryChangeService.countChangeRecordByTime(queryVO);
        return new Result<List<LineChartVO>>().successResult(lineChartVOList);
    }

    @GetMapping("/change/count/distribution")
    @ApiOperation(value = "更换记录-查询分布分析")
    public Result<List<PieChartVO>> countChangeRecordByDistribution(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<PieChartVO> pieChartVOList = buTrainHistoryChangeService.countChangeRecordByDistribution(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/fault/page")
    @ApiOperation(value = "故障记录-查询(分页)")
    public Result<IPage<BuTrainHistoryFault>> pageFaultRecord(@Validated BuTrainHistoryFaultQueryVO queryVO,
                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryFault> page = buTrainHistoryFaultService.pageFaultRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryFault>>().successResult(page);
    }

    @GetMapping("/fault/count/time")
    @ApiOperation(value = "故障记录-查询同比分析", notes = "type=月份，jeecg=当年，jeebt=上一年")
    public Result<List<LineChartVO>> countFaultRecordByTime(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<LineChartVO> lineChartVOList = buTrainHistoryFaultService.countFaultRecordByTime(queryVO);
        return new Result<List<LineChartVO>>().successResult(lineChartVOList);
    }

    @GetMapping("/fault/count/distribution")
    @ApiOperation(value = "故障记录-查询分布分析")
    public Result<List<PieChartVO>> countFaultRecordByDistribution(@Validated HistoryRecordsQueryVO queryVO) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        List<PieChartVO> pieChartVOList = buTrainHistoryFaultService.countFaultRecordByDistribution(queryVO);
        return new Result<List<PieChartVO>>().successResult(pieChartVOList);
    }

    @GetMapping("/measure/page")
    @ApiOperation(value = "测量记录-查询(分页)")
    public Result<IPage<BuTrainHistoryMeasure>> pageMeasureRecord(@Validated BuTrainHistoryMeasureQueryVO queryVO,
                                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryMeasure> page = buTrainHistoryMeasureService.pageMeasureRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryMeasure>>().successResult(page);
    }

    @GetMapping("/asset/use/page")
    @ApiOperation(value = "使用记录-查询部件使用记录(分页)")
    public Result<IPage<BuTrainAssetUseRecordVO>> pageAssetUseRecordVO(@RequestParam @ApiParam(value = "设备id", required = true) String assetId,
                                                                       @RequestParam(required = false) @ApiParam(value = "车号") String trainNo,
                                                                       @RequestParam(required = false) @ApiParam(value = "更换日期 yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd") Date changeDate,
                                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                                       @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTrainAssetUseRecordVO> page = buTrainHistoryChangeService.pageAssetUseRecordVO(assetId, trainNo, changeDate, pageNo, pageSize);
        return new Result<IPage<BuTrainAssetUseRecordVO>>().successResult(page);
    }

    @GetMapping("/forms/page")
    @ApiOperation(value = "表单-查询相关表单(分页)")
    public Result<IPage<BuTrainHistoryFormRecordVO>> pageFormRecordVO(@Validated BuTrainHistoryFormQueryVO queryVO,
                                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        preTreatRecordsQueryVO(queryVO);
        IPage<BuTrainHistoryFormRecordVO> page = buTrainHistoryFormService.pageFormRecordVO(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainHistoryFormRecordVO>>().successResult(page);
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
                // 现在前端传过来的structureDetailId，其实是资产设备id，赋值到设备id，查询sql中会查询下级记录
                queryVO.setAssetId(queryVO.getStructureDetailId());
            }
        }
    }

    @GetMapping("/workLeaveRecord/page")
    @ApiOperation(value = "查询开口项记录（分页）")
    public Result<IPage<BuWorkLeaveRecord>> workLeaveRecordPage(@Validated BuWorkLeaveRecordQueryVO queryVO,
                                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkLeaveRecord> page = buTrainInfoProduceService.pageLeaveRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkLeaveRecord>>().successResult(page);
    }

    @GetMapping("/workRectify/page")
    @ApiOperation(value = "查询整改记录(分页)")
    public Result<IPage<BuWorkRectify>> page(@Validated BuWorkRectifyQueryVO queryVO,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkRectify> page = buTrainInfoProduceService.pageRectify(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkRectify>>().successResult(page);
    }
}