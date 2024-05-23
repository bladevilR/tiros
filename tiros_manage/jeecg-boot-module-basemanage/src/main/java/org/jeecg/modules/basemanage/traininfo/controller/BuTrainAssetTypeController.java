package org.jeecg.modules.basemanage.traininfo.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeQueryVO;
import org.jeecg.modules.basemanage.traininfo.service.BuTrainAssetTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 设备类型结构 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Api(tags = "车型设备")
@Slf4j
@RestController
@RequestMapping("/trainAssetType")
public class BuTrainAssetTypeController {

    private final BuTrainAssetTypeService trainAssetTypeService;

    public BuTrainAssetTypeController(BuTrainAssetTypeService trainAssetTypeService) {
        this.trainAssetTypeService = trainAssetTypeService;
    }


    @GetMapping(value = "/listChildren")
    @ApiOperation("查询设备类型子节点")
    @OperationLog()
    public Result<List<BuTrainAssetType>> listTrainAssetTypeChildren(@Validated BuTrainAssetTypeQueryVO assetTypeQueryVO) throws Exception {
        List<BuTrainAssetType> assetTypeList = trainAssetTypeService.selectTrainAssetTypeChildren(assetTypeQueryVO);
        return new Result<List<BuTrainAssetType>>().successResult(assetTypeList);
    }

    @GetMapping(value = "/get")
    @ApiOperation("根据id查询设备类型信息")
    @OperationLog()
    public Result<BuTrainAssetType> getAssetTypeById(@RequestParam @ApiParam(value = "设备类型id", required = true) String id) throws Exception {
        BuTrainAssetType assetType = trainAssetTypeService.getAssetTypeById(id);
        return new Result<BuTrainAssetType>().successResult(assetType);
    }

    @PostMapping("/add")
    @ApiOperation("新增设备类型")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuTrainAssetType trainAssetType) throws Exception {
        boolean flag = trainAssetTypeService.saveAssetType(trainAssetType);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/edit")
    @ApiOperation("修改设备类型")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuTrainAssetType trainAssetType) throws Exception {
        boolean flag = trainAssetTypeService.updateTrainAssetTypeById(trainAssetType);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除设备类型")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "设备类型ids,多个用英文逗号分割", required = true) String ids) throws Exception {
        boolean flag = trainAssetTypeService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/rewrite-code-wbs")
    @ApiOperation("刷新重写该车型的设备类型的编码和wbs")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> rewriteCodeAndWbsByTrainTypeId(@RequestParam @ApiParam(value = "所属车型id", required = true) String trainTypeId) throws Exception {
        boolean flag = trainAssetTypeService.rewriteCodeAndWbsByTrainTypeId(trainTypeId);
        return new Result<Boolean>().successResult(flag);
    }

}
