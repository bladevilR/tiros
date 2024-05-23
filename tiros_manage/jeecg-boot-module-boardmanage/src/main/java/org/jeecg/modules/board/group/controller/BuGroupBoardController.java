package org.jeecg.modules.board.group.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.board.group.bean.vo.*;
import org.jeecg.modules.board.group.service.BuGroupBoardService;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 班组看板 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Api(tags = "班组看板")
@Slf4j
@RestController
@RequestMapping("/board/group")
public class BuGroupBoardController {

    private final BuGroupBoardService buGroupBoardService;

    public BuGroupBoardController(BuGroupBoardService buGroupBoardService) {
        this.buGroupBoardService = buGroupBoardService;
    }


    @GetMapping("/order/total/count")
    @ApiOperation(value = "查询-班组工单进度")
    @OperationLog()
    public Result<BuGroupOrderCount> getGroupOrderCount() throws Exception {
        BuGroupOrderCount groupOrderCount = buGroupBoardService.getGroupOrderCount();
        return new Result<BuGroupOrderCount>().successResult(groupOrderCount);
    }

    @GetMapping("/order/today/list")
    @ApiOperation(value = "查询-班组当日工单列表")
    @OperationLog()
    public Result<List<BuWorkOrder>> listGroupTodayOrder() throws Exception {
        List<BuWorkOrder> orderList = buGroupBoardService.listGroupTodayOrder();
        return new Result<List<BuWorkOrder>>().successResult(orderList);
    }

    @GetMapping("/person/rank")
    @ApiOperation(value = "查询-班组人员排名")
    @OperationLog()
    public Result<List<BuGroupPersonRank>> listGroupPersonRank() throws Exception {
        List<BuGroupPersonRank> personRankList = buGroupBoardService.listGroupPersonRank();
        return new Result<List<BuGroupPersonRank>>().successResult(personRankList);
    }

    @GetMapping("/material/lack")
    @ApiOperation(value = "查询-班组缺料情况")
    @OperationLog()
    public Result<List<BuGroupMaterialLack>> listGroupMaterialLack() throws Exception {
        List<BuGroupMaterialLack> lackList = buGroupBoardService.listGroupMaterialLack();
        return new Result<List<BuGroupMaterialLack>>().successResult(lackList);
    }

    @GetMapping("/tool/check-alert")
    @ApiOperation(value = "查询-班组工器具预警")
    @OperationLog()
    public Result<List<BuGroupToolCheckAlert>> listGroupToolCheckAlert() throws Exception {
        List<BuGroupToolCheckAlert> toolAlertList = buGroupBoardService.listGroupToolCheckAlert();
        return new Result<List<BuGroupToolCheckAlert>>().successResult(toolAlertList);
    }

    @GetMapping("/rank")
    @ApiOperation(value = "查询-班组排名")
    @OperationLog()
    public Result<BuGroupRank> getGroupRank() throws Exception {
        BuGroupRank groupRank = buGroupBoardService.getGroupRank();
        return new Result<BuGroupRank>().successResult(groupRank);
    }

}

