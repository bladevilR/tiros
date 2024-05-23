package org.jeecg.modules.dispatch.workorder.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderAnnex;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderAnnexSaveVO;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderAnnexService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 工单附件 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
@Api(tags = "工单附件")
@Slf4j
@RestController
@RequestMapping("/workorder/order/annex")
public class BuWorkOrderAnnexController {

    private final BuWorkOrderAnnexService buWorkOrderAnnexService;

    public BuWorkOrderAnnexController(BuWorkOrderAnnexService buWorkOrderAnnexService) {
        this.buWorkOrderAnnexService = buWorkOrderAnnexService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询工单附件列表")
    @OperationLog()
    public Result<List<BuWorkOrderAnnex>> listAnnex(@RequestParam @ApiParam(value = "工单id", required = true) String orderId,
                                                    @RequestParam(required = false) @ApiParam(value = "工单任务id") String taskId) throws Exception {
        List<BuWorkOrderAnnex> annexList = buWorkOrderAnnexService.listAnnex(orderId, taskId);
        return new Result<List<BuWorkOrderAnnex>>().successResult(annexList);
    }

    @PostMapping("/save")
    @ApiOperation(value = "增加工单附件列表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> add(@RequestBody @Validated BuWorkOrderAnnexSaveVO orderAnnexSaveVO) throws Exception {
        boolean flag = buWorkOrderAnnexService.saveAnnex(orderAnnexSaveVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除工单附件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工单附件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkOrderAnnexService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

