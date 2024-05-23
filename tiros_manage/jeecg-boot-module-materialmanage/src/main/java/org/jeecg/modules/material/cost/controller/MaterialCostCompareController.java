package org.jeecg.modules.material.cost.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.vo.CompareInfoVO;
import org.jeecg.modules.material.cost.bean.vo.CompareQueryVO;
import org.jeecg.modules.material.cost.bean.vo.CompareResultVO;
import org.jeecg.modules.material.cost.service.MaterialCostCompareService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 物料消耗对比 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Api(tags = "物料消耗对比")
@Slf4j
@RestController
@RequestMapping("/material/cost")
public class MaterialCostCompareController {

    public final MaterialCostCompareService materialCostCompareService;

    public MaterialCostCompareController(MaterialCostCompareService materialCostCompareService) {
        this.materialCostCompareService = materialCostCompareService;
    }


    @GetMapping("/compare/list")
    @ApiOperation(value = "物料消耗对比列表")
    @OperationLog()
    public Result<CompareInfoVO> listMaterialCostCompare(@Validated CompareQueryVO queryVO) throws Exception {
        CompareInfoVO compareInfoVO = materialCostCompareService.listMaterialCostCompare(queryVO, false);
        return new Result<CompareInfoVO>().successResult(compareInfoVO);
    }

//    @GetMapping("/compare/detail")
//    @ApiOperation(value = "物料消耗对比详情-工单物料明细")
//    @OperationLog()
//    public Result<List<BuWorkOrderMaterial>> getMaterialCostCompareDetail(@RequestParam(required = false) @ApiParam(value = "工单物料ids，多个逗号分隔") String orderMaterialIds) throws Exception {
//        List<BuWorkOrderMaterial> orderMaterialList = materialCostCompareService.listOrderMaterialByIds(orderMaterialIds);
//        return new Result<List<BuWorkOrderMaterial>>().successResult(orderMaterialList);
//    }

//    @PostMapping("/verify-batch")
//    @ApiOperation(value = "核实物料消耗（批量）", notes = "只改核实状态")
//    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
//    public Result<Boolean> verifyOrderMaterialBatch(@RequestParam @ApiParam(value = "工单物料ids，多个逗号分隔", required = true) String orderMaterialIds) throws Exception {
//        boolean flag = materialCostCompareService.verifyOrderMaterialBatch(orderMaterialIds);
//        return new Result<Boolean>().successResult(flag);
//    }

    @PostMapping("/verify-orderMaterialList")
    @ApiOperation(value = "核实物料消耗（工单物料列表）", notes = "单条数据核实，接收修改后的工单物料+实际消耗")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> verifyOrderMaterial(@RequestBody List<BuWorkOrderMaterial> orderMaterialList) throws Exception {
        boolean flag = materialCostCompareService.verifyOrderMaterialList(orderMaterialList);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/compare/export")
    @ApiOperation("导出")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> export(@Validated CompareQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = materialCostCompareService.exportMaterialCostCompare(queryVO);
            String fileName = String.format("物料消耗核实%s", workbook.getSheetAt(0).getSheetName());
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-Disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
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
