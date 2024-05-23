package org.jeecg.modules.basemanage.workcheck.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheck;
import org.jeecg.modules.basemanage.workcheck.bean.vo.BuWorkCheckQueryVO;
import org.jeecg.modules.basemanage.workcheck.service.BuWorkCheckService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
@Api(tags = "作业检查表(模板)")
@Slf4j
@RestController
@RequestMapping("/workcheck")
public class BuWorkCheckController {

    private final BuWorkCheckService workCheckService;

    public BuWorkCheckController(BuWorkCheckService workCheckService) {
        this.workCheckService = workCheckService;
    }


    @GetMapping(value = "/list")
    @ApiOperation("查询作业检查表(分页)")
    @OperationLog()
    public Result<Page<BuWorkCheck>> queryPageList(@Validated BuWorkCheckQueryVO queryVO,
                                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuWorkCheck> page = workCheckService.selectPage(new Page<>(pageNo, pageSize), queryVO);
        return new Result<Page<BuWorkCheck>>().successResult(page);
    }

    @GetMapping(value = "/get")
    @ApiOperation("根据id查询作业检查表详情")
    @OperationLog()
    public Result<BuWorkCheck> getWorkCheckById(@RequestParam @ApiParam(value = "检查表单id", readOnly = true) String id) throws Exception {
        BuWorkCheck check = workCheckService.getWorkCheckById(id);
        return new Result<BuWorkCheck>().successResult(check);
    }

    @PostMapping(value = "/saveOrUpdate")
    @ApiOperation("新增或者更新作业检查表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7, saveParam = false)
    public Result<Boolean> saveOrUpdate(@RequestBody @Validated BuWorkCheck workCheck) throws Exception {
        boolean flag = workCheckService.saveOrUpdateWorkCheck(workCheck);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/deleteBatch")
    @ApiOperation("批量删除作业检查表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchCategory(@RequestParam @ApiParam(value = "作业检查表ids,多个逗号隔开", required = true) String ids) throws Exception {
        boolean flag = workCheckService.deleteBatchWorkCheck(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

