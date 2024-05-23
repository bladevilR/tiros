package org.jeecg.modules.dispatch.exchange.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangLeaveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 开口项（遗留问题） 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Api(tags = "交接车管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/exchange/leave")
public class BuRepairExchangLeaveController {

    private final BuRepairExchangLeaveService buRepairExchangLeaveService;

    public BuRepairExchangLeaveController(BuRepairExchangLeaveService buRepairExchangLeaveService) {
        this.buRepairExchangLeaveService = buRepairExchangLeaveService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询交接车开口项记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairExchangLeave>> page(@RequestParam @ApiParam(value = "交接车记录id", required = true) String exchangeId,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairExchangLeave> page = buRepairExchangLeaveService.page(exchangeId, pageNo, pageSize);
        return new Result<IPage<BuRepairExchangLeave>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增交接车开口项记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuRepairExchangLeave buRepairExchangLeave) {
        boolean flag = buRepairExchangLeaveService.save(buRepairExchangLeave);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改交接车开口项记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuRepairExchangLeave buRepairExchangLeave) {
        boolean flag = buRepairExchangLeaveService.updateById(buRepairExchangLeave);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除交接车开口项记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "交接车开口项记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairExchangLeaveService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

