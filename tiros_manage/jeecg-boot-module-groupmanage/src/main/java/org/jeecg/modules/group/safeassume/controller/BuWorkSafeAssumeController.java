package org.jeecg.modules.group.safeassume.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.safeassume.bean.BuWorkSafeAssume;
import org.jeecg.modules.group.safeassume.bean.vo.BuWorkSafeAssumeQueryVO;
import org.jeecg.modules.group.safeassume.service.BuWorkSafeAssumeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 安全预想 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Api(tags = "安全预想")
@Slf4j
@RestController
@RequestMapping("/group/safeassume")
public class BuWorkSafeAssumeController {

    private final BuWorkSafeAssumeService buWorkSafeAssumeService;

    public BuWorkSafeAssumeController(BuWorkSafeAssumeService buWorkSafeAssumeService) {
        this.buWorkSafeAssumeService = buWorkSafeAssumeService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询安全预想（分页）")
    @OperationLog()
    public Result<IPage<BuWorkSafeAssume>> page(@Validated BuWorkSafeAssumeQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkSafeAssume> page = buWorkSafeAssumeService.pageSafeAssume(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkSafeAssume>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询安全预想详情")
    @OperationLog()
    public Result<BuWorkSafeAssume> get(@RequestParam @ApiParam(value = "安全预想id", required = true) String id) throws Exception {
        BuWorkSafeAssume safeAssume = buWorkSafeAssumeService.selectById(id);
        return new Result<BuWorkSafeAssume>().successResult(safeAssume);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增安全预想")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuWorkSafeAssume buWorkSafeAssume) {
        boolean flag = buWorkSafeAssumeService.save(buWorkSafeAssume);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改安全预想")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuWorkSafeAssume buWorkSafeAssume) {
        boolean flag = buWorkSafeAssumeService.updateById(buWorkSafeAssume);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除安全预想（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "安全预想ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkSafeAssumeService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

