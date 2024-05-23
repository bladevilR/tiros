package org.jeecg.modules.report.fault.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.report.fault.bean.vo.FaultCountQueryVO;
import org.jeecg.modules.report.fault.bean.vo.FaultTrainCountSysVO;
import org.jeecg.modules.report.fault.service.BuFaultTrainCountReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 故障报表 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-15
 */
@Api(tags = "故障汇总(单列)")
@Slf4j
@RestController
@RequestMapping("/report/fault/train-count")
public class BuFaultTrainCountReportController {

    private final BuFaultTrainCountReportService buFaultTrainCountReportService;

    public BuFaultTrainCountReportController(BuFaultTrainCountReportService buFaultTrainCountReportService) {
        this.buFaultTrainCountReportService = buFaultTrainCountReportService;
    }


    @GetMapping("/sys")
    @ApiOperation(value = "故障各系统统计(含架修期和质保期)")
    @OperationLog()
    public Result<List<FaultTrainCountSysVO>> countTrainSysFault(@Validated FaultCountQueryVO queryVO) throws Exception {
        List<FaultTrainCountSysVO> trainCountSysVOList = buFaultTrainCountReportService.countTrainSysFault(queryVO);
        return new Result<List<FaultTrainCountSysVO>>().successResult(trainCountSysVOList);
    }

}
