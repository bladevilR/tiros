package org.jeecg.modules.basemanage.line.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.service.IBuMtrLineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 线路 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-24
 */
@AppController
@Api(tags = "线路-公共")
@Slf4j
@RestController
@RequestMapping("/app/line")
public class AppMtrLineController {

    private final IBuMtrLineService buMtrLineService;

    public AppMtrLineController(IBuMtrLineService buMtrLineService) {
        this.buMtrLineService = buMtrLineService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询所有线路", notes = "根据当前人员所属车辆段过滤，暂时没过滤")
    @OperationLog()
    public Result<List<BuMtrLine>> listByCurrentUser() throws Exception {
        List<BuMtrLine> lineList = buMtrLineService.listByCurrentUser();
        return new Result<List<BuMtrLine>>().successResult(lineList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查线路")
    @OperationLog()
    public Result<BuMtrLine> getById(@RequestParam @ApiParam(value = "线路id", required = true) String lineId) throws Exception {
        BuMtrLine line = buMtrLineService.getLineById(lineId);
        return new Result<BuMtrLine>().successResult(line);
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询所有线路(分页)")
    @OperationLog()
    public Result<IPage<BuMtrLine>> page(@RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        List<BuMtrLine> lineList = buMtrLineService.listByCurrentUser();

        List<BuMtrLine> lineListVo = lineList.stream()
                .skip(pageSize * (pageNo - 1))
                .limit(pageSize).collect(Collectors.toList());
        IPage<BuMtrLine> page = new Page();
        page.setRecords(lineListVo);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(lineList.size());
        return new Result<IPage<BuMtrLine>>().successResult(page);
    }



}
