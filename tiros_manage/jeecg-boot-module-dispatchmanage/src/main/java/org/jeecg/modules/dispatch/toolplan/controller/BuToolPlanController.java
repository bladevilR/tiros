package org.jeecg.modules.dispatch.toolplan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.toolplan.bean.BuToolPlan;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanCheckVO;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanQueryVO;
import org.jeecg.modules.dispatch.toolplan.service.BuToolPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 工具(装)运用/保养计划 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-09
 */
@Api(tags = "工装运用/保养计划管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/toolplan")
public class BuToolPlanController {

    private final BuToolPlanService buToolPlanService;

    public BuToolPlanController(BuToolPlanService buToolPlanService) {
        this.buToolPlanService = buToolPlanService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工装运用/保养计划（分页）")
    @OperationLog()
    public Result<IPage<BuToolPlan>> page(@Validated BuToolPlanQueryVO queryVO,
                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuToolPlan> page = buToolPlanService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuToolPlan>>().successResult(page);
    }

    @PostMapping("/check")
    @ApiOperation(value = "工装运用/保养计划冲突检查", notes = "返回值true表示没有冲突，false表示有冲突，有冲突时会抛出异常信息")
    @OperationLog()
    public Result<Boolean> check(@RequestBody @Validated BuToolPlanCheckVO checkVO) throws Exception {
        boolean flag = buToolPlanService.checkConflict(checkVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增工装运用/保养计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuToolPlan buToolPlan) throws Exception {
        boolean flag = buToolPlanService.saveToolPlan(buToolPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改工装运用/保养计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuToolPlan buToolPlan) throws Exception {
        boolean flag = buToolPlanService.updateToolPlan(buToolPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除工装运用/保养计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工装运用/保养计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buToolPlanService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

