package org.jeecg.modules.group.tool.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.tool.service.BuMaterialToolService;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 工器具 APP前端控制器
 * </p>
 *
 * @author lidafeng
 * @since 2021-02-24
 */
@Api(tags = "工器具管理(工班)")
@AppController
@Slf4j
@RestController
@RequestMapping("/app/group/tool")
public class AppToolGroupController {

    private final BuMaterialToolService buMaterialToolService;

    public AppToolGroupController(BuMaterialToolService buMaterialToolService) {
        this.buMaterialToolService = buMaterialToolService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询工器具记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialTools>> page(@Validated BuMaterialToolsQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialTools> page = buMaterialToolService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialTools>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查询工器具信息")
    @OperationLog()
    public Result<BuMaterialTools> get(@RequestParam @ApiParam(value = "工器具id", required = true) String id) throws Exception {
        BuMaterialTools toolEquipment = buMaterialToolService.getById(id);
        return new Result<BuMaterialTools>().successResult(toolEquipment);
    }

    @GetMapping("/usage")
    @ApiOperation(value = "查询工器具使用记录（分页）")
    @OperationLog()
    public Result<IPage<BuToolUsageRecordVO>> pageUsage(@RequestParam @ApiParam(value = "工器具id", required = true) String id,
                                                        @RequestParam(required = false) @ApiParam(value = "车号") String trainNo,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuToolUsageRecordVO> page = buMaterialToolService.pageUsage(id, trainNo, pageNo, pageSize);
        return new Result<IPage<BuToolUsageRecordVO>>().successResult(page);
    }

}

