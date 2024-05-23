package org.jeecg.modules.basemanage.org.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.org.bean.BuTraining;
import org.jeecg.modules.basemanage.org.bean.vo.BuTrainingQueryVO;
import org.jeecg.modules.basemanage.org.service.BuTrainingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 培训记录 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Api(tags = "组织人员信息")
@Slf4j
@RestController
@RequestMapping("/org/user/training")
public class BuTrainingController {

    private final BuTrainingService buTrainingService;

    public BuTrainingController(BuTrainingService buTrainingService) {
        this.buTrainingService = buTrainingService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "培训-查询(分页)")
    @OperationLog()
    public Result<IPage<BuTraining>> pageTraining(@Validated BuTrainingQueryVO queryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTraining> page = buTrainingService.pageTraining(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTraining>>().successResult(page);
    }

    @PostMapping("/save")
    @ApiOperation(value = "培训-保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> addTraining(@RequestBody @Validated BuTraining training) throws Exception {
        boolean flag = buTrainingService.saveTraining(training);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "培训-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteTrainingBatch(@RequestParam @ApiParam(value = "培训ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buTrainingService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

