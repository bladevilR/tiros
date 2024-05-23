package org.jeecg.modules.dispatch.serialplan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairReguDetail;
import org.jeecg.modules.dispatch.serialplan.bean.MustMaterialLack;
import org.jeecg.modules.dispatch.serialplan.bean.tp.BuTpRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanQueryVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanRelationVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanVOGantt;
import org.jeecg.modules.dispatch.serialplan.bean.vo.PlanProgressVO;
import org.jeecg.modules.dispatch.serialplan.service.BuRepairPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Api(tags = "列计划管理")
@Slf4j
@RestController
@RequestMapping("/serialPlan/plan")
public class BuRepairPlanController {

    private final BuRepairPlanService buRepairPlanService;

    public BuRepairPlanController(BuRepairPlanService buRepairPlanService) {
        this.buRepairPlanService = buRepairPlanService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询列计划记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairPlan>> page(@Validated BuRepairPlanQueryVO queryVO,
                                            @RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairPlan> page = buRepairPlanService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairPlan>>().successResult(page);
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "根据id查询列计划详情(含任务)")
    @OperationLog()
    public Result<BuRepairPlanVOGantt> selectPlanAndTask(@ApiParam(value = "列计划id") @PathVariable String id) throws Exception {
        BuRepairPlanVOGantt planGantt = buRepairPlanService.selectPlanAndTask(id);
        return new Result<BuRepairPlanVOGantt>().successResult(planGantt);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增列计划", notes = "如果选择了计划模板，根据计划模板的任务及任务关联信息生成列计划的对应信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuRepairPlan buRepairPlan) throws Exception {
        boolean flag = buRepairPlanService.savePlan(buRepairPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑列计划",
            notes = "如果修改了计划模板、时间、车辆中的任何一项， 删除原有的所有任务和任务关联信息后，根据新的计划模板的任务及任务关联信息生成列计划的对应信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuRepairPlan buRepairPlan) throws Exception {
        boolean flag = buRepairPlanService.editPlan(buRepairPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/rebuild")
    @ApiOperation(value = "重新生成列计划任务及任务相关的信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> rebuild(@RequestParam @ApiParam(value = "列计划id", required = true) String planId, @RequestParam @ApiParam(value = "重新生成第几天及后面的", required = false) Integer beginDayIndex) {
        boolean result = buRepairPlanService.rebuild(planId, beginDayIndex);
        return new Result<Boolean>().successResult(result);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除列计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "列计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/suspend")
    @ApiOperation(value = "暂停列计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> suspendPlanBatch(@RequestParam @ApiParam(value = "列计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanService.suspendPlanBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/activate")
    @ApiOperation(value = "激活列计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> activityPlanBatch(@RequestParam @ApiParam(value = "列计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanService.activatePlanBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/exchange")
    @ApiOperation(value = "查询维修车辆")
    @OperationLog()
    public Result<List<BuRepairExchang>> exchange(@RequestParam(required = false) @ApiParam(value = "线路id") String lineId) throws Exception {
        List<BuRepairExchang> exchangs = buRepairPlanService.selectRePairExchang(lineId);
        return new Result<List<BuRepairExchang>>().successResult(exchangs);
    }

    @GetMapping("/startDate")
    @ApiOperation(value = "查询计划开始时间")
    @OperationLog()
    public Result<Date> startDate(@RequestParam @ApiParam(value = "车辆序号") int trainIndex,
                                  @RequestParam @ApiParam(value = "线路id") String lineId) throws Exception {
        BuRepairPlanYearDetail yearDetail = buRepairPlanService.selectRepairYearStartDate(trainIndex, lineId);
        return new Result<Date>().successResult(yearDetail.getStartDate());
    }

    @GetMapping("/tpPlan")
    @ApiOperation(value = "查询计划模板")
    @OperationLog()
    public Result<List<BuTpRepairPlan>> tpPlan() throws Exception {
        List<BuTpRepairPlan> tpRepairPlans = buRepairPlanService.selectTpRepairPlan();
        return new Result<List<BuTpRepairPlan>>().successResult(tpRepairPlans);
    }

    @GetMapping("/relevanceInfo/{id}")
    @ApiOperation(value = "查询列计划关联的信息详情", notes = "取消关联规程的查询，返回空数组")
    @OperationLog()
    public Result<BuRepairPlanRelationVO> relevanceInfo(@ApiParam(value = "计划id") @PathVariable String id) throws Exception {
        BuRepairPlanRelationVO relationVO = buRepairPlanService.selectPlanRelevanceInfo(id);
        return new Result<BuRepairPlanRelationVO>().successResult(relationVO);
    }

    @GetMapping("/noRelevance/count")
    @ApiOperation(value = "根据id查未关联的规程明细数量")
    @OperationLog()
    public Result<Integer> noRelevanceCount(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                            @RequestParam @ApiParam(value = "规程id", required = true) String reguId) throws Exception {
        Integer noRelevanceCount = buRepairPlanService.noRelevanceCount(planId, reguId);
        return new Result<Integer>().successResult(noRelevanceCount);
    }

    @GetMapping("/noRelevance/detail")
    @ApiOperation(value = "根据id查未关联的规程明细数据列表")
    @OperationLog()
    public Result<List<BuRepairReguDetail>> noRelevanceDetail(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                              @RequestParam @ApiParam(value = "规程id", required = true) String reguId) throws Exception {
        List<BuRepairReguDetail> reguDetailList = buRepairPlanService.noRelevanceDetail(planId, reguId);
        return new Result<List<BuRepairReguDetail>>().successResult(reguDetailList);
    }

    @GetMapping("/unFinish/get")
    @ApiOperation(value = "未完成任务-根据列计划id查询未完成任务信息")
    @OperationLog()
    public Result<PlanProgressVO> getPlanUnFinishTaskInfo(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                          @RequestParam(required = false) @ApiParam(value = "班组id") String groupId,
                                                          @RequestParam(required = false) @ApiParam(value = "任务状态") Integer status) throws Exception {
        PlanProgressVO progressVO = buRepairPlanService.getPlanUnFinishTaskInfo(planId, groupId, status);
        return new Result<PlanProgressVO>().successResult(progressVO);
    }

    @PostMapping("/unFinish/finish")
    @ApiOperation(value = "未完成任务-设置列计划任务为已完成")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setPlanTaskFinish(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                             @RequestParam @ApiParam(value = "列计划任务ids，多个逗号分隔", required = true) String planTaskIds) throws Exception {
        boolean flag = buRepairPlanService.setPlanTaskFinish(planId, planTaskIds);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "导入历史列计划和接车（excel）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_5, saveParam = false)
    public Result<String> importExcel(@RequestParam @ApiParam(value = "历史数据excel文件", required = true) MultipartFile excelFile,
                                      @RequestParam(required = false) @ApiParam(value = "是否清空旧的历史数据") Boolean clearOldData) throws Exception {
        String flag = buRepairPlanService.importHistoryDataFromExcel(excelFile, clearOldData);
        return new Result<String>().successResult(flag);
    }

    @PostMapping("/plan-history-cost/import")
    @ApiOperation(value = "导入列计划历史成本数据（excel）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_5, saveParam = false)
    public Result<String> importPlanHistoryCostDataFromExcel(@RequestParam @ApiParam(value = "历史成本数据excel文件", required = true) MultipartFile excelFile,
                                                             @RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        String flag = buRepairPlanService.importPlanHistoryCostDataFromExcel(excelFile, planId);
        return new Result<String>().successResult(flag);
    }

    @GetMapping("/material/lack")
    @ApiOperation(value = "当前列计划必换件缺料")
    @OperationLog()
    public Result<List<MustMaterialLack>> mustMaterialLack() throws Exception {
        List<MustMaterialLack> mustMaterialLack = buRepairPlanService.mustMaterialLack();
        return new Result<List<MustMaterialLack>>().successResult(mustMaterialLack);
    }

}

