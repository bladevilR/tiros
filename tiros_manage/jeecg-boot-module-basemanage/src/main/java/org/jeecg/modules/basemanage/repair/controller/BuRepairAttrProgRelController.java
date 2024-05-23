package org.jeecg.modules.basemanage.repair.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttrProgRel;
import org.jeecg.modules.basemanage.repair.service.BuRepairAttrProgRelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 检修属性修程关联 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Api(tags = "检修属性修程关联")
@Slf4j
@RestController
@RequestMapping("/base/repair/attr-rel")
public class BuRepairAttrProgRelController {

    private final BuRepairAttrProgRelService buRepairAttrProgRelService;

    public BuRepairAttrProgRelController(BuRepairAttrProgRelService buRepairAttrProgRelService) {
        this.buRepairAttrProgRelService = buRepairAttrProgRelService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<List<BuRepairAttrProgRel>> listByAttributeId(@RequestParam @ApiParam(value = "检修属性id", required = true) String attributeId) throws Exception {
        List<BuRepairAttrProgRel> list = buRepairAttrProgRelService.listByAttributeId(attributeId);
        return new Result<List<BuRepairAttrProgRel>>().successResult(list);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> save(@RequestBody @Validated BuRepairAttrProgRel attrProgRel) throws Exception {
        boolean flag = buRepairAttrProgRelService.saveOrUpdate(attrProgRel);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "检修属性ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairAttrProgRelService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}

