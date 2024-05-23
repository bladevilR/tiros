package org.jeecg.modules.material.apply.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.service.BuMaterialApplyDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 领用明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Api(tags = "领用管理")
@Slf4j
@RestController
@RequestMapping("/material/apply/detail")
public class BuMaterialApplyDetailController {

    private final BuMaterialApplyDetailService buMaterialApplyDetailService;

    public BuMaterialApplyDetailController(BuMaterialApplyDetailService buMaterialApplyDetailService) {
        this.buMaterialApplyDetailService = buMaterialApplyDetailService;
    }


    @GetMapping("/listByApplyId")
    @ApiOperation(value = "根据领用单id查询领用明细列表", notes = "包含领用明细下的分配明细")
    @OperationLog()
    public Result<List<BuMaterialApplyDetail>> listByApplyId(@RequestParam @ApiParam(value = "物资领用记录id", required = true) String applyId,
                                                             @RequestParam(required = false) @ApiParam(value = "领用明细状态 字典dictCode=bu_material_apply_detail_status") List<Integer> status) throws Exception {
        List<BuMaterialApplyDetail> detailList = buMaterialApplyDetailService.listByApplyId(applyId, status);
        return new Result<List<BuMaterialApplyDetail>>().successResult(detailList);
    }

    @GetMapping("/listByOrderId")
    @ApiOperation(value = "根据工单id查询领用明细列表", notes = "包含领用明细下的分配明细")
    @OperationLog()
    public Result<List<BuMaterialApplyDetail>> listByOrderId(@RequestParam @ApiParam(value = "工单id", required = true) String orderId,
                                                             @RequestParam(required = false) @ApiParam(value = "领用明细状态 字典dictCode=bu_material_apply_detail_status") List<Integer> status) throws Exception {
        List<BuMaterialApplyDetail> detailList = buMaterialApplyDetailService.listByOrderId(orderId, status);
        return new Result<List<BuMaterialApplyDetail>>().successResult(detailList);
    }

}