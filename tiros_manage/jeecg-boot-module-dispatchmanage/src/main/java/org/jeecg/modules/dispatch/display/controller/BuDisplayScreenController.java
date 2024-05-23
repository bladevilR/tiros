package org.jeecg.modules.dispatch.display.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.dispatch.display.bean.BuDisplayScreen;
import org.jeecg.modules.dispatch.display.service.BuDisplayScreenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 大屏信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Api(tags = "投屏设置")
@Slf4j
@RestController
@RequestMapping("/dispatch/display/screen")
public class BuDisplayScreenController {

    private final BuDisplayScreenService buDisplayScreenService;

    public BuDisplayScreenController(BuDisplayScreenService buDisplayScreenService) {
        this.buDisplayScreenService = buDisplayScreenService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询大屏信息列表", notes = "大屏类型  字典dictCode=bu_display_screen_type；工班id  字典dictCode=(bu_mtr_workshop_group,group_name,id)；大屏类型screenType=2时需要工班id(groupId)")
    @OperationLog()
    public Result<List<BuDisplayScreen>> listByTypeAndGroupId(@RequestParam @ApiParam(value = "大屏类型", required = true) Integer screenType,
                                                             @RequestParam(required = false) @ApiParam(value = "工班id") String groupId) throws Exception {
        List<BuDisplayScreen> displayScreenList = buDisplayScreenService.listByTypeAndGroupId(screenType, groupId);
        return new Result<List<BuDisplayScreen>>().successResult(displayScreenList);
    }

}

