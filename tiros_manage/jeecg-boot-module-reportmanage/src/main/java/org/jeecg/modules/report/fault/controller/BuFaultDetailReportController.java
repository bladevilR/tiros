package org.jeecg.modules.report.fault.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.fault.bean.BuFaultInfo;
import org.jeecg.modules.report.fault.bean.vo.FaultDetailQueryVO;
import org.jeecg.modules.report.fault.service.BuFaultDetailReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 故障报表 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Api(tags = "故障明细")
@Slf4j
@RestController
@RequestMapping("/report/fault/detail")
public class BuFaultDetailReportController {

    private final BuFaultDetailReportService buFaultDetailReportService;

    public BuFaultDetailReportController(BuFaultDetailReportService buFaultDetailReportService) {
        this.buFaultDetailReportService = buFaultDetailReportService;
    }


    @GetMapping("/list")
    @ApiOperation(value = "查询故障明细记录(所有)")
    @OperationLog()
    public Result<List<BuFaultInfo>> listFault(@Validated FaultDetailQueryVO queryVO) throws Exception {
        List<BuFaultInfo> faultInfoList = buFaultDetailReportService.listFault(queryVO);
        return new Result<List<BuFaultInfo>>().successResult(faultInfoList);
    }

    @GetMapping("/export")
    @ApiOperation(value = "故障明细导出")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportFaultDetail(@Validated FaultDetailQueryVO queryVO, HttpServletResponse httpServletResponse) throws Exception {
        HSSFWorkbook workbook = buFaultDetailReportService.exportFaultDetail(queryVO);
        String fileName = workbook.getSheetAt(0).getSheetName();

        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

        return null;
    }

}

