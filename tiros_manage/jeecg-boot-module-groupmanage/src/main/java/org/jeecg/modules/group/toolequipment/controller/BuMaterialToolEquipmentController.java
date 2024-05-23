package org.jeecg.modules.group.toolequipment.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsSetStatusVO;
import org.jeecg.modules.group.toolequipment.service.BuMaterialToolEquipmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 工装信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
@Api(tags = "工装管理")
@Slf4j
@RestController
@RequestMapping("/group/toolequipment")
public class BuMaterialToolEquipmentController {

    private final BuMaterialToolEquipmentService buMaterialToolEquipmentService;

    public BuMaterialToolEquipmentController(BuMaterialToolEquipmentService buMaterialToolEquipmentService) {
        this.buMaterialToolEquipmentService = buMaterialToolEquipmentService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工装记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialTools>> page(@Validated BuMaterialToolsQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialTools> page = buMaterialToolEquipmentService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialTools>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查询工装信息")
    @OperationLog()
    public Result<BuMaterialTools> get(@RequestParam @ApiParam(value = "工装id", required = true) String id) throws Exception {
        BuMaterialTools toolEquipment = buMaterialToolEquipmentService.getById(id);
        return new Result<BuMaterialTools>().successResult(toolEquipment);
    }

    @PostMapping("/setStatus")
    @ApiOperation(value = "设置工装状态")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setStatus(@RequestBody @Validated BuMaterialToolsSetStatusVO setStatusVO) throws Exception {
        boolean flag = buMaterialToolEquipmentService.setStatus(setStatusVO.getId(), setStatusVO.getStatus());
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/usage")
    @ApiOperation(value = "查询工装使用记录（分页）")
    @OperationLog()
    public Result<IPage<BuToolUsageRecordVO>> pageUsage(@RequestParam @ApiParam(value = "工装id", required = true) String id,
                                                        @RequestParam(required = false) @ApiParam(value = "车号") String trainNo,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuToolUsageRecordVO> page = buMaterialToolEquipmentService.pageUsage(id, trainNo, pageNo, pageSize);
        return new Result<IPage<BuToolUsageRecordVO>>().successResult(page);
    }

}

