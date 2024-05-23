package org.jeecg.modules.material.material.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.vo.MaterialReplaceSaveVO;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 可替换物资 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-07-21
 */
@Slf4j
@Api(tags = "物资类型")
@RestController
@RequestMapping("/material/can-replace")
public class BuMaterialTypeReplaceController {

    private final BuMaterialTypeReplaceService buMaterialTypeReplaceService;

    public BuMaterialTypeReplaceController(BuMaterialTypeReplaceService buMaterialTypeReplaceService) {
        this.buMaterialTypeReplaceService = buMaterialTypeReplaceService;
    }


    @GetMapping("/list")
    @ApiOperation("可替换-查询可替换物资列表")
    @OperationLog()
    public Result<List<BuMaterialType>> listReplace(@RequestParam @ApiParam(value = "物质类型id", required = true) String id) throws Exception {
        List<BuMaterialType> replaceList = buMaterialTypeReplaceService.listReplace(id);
        return new Result<List<BuMaterialType>>().successResult(replaceList);
    }

    @PostMapping("/save")
    @ApiOperation("可替换-保存可替换物资")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> saveReplace(@RequestBody @Validated MaterialReplaceSaveVO saveVO) throws Exception {
        boolean flag = buMaterialTypeReplaceService.saveReplace(saveVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping(value = "/import")
    @ApiOperation(value = "可替换-导入")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_5)
    public Result<Boolean> importMaterialReplace(@RequestParam("file") MultipartFile file) throws Exception {
        boolean flag = buMaterialTypeReplaceService.importMaterialReplace(file);
        return new Result<Boolean>().successResult(flag);
    }

}
