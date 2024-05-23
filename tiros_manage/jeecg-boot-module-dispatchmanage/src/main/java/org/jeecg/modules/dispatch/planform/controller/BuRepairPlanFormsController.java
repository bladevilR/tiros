package org.jeecg.modules.dispatch.planform.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.planform.bean.*;
import org.jeecg.modules.dispatch.planform.bean.vo.*;
import org.jeecg.modules.dispatch.planform.service.BuRepairPlanFormsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 列计划表单，当列计划审批通过后，根据该配置表自动生成相关数据记录 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Api(tags = "表单-实例")
@Slf4j
@RestController
@RequestMapping("/serialPlan/plan/forms")
public class BuRepairPlanFormsController {

    private final BuRepairPlanFormsService buRepairPlanFormsService;

    public BuRepairPlanFormsController(BuRepairPlanFormsService buRepairPlanFormsService) {
        this.buRepairPlanFormsService = buRepairPlanFormsService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "列计划表单-查询分页")
    @OperationLog()
    public Result<IPage<BuRepairPlanForms>> pageForms(@Validated BuRepairPlanFormsQueryVO queryVO,
                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairPlanForms> page = buRepairPlanFormsService.pageForms(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairPlanForms>>().successResult(page);
    }

    @GetMapping("/list")
    @ApiOperation(value = "表单-查询列表")
    @OperationLog()
    public Result<List<BuRepairPlanForms>> listForms(@Validated BuRepairPlanFormsQueryVO queryVO) throws Exception {
        List<BuRepairPlanForms> list = buRepairPlanFormsService.listForms(queryVO);
        return new Result<List<BuRepairPlanForms>>().successResult(list);
    }

    @GetMapping("/get")
    @ApiOperation(value = "列计划表单-根据id查询")
    @OperationLog()
    public Result<BuRepairPlanForms> getFormById(@RequestParam @ApiParam(value = "列计划表单id", required = true) String id) throws Exception {
        BuRepairPlanForms form = buRepairPlanFormsService.getFormById(id);
        return new Result<BuRepairPlanForms>().successResult(form);
    }

    @PostMapping("/add")
    @ApiOperation(value = "列计划表单-新增", tags = "调度管理——工单管理")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> addForm(@RequestBody @Validated BuRepairPlanForms buRepairPlanForms) {
        boolean flag = buRepairPlanFormsService.save(buRepairPlanForms);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "列计划表单-修改", tags = "调度管理——工单管理")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateFormById(@RequestBody @Validated BuRepairPlanForms buRepairPlanForms) throws Exception {
        boolean flag = buRepairPlanFormsService.updateFormById(buRepairPlanForms);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit/titleOrAsset")
    @ApiOperation(value = "修改表单实例的名称或车辆设备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> editTitleOrAsset(@RequestBody @Validated BuRepairPlanFormEditVO planFormEditVO) throws Exception {
        boolean flag = buRepairPlanFormsService.editTitleOrAsset(planFormEditVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "列计划表单-删除(批量)", tags = "调度管理——工单管理")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchForms(@RequestParam @ApiParam(value = "表单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanFormsService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/generate/plan")
    @ApiOperation(value = "列计划表单-生成列计划表单实例")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> generateFormsRelationByPlanId(@RequestParam @ApiParam(value = "列计划id", required = true) String planId) throws Exception {
        boolean flag = buRepairPlanFormsService.generateFormsRelationDataByPlanId(planId);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/online-form/get")
    @ApiOperation(value = "数据表单-数据记录表(实例)-根据id查询", tags = "工班管理——工单作业")
    @OperationLog()
    public Result<BuPlanFormDataRecord> getFormDataRecordFormById(@Validated BuPlanOnlineFormQueryVO queryVO) throws Exception {
        BuPlanFormDataRecord formDataRecord = buRepairPlanFormsService.getFormDataRecordById(queryVO);
        return new Result<BuPlanFormDataRecord>().successResult(formDataRecord);
    }

    @GetMapping("/online-form/alert-records")
    @ApiOperation(value = "数据表单-数据记录表(实例)-根据id查询预警记录")
    @OperationLog()
    public Result<List<BuPlanFormValues>> listAlertRecordByDataRecordFormId(@RequestParam @ApiParam(value = "数据表单实例id'", required = true) String dataRecordId) throws Exception {
        List<BuPlanFormValues> valuesList = buRepairPlanFormsService.listAlertRecordByDataRecordFormId(dataRecordId);
        return new Result<List<BuPlanFormValues>>().successResult(valuesList);
    }

    @GetMapping("/work-record/get")
    @ApiOperation(value = "记录表单-作业记录表(实例)-根据id查询", tags = "工班管理——工单作业")
    @OperationLog()
    public Result<BuPlanFormWorkRecord> getFormWorkRecordById(@Validated BuPlanWorkRecordFormQueryVO queryVO) throws Exception {
        BuPlanFormWorkRecord planFormWorkRecord = buRepairPlanFormsService.getFormWorkRecordById(queryVO);
        return new Result<BuPlanFormWorkRecord>().successResult(planFormWorkRecord);
    }

    @GetMapping("/check-record/get")
    @ApiOperation(value = "检查表单-检查记录表(实例)-根据id查询", tags = "工班管理——工单作业")
    @OperationLog()
    public Result<BuPlanFormCheckRecord> getFormCheckRecordById(@Validated BuPlanCheckRecordFormQueryVO queryVO) throws Exception {
        BuPlanFormCheckRecord planFormCheckRecord = buRepairPlanFormsService.getFormCheckRecordById(queryVO);
        return new Result<BuPlanFormCheckRecord>().successResult(planFormCheckRecord);
    }

    @GetMapping("/check-record/item/list")
    @ApiOperation(value = "检查表单-检查记录表(实例)-查询检查项列表", tags = "工班管理——工单作业")
    @OperationLog()
    public Result<List<BuPlanFormCheckRecordItem>> listCheckRecordItemByCheckRecordId(@Validated BuPlanCheckRecordFormQueryVO queryVO) throws Exception {
        List<BuPlanFormCheckRecordItem> itemList = buRepairPlanFormsService.listCheckRecordItemByCheckRecordId(queryVO);
        return new Result<List<BuPlanFormCheckRecordItem>>().successResult(itemList);
    }

    @GetMapping("/instance/list")
    @ApiOperation(value = "获取所有表单实例")
    @OperationLog()
    public Result<List<PlanFormInstance>> listPlanFormInst(@Validated BuRepairPlanFormsInstQueryVO queryVO) throws Exception {
        List<PlanFormInstance> formInstanceList = buRepairPlanFormsService.listPlanFormInst(queryVO);
        return new Result<List<PlanFormInstance>>().successResult(formInstanceList);
    }

    @PostMapping("/instance/add")
    @ApiOperation(value = "添加表单实例", tags = "创建实例表单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> addPlanFormInst(@RequestBody @Validated BuRepairPlanForms planForm) throws Exception {
        boolean flag = buRepairPlanFormsService.addPlanFormInst(planForm);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/instance/delete")
    @ApiOperation(value = "表单实例-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchPlanFormInst(@RequestBody @Validated List<BuRepairPlanFormInstDeleteVO> formInstDeleteVOList) throws Exception {
        boolean flag = buRepairPlanFormsService.deleteBatchPlanFormInst(formInstDeleteVOList);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/instance/regenerate")
    @ApiOperation(value = "表单实例-重新生成")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> regeneratePlanFormInst(@RequestParam @ApiParam(value = "列计划表单id", required = true) String planFormId,
                                                  @RequestParam(required = false) @ApiParam(value = "旧的表单类型 当和新的表单类型不一样时传入") Integer oldFormType) throws Exception {
        boolean flag = buRepairPlanFormsService.regeneratePlanFormInst(planFormId, oldFormType);
        return new Result<Boolean>().successResult(flag);
    }

}

