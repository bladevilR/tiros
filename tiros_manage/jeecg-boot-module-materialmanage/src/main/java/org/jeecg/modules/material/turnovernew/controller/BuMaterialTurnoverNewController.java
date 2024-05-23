package org.jeecg.modules.material.turnovernew.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.turnovernew.bean.BuMaterialTurnoverNew;
import org.jeecg.modules.material.turnovernew.bean.vo.BuMaterialTurnoverQueryVONew;
import org.jeecg.modules.material.turnovernew.service.BuMaterialTurnoverNewService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 周转件 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-09-18
 */
@Api(tags = "周转件管理")
@Slf4j
@RestController
@RequestMapping("/material/turnover")
public class BuMaterialTurnoverNewController {

    private final BuMaterialTurnoverNewService buMaterialTurnoverNewService;

    public BuMaterialTurnoverNewController(BuMaterialTurnoverNewService buMaterialTurnoverNewService) {
        this.buMaterialTurnoverNewService = buMaterialTurnoverNewService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询周转件（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialTurnoverNew>> pageTurnover(@Validated BuMaterialTurnoverQueryVONew queryVO,
                                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialTurnoverNew> page = buMaterialTurnoverNewService.pageTurnover(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialTurnoverNew>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查看周转件详情")
    @OperationLog()
    public Result<BuMaterialTurnoverNew> getTurnover(@RequestParam @ApiParam(value = "周转件id", required = true) String id) throws Exception {
        BuMaterialTurnoverNew buMaterialSparePart = buMaterialTurnoverNewService.getTurnoverById(id);
        return new Result<BuMaterialTurnoverNew>().successResult(buMaterialSparePart);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存周转件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> saveTurnover(@RequestBody @Validated BuMaterialTurnoverNew turnover) {
        boolean flag = buMaterialTurnoverNewService.saveTurnover(turnover);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除周转件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "周转件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialTurnoverNewService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "导入周转件信息（excel）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_5, saveParam = false)
    public Result<Boolean> importExcel(@RequestParam @ApiParam(value = "周转件信息excel文件", required = true) MultipartFile excelFile,
                                       @RequestParam(required = false) @ApiParam(value = "是否清空旧数据") Boolean clearOldData) throws Exception {
        boolean flag = buMaterialTurnoverNewService.importTurnoverFromExcel(excelFile, clearOldData);
        return new Result<Boolean>().successResult(flag);
    }

}
