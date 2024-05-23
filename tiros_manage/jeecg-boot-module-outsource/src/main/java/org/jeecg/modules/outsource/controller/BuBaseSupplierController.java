package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.outsource.bean.BuBaseSupplier;
import org.jeecg.modules.outsource.bean.vo.BuBaseSupplierQueryVO;
import org.jeecg.modules.outsource.service.BuBaseSupplierService;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p>
 * 厂商信息表 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "委外厂商管理")
@Slf4j
@RestController
@RequestMapping("/outsource/supplier/")
public class BuBaseSupplierController {

    private final BuBaseSupplierService supplierService;

    public BuBaseSupplierController(BuBaseSupplierService supplierService) {
        this.supplierService = supplierService;
    }


    @GetMapping("page")
    @ApiOperation(value = "查询厂商（分页）")
    @OperationLog()
    public Result<IPage<BuBaseSupplier>> page(@Validated BuBaseSupplierQueryVO queryVO,
                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        Page<BuBaseSupplier> page = new Page<>(pageNo, pageSize);
        IPage<BuBaseSupplier> pageList = supplierService.pageList(page, queryVO);

        return new Result<IPage<BuBaseSupplier>>().successResult(pageList);
    }

    @PostMapping("add")
    @ApiOperation(value = "新增厂商")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuBaseSupplier supplier) {
        boolean flag = supplierService.saveSupplier(supplier);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改厂商")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuBaseSupplier supplier) {
        boolean flag = supplierService.updateById(supplier);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除厂商（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工单ids，多个逗号分隔", required = true) String ids) {
        boolean flag = supplierService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}

