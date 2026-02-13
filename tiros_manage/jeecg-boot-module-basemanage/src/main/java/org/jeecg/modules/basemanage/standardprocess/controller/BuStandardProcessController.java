package org.jeecg.modules.basemanage.standardprocess.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.standardprocess.entity.BuStandardProcess;
import org.jeecg.modules.basemanage.standardprocess.service.IBuStandardProcessService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "标准工序管理")
@Slf4j
@RestController
@RequestMapping("/base/standard-process")
public class BuStandardProcessController extends JeecgController<BuStandardProcess, IBuStandardProcessService> {

    @Resource
    private IBuStandardProcessService service;

    @GetMapping("/page")
    @ApiOperation(value = "标准工序-分页查询")
    @OperationLog()
    public Result<IPage<BuStandardProcess>> queryPage(BuStandardProcess query,
                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuStandardProcess> page = service.queryPage(query, pageNo, pageSize);
        return new Result<IPage<BuStandardProcess>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "标准工序-根据id查询")
    @OperationLog()
    public Result<BuStandardProcess> get(@RequestParam @ApiParam(value = "id", required = true) String id) {
        BuStandardProcess record = service.getById(id);
        return new Result<BuStandardProcess>().successResult(record);
    }

    @PostMapping("/save")
    @ApiOperation(value = "标准工序-新增")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody BuStandardProcess record) {
        boolean flag = service.saveRecord(record);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/update")
    @ApiOperation(value = "标准工序-修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> update(@RequestBody BuStandardProcess record) {
        boolean flag = service.updateRecord(record);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "标准工序-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids) {
        boolean flag = service.deleteRecord(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "标准工序-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuStandardProcess record) {
        return super.exportXls(request, record, BuStandardProcess.class, "标准工序");
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "标准工序-导入")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BuStandardProcess.class);
    }

    @GetMapping("/exportPdf")
    @ApiOperation(value = "标准工序-导出PDF")
    @OperationLog()
    public void exportPdf(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids,
                          HttpServletResponse response) {
        try {
            List<String> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            byte[] pdfBytes = service.exportPdf(idList);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=standard_process.pdf");
            response.setContentLength(pdfBytes.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(pdfBytes);
                out.flush();
            }
        } catch (Exception e) {
            log.error("导出PDF失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
