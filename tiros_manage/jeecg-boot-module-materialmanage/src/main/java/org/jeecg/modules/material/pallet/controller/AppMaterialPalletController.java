package org.jeecg.modules.material.pallet.controller;


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
import org.jeecg.modules.material.apply.service.BuMaterialApplyDetailService;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.bean.vo.BuMaterialPalletVO;
import org.jeecg.modules.material.pallet.service.BuMaterialPalletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 物资托盘 APP控制器
 * </p>
 *
 * @author lidafeng
 * @since 2021-03-03
 */
@Api(tags = "托盘管理&扫一扫")
@AppController
@Slf4j
@RestController
@RequestMapping("/app/material/pallet")
public class AppMaterialPalletController {

    private final BuMaterialPalletService buMaterialPalletService;
    private final BuMaterialApplyDetailService buMaterialApplyDetailService;

    public AppMaterialPalletController(BuMaterialPalletService buMaterialPalletService,
                                       BuMaterialApplyDetailService buMaterialApplyDetailService) {
        this.buMaterialPalletService = buMaterialPalletService;
        this.buMaterialApplyDetailService = buMaterialApplyDetailService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询托盘信息记录(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialPallet>> page(@RequestParam(required = false) @ApiParam(value = "托盘名称或编码") String searchText,
                                                @RequestParam(required = false) @ApiParam(value = "物质类型id，多个逗号分隔") String typeId,
                                                @RequestParam(required = false) @ApiParam(value = "有效状态") String status,
                                                @RequestParam(required = false) @ApiParam(value = "使用状态") String useStatus,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialPallet> page = buMaterialPalletService.page(searchText, typeId, status, useStatus, pageNo, pageSize);
        return new Result<IPage<BuMaterialPallet>>().successResult(page);
    }
/*
    @GetMapping("/getMaterialApplyDetailByPalletId")
    @ApiOperation(value = "获取指定托盘物料列表", notes = "包含领用明细下的分配明细")
    public Result<List<BuMaterialApplyDetail>> getMaterialApplyDetailByPalletId(@RequestParam @ApiParam(value = "托盘ID", required = true) String palletId) throws Exception {
        List<BuMaterialApplyDetail> detailList = buMaterialApplyDetailService.listByPalletId(palletId);
        return new Result<List<BuMaterialApplyDetail>>().successResult(detailList);
    }*/

    @GetMapping("/getMaterialByPalletId")
    @ApiOperation(value = "获取指定托盘物料列表")
    @OperationLog()
    public Result<BuMaterialPalletVO> getMaterialByPalletId(@RequestParam @ApiParam(value = "托盘ID", required = true) String palletId) throws Exception {
        BuMaterialPalletVO material = buMaterialApplyDetailService.getMaterialByPalletId(palletId);
        return new Result<BuMaterialPalletVO>().successResult(material);
    }
}

