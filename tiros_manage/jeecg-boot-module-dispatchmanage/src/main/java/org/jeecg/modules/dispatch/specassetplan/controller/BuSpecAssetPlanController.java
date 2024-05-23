package org.jeecg.modules.dispatch.specassetplan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.specassetplan.bean.BuSpecAssetPlan;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetMonthUsageQueryVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanCheckVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanQueryVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanVO;
import org.jeecg.modules.dispatch.specassetplan.service.BuSpecAssetPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

/**
 * <p>
 * 特种设备运用/保养计划 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-03
 */
@Api(tags = "特种设备运用/保养计划")
@RestController
@RequestMapping("/dispatch/specasset-plan")
public class BuSpecAssetPlanController {

    private final BuSpecAssetPlanService buSpecAssetPlanService;

    public BuSpecAssetPlanController(BuSpecAssetPlanService buSpecAssetPlanService) {
        this.buSpecAssetPlanService = buSpecAssetPlanService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询特种设备运用/保养计划（分页）")
    @OperationLog()
    public Result<IPage<BuSpecAssetPlan>> page(@Validated BuSpecAssetPlanQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuSpecAssetPlan> page = buSpecAssetPlanService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuSpecAssetPlan>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询特种设备运用/保养计划")
    @OperationLog()
    public Result<BuSpecAssetPlan> getSpecAssetPlanById(@RequestParam @ApiParam(value = "特种设备运用/保养计划id", required = true) String id) throws Exception {
        BuSpecAssetPlan specAssetPlan = buSpecAssetPlanService.getSpecAssetPlanById(id);
        return new Result<BuSpecAssetPlan>().successResult(specAssetPlan);
    }

    @PostMapping("/check")
    @ApiOperation(value = "特种设备运用/保养计划冲突检查", notes = "返回值true表示没有冲突，false表示有冲突，有冲突时会抛出异常信息")
    @OperationLog()
    public Result<Boolean> check(@RequestBody @Validated BuSpecAssetPlanCheckVO checkVO) throws Exception {
        boolean flag = buSpecAssetPlanService.checkConflict(checkVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增特种设备运用/保养计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> addSpecAssetPlan(@RequestBody @Validated BuSpecAssetPlan specAssetPlan) throws Exception {
        boolean flag = buSpecAssetPlanService.addSpecAssetPlan(specAssetPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改特种设备运用/保养计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateSpecAssetPlan(@RequestBody @Validated BuSpecAssetPlan specAssetPlan) throws Exception {
        boolean flag = buSpecAssetPlanService.updateSpecAssetPlan(specAssetPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除特种设备运用/保养计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "特种设备运用/保养计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buSpecAssetPlanService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/get-month-usage")
    @ApiOperation(value = "查询特种设备月份使用情况")
    @OperationLog()
    public Result<LinkedHashMap<String, BuSpecAssetPlanVO>> getSpecAssetMonthUsage(@Validated BuSpecAssetMonthUsageQueryVO usageQueryVO) throws Exception {
        LinkedHashMap<String, BuSpecAssetPlanVO> usage = buSpecAssetPlanService.getSpecAssetMonthUsage(usageQueryVO);
        return new Result<LinkedHashMap<String, BuSpecAssetPlanVO>>().successResult(usage);
    }

}
