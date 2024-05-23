package org.jeecg.modules.material.apply.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderQueryVO;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyConfirmVO;
import org.jeecg.modules.material.apply.service.BuMaterialApplyDetailService;
import org.jeecg.modules.material.apply.service.BuMaterialApplyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 物料申请(领用) APP端控制器
 * </p>
 *
 * @author lidafeng
 * @since 2021-03-01
 */
@Api(tags = "领用管理")
@AppController
@Slf4j
@RestController
@RequestMapping("/app/material/apply")
public class AppMaterialApplyController {

    private final BuMaterialApplyService buMaterialApplyService;
    private final BuMaterialApplyDetailService buMaterialApplyDetailService;

    public AppMaterialApplyController(BuMaterialApplyService buMaterialApplyService,
                                      BuMaterialApplyDetailService buMaterialApplyDetailService) {
        this.buMaterialApplyService = buMaterialApplyService;
        this.buMaterialApplyDetailService = buMaterialApplyDetailService;
    }


    @GetMapping("/page-order")
    @ApiOperation(value = "查询领用工单（分页）")
    @OperationLog()
    public Result<IPage<AppApplyOrderVO>> pageApplyOrderForApp(@Validated AppApplyOrderQueryVO queryVO,
                                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<AppApplyOrderVO> page = buMaterialApplyService.pageApplyOrderForApp(queryVO, pageNo, pageSize);
        return new Result<IPage<AppApplyOrderVO>>().successResult(page);
    }

    @GetMapping("/detail/list-by-orderId")
    @ApiOperation(value = "根据工单id查询领用明细", notes = "包含领用明细下的分配明细")
    @OperationLog()
    public Result<List<BuMaterialApplyDetail>> listForAppByOrderId(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailService.listByOrderId(orderId, null);
        return new Result<List<BuMaterialApplyDetail>>().successResult(applyDetailList);
    }

    @PostMapping("/readyConfirm")
    @ApiOperation(value = "发料确认")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> readyConfirm(@RequestBody @Validated BuMaterialApplyConfirmVO confirmVO) throws Exception {
        boolean flag = buMaterialApplyService.readyConfirmForApp(confirmVO);
        return new Result<Boolean>().successResult(flag);
    }

//    @PostMapping("/receive-confirm")
//    @ApiOperation(value = "领料确认")
//    public Result<Boolean> readyConfirm(@RequestParam @ApiParam(value = "ids类型 1托盘id 2领用明细id", required = true) Integer fromType,
//                                        @RequestParam @ApiParam(value = "领用明细/托盘ids，多个逗号分隔", required = true) String ids) throws Exception {
//        boolean flag = buMaterialApplyService.receiveConfirm(fromType, ids, false);
//        return new Result<Boolean>().successResult(flag);
//    }

    @PostMapping("/receive-confirm")
    @ApiOperation(value = "领料确认", notes = "修改对应的领用单状态及明细状态、完成工单流程当前任务")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> receiveConfirmForApp(@RequestParam @ApiParam(value = "工单id", required = true) String orderId) throws Exception {
        boolean flag = buMaterialApplyService.receiveConfirmForApp(orderId);
        return new Result<Boolean>().successResult(flag);
    }

}
