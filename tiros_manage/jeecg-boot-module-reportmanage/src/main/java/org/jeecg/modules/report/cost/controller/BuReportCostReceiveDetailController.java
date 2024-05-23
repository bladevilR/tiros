package org.jeecg.modules.report.cost.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.cost.bean.BuMaterialAssignDetail;
import org.jeecg.modules.report.cost.bean.vo.BuMaterialReceiveQueryVO;
import org.jeecg.modules.report.cost.service.BuMaterialAssignDetailReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 领料明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
@Api(tags = "领料明细")
@Slf4j
@RestController
@RequestMapping("/report/cost/receive/detail")
public class BuReportCostReceiveDetailController {

    private final BuMaterialAssignDetailReportService buMaterialAssignDetailReportService;

    public BuReportCostReceiveDetailController(BuMaterialAssignDetailReportService buMaterialAssignDetailReportService) {
        this.buMaterialAssignDetailReportService = buMaterialAssignDetailReportService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialAssignDetail>> pageMaterialReceive(@Validated BuMaterialReceiveQueryVO queryVO,
                                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialAssignDetail> page = buMaterialAssignDetailReportService.pageMaterialReceive(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialAssignDetail>>().successResult(page);
    }

    @GetMapping("/export")
    @ApiOperation(value = "导出(excel)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportMaterialReceive(@Validated BuMaterialReceiveQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialAssignDetailReportService.exportMaterialReceive(queryVO);
            String fileName = String.format("领料明细-%s", workbook.getSheetAt(0).getSheetName());
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
