package org.jeecg.modules.basemanage.layout.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.layout.bean.SysWidgets;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysWidgetsQueryVO;
import org.jeecg.modules.basemanage.layout.service.SysWidgetsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 桌面部件 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Api(tags = "桌面布局")
@Slf4j
@RestController
@RequestMapping("/layout/widgets")
public class SysWidgetsController {

    private final SysWidgetsService sysWidgetsService;

    public SysWidgetsController(SysWidgetsService sysWidgetsService) {
        this.sysWidgetsService = sysWidgetsService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "桌面部件-查询(分页)")
    @OperationLog()
    public Result<IPage<SysWidgets>> pageWidgets(@Validated BuSysWidgetsQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<SysWidgets> page = sysWidgetsService.pageWidgets(queryVO, pageNo, pageSize);
        return new Result<IPage<SysWidgets>>().successResult(page);
    }

    @RequiresPermissions(value = {"/isystem/layout/list"})
    @PostMapping("/save")
    @ApiOperation(value = "桌面部件-保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveWidgets(@RequestBody @Validated SysWidgets widgets) throws Exception {
        boolean flag = sysWidgetsService.saveOrUpdate(widgets);
        return new Result<Boolean>().successResult(flag);
    }

    @RequiresPermissions(value = {"/isystem/layout/list"})
    @PostMapping("/delete")
    @ApiOperation(value = "桌面部件-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "桌面部件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = sysWidgetsService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

