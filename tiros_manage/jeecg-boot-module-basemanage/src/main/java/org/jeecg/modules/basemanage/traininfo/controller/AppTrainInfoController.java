package org.jeecg.modules.basemanage.traininfo.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetListQueryVOForApp;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainInfoQueryVO;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainAssetService;
import org.jeecg.modules.basemanage.traininfo.service.BuTrainAssetTypeService;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-24
 */
@AppController
@Api(tags = "车辆-公共")
@RestController
@RequestMapping("/app/train")
public class AppTrainInfoController {

    private final IBuTrainInfoService buTrainInfoService;
    private final BuTrainAssetTypeService buTrainAssetTypeService;
    private final IBuTrainAssetService buTrainAssetService;

    public AppTrainInfoController(IBuTrainInfoService buTrainInfoService,
                                  BuTrainAssetTypeService buTrainAssetTypeService,
                                  IBuTrainAssetService buTrainAssetService) {
        this.buTrainInfoService = buTrainInfoService;
        this.buTrainAssetTypeService = buTrainAssetTypeService;
        this.buTrainAssetService = buTrainAssetService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询车辆列表")
    @OperationLog()
    public Result<Page<BuTrainInfo>> queryPage(@Validated BuTrainInfoQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuTrainInfo> page = buTrainInfoService.pageTrainInfo(queryVO, new Page<>(pageNo, pageSize));
        return new Result<Page<BuTrainInfo>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据车号查询车辆信息")
    @OperationLog()
    public Result<BuTrainInfo> get(@RequestParam @ApiParam(value = "车号", readOnly = true) String trainNo) throws Exception {
        BuTrainInfo trainInfo = buTrainInfoService.getTrainInfo(trainNo);
        return new Result<BuTrainInfo>().successResult(trainInfo);
    }

    @GetMapping("/sys/list")
    @ApiOperation(value = "根据车号查询车辆系统")
    @OperationLog()
    public Result<IPage<BuTrainAssetType>> listSystemAssetType(@RequestParam @ApiParam(value = "车号", readOnly = true) String trainNo,
                                                               @RequestParam @ApiParam(value = "上级结构id") String parentId,
                                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        List<BuTrainAssetType> assetTypeList = buTrainAssetTypeService.listTopSystemAssetType(trainNo, parentId);
        List<BuTrainAssetType> assetTypeListVo = assetTypeList.stream()
                .skip(pageSize * (pageNo - 1))
                .limit(pageSize).collect(Collectors.toList());
        IPage<BuTrainAssetType> page = new Page();
        page.setRecords(assetTypeListVo);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(assetTypeList.size());
        return new Result<IPage<BuTrainAssetType>>().successResult(page);
    }

    @GetMapping(value = "/asset/list")
    @ApiOperation(value = "根据条件查询车辆设备(列表)", notes = "只传车号不传其他条件，会查车辆顶级设备(无上级的设备)")
    @OperationLog()
    public Result<IPage<BuTrainAsset>> listTrainAssetForApp(@Validated BuTrainAssetListQueryVOForApp queryVO,
                                                            @RequestParam(defaultValue = "1") Integer pageNo,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        List<BuTrainAsset> assetList = buTrainAssetService.listTrainAssetForApp(queryVO);
        List<BuTrainAsset> assetListVo = assetList.stream()
                .skip(pageSize * (pageNo - 1))
                .limit(pageSize).collect(Collectors.toList());
        IPage<BuTrainAsset> page = new Page();
        page.setRecords(assetListVo);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(assetList.size());
        return new Result<IPage<BuTrainAsset>>().successResult(page);
    }

}
