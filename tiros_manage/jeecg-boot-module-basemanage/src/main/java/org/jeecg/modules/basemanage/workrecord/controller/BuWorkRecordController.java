package org.jeecg.modules.basemanage.workrecord.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordCategory;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordDetail;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordDetailQueryVO;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordQueryVO;
import org.jeecg.modules.basemanage.workrecord.service.BuWorkRecordCategoryService;
import org.jeecg.modules.basemanage.workrecord.service.BuWorkRecordDetailService;
import org.jeecg.modules.basemanage.workrecord.service.BuWorkRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 作业记录表 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Api(tags = "作业记录")
@Slf4j
@RestController
@RequestMapping("/workRecord")
public class BuWorkRecordController {

    private final BuWorkRecordService workRecordService;
    private final BuWorkRecordDetailService workRecordDetailService;
    private final BuWorkRecordCategoryService workRecordCategoryService;

    public BuWorkRecordController(BuWorkRecordService workRecordService,
                                  BuWorkRecordDetailService workRecordDetailService,
                                  BuWorkRecordCategoryService workRecordCategoryService) {
        this.workRecordService = workRecordService;
        this.workRecordDetailService = workRecordDetailService;
        this.workRecordCategoryService = workRecordCategoryService;
    }


    @GetMapping(value = "/page")
    @ApiOperation("作业记录表-查询(分页)")
    @OperationLog()
    public Result<Page<BuWorkRecord>> pageWorkRecord(@Validated BuWorkRecordQueryVO queryVO,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest req) throws Exception {
        Page<BuWorkRecord> page = workRecordService.pageWorkRecord(new Page<>(pageNo, pageSize), queryVO);
        return new Result<Page<BuWorkRecord>>().successResult(page);
    }

    @GetMapping(value = "/get")
    @ApiOperation("作业记录表-根据id查询(含分类和明细)")
    @OperationLog()
    public Result<BuWorkRecord> getWorkRecordById(@RequestParam @ApiParam(value = "作业记录表id", required = true) String id) throws Exception {
        BuWorkRecord workRecord = workRecordService.getWorkRecordById(id);
        return new Result<BuWorkRecord>().successResult(workRecord);
    }

    @PostMapping(value = "/saveOrUpdate")
    @ApiOperation("作业记录表-新增或者更新")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdate(@RequestBody @Validated BuWorkRecord buWorkRecord) {
        boolean flag = workRecordService.saveOrUpdate(buWorkRecord);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("作业记录表-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "作业记录表id,多个用英文逗号隔开") String ids) throws Exception {
        boolean flag = workRecordService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping(value = "/category/list/{id}")
    @ApiOperation("作业记录表分类-查询列表")
    @OperationLog()
    public Result<List<BuWorkRecordCategory>> listWorkRecordCategory(@PathVariable @ApiParam(value = "作业记录id") String id) throws Exception {
        List<BuWorkRecordCategory> categoryList = workRecordCategoryService.listByWorkRecordId(id);
        return new Result<List<BuWorkRecordCategory>>().successResult(categoryList);
    }

    @PostMapping(value = "/category/saveOrUpdate")
    @ApiOperation("作业记录表分类-新增或更新")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdateWorkRecordCategory(@RequestBody @Validated BuWorkRecordCategory buWorkRecordCategory) {
        boolean flag = workRecordCategoryService.saveOrUpdate(buWorkRecordCategory);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/category/delete")
    @ApiOperation("作业记录表分类-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteWorkRecordCategory(@RequestParam @ApiParam(value = "作业记录分类项ids,多个逗号隔开", required = true) String ids) throws Exception {
        boolean flag = workRecordCategoryService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping(value = "/detail/list")
    @ApiOperation("作业记录表明细-查询列表")
    @OperationLog()
    public Result<List<BuWorkRecordDetail>> listWorkRecordDetail(@Validated BuWorkRecordDetailQueryVO queryVO) throws Exception {
        List<BuWorkRecordDetail> recordDetailList = workRecordDetailService.listWorkRecordDetail(queryVO);
        return new Result<List<BuWorkRecordDetail>>().successResult(recordDetailList);
    }

    @PostMapping(value = "/detail/saveOrUpdate")
    @ApiOperation("作业记录表明细-新增或更新")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdateWorkRecordDetail(@RequestBody @Validated BuWorkRecordDetail buWorkRecordDetail) {
        boolean flag = workRecordDetailService.saveOrUpdate(buWorkRecordDetail);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping(value = "/detail/delete/{id}")
    @ApiOperation("作业记录表明细-删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteWorkRecordDetail(@PathVariable @ApiParam(value = "作业记录表明细id") String id) {
        boolean flag = workRecordDetailService.removeById(id);
        return new Result<Boolean>().successResult(flag);
    }

}