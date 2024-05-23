package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.modules.system.entity.SysAnnouncement;
import org.jeecg.modules.system.service.ISysAnnouncementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统通告 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@AppController
@Api(tags = "通告-公共")
@Slf4j
@RestController
@RequestMapping("/app/announcement")
public class AppAnnouncementController {

    private final ISysAnnouncementService sysAnnouncementService;

    public AppAnnouncementController(ISysAnnouncementService sysAnnouncementService) {
        this.sysAnnouncementService = sysAnnouncementService;
    }


    @GetMapping("/last")
    @ApiOperation(value = "查询用户最新通告", notes = "按推送时间倒序;默认5条")
    public Result<List<SysAnnouncement>> listLastAnnouncement(@RequestParam(required = false) Integer number) throws Exception {
        List<SysAnnouncement> announcementList = sysAnnouncementService.listLastAnnouncement(number);
        return new Result<List<SysAnnouncement>>().successResult(announcementList);
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询用户通告(分页)", notes = "按推送时间倒序")
    public Result<IPage<SysAnnouncement>> pageUserMessage(@RequestParam(required = false) @ApiParam(value = "标题") String title,
                                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<SysAnnouncement> page = sysAnnouncementService.pageUserAnnouncement(title, pageNo, pageSize);
        return new Result<IPage<SysAnnouncement>>().successResult(page);
    }

    @GetMapping(value = "/get")
    @ApiOperation(value = "根据id查询通告详情")
    public Result<SysAnnouncement> getAnnouncementById(@RequestParam String id) throws Exception {
        SysAnnouncement announcement = sysAnnouncementService.getAnnouncementById(id);
        return new Result<SysAnnouncement>().successResult(announcement);
    }

}
