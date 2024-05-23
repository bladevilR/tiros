package org.jeecg.modules.dispatch.exchange.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeQueryVO;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeRemarkSaveVO;
import org.jeecg.modules.dispatch.exchange.service.BuRepairExchangService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 交接车记录 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-24
 */
@Api(tags = "交接车管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/exchange")
public class BuRepairExchangController {

    private final BuRepairExchangService buRepairExchangService;

    public BuRepairExchangController(BuRepairExchangService buRepairExchangService) {
        this.buRepairExchangService = buRepairExchangService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询交接车记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairExchang>> page(@Validated BuRepairExchangeQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairExchang> page = buRepairExchangService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairExchang>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询交接车记录")
    @OperationLog()
    public Result<BuRepairExchang> get(@RequestParam @ApiParam(value = "交接车记录id", required = true) String id) throws Exception {
        BuRepairExchang repairExchang = buRepairExchangService.getRepairExchangeById(id);
        return new Result<BuRepairExchang>().successResult(repairExchang);
    }

    @GetMapping("/getByTrainNo")
    @ApiOperation(value = "根据车号查询交接车记录")
    @OperationLog()
    public Result<BuRepairExchang> getRepairedReceiveExchangeByTrainNo(@RequestParam @ApiParam(value = "车号", required = true) String trainNo) throws Exception {
        BuRepairExchang repairExchang = buRepairExchangService.getRepairedReceiveExchangeByTrainNo(trainNo);
        return new Result<BuRepairExchang>().successResult(repairExchang);
    }

    @GetMapping("/list-deliverable")
    @ApiOperation(value = "查询可交车的接车记录", notes = "已维修且未关联交车记录的接车记录")
    @OperationLog()
    public Result<List<BuRepairExchang>> listDeliverable() throws Exception {
        List<BuRepairExchang> exchangeList = buRepairExchangService.listDeliverable();
        return new Result<List<BuRepairExchang>>().successResult(exchangeList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增交接车记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuRepairExchang buRepairExchang) throws Exception {
        boolean flag = buRepairExchangService.saveRepairExchange(buRepairExchang);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改交接车记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuRepairExchang buRepairExchang) throws Exception {
        boolean flag = buRepairExchangService.updateRepairExchange(buRepairExchang);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit-remark")
    @ApiOperation(value = "修改交接车记录的备注")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateRemarkById(@RequestBody @Validated BuRepairExchangeRemarkSaveVO remarkSaveVO) throws Exception {
        boolean flag = buRepairExchangService.updateRemarkById(remarkSaveVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除交接车记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "交接车记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairExchangService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/confirm")
    @ApiOperation(value = "交接车确认")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmExchange(@RequestBody @Validated BuRepairExchang buRepairExchang) throws Exception {
        boolean flag = buRepairExchangService.confirmExchange(buRepairExchang);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/getTrainNumber")
    @ApiOperation(value = "根据车号查询接车车辆序号（今年第几辆）")
    @OperationLog()
    public Result<Integer> getTrainNumber() throws Exception {
        Integer trainNumber = buRepairExchangService.getTrainNumber();
        return new Result<Integer>().successResult(trainNumber);
    }

    @GetMapping("/validation/trainNumber")
    @ApiOperation(value = "验证架修序号是否重复")
    @OperationLog()
    public Result<Boolean> validationTrainNumber(@ApiParam(value = "车辆号") @RequestParam String trainNo, @ApiParam(value = "架修序号") @RequestParam Integer trainNumber) throws Exception {
        Boolean flag = buRepairExchangService.validationTrainNumber(trainNo, trainNumber);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/getItemNo/{programId}")
    @ApiOperation(value = "根据车号和修程查询接车项目序号（今年该项目第几辆）")
    @OperationLog()
    public Result<Integer> getItemNo(@PathVariable @ApiParam(value = "修程id", required = true) String programId) throws Exception {
        Integer trainNumber = buRepairExchangService.getItemNo(programId);
        return new Result<Integer>().successResult(trainNumber);
    }

}

