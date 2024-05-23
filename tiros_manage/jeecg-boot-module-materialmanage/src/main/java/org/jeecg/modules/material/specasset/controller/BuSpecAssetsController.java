package org.jeecg.modules.material.specasset.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.specasset.bean.BuSpecAssets;
import org.jeecg.modules.material.specasset.bean.vo.BuSpecAssetQueryVO;
import org.jeecg.modules.material.specasset.service.BuSpecAssetsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 特种设备 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
@Api(tags = "特种设备")
@RestController
@RequestMapping("/material/specasset")
public class BuSpecAssetsController {

    private final BuSpecAssetsService buSpecAssetsService;

    public BuSpecAssetsController(BuSpecAssetsService buSpecAssetsService) {
        this.buSpecAssetsService = buSpecAssetsService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询（分页）")
    @OperationLog()
    public Result<IPage<BuSpecAssets>> pageSpecAsset(@Validated BuSpecAssetQueryVO queryVO,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuSpecAssets> page = buSpecAssetsService.pageSpecAsset(queryVO, pageNo, pageSize);
        return new Result<IPage<BuSpecAssets>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询")
    @OperationLog()
    public Result<BuSpecAssets> getSpecAssetById(@RequestParam @ApiParam(value = "特种设备id", required = true) String id) throws Exception {
        BuSpecAssets specAsset = buSpecAssetsService.getSpecAssetById(id);
        return new Result<BuSpecAssets>().successResult(specAsset);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增/修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveSpecAsset(@RequestBody @Validated BuSpecAssets specAsset) throws Exception {
        boolean flag = buSpecAssetsService.saveSpecAsset(specAsset);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "特种设备ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buSpecAssetsService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}