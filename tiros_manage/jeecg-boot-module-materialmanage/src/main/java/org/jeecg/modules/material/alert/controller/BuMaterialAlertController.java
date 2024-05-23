package org.jeecg.modules.material.alert.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertQueryVO;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertVO;
import org.jeecg.modules.material.alert.service.BuMaterialAlertService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 物资预警查看
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-19
 */
@Api(tags = "预警查看")
@Slf4j
@Controller
@RequestMapping("/material/alert")
public class BuMaterialAlertController {
    private final BuMaterialAlertService buMaterialAlertService;

    public BuMaterialAlertController(BuMaterialAlertService buMaterialAlertService) {
        this.buMaterialAlertService = buMaterialAlertService;
    }

    @GetMapping("/page")
    @ResponseBody
    @ApiOperation(value = "查询库存预警记录(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialAlertVO>> page(@Validated BuMaterialAlertQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialAlertVO> page = buMaterialAlertService.pageDefault(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialAlertVO>>().successResult(page);
    }

    @GetMapping("/exportWeek")
    @ApiOperation(value = "周物资预警导出(excel)", notes = "仅库存预警")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportWeekMaterialAlert(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialAlertService.exportWeekMaterialAlert();
            String fileName = String.format("周物资预警-%s", workbook.getSheetAt(0).getSheetName());
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

    @GetMapping("/exportMonth")
    @ApiOperation(value = "月物资预警导出(excel)", notes = "仅库存预警")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportMonthMaterialAlert(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialAlertService.exportMonthMaterialAlert();
            String fileName = String.format("月物资预警-%s", workbook.getSheetAt(0).getSheetName());
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

    @GetMapping("/exportTheshold")
    @ApiOperation(value = "3列车物资预警导出(excel)", notes = "仅库存预警")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportThesholdMaterialAlert(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialAlertService.exportThesholdMaterialAlert();
            String fileName = String.format("3列车物资预警-%s", workbook.getSheetAt(0).getSheetName());
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

