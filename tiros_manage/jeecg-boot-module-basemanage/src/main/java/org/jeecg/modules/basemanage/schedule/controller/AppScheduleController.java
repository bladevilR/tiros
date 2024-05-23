package org.jeecg.modules.basemanage.schedule.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.schedule.bean.BuBaseSchedule;
import org.jeecg.modules.basemanage.schedule.bean.vo.BuBaseScheduleCheckVO;
import org.jeecg.modules.basemanage.schedule.service.BuBaseScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 日程信息 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@AppController
@Api(tags = "日程-公共")
@RestController
@RequestMapping("/app/schedule")
public class AppScheduleController {

    private final BuBaseScheduleService buBaseScheduleService;

    public AppScheduleController(BuBaseScheduleService buBaseScheduleService) {
        this.buBaseScheduleService = buBaseScheduleService;
    }


    @GetMapping("/check")
    @ApiOperation(value = "查询当前用户指定月份每日是否有日程安排", notes = "默认当月")
    @OperationLog()
    public Result<List<BuBaseScheduleCheckVO>> listScheduleCheck(@RequestParam(required = false) @ApiParam(value = "年") Integer year,
                                                                 @RequestParam(required = false) @ApiParam(value = "月") Integer month) throws Exception {
        List<BuBaseScheduleCheckVO> checkVOList = buBaseScheduleService.listScheduleCheck(year, month);
        return new Result<List<BuBaseScheduleCheckVO>>().successResult(checkVOList);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询当前用户指定日期日程明细(列表)", notes = "默认当天")
    @OperationLog()
    public Result<List<BuBaseSchedule>> listSchedule(@RequestParam(required = false) @ApiParam(value = "日期 yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) throws Exception {
        List<BuBaseSchedule> scheduleList = buBaseScheduleService.listSchedule(date);
        return new Result<List<BuBaseSchedule>>().successResult(scheduleList);
    }

}

