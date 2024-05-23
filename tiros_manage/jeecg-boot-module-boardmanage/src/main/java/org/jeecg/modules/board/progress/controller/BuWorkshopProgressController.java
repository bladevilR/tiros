package org.jeecg.modules.board.progress.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.progress.bean.vo.*;
import org.jeecg.modules.board.progress.service.BuWorkOrderBoardService;
import org.jeecg.modules.board.progress.service.BuWorkOrderTaskBoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 进度看板（车间） 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/13
 */
@Api(tags = "进度看板(车间)")
@Slf4j
@RestController
@RequestMapping("/board/workshopProgress")
public class BuWorkshopProgressController {

    private final BuWorkOrderBoardService buWorkOrderBoardService;
    private final BuWorkOrderTaskBoardService buWorkOrderTaskBoardService;

    public BuWorkshopProgressController(BuWorkOrderBoardService buWorkOrderBoardService,
                                        BuWorkOrderTaskBoardService buWorkOrderTaskBoardService) {
        this.buWorkOrderBoardService = buWorkOrderBoardService;
        this.buWorkOrderTaskBoardService = buWorkOrderTaskBoardService;
    }


    @GetMapping("/getTaskTrend")
    @ApiOperation(value = "查询当月每天任务完成趋势", notes = "type=日,jeecg=计划任务数,jeebt=实际完成数")
    @OperationLog()
    public Result<List<LineChartVO>> listCurrentMonthTaskFinishTrend(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<LineChartVO> lineChartVOList = buWorkOrderTaskBoardService.listCurrentMonthTaskFinishTrend(queryVO);
        return new Result<List<LineChartVO>>().successResult(lineChartVOList);
    }

    @GetMapping("/listWorkGroupTaskProgress")
    @ApiOperation(value = "查询工班任务进度")
    @OperationLog()
    public Result<List<BuWorkGroupTaskProgressVO>> listWorkGroupTaskProgress(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<BuWorkGroupTaskProgressVO> workGroupTaskProgressVOList = buWorkOrderTaskBoardService.listWorkGroupTaskProgress(queryVO);
        return new Result<List<BuWorkGroupTaskProgressVO>>().successResult(workGroupTaskProgressVOList);
    }

    @GetMapping("/getWorkGroupOrderInfo")
    @ApiOperation(value = "根据工班id查询工单信息")
    @OperationLog()
    public Result<BuWorkGroupOrderInfoVO> getWorkGroupOrderInfo(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        BuWorkGroupOrderInfoVO groupOrderInfoVO = buWorkOrderBoardService.getWorkGroupOrderInfo(queryVO);
        return new Result<BuWorkGroupOrderInfoVO>().successResult(groupOrderInfoVO);
    }

    @GetMapping("/listOrderTask")
    @ApiOperation(value = "根据工单id和任务状态查询工单任务")
    @OperationLog()
    public Result<List<BuWorkOrderTask>> listOrderTask(@RequestParam @ApiParam(value = "工单id", required = true) String orderId,
                                                       @RequestParam(required = false) @ApiParam(value = "任务状态  字典dictCode=bu_order_task_status") Integer taskStatus) throws Exception {
        List<BuWorkOrderTask> taskList = buWorkOrderTaskBoardService.listOrderTask(orderId, taskStatus);
        return new Result<List<BuWorkOrderTask>>().successResult(taskList);
    }

    @GetMapping("/listOutsourceOrderProgress")
    @ApiOperation(value = "查询委外工单进度")
    @OperationLog()
    public Result<List<BuWorkOutsourceOrderVO>> listOutsourceOrderProgress(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<BuWorkOutsourceOrderVO> outsourceOrderVOWithGanttList = buWorkOrderBoardService.listOutsourceOrderProgress(queryVO);
        return new Result<List<BuWorkOutsourceOrderVO>>().successResult(outsourceOrderVOWithGanttList);
    }

    @GetMapping("/listOutsourceTaskProgress")
    @ApiOperation(value = "查询委外任务进度")
    @OperationLog()
    public Result<List<BuWorkOutsourceTaskVO>> listOutsourceTaskProgress(@Validated BuBoardProgressQueryVO queryVO) throws Exception {
        List<BuWorkOutsourceTaskVO> outsourceTaskVOVOWithGanttList = buWorkOrderBoardService.listOutsourceTaskVOProgress(queryVO);
        return new Result<List<BuWorkOutsourceTaskVO>>().successResult(outsourceTaskVOVOWithGanttList);
    }

}
