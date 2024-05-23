package org.jeecg.modules.basemanage.tpplan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanForms;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanFormsQueryVO;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanFormsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 计划模板表单 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Api(tags = "计划模板")
@Slf4j
@RestController
@RequestMapping("/tp-plan/forms")
public class BuTpRepairPlanFormsController {

    private final BuTpRepairPlanFormsService buTpRepairPlanFormsService;

    public BuTpRepairPlanFormsController(BuTpRepairPlanFormsService buTpRepairPlanFormsService) {
        this.buTpRepairPlanFormsService = buTpRepairPlanFormsService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "表单-查询分页")
    @OperationLog()
    public Result<IPage<BuTpRepairPlanForms>> pageForms(@Validated BuTpRepairPlanFormsQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTpRepairPlanForms> page = buTpRepairPlanFormsService.pageForms(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTpRepairPlanForms>>().successResult(page);
    }

    @GetMapping("/list")
    @ApiOperation(value = "表单-查询列表")
    @OperationLog()
    public Result<List<BuTpRepairPlanForms>> listForms(@Validated BuTpRepairPlanFormsQueryVO queryVO) throws Exception {
        List<BuTpRepairPlanForms> list = buTpRepairPlanFormsService.listForms(queryVO);
        return new Result<List<BuTpRepairPlanForms>>().successResult(list);
    }

    @GetMapping("/get")
    @ApiOperation(value = "表单-根据id查询")
    @OperationLog()
    public Result<BuTpRepairPlanForms> get(@RequestParam @ApiParam(value = "计划模板表单id", required = true) String id) throws Exception {
        BuTpRepairPlanForms form = buTpRepairPlanFormsService.getFormById(id);
        return new Result<BuTpRepairPlanForms>().successResult(form);
    }

    @PostMapping("/add")
    @ApiOperation(value = "表单-新增")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuTpRepairPlanForms buTpRepairPlanForms) {
        boolean flag = buTpRepairPlanFormsService.save(buTpRepairPlanForms);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "表单-修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuTpRepairPlanForms buTpRepairPlanForms) {
        boolean flag = buTpRepairPlanFormsService.updateFormById(buTpRepairPlanForms);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "表单-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "计划模板表单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buTpRepairPlanFormsService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

