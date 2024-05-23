package org.jeecg.modules.material.tools.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsQueryVO;
import org.jeecg.modules.material.tools.service.BuMaterialToolsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 工具信息 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-34
 */
@AppController
@Api(tags = "工器具-公共")
@Slf4j
@RestController
@RequestMapping("/app/material/tools")
public class AppMaterialToolsController {

    private final BuMaterialToolsService buMaterialToolsService;

    public AppMaterialToolsController(BuMaterialToolsService buMaterialToolsService) {
        this.buMaterialToolsService = buMaterialToolsService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工具（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialTools>> page(@Validated BuMaterialToolsQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialTools> page = buMaterialToolsService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialTools>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查询工具详情")
    @OperationLog()
    public Result<BuMaterialTools> getToolsById(@RequestParam() @ApiParam(value = "工具id",required = true) String id) throws Exception {
        BuMaterialTools tools = buMaterialToolsService.getToolsById(id);
        return new Result<BuMaterialTools>().successResult(tools);
    }

}