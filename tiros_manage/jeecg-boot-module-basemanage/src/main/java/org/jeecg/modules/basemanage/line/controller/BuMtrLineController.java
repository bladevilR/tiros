package org.jeecg.modules.basemanage.line.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.service.IBuMtrLineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 线路 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Api(tags = "线路")
@Slf4j
@RestController
@RequestMapping("/line")
public class BuMtrLineController {

    private final IBuMtrLineService buMtrLineService;

    public BuMtrLineController(IBuMtrLineService buMtrLineService) {
        this.buMtrLineService = buMtrLineService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "获取所有线路")
    @OperationLog()
    public Result<List<BuMtrLine>> listAll() throws Exception {
        List<BuMtrLine> lineList = buMtrLineService.listAll();
        return new Result<List<BuMtrLine>>().successResult(lineList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查线路")
    @OperationLog()
    public Result<BuMtrLine> getById(@RequestParam @ApiParam(value = "线路id", required = true) String lineId) throws Exception {
        BuMtrLine line = buMtrLineService.getLineById(lineId);
        return new Result<BuMtrLine>().successResult(line);
    }

}
