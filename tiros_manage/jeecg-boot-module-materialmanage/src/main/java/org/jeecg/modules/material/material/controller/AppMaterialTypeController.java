package org.jeecg.modules.material.material.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeTool;
import org.jeecg.modules.material.material.bean.vo.BuMaterialTypeQueryVO;
import org.jeecg.modules.material.material.service.BuMaterialTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 物资类型 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-34
 */
@AppController
@Slf4j
@Api(tags = "物资-公共")
@RestController
@RequestMapping("/app/material")
public class AppMaterialTypeController {

    private final BuMaterialTypeService materialTypeService;

    public AppMaterialTypeController(BuMaterialTypeService materialTypeService) {
        this.materialTypeService = materialTypeService;
    }


    @GetMapping("/list")
    @ApiOperation("查询物资类型(分页)")
    @OperationLog()
    public Result<Page<BuMaterialType>> list(@Validated BuMaterialTypeQueryVO queryVO,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuMaterialType> page = materialTypeService.pageMaterialType(queryVO, pageNo, pageSize);
        return new Result<Page<BuMaterialType>>().successResult(page);
    }

    @GetMapping("/list/tool")
    @ApiOperation("查询工器具类型(分页)")
    @OperationLog()
    public Result<Page<BuMaterialTypeTool>> listTool(@Validated BuMaterialTypeQueryVO queryVO,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuMaterialTypeTool> page = materialTypeService.pageMaterialTypeTool(queryVO, pageNo, pageSize);
        return new Result<Page<BuMaterialTypeTool>>().successResult(page);
    }

    @GetMapping("/listMust")
    @ApiOperation("查询必换件物资类型(分页)")
    @OperationLog()
    public Result<Page<BuMaterialType>> listMust(@Validated BuMaterialTypeQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Result<Page<BuMaterialType>> result = new Result<>();
        Page<BuMaterialType> pageList = materialTypeService.selectMustMaterialTypePage(queryVO, pageNo, pageSize);
        result.setResult(pageList);
        return result;
    }

    @GetMapping("/get")
    @ApiOperation("根据id查询物资类型")
    @OperationLog()
    public Result<BuMaterialType> getMaterialType(@RequestParam @ApiParam(value = "物质类型id") String id) throws Exception {
        BuMaterialType materialType = materialTypeService.getMaterialTypeById(id);
        return new Result<BuMaterialType>().successResult(materialType);
    }

    @GetMapping("/get/tool")
    @ApiOperation("根据id查询工器具类型")
    @OperationLog()
    public Result<BuMaterialTypeTool> getMaterialTypeTool(@RequestParam @ApiParam(value = "工器具类型id") String id) throws Exception {
        BuMaterialTypeTool materialTypeTool = materialTypeService.getMaterialTypeToolById(id);
        return new Result<BuMaterialTypeTool>().successResult(materialTypeTool);
    }

}

