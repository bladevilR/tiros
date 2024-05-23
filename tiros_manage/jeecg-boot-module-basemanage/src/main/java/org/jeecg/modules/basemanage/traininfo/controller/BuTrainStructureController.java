package org.jeecg.modules.basemanage.traininfo.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructure;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureVO;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainStructureService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 车辆结构 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
@Api(tags = "车辆结构管理")
@Slf4j
@RestController
@RequestMapping("/trainStructure/")
public class BuTrainStructureController {

    private final IBuTrainStructureService buTrainStructureService;

    public BuTrainStructureController(IBuTrainStructureService buTrainStructureService) {
        this.buTrainStructureService = buTrainStructureService;
    }


    @GetMapping(value = "/list")
    @ApiOperation("查询车辆结构信息")
    @OperationLog()
    public Result<List<BuTrainStructure>> list(@RequestParam(required = false) @ApiParam(value = "线路id") String lineId) throws Exception {
        List<BuTrainStructure> trainStructureList = buTrainStructureService.listAll(lineId);
        return new Result<List<BuTrainStructure>>().successResult(trainStructureList);
    }

    @PostMapping("/add")
    @ApiOperation("新增车辆结构信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuTrainStructureVO trainStructureVO) throws Exception {
        boolean flag = buTrainStructureService.saveTrainStructure(trainStructureVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/edit")
    @ApiOperation("修改车辆结构信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@Validated @RequestBody BuTrainStructureVO trainStructureVO) throws Exception {
        boolean flag = buTrainStructureService.updateTrainStructure(trainStructureVO);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除车辆结构信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "车辆结构id,多个用英文逗号分割") String ids) throws Exception {
        boolean flag = buTrainStructureService.deleteBatchByStructureIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/copy")
    @ApiOperation("复制车辆结构信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> copy(@RequestBody @Validated BuTrainStructureVO trainStructureVO) throws Exception {
        boolean flag = buTrainStructureService.copyTrainStructureByStructureId(trainStructureVO.getId());
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/rewrite-code-wbs")
    @ApiOperation("刷新重写该车型的设备类型的编码和wbs")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> rewriteCodeAndWbsByStructId(@RequestParam @ApiParam(value = "车辆结构id", required = true) String structId) throws Exception {
        boolean flag = buTrainStructureService.rewriteCodeAndWbsByStructId(structId);
        return new Result<Boolean>().successResult(flag);
    }

}
