package org.jeecg.modules.material.pallet.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.bean.vo.PalletMaterialTypesVO;
import org.jeecg.modules.material.pallet.service.BuMaterialPalletService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 物资托盘 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
@Api(tags = "托盘管理")
@Slf4j
@RestController
@RequestMapping("/material/pallet")
public class BuMaterialPalletController {

    private final BuMaterialPalletService buMaterialPalletService;

    public BuMaterialPalletController(BuMaterialPalletService buMaterialPalletService) {
        this.buMaterialPalletService = buMaterialPalletService;
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

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询托盘信息")
    @OperationLog()
    public Result<BuMaterialPallet> getPalletById(@RequestParam @ApiParam(value = "托盘id", required = true) String id) throws Exception {
        BuMaterialPallet pallet = buMaterialPalletService.getPalletById(id);
        return new Result<BuMaterialPallet>().successResult(pallet);
    }

    @GetMapping("/list-unused")
    @ApiOperation(value = "根据物资类型id查询未使用的托盘(列表)")
    @OperationLog()
    public Result<List<BuMaterialPallet>> listUnusedPalletByMaterialTypeId(@RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId) throws Exception {
        List<BuMaterialPallet> palletList = buMaterialPalletService.listUnusedPalletByMaterialTypeId(materialTypeId);
        return new Result<List<BuMaterialPallet>>().successResult(palletList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增托盘")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialPallet buMaterialPallet) throws Exception {
        boolean flag = buMaterialPalletService.savePallet(buMaterialPallet);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改托盘")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuMaterialPallet buMaterialPallet) {
        boolean flag = buMaterialPalletService.updateById(buMaterialPallet);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/set-materialTypes")
    @ApiOperation(value = "设置托盘物资类型(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setPalletMaterialTypes(@RequestBody @Validated PalletMaterialTypesVO palletMaterialTypesVO) throws Exception {
        boolean flag = buMaterialPalletService.setPalletMaterialTypes(palletMaterialTypesVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除托盘(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "托盘ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialPalletService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

