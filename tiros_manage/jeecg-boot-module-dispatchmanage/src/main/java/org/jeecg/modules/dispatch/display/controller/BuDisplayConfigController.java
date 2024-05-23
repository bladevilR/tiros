package org.jeecg.modules.dispatch.display.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.display.bean.BuDisplayConfig;
import org.jeecg.modules.dispatch.display.service.BuDisplayConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 播放配置 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Api(tags = "投屏设置")
@Slf4j
@RestController
@RequestMapping("/dispatch/display/config")
public class BuDisplayConfigController {

    private final BuDisplayConfigService buDisplayConfigService;

    public BuDisplayConfigController(BuDisplayConfigService buDisplayConfigService) {
        this.buDisplayConfigService = buDisplayConfigService;
    }


    @PostMapping("/setResourceToScreen")
    @ApiOperation(value = "设置看板资源投放内容")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setResourceToScreen(@RequestParam @ApiParam(value = "大屏id", required = true) String screenId,
                                               @RequestParam @ApiParam(value = "看板资源id", required = true) String resourceId) throws Exception {
        boolean flag = buDisplayConfigService.setResourceToScreen(screenId, resourceId);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/setCustomToScreen")
    @ApiOperation(value = "设置自定义内容")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setCustomToScreen(@RequestParam @ApiParam(value = "文件路径", required = true) String filePath,
                                             @RequestParam @ApiParam(value = "资源标题", required = true) String title,
                                             @RequestParam @ApiParam(value = "大屏id", required = true) String screenId) throws Exception {
        boolean flag = buDisplayConfigService.setCustomToScreen(filePath, title, screenId);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/getPlayContent")
    @ApiOperation(value = "获取播放内容")
    @OperationLog()
    public Result<List<BuDisplayConfig>> getPlayContent(@RequestParam @ApiParam(value = "大屏id", required = true) String screenId,
                                                        @RequestParam(required = false) @ApiParam(value = "资源类型") Integer resourceType) throws Exception {
        List<BuDisplayConfig> displayConfigList = buDisplayConfigService.listPlayContent(screenId, resourceType);
        return new Result<List<BuDisplayConfig>>().successResult(displayConfigList);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除播放内容（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "播放配置ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buDisplayConfigService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

