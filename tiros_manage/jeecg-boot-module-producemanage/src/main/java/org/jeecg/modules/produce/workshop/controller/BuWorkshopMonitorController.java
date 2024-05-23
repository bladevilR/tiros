package org.jeecg.modules.produce.workshop.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.service.BuFaultInfoProduceService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainInfo;
import org.jeecg.modules.produce.trainhistory.service.BuTrainInfoProduceService;
import org.jeecg.modules.produce.workshop.bean.vo.BuWorkstationQueryVO;
import org.jeecg.modules.produce.workshop.bean.vo.BuWorkstationWorkVO;
import org.jeecg.modules.produce.workshop.service.BuWorkshopMonitorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 架修车间 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-30
 */
@Api(tags = "车间监控")
@Slf4j
@RestController
@RequestMapping("/produce/workshop")
public class BuWorkshopMonitorController {

    private final BuFaultInfoProduceService buFaultInfoProduceService;
    private final BuWorkshopMonitorService buWorkshopMonitorService;
    private final BuTrainInfoProduceService buTrainInfoProduceService;

    public BuWorkshopMonitorController(BuFaultInfoProduceService buFaultInfoProduceService,
                                       BuWorkshopMonitorService buWorkshopMonitorService,
                                       BuTrainInfoProduceService buTrainInfoProduceService) {
        this.buFaultInfoProduceService = buFaultInfoProduceService;
        this.buWorkshopMonitorService = buWorkshopMonitorService;
        this.buTrainInfoProduceService = buTrainInfoProduceService;
    }


    @GetMapping("/fault/latest")
    @ApiOperation(value = "查询车间最新故障信息")
    @OperationLog()
    public Result<List<BuFaultInfo>> listLatestFault(@RequestParam @ApiParam(value = "车间id", required = true) String workshopId,
                                                     @RequestParam(required = false) @ApiParam(value = "故障条数,为空默认3条") Integer num) throws Exception {
        List<BuFaultInfo> faultInfoList = buFaultInfoProduceService.listLatestFault(workshopId, num);
        return new Result<List<BuFaultInfo>>().successResult(faultInfoList);
    }

    @GetMapping("/fault/{id}")
    @ApiOperation(value = "根据故障id查询详情")
    @OperationLog()
    public Result<BuFaultInfo> getFaultById(@PathVariable String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoProduceService.getFaultById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/graph")
    @ApiOperation(value = "查询车间平面图", notes = "返回车间平面图存放地址")
    @OperationLog()
    public Result<String> getWorkshopGraphAddress(@RequestParam @ApiParam(value = "车间id", required = true) String workshopId) throws Exception {
        String graphAddress = buWorkshopMonitorService.getGraphAddressByWorkShopId(workshopId);
        return new Result<String>().successResult(graphAddress);
    }

    @GetMapping("/workstation/list")
    @ApiOperation(value = "查询车间工位列表")
    @OperationLog()
    public Result<List<BuWorkstationWorkVO>> listWorkstation(@Validated BuWorkstationQueryVO queryVO) throws Exception {
        List<BuWorkstationWorkVO> workstationWorkVOList = buWorkshopMonitorService.listWorkstation(queryVO);
        return new Result<List<BuWorkstationWorkVO>>().successResult(workstationWorkVOList);
    }

    @GetMapping("/workstation/{id}")
    @ApiOperation(value = "根据工位id查询工位作业信息")
    @OperationLog()
    public Result<BuWorkstationWorkVO> getWorkstationWorkInfo(@PathVariable String id) throws Exception {
        BuWorkstationWorkVO workstationWorkVO = buWorkshopMonitorService.getWorkstationWorkInfo(id);
        return new Result<BuWorkstationWorkVO>().successResult(workstationWorkVO);
    }

    @GetMapping("/train-track")
    @ApiOperation(value = "查询车辆股道信息", notes = "有设置股道的车辆信息")
    @OperationLog()
    public Result<List<BuTrainInfo>> listTrainHasTrack(@RequestParam(required = false) @ApiParam(value = "线路id 非必须") String lineId) throws Exception {
        List<BuTrainInfo> trainInfoList = buTrainInfoProduceService.listTrainHasTrack(lineId);
        return new Result<List<BuTrainInfo>>().successResult(trainInfoList);
    }

}

