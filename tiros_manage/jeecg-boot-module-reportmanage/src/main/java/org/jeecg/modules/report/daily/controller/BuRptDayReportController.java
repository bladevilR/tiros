package org.jeecg.modules.report.daily.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.daily.bean.BuRptDayReport;
import org.jeecg.modules.report.daily.bean.vo.BuDayReportQueryVO;
import org.jeecg.modules.report.daily.service.BuRptDayReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 生产日报 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
@Api(tags = "生产日报")
@Slf4j
@RestController
@RequestMapping("/report/daily")
public class BuRptDayReportController {

    private final BuRptDayReportService buRptDayReportService;

    public BuRptDayReportController(BuRptDayReportService buRptDayReportService) {
        this.buRptDayReportService = buRptDayReportService;
    }


    @GetMapping("/get")
    @ApiOperation(value = "查询生产日报", notes = "如果不存在，则生成。未保存到数据库")
    @OperationLog()
    public Result<BuRptDayReport> getDayReport(@Validated BuDayReportQueryVO queryVO) throws Exception {
        BuRptDayReport dayReport = buRptDayReportService.getDayReport(queryVO);
        return new Result<BuRptDayReport>().successResult(dayReport);
    }

    @GetMapping("/generate")
    @ApiOperation(value = "生成生产日报", notes = "未保存到数据库")
    @OperationLog()
    public Result<BuRptDayReport> generateDayReport(@Validated BuDayReportQueryVO queryVO) throws Exception {
        BuRptDayReport dayReport = buRptDayReportService.generateDayReport(queryVO);
        return new Result<BuRptDayReport>().successResult(dayReport);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存生产日报")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveDayReport(@RequestBody @Validated BuRptDayReport dayReport) throws Exception {
        Boolean flag = buRptDayReportService.saveDayReport(dayReport);
        return new Result<Boolean>().successResult(flag);
    }

}

