package org.jeecg.modules.outsource.training.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;
import org.jeecg.modules.outsource.training.bean.BuTrainingRecord;
import org.jeecg.modules.outsource.training.bean.vo.BuTrainingRecordQueryVO;
import org.jeecg.modules.outsource.training.service.BuTrainingRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2021-05-02
 */
@Api(tags = "委外培训记录")
@Slf4j
@RestController
@RequestMapping("/training/record/")
public class BuTrainingRecordController {

    private final BuTrainingRecordService trainingRecordService;

    public BuTrainingRecordController(BuTrainingRecordService trainingRecordService) {
        this.trainingRecordService = trainingRecordService;
    }


    @GetMapping("page")
    @ApiOperation(value = "查询培训记录（分页）")
    @OperationLog()
    public Result<IPage<BuTrainingRecord>> page(@Validated BuTrainingRecordQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTrainingRecord> page = trainingRecordService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainingRecord>>().successResult(page);
    }

    @GetMapping("user/page")
    @ApiOperation(value = "查询培训记录（分页）根据人员")
    @OperationLog()
    public Result<IPage<BuTrainingRecord>> userPage(@Validated BuTrainingRecordQueryVO queryVO,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTrainingRecord> page = trainingRecordService.userPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTrainingRecord>>().successResult(page);
    }

    @PostMapping("add")
    @ApiOperation(value = "新增培训记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuTrainingRecord trainingRecord) throws Exception {
        boolean flag = trainingRecordService.saveTrainingRecord(trainingRecord);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改培训记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuTrainingRecord trainingRecord) throws Exception {
        boolean flag = trainingRecordService.editTrainingRecord(trainingRecord);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除培训记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") @ApiParam(name = "ids", value = "培训记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean deleteFlag = trainingRecordService.delBatch(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(deleteFlag);
    }

}
