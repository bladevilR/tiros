package org.jeecg.modules.basemanage.traininfo.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.entity.LineTrainTree;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainInfoQueryVO;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 车辆结构，平时在界面显示时从车辆结构中查数据，只有在保存业务数据要用到具体的设备时，才用对应的：结构id+车辆ID去查到 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@RestController
@RequestMapping("/traininfo")
@Api(tags = "车辆信息管理")
@Slf4j
public class BuTrainInfoController {

    private final IBuTrainInfoService buTrainInfoService;

    public BuTrainInfoController(IBuTrainInfoService buTrainInfoService) {
        this.buTrainInfoService = buTrainInfoService;
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
    public Result<BuTrainInfo> get(String trainNo) throws Exception {
        BuTrainInfo trainInfo = buTrainInfoService.getTrainInfo(trainNo);
        return new Result<BuTrainInfo>().successResult(trainInfo);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增车辆信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuTrainInfo buTrainInfo) throws Exception {
        boolean flag = buTrainInfoService.saveTrainInfo(buTrainInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/edit")
    @ApiOperation(value = "修改车辆信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@Validated @RequestBody BuTrainInfo buTrainInfo) throws Exception {
        boolean flag = buTrainInfoService.updateTrainInfo(buTrainInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "批量删除车辆信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "车辆ids，多个逗号分隔") String ids) throws Exception {
        Boolean flag = buTrainInfoService.deleteBatchByTrainInfoIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/isExistTrainCode/{code}")
    @ApiOperation(value = "判断车辆编号是否存在")
    @OperationLog()
    public Result<Boolean> existTrainCode(@PathVariable String code) {
        boolean flag = buTrainInfoService.isExistTrainCode(code);
        return new Result<Boolean>().successResult(flag).success(flag ? "编号已存在" : "编号不存在");
    }

    @GetMapping("/line-train-tree")
    @ApiOperation(value = "获取线路车辆数据(树形)")
    @OperationLog()
    public Result<List<LineTrainTree>> getLineTrainTree() throws Exception {
        List<LineTrainTree> lineTrainTreeList = buTrainInfoService.getLineTrainTree();
        return new Result<List<LineTrainTree>>().successResult(lineTrainTreeList);
    }

}
