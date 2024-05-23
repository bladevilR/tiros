package org.jeecg.modules.basemanage.traininfo.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTreeQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeCopyVO;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainAssetService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 车辆设备，按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Api(tags = "车辆设备")
@Slf4j
@RestController
@RequestMapping("/trainAsset")
public class BuTrainAssetController {

    private final IBuTrainAssetService buTrainAssetService;

    public BuTrainAssetController(IBuTrainAssetService buTrainAssetService) {
        this.buTrainAssetService = buTrainAssetService;
    }


    @GetMapping(value = "/listChildren")
    @ApiOperation("查询车辆设备子节点")
    @OperationLog()
    public Result<List<BuTrainAsset>> listTrainAssetTypeChildren(@Validated BuTrainAssetQueryVO trainAssetQueryVO) {
        List<BuTrainAsset> childrenList = buTrainAssetService.selectTrainAssetChild(trainAssetQueryVO);
        return new Result<List<BuTrainAsset>>().successResult(childrenList);
    }

    @GetMapping("/produceTrainAsset")
    @ApiOperation("生成车辆设备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> produceTrainAssetBatch(@ApiParam(value = "车辆id") @RequestParam String id,
                                                  @ApiParam(value = "车辆结构id") @RequestParam String structId) throws Exception {
        Boolean flag = buTrainAssetService.produceTrainAsset(id, structId);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/importStructure")
    @ApiOperation("导入系统结构创建")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> importByAssetType(@Validated @RequestBody BuTrainAssetTypeCopyVO importVO) throws Exception {
        boolean flag = buTrainAssetService.importByAssetType(importVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/add")
    @ApiOperation("新增车辆设备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuTrainAsset trainAsset) throws Exception {
        boolean flag = buTrainAssetService.saveTrainAsset(trainAsset);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/edit")
    @ApiOperation("修改车型设备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuTrainAsset trainAsset) throws Exception {
        boolean flag = buTrainAssetService.updateTrainAsset(trainAsset);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除车辆设备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "车型ids,多个用英文逗号分割") String ids) throws Exception {
        boolean flag = buTrainAssetService.removeAllByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping(value = "/list-tree")
    @ApiOperation(value = "根据条件查询车辆设备(列表或树)", notes = "只传车号不传其他条件，会查车辆顶级设备(无上级的设备)")
    @OperationLog()
    public Result<List<BuTrainAsset>> listTrainAsset(@Validated BuTrainAssetTreeQueryVO queryVO) throws Exception {
        List<BuTrainAsset> assetList = buTrainAssetService.listTrainAsset(queryVO);
        return new Result<List<BuTrainAsset>>().successResult(assetList);
    }

    @GetMapping(value = "/list-child")
    @ApiOperation(value = "根据车号和车辆结构明细查询下级车辆设备列表", notes = "只传车号不传车辆结构明细，会查车辆顶级设备")
    @OperationLog()
    public Result<List<BuTrainAsset>> listTrainAsset(@RequestParam @ApiParam(value = "车辆号", required = true) String trainNo,
                                                     @RequestParam(required = false) @ApiParam(value = "车辆结构明细id") String structDetailId) throws Exception {
        List<BuTrainAsset> assetList = buTrainAssetService.listChildByTrainNoAndStructDetailId(trainNo, structDetailId);
        return new Result<List<BuTrainAsset>>().successResult(assetList);
    }

}
