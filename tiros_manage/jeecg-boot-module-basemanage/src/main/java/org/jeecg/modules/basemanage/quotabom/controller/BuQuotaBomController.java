package org.jeecg.modules.basemanage.quotabom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.quotabom.entity.BuQuotaBom;
import org.jeecg.modules.basemanage.quotabom.service.IBuQuotaBomService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "定额BOM管理")
@Slf4j
@RestController
@RequestMapping("/base/quota-bom")
public class BuQuotaBomController extends JeecgController<BuQuotaBom, IBuQuotaBomService> {

    @Resource
    private IBuQuotaBomService service;

    @GetMapping("/page")
    @ApiOperation(value = "定额BOM-分页查询")
    @OperationLog()
    public Result<IPage<BuQuotaBom>> queryPage(BuQuotaBom query,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuQuotaBom> page = service.queryPage(query, pageNo, pageSize);
        return new Result<IPage<BuQuotaBom>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "定额BOM-根据id查询")
    @OperationLog()
    public Result<BuQuotaBom> get(@RequestParam @ApiParam(value = "id", required = true) String id) {
        BuQuotaBom record = service.getById(id);
        return new Result<BuQuotaBom>().successResult(record);
    }

    @PostMapping("/save")
    @ApiOperation(value = "定额BOM-新增")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody BuQuotaBom record) {
        boolean flag = service.saveRecord(record);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/update")
    @ApiOperation(value = "定额BOM-修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> update(@RequestBody BuQuotaBom record) {
        boolean flag = service.updateRecord(record);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "定额BOM-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids) {
        boolean flag = service.deleteRecord(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "定额BOM-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuQuotaBom record) {
        return super.exportXls(request, record, BuQuotaBom.class, "定额BOM");
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "定额BOM-导入")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BuQuotaBom.class);
    }
}
