package org.jeecg.modules.dispatch.exchange.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangRectifyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 交接车整改项 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Api(tags = "交接车管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/exchange/rectify")
public class BuRepairExchangRectifyController {

    private final BuRepairExchangRectifyService buRepairExchangRectifyService;

    public BuRepairExchangRectifyController(BuRepairExchangRectifyService buRepairExchangRectifyService) {
        this.buRepairExchangRectifyService = buRepairExchangRectifyService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询交接车整改项记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairExchangRectify>> page(@RequestParam @ApiParam(value = "交接车记录id", required = true) String exchangeId,
                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairExchangRectify> page = buRepairExchangRectifyService.page(exchangeId, pageNo, pageSize);
        return new Result<IPage<BuRepairExchangRectify>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增交接车整改项记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuRepairExchangRectify buRepairExchangRectify) {
        boolean flag = buRepairExchangRectifyService.save(buRepairExchangRectify);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改交接车整改项记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuRepairExchangRectify buRepairExchangRectify) {
        boolean flag = buRepairExchangRectifyService.updateById(buRepairExchangRectify);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除交接车整改项记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "交接车整改项记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairExchangRectifyService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

