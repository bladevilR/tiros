package org.jeecg.modules.material.warehouse.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.bean.vo.BuMtrWarehouseQueryVO;
import org.jeecg.modules.material.warehouse.service.BuMtrWarehouseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 仓库信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
@Api(tags = "仓库设置")
@Slf4j
@RestController
@RequestMapping("/material/warehouse")
public class BuMtrWarehouseController {

    private final BuMtrWarehouseService buMtrWarehouseService;

    public BuMtrWarehouseController(BuMtrWarehouseService buMtrWarehouseService) {
        this.buMtrWarehouseService = buMtrWarehouseService;
    }


    @GetMapping("/allTree")
    @ApiOperation(value = "获取仓库树")
    @OperationLog()
    public Result<List<BuMtrWarehouse>> allTrees() throws Exception {
        List<BuMtrWarehouse> buMtrWarehouseList = buMtrWarehouseService.getAllTrees(false);
        return new Result<List<BuMtrWarehouse>>().successResult(buMtrWarehouseList);
    }

    @GetMapping("/list-child")
    @ApiOperation(value = "根据上级id获取子仓库列表")
    @OperationLog()
    public Result<List<BuMtrWarehouse>> tree(@RequestParam(required = false) @ApiParam(value = "上级仓库id") String parentId) throws Exception {
        List<BuMtrWarehouse> buMtrWarehouseList = buMtrWarehouseService.listByParentId(parentId, false);
        return new Result<List<BuMtrWarehouse>>().successResult(buMtrWarehouseList);
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询仓库(分页)")
    @OperationLog()
    public Result<IPage<BuMtrWarehouse>> page(@Validated BuMtrWarehouseQueryVO queryVO,
                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMtrWarehouse> page = buMtrWarehouseService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMtrWarehouse>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询仓库")
    @OperationLog()
    public Result<BuMtrWarehouse> get(@RequestParam @ApiParam(value = "仓库id", required = true) String id) throws Exception {
        BuMtrWarehouse warehouse = buMtrWarehouseService.getWarehouseById(id);
        return new Result<BuMtrWarehouse>().successResult(warehouse);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增仓库")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuMtrWarehouse buMtrWarehouse) throws Exception {
        boolean flag = buMtrWarehouseService.saveWarehouse(buMtrWarehouse);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改仓库")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuMtrWarehouse buMtrWarehouse) throws Exception {
        boolean flag = buMtrWarehouseService.updateWarehouse(buMtrWarehouse);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除仓库（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "仓库ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMtrWarehouseService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/close")
    @ApiOperation(value = "关账/取消关账")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> close(@RequestParam @ApiParam(value = "仓库id", required = true) String id) throws Exception {
        boolean flag = buMtrWarehouseService.changeClose(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "导入库位信息（excel）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_5, saveParam = false)
    public Result<Boolean> importExcel(@RequestParam @ApiParam(value = "库位信息excel文件", required = true) MultipartFile excelFile,
                                       @RequestParam @ApiParam(value = "上级id，数据导入此仓库下", required = true) String parentId) throws Exception {
        boolean flag = buMtrWarehouseService.importWarehouseInfoFromExcel(excelFile, parentId);
        return new Result<Boolean>().successResult(flag);
    }

}

