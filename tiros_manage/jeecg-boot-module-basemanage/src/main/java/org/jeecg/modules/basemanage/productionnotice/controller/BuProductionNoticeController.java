package org.jeecg.modules.basemanage.productionnotice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "生产通知单管理")
@Slf4j
@RestController
@RequestMapping("/base/production-notice")
public class BuProductionNoticeController extends JeecgController<BuProductionNotice, IBuProductionNoticeService> {

    @Resource
    private IBuProductionNoticeService productionNoticeService;

    @GetMapping("/page")
    @ApiOperation(value = "生产通知单-分页查询")
    @OperationLog()
    public Result<IPage<BuProductionNotice>> queryPage(@Validated BuProductionNoticeQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuProductionNotice> page = productionNoticeService.queryPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuProductionNotice>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "生产通知单-根据id查询")
    @OperationLog()
    public Result<BuProductionNotice> get(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        BuProductionNotice notice = productionNoticeService.getById(id);
        return new Result<BuProductionNotice>().successResult(notice);
    }

    @PostMapping("/save")
    @ApiOperation(value = "生产通知单-新增")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuProductionNotice notice) {
        boolean flag = productionNoticeService.saveNotice(notice);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/update")
    @ApiOperation(value = "生产通知单-修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> update(@RequestBody @Validated BuProductionNotice notice) {
        boolean flag = productionNoticeService.updateNotice(notice);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "生产通知单-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "通知单ids，多个逗号分隔", required = true) String ids) {
        boolean flag = productionNoticeService.deleteNotice(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "生产通知单-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuProductionNotice notice) {
        return super.exportXls(request, notice, BuProductionNotice.class, "生产通知单");
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "生产通知单-导入")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BuProductionNotice.class);
    }
}
