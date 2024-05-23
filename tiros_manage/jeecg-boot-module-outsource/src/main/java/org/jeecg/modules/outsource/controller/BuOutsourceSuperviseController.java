package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuOutsourceSupervise;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceSuperviseQueryVO;
import org.jeecg.modules.outsource.service.BuOutsourceSuperviseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 委外监修 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "委外监修")
@Slf4j
@RestController
@RequestMapping("/outsource/supervise")
public class BuOutsourceSuperviseController {

    private final BuOutsourceSuperviseService superviseService;

    public BuOutsourceSuperviseController(BuOutsourceSuperviseService superviseService) {
        this.superviseService = superviseService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询监修任务（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceSupervise>> page(@Validated BuOutsourceSuperviseQueryVO queryVO,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceSupervise> page = superviseService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceSupervise>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询监修任务")
    @OperationLog()
    public Result<BuOutsourceSupervise> getSuperviseById(@RequestParam @ApiParam(value = "监修任务id", required = true) String id) throws Exception {
        BuOutsourceSupervise supervise = superviseService.getSuperviseById(id);
        return new Result<BuOutsourceSupervise>().successResult(supervise);
    }

    @GetMapping("/get-by-order")
    @ApiOperation(value = "根据工单id和任务id查询监修任务")
    @OperationLog()
    public Result<BuOutsourceSupervise> getSuperviseByOrderAndTask(@RequestParam @ApiParam(value = "工单id", required = true) String workOrderId,
                                                                   @RequestParam @ApiParam(value = "工单任务id", required = true) String orderTaskId) throws Exception {
        BuOutsourceSupervise supervise = superviseService.getSuperviseByOrderAndTask(workOrderId, orderTaskId);
        if (null != supervise) {
            return new Result<BuOutsourceSupervise>().successResult(supervise);
        } else {
            Result<BuOutsourceSupervise> result = new Result<BuOutsourceSupervise>().successResult(null);
            result.setSuccess(false);
            result.setMessage("无监修任务记录");
            return result;
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuOutsourceSupervise supplier) throws Exception {
        boolean flag = superviseService.saveSupervise(supplier);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuOutsourceSupervise supplier) throws Exception {
        boolean flag = superviseService.updateById(supplier);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除任务（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "任务ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = superviseService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}

