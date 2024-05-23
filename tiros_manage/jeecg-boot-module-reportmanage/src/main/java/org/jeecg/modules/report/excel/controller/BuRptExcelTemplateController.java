package org.jeecg.modules.report.excel.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.report.excel.bean.BuRptExcelTemplate;
import org.jeecg.modules.report.excel.service.BuRptExcelTemplateService;
import org.jeecg.modules.report.utils.ExcelUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 报表中心 表格模板 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-08
 */
@Api(tags = "表格模板")
@Slf4j
@RestController
@RequestMapping("/report/excel")
public class BuRptExcelTemplateController {

    private final BuRptExcelTemplateService buRptExcelTemplateService;

    public BuRptExcelTemplateController(BuRptExcelTemplateService buRptExcelTemplateService) {
        this.buRptExcelTemplateService = buRptExcelTemplateService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询表格模板(分页)")
    @OperationLog()
    public Result<IPage<BuRptExcelTemplate>> pageExcelTemplate(@RequestParam(required = false) @ApiParam(value = "编码或名称") String searchText,
                                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRptExcelTemplate> page = buRptExcelTemplateService.pageExcelTemplate(searchText, pageNo, pageSize);
        return new Result<IPage<BuRptExcelTemplate>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据编码查询表格模板信息")
    @OperationLog()
    public Result<BuRptExcelTemplate> getByCode(@RequestParam @ApiParam(value = "表格模板编码", required = true) String tempCode) throws Exception {
        BuRptExcelTemplate excelTemplate = buRptExcelTemplateService.getExcelTemplateByCode(tempCode);
        return new Result<BuRptExcelTemplate>().successResult(excelTemplate);
    }

    @PostMapping("/save")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    @ApiOperation(value = "保存表格模板(新增或修改)")
    public Result<Boolean> saveExcelTemplate(@RequestBody @Validated BuRptExcelTemplate excelTemplate) throws Exception {
        boolean flag = buRptExcelTemplateService.saveOrUpdate(excelTemplate);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除表格模板(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "表格模板ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRptExcelTemplateService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/downfile")
    public ResponseEntity<byte[]> downExcelFile(@RequestParam(value = "exceldata") String exceldata, HttpServletRequest request, HttpServletResponse response) {
        //去除luckysheet中 &#xA 的换行
        exceldata = exceldata.replace("&#xA;", "\\r\\n");
        return ExcelUtils.exportLuckySheetXlsx(exceldata, request, response);
    }

}

