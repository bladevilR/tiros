package org.jeecg.modules.dispatch.fault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCategory;
import org.jeecg.modules.dispatch.fault.bean.vo.BuFaultCategoryQueryVO;
import org.jeecg.modules.dispatch.fault.service.BuFaultCategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 故障分类 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-29
 */
@Api(tags = "故障管理(分类)")
@Slf4j
@RestController
@RequestMapping("/dispatch/fault/category")
public class BuFaultCategoryController {

    private final BuFaultCategoryService buFaultCategoryService;

    public BuFaultCategoryController(BuFaultCategoryService buFaultCategoryService) {
        this.buFaultCategoryService = buFaultCategoryService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<IPage<BuFaultCategory>> page(@Validated BuFaultCategoryQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFaultCategory> page = buFaultCategoryService.pageFaultCategory(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFaultCategory>>().successResult(page);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> save(@RequestBody @Validated BuFaultCategory faultCategory) throws Exception {
        boolean flag = buFaultCategoryService.saveOrUpdate(faultCategory);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "故障分类ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buFaultCategoryService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}
