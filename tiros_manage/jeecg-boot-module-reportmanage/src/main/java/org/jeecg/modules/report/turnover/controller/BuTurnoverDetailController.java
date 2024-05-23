package org.jeecg.modules.report.turnover.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailQueryVO;
import org.jeecg.modules.report.turnover.bean.vo.TurnoverDetailResultVO;
import org.jeecg.modules.report.turnover.service.BuTrainHistoryChangeReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 周转件明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
@Api(tags = "周转件明细")
@Slf4j
@RestController
@RequestMapping("/report/turnover/detail")
public class BuTurnoverDetailController {

    private final BuTrainHistoryChangeReportService buTrainHistoryChangeReportService;

    public BuTurnoverDetailController(BuTrainHistoryChangeReportService buTrainHistoryChangeReportService) {
        this.buTrainHistoryChangeReportService = buTrainHistoryChangeReportService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询周转件明细记录（分页）", notes = "原/现部件安装位置、原部件车号、备注 暂时未空")
    @OperationLog()
    public Result<IPage<TurnoverDetailResultVO>> page(@Validated TurnoverDetailQueryVO queryVO,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) throws Exception {
        IPage<TurnoverDetailResultVO> page = buTrainHistoryChangeReportService.pageTurnoverDetail(queryVO, pageNo, pageSize);
        return new Result<IPage<TurnoverDetailResultVO>>().successResult(page);
    }

}
