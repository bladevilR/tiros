package org.jeecg.modules.basemanage.layout.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.layout.bean.SysLayouts;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysLayoutsQueryVO;
import org.jeecg.modules.basemanage.layout.service.SysLayoutsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 界面布局 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Api(tags = "桌面布局")
@Slf4j
@RestController
@RequestMapping("/layout/layouts")
public class SysLayoutsController {

    private final SysLayoutsService sysLayoutsService;

    public SysLayoutsController(SysLayoutsService sysLayoutsService) {
        this.sysLayoutsService = sysLayoutsService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "界面布局-查询(分页)")
    @OperationLog()
    public Result<IPage<SysLayouts>> pageLayouts(@Validated BuSysLayoutsQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<SysLayouts> page = sysLayoutsService.pageLayouts(queryVO, pageNo, pageSize);
        return new Result<IPage<SysLayouts>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "界面布局-根据id查询")
    @OperationLog()
    public Result<SysLayouts> getLayouts(@RequestParam @ApiParam(value = "界面布局id", required = true) String id) throws Exception {
        SysLayouts layouts = sysLayoutsService.getLayouts(id);
        return new Result<SysLayouts>().successResult(layouts);
    }

    @GetMapping("/get/current-user-main")
    @ApiOperation(value = "界面布局-查询当前人员主面板布局", notes = "查询当前登录人员主面板布局，包括布局下组件列表")
    @OperationLog()
    public Result<SysLayouts> getUserDefaultLayouts() throws Exception {
        SysLayouts layouts = sysLayoutsService.getUserDefaultLayouts();
        return new Result<SysLayouts>().successResult(layouts);
    }

    @RequiresPermissions(value = {"/isystem/layout/list"})
    @PostMapping("/save")
    @ApiOperation(value = "界面布局-保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveLayouts(@RequestBody @Validated SysLayouts layouts) throws Exception {
        boolean flag = sysLayoutsService.saveLayouts(layouts);
        return new Result<Boolean>().successResult(flag);
    }

    @RequiresPermissions(value = {"/isystem/layout/list"})
    @PostMapping("/delete")
    @ApiOperation(value = "界面布局-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "界面布局ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = sysLayoutsService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

