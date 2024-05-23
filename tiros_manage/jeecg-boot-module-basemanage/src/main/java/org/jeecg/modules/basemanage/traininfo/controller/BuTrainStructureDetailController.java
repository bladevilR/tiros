package org.jeecg.modules.basemanage.traininfo.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeCopyVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureDetailQueryVO;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainStructureDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-1s5
 */
@Api(tags = "车辆结构详情")
@Slf4j
@RestController
@RequestMapping("/trainStructureDetail")
public class BuTrainStructureDetailController {

    private final IBuTrainStructureDetailService buTrainStructureDetailService;

    public BuTrainStructureDetailController(IBuTrainStructureDetailService buTrainStructureDetailService) {
        this.buTrainStructureDetailService = buTrainStructureDetailService;
    }


    @GetMapping(value = "/listChildren")
    @ApiOperation("查询车辆结构子节点(明细)")
    @OperationLog()
    public Result<List<BuTrainStructureDetail>> listTrainAssetTypeChildren(@Validated BuTrainStructureDetailQueryVO detailQueryVO) throws Exception {
        List<BuTrainStructureDetail> childrenList = buTrainStructureDetailService.selectTrainStructureDetailChildren(detailQueryVO);
        return new Result<List<BuTrainStructureDetail>>().successResult(childrenList);
    }

    @PostMapping("/add")
    @ApiOperation("新增车辆结构明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuTrainStructureDetail trainStructureDetail) throws Exception {
        boolean flag = buTrainStructureDetailService.saveTrainStructureDetail(trainStructureDetail);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/edit")
    @ApiOperation("修改车辆结构明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuTrainStructureDetail trainStructureDetail) throws Exception {
        boolean flag = buTrainStructureDetailService.updateTrainStructureDetail(trainStructureDetail);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除车辆结构明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "车辆结构id,多个用英文逗号分割") String ids) throws Exception {
        Boolean flag = buTrainStructureDetailService.deleteBatchByDetailIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/importStructure")
    @ApiOperation("导入系统结构创建")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> importStructure(@Validated @RequestBody BuTrainAssetTypeCopyVO trainAssetTypeCopy) throws Exception {
        Boolean flag = buTrainStructureDetailService.importAssetTypeToStructure(trainAssetTypeCopy);
        return new Result<Boolean>().successResult(flag);
    }

}
