package org.jeecg.modules.tiros.importer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.tiros.util.UploadFileCheckUtil;
import org.jeecg.modules.dispatch.fault.service.BuFaultInfoService;
import org.jeecg.modules.tiros.importer.service.ImporterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author yuyougen
 * @title: ImporterController
 * @projectName tiros-manage-parent
 * @description: TODO
 * @date 2021/4/1514:11
 */
@TirosController
@Api(tags = "基础数据导入")
@Slf4j
@RestController
@RequestMapping("/import/")
public class ImporterController {

    private final ImporterService importerService;
    private final BuFaultInfoService buFaultInfoService;

    public ImporterController(ImporterService importerService,
                              BuFaultInfoService buFaultInfoService) {
        this.importerService = importerService;
        this.buFaultInfoService = buFaultInfoService;
    }

    @PostMapping(value = "regu")
    @ApiOperation(value = "导入规程")
    public Result<Boolean> importRegu(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importRegu(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "tp-plan")
    @ApiOperation(value = "导入计划模板")
    public Result<Boolean> importPlan(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importPlan(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "workRecord")
    @ApiOperation(value = "导入作业记录表")
    public Result<Boolean> importWorkRecord(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".doc", ".docx"));

        importerService.importWorkRecord(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "materialMust")
    @ApiOperation(value = "导入必换件", notes = "需要用request传线路:lineId,修程:repairProgramId,是否清楚原有数据clear boolean类型")
    public Result<Boolean> importMaterialMust(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importMaterialMust(file, request);
        return new Result<Boolean>().successResult(true);
    }

//    @PostMapping(value = "materialEntryOrder")
//    @ApiOperation(value = "导入入库单")
//    public Result<Boolean> importMaterialEntryOrder(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
//        // 检查文件类型
//        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));
//
//        importerService.importMaterialEntryOrder(file, request);
//        return new Result<Boolean>().successResult(true);
//    }

    @PostMapping(value = "materialType")
    @ApiOperation(value = "导入物资类型")
    public Result<Boolean> importMaterialType(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        boolean flag = importerService.importMaterialType(file, request);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping(value = "materialSparePart")
    @ApiOperation(value = "导入列管备件")
    public Result<Boolean> importMaterialSparePart(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importMaterialSparePart(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "rptBoard")
    @ApiOperation(value = "导入单列成本")
    public Result<Boolean> importRptBoard(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importRptBoard(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "materialTools")
    @ApiOperation(value = "导入工装工器具", notes = "需要用request传类型:type")
    public Result<Boolean> importMaterialTools(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importMaterialTools(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "workCheck")
    @ApiOperation(value = "导入作业检查表", notes = "需要用request传线路:lineId,修程:repairProId")
    public Result<Boolean> importWorkCheck(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".doc", ".docx"));

        importerService.importWorkCheck(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "groupStock")
    @ApiOperation(value = "导入班组库存", notes = "需要用request传参数cleanup:是否清除已有数据(默认true）")
    public Result<Boolean> importGroupStock(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importGroupStock(file, request);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "materialApply/sync/price")
    @ApiOperation(value = "导入物资领用明细(同步领用明细价格)")
    public Result<Boolean> importMaterialApplyUsingSyncPrice(@RequestParam("file") MultipartFile file) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));

        importerService.importMaterialApplyUsingSyncPrice(file);
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping(value = "materialCost/system")
    @ApiOperation(value = "导入物料成本(系统维度)")
    public Result<Boolean> importMaterialCostBySystem(@RequestParam("files") List<MultipartFile> files) throws Exception {
        for (MultipartFile file : files) {
            // 检查文件类型
            UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));
        }

        importerService.importMaterialCostBySystem(files);
        return new Result<Boolean>().successResult(true);
    }

//    @PostMapping(value = "fault/history")
//    @ApiOperation(value = "导入历史故障", notes = "需要用request传参数type:1架修，2大修")
//    public Result<String> importFaultHistory(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
//        // 检查文件类型
//        UploadFileCheckUtil.checkFileValid(file, null, Arrays.asList(".xls", ".xlsx"));
//
//        String resultString = importerService.importFaultHistory(file, request);
//        return new Result<String>().successResult(resultString);
//    }
    @PostMapping(value = "fault/history")
    @ApiOperation(value = "导入历史故障", notes = "需要用request传参数type：1架修 2大修")
    public Result<String> importFaultHistory(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        int type = StringUtils.isBlank(request.getParameter("type")) ? 1 : Integer.parseInt(request.getParameter("type"));
        String resultString = buFaultInfoService.importFaultHistory(file, type);
        return new Result<String>().successResult(resultString);
    }

}
