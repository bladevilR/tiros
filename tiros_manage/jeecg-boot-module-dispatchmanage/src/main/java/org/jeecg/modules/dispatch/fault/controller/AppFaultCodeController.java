package org.jeecg.modules.dispatch.fault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCategory;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCode;
import org.jeecg.modules.dispatch.fault.bean.BuFaultReason;
import org.jeecg.modules.dispatch.fault.bean.BuFaultSolution;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCategoryQueryVO;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCodeQueryVO;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultReasonQueryVO;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultSolutionQueryVO;
import org.jeecg.modules.dispatch.fault.service.BuFaultCategoryService;
import org.jeecg.modules.dispatch.fault.service.BuFaultCodeService;
import org.jeecg.modules.dispatch.fault.service.BuFaultReasonService;
import org.jeecg.modules.dispatch.fault.service.BuFaultSolutionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 故障分类 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-29
 */
@AppController
@Api(tags = "故障分类代码-公共")
@Slf4j
@RestController
@RequestMapping("/app/fault")
public class AppFaultCodeController {

    private final BuFaultCategoryService buFaultCategoryService;
    private final BuFaultCodeService buFaultCodeService;
    private final BuFaultReasonService buFaultReasonService;
    private final BuFaultSolutionService buFaultSolutionService;

    public AppFaultCodeController(BuFaultCategoryService buFaultCategoryService,
                                  BuFaultCodeService buFaultCodeService,
                                  BuFaultReasonService buFaultReasonService,
                                  BuFaultSolutionService buFaultSolutionService) {
        this.buFaultCategoryService = buFaultCategoryService;
        this.buFaultCodeService = buFaultCodeService;
        this.buFaultReasonService = buFaultReasonService;
        this.buFaultSolutionService = buFaultSolutionService;
    }


    @GetMapping("/category/page")
    @ApiOperation(value = "查询分类(分页)")
    @OperationLog()
    public Result<IPage<BuFaultCategory>> page(@Validated BuFaultCategoryQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultCategory> page = buFaultCategoryService.pageFaultCategory(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultCategory>>().successResult(page);
    }

    @GetMapping("/code/page")
    @ApiOperation(value = "查询代码(分页)")
    @OperationLog()
    public Result<IPage<BuFaultCode>> page(@Validated BuFaultCodeQueryVO queryVO,
                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultCode> page = buFaultCodeService.pageFaultCode(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultCode>>().successResult(page);
    }

    @GetMapping("/reason/page")
    @ApiOperation(value = "查询原因(分页)")
    @OperationLog()
    public Result<IPage<BuFaultReason>> page(@Validated BuFaultReasonQueryVO queryVO,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultReason> page = buFaultReasonService.pageFaultReason(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultReason>>().successResult(page);
    }

    @GetMapping("/solution/page")
    @ApiOperation(value = "查询处理措施(分页)")
    @OperationLog()
    public Result<IPage<BuFaultSolution>> page(@Validated BuFaultSolutionQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultSolution> page = buFaultSolutionService.pageFaultSolution(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultSolution>>().successResult(page);
    }

}
