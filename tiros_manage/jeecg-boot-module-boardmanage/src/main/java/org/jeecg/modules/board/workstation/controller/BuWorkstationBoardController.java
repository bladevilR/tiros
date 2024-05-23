package org.jeecg.modules.board.workstation.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.board.workstation.bean.vo.*;
import org.jeecg.modules.board.workstation.service.BuMtrWorkstationBoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 工位看板(车间) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-18
 */
@Api(tags = "工位看板(车间)")
@Slf4j
@RestController
@RequestMapping("/board/workstation")
public class BuWorkstationBoardController {

    private final BuMtrWorkstationBoardService buMtrWorkstationBoardService;

    public BuWorkstationBoardController(BuMtrWorkstationBoardService buMtrWorkstationBoardService) {
        this.buMtrWorkstationBoardService = buMtrWorkstationBoardService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询工位(列表)")
    @OperationLog()
    public Result<List<BuWorkstationVO>> listWorkstationTodayData(@Validated BuWorkstationQueryVO queryVO) throws Exception {
        List<BuWorkstationVO> workstationVOList = buMtrWorkstationBoardService.listWorkstation(queryVO);
        return new Result<List<BuWorkstationVO>>().successResult(workstationVOList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查询工位全部信息", notes = "任务+必换件+故障+作业人数")
    @OperationLog()
    public Result<BuWorkstationVO> getWorkstationTodayData(@Validated BuWorkstationDataQueryVO queryVO) throws Exception {
        BuWorkstationVO workstationVO = buMtrWorkstationBoardService.getWorkstationAllData(queryVO);
        return new Result<BuWorkstationVO>().successResult(workstationVO);
    }

    @GetMapping("/get-task")
    @ApiOperation(value = "查询工位任务信息")
    @OperationLog()
    public Result<BuWorkstationTaskVO> getWorkstationTaskData(@Validated BuWorkstationDataQueryVO queryVO) throws Exception {
        BuWorkstationTaskVO taskVO = buMtrWorkstationBoardService.getWorkstationTaskData(queryVO);
        return new Result<BuWorkstationTaskVO>().successResult(taskVO);
    }

    @GetMapping("/get-must")
    @ApiOperation(value = "查询工位必换件信息")
    @OperationLog()
    public Result<BuWorkstationMustReplaceVO> getWorkstationMustReplaceData(@Validated BuWorkstationDataQueryVO queryVO) throws Exception {
        BuWorkstationMustReplaceVO mustReplaceVO = buMtrWorkstationBoardService.getWorkstationMustReplaceData(queryVO);
        return new Result<BuWorkstationMustReplaceVO>().successResult(mustReplaceVO);
    }

    @GetMapping("/get-fault")
    @ApiOperation(value = "查询工位故障信息")
    @OperationLog()
    public Result<BuWorkstationFaultVO> getWorkstationFaultData(@Validated BuWorkstationDataQueryVO queryVO) throws Exception {
        BuWorkstationFaultVO faultVO = buMtrWorkstationBoardService.getWorkstationFaultData(queryVO);
        return new Result<BuWorkstationFaultVO>().successResult(faultVO);
    }

    @GetMapping("/get-worker")
    @ApiOperation(value = "查询工位作业人数信息")
    @OperationLog()
    public Result<BuWorkstationWorkerVO> getWorkstationWorkerData(@Validated BuWorkstationDataQueryVO queryVO) throws Exception {
        BuWorkstationWorkerVO workerVO = buMtrWorkstationBoardService.getWorkstationWorkerData(queryVO);
        return new Result<BuWorkstationWorkerVO>().successResult(workerVO);
    }

}

