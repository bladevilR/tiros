package org.jeecg.modules.basemanage.qualityvisual.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.qualityvisual.entity.BuQualityVisual;
import org.jeecg.modules.basemanage.qualityvisual.service.IBuQualityVisualService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "质量可视化管理")
@Slf4j
@RestController
@RequestMapping("/base/quality-visual")
public class BuQualityVisualController extends JeecgController<BuQualityVisual, IBuQualityVisualService> {

    @Resource
    private IBuQualityVisualService service;

    @GetMapping("/page")
    @ApiOperation(value = "质量可视化-分页查询")
    @OperationLog()
    public Result<IPage<BuQualityVisual>> queryPage(BuQualityVisual query,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuQualityVisual> page = service.queryPage(query, pageNo, pageSize);
        return new Result<IPage<BuQualityVisual>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "质量可视化-根据id查询")
    @OperationLog()
    public Result<BuQualityVisual> get(@RequestParam @ApiParam(value = "id", required = true) String id) {
        BuQualityVisual record = service.getById(id);
        return new Result<BuQualityVisual>().successResult(record);
    }

    @PostMapping("/save")
    @ApiOperation(value = "质量可视化-新增")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody BuQualityVisual record) {
        boolean flag = service.saveRecord(record);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/update")
    @ApiOperation(value = "质量可视化-修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> update(@RequestBody BuQualityVisual record) {
        boolean flag = service.updateRecord(record);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "质量可视化-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids) {
        boolean flag = service.deleteRecord(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "质量可视化-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuQualityVisual record) {
        return super.exportXls(request, record, BuQualityVisual.class, "质量可视化");
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "质量可视化-导入")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BuQualityVisual.class);
    }
}
