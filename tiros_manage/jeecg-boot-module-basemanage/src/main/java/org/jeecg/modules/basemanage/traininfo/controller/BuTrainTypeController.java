package org.jeecg.modules.basemanage.traininfo.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainTypeQueryVO;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 车型信息 如：A型车，B型车 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-14
 */
@Api(tags = "车型管理")
@Slf4j
@RestController
@RequestMapping("/trainType")
public class BuTrainTypeController {

    private final IBuTrainTypeService trainTypeService;

    public BuTrainTypeController(IBuTrainTypeService trainTypeService) {
        this.trainTypeService = trainTypeService;
    }


    @GetMapping(value = "/list")
    @ApiOperation("查询车型")
    @OperationLog()
    public Result<Page<BuTrainType>> queryPageList(@Validated BuTrainTypeQueryVO trainTypeQueryVO,
                                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BuTrainType> pageList = trainTypeService.selectTrainTypePage(new Page<>(pageNo, pageSize), trainTypeQueryVO);
        return new Result<Page<BuTrainType>>().successResult(pageList);
    }

    @GetMapping(value = "/listAll")
    @ApiOperation("查询所有车型")
    @OperationLog()
    public Result<List<BuTrainType>> listAll() {
        return new Result<List<BuTrainType>>().successResult(trainTypeService.list());
    }

    @PostMapping("/add")
    @ApiOperation("新增车型")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuTrainType trainType) {
        trainTypeService.save(trainType);
        return new Result<Boolean>().successResult(true);
    }

    @PutMapping("/edit")
    @ApiOperation("修改车型")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@Validated @RequestBody BuTrainType trainType) {
        trainTypeService.updateById(trainType);
        return new Result<Boolean>().successResult(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除车型")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "车型ids，多个逗号分割", required = true) String ids) {
        boolean flag = trainTypeService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}
