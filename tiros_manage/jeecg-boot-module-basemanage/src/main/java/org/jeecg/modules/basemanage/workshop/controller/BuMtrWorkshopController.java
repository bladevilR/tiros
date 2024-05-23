package org.jeecg.modules.basemanage.workshop.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrWorkshop;
import org.jeecg.modules.basemanage.workshop.service.BuMtrWorkshopService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 架修车间 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Api(tags = "车间管理")
@Slf4j
@RestController
@RequestMapping("/workshop")
public class BuMtrWorkshopController {

    private final BuMtrWorkshopService buMtrWorkshopService;

    public BuMtrWorkshopController(BuMtrWorkshopService buMtrWorkshopService) {
        this.buMtrWorkshopService = buMtrWorkshopService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "获取所有车间列表")
    @OperationLog()
    public Result<List<BuMtrWorkshop>> listAll() throws Exception {
        List<BuMtrWorkshop> workshopList = buMtrWorkshopService.listAll();
        return new Result<List<BuMtrWorkshop>>().successResult(workshopList);
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "根据id获取车间信息")
    @OperationLog()
    public Result<BuMtrWorkshop> getWorkshopById(@PathVariable String id) throws Exception {
        BuMtrWorkshop workshop = buMtrWorkshopService.getWorkshopById(id);
        return new Result<BuMtrWorkshop>().successResult(workshop);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增车间")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMtrWorkshop buMtrWorkshop) {
        boolean flag = buMtrWorkshopService.save(buMtrWorkshop);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改车间")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuMtrWorkshop buMtrWorkshop) {
        boolean flag = buMtrWorkshopService.updateById(buMtrWorkshop);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除车间（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "车间ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMtrWorkshopService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
