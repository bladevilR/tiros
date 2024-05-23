package org.jeecg.modules.basemanage.traininfo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.BuMaximoTrainAssetExt;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuMaximoTrainAssetChildrenQueryVO;
import org.jeecg.modules.basemanage.traininfo.service.BuMaximoTrainAssetExtService;
import org.jeecg.modules.basemanage.traininfo.service.BuMaximoTrainAssetService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * maximo资产设备 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Api(tags = "车辆设备（maximo资产）")
@Slf4j
@RestController
@RequestMapping("/maximo-asset")
public class BuMaximoTrainAssetController {

    private final BuMaximoTrainAssetService buMaximoTrainAssetService;
    private final BuMaximoTrainAssetExtService buMaximoTrainAssetExtService;

    public BuMaximoTrainAssetController(BuMaximoTrainAssetService buMaximoTrainAssetService,
                                        BuMaximoTrainAssetExtService buMaximoTrainAssetExtService) {
        this.buMaximoTrainAssetService = buMaximoTrainAssetService;
        this.buMaximoTrainAssetExtService = buMaximoTrainAssetExtService;
    }


    @GetMapping(value = "/list-child")
    @ApiOperation("查询资产设备子节点")
    @OperationLog()
    public Result<List<BuMaximoTrainAsset>> listTrainAssetTypeChildren(@Validated BuMaximoTrainAssetChildrenQueryVO queryVO) throws Exception {
        List<BuMaximoTrainAsset> assetList = buMaximoTrainAssetService.listMaximoTrainAssetChild(queryVO);
        return new Result<List<BuMaximoTrainAsset>>().successResult(assetList);
    }

    @GetMapping(value = "/get")
    @ApiOperation("根据id查询资产设备和扩展信息")
    @OperationLog()
    public Result<BuMaximoTrainAsset> getTrainAssetById(@RequestParam @ApiParam(value = "资产设备id", required = true) String id) throws Exception {
        BuMaximoTrainAsset asset = buMaximoTrainAssetService.getTrainAssetById(id);
        return new Result<BuMaximoTrainAsset>().successResult(asset);
    }

    @PostMapping(value = "/ext/save")
    @ApiOperation("设置资产设备扩展信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveAssetExt(@RequestBody @Validated BuMaximoTrainAssetExt assetExt) throws Exception {
        boolean flag = buMaximoTrainAssetExtService.saveAssetExt(assetExt);
        return new Result<Boolean>().successResult(flag);
    }

}
