package org.jeecg.modules.board.homepage.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.board.homepage.bean.BuRptBoardDataItem;
import org.jeecg.modules.board.homepage.bean.vo.BuRepairPlanVOWithTaskGantt;
import org.jeecg.modules.board.homepage.service.BuRptBoardDataItemService;
import org.jeecg.modules.board.progress.service.BuRepairPlanBoardService;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.quality.service.BuFaultInfoBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 首页看板 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Api(tags = "首页看板")
@Slf4j
@RestController
@RequestMapping("/board/homepage")
public class BuHomepageBoardController {

    private final BuRptBoardDataItemService buRptBoardDataItemService;
    private final BuFaultInfoBoardService buFaultInfoBoardService;
    private final BuRepairPlanBoardService buRepairPlanBoardService;

    public BuHomepageBoardController(BuRptBoardDataItemService buRptBoardDataItemService,
                                     BuFaultInfoBoardService buFaultInfoBoardService,
                                     BuRepairPlanBoardService buRepairPlanBoardService) {
        this.buRptBoardDataItemService = buRptBoardDataItemService;
        this.buFaultInfoBoardService = buFaultInfoBoardService;
        this.buRepairPlanBoardService = buRepairPlanBoardService;
    }


    @GetMapping("/data/item/alert")
    @ApiOperation(value = "预警区数据获取")
    @OperationLog()
    public Result<List<BuRptBoardDataItem>> listAlert() throws Exception {
        List<BuRptBoardDataItem> itemList = buRptBoardDataItemService.listByCategoryId("1");
        return new Result<List<BuRptBoardDataItem>>().successResult(itemList);
    }

    @GetMapping("/data/item/data")
    @ApiOperation(value = "数据区数据获取")
    @OperationLog()
    public Result<List<BuRptBoardDataItem>> listData() throws Exception {
        List<BuRptBoardDataItem> itemList = buRptBoardDataItemService.listByCategoryId("2");
        return new Result<List<BuRptBoardDataItem>>().successResult(itemList);
    }

    @GetMapping("/fault/listLatest")
    @ApiOperation(value = "获取最新故障信息")
    @OperationLog()
    public Result<List<BuFaultInfo>> listLatestFault(@RequestParam(required = false) @ApiParam(value = "故障条数,为空默认8条") Integer num) throws Exception {
        List<BuFaultInfo> faultInfoList = buFaultInfoBoardService.listLatestFault(num);
        return new Result<List<BuFaultInfo>>().successResult(faultInfoList);
    }

    @GetMapping("/fault/get")
    @ApiOperation(value = "根据id查询故障详细信息")
    @OperationLog()
    public Result<BuFaultInfo> getFaultInfo(@RequestParam @ApiParam(value = "故障id", required = true) String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoBoardService.getFaultInfoById(id);
        return new Result<BuFaultInfo>().successResult(faultInfo);
    }

    @GetMapping("/plan/list")
    @ApiOperation(value = "获取架修任务全貌")
    @OperationLog()
    public Result<List<BuRepairPlanVOWithTaskGantt>> listAllRepairPlanBasicGantt() throws Exception {
        List<BuRepairPlanVOWithTaskGantt> repairPlanVOWithGanttList = buRepairPlanBoardService.listAllRepairPlanBasicGantt();
        return new Result<List<BuRepairPlanVOWithTaskGantt>>().successResult(repairPlanVOWithGanttList);
    }

}

