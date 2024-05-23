package org.jeecg.modules.report.cost.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.cost.bean.vo.BuKpiTimeItemVO;
import org.jeecg.modules.report.cost.bean.vo.KpiQueryVO;
import org.jeecg.modules.report.cost.service.BuRptPersonKpiReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 人工成本明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-01
 */
@Api(tags = "人工成本明细")
@Slf4j
@RestController
@RequestMapping("/report/cost/kpi")
public class BuReportCostKpiController {

    private final BuRptPersonKpiReportService buRptPersonKpiReportService;

    public BuReportCostKpiController(BuRptPersonKpiReportService buRptPersonKpiReportService) {
        this.buRptPersonKpiReportService = buRptPersonKpiReportService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<IPage<BuKpiTimeItemVO>> pageTimeKpi(@Validated KpiQueryVO queryVO,
                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuKpiTimeItemVO> page = buRptPersonKpiReportService.pageTimeKpi(queryVO, pageNo, pageSize);
        return new Result<IPage<BuKpiTimeItemVO>>().successResult(page);
    }

    @GetMapping("/export")
    @ApiOperation(value = "导出(excel)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportTimeKpi(@Validated KpiQueryVO queryVO, HttpServletResponse httpServletResponse) throws Exception {
        HSSFWorkbook workbook = buRptPersonKpiReportService.exportTimeKpi(queryVO);
        String fileName = String.format("人工成本明细-%s", workbook.getSheetAt(0).getSheetName());

        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

        return null;
    }

}
