package org.jeecg.modules.material.stock.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialGroupStockQueryVO;
import org.jeecg.modules.material.stock.bean.vo.GroupStockAtrrVO;
import org.jeecg.modules.material.stock.service.BuMaterialGroupStockService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 班组库存管理 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Api(tags = "库存管理(班组)")
@Slf4j
@RestController
@RequestMapping("/material/stock/group")
public class BuMaterialGroupStockController {

    private final BuMaterialGroupStockService buMaterialGroupStockService;

    public BuMaterialGroupStockController(BuMaterialGroupStockService buMaterialGroupStockService) {
        this.buMaterialGroupStockService = buMaterialGroupStockService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询班组库存(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialGroupStock>> pageGroupStock(@Validated BuMaterialGroupStockQueryVO queryVO,
                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialGroupStock> page = buMaterialGroupStockService.pageGroupStock(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialGroupStock>>().successResult(page);
    }

    @GetMapping("/list-for-taskWrite")
    @ApiOperation(value = "查询班组库存(用于作业填报查班组库存，没有会新增库存量为0的记录)")
    @OperationLog()
    public Result<List<BuMaterialGroupStock>> listGroupStockForTaskWrite(@RequestParam @ApiParam(value = "班组id", required = true) String groupId,
                                                                         @RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId,
                                                                         @RequestParam(required = false) @ApiParam(value = "工单物料id") String orderMaterialId) throws Exception {
        List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockService.listGroupStockForTaskWrite(groupId, materialTypeId, orderMaterialId);
        return new Result<List<BuMaterialGroupStock>>().successResult(groupStockList);
    }

    @GetMapping("/get-amount")
    @ApiOperation(value = "查询班组指定物资库存量")
    @OperationLog()
    public Result<Double> getGroupMaterialStockAmount(@RequestParam @ApiParam(value = "班组id", required = true) String groupId,
                                                      @RequestParam @ApiParam(value = "物资类型id", required = true) String materialTypeId) throws Exception {
        Double amount = buMaterialGroupStockService.getGroupMaterialStockAmount(groupId, materialTypeId);
        return new Result<Double>().successResult(amount);
    }

    @GetMapping("/export")
    @ApiOperation(value = "班组库存导出(excel)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportGroupStock(@Validated BuMaterialGroupStockQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialGroupStockService.exportGroupStock(queryVO);
            String fileName = String.format("班组库存-%s", new Date());
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

    @PostMapping("/setAttribute")
    @ApiModelProperty(value = "设置物资属性")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setMaterialAttribute(@RequestBody @Validated GroupStockAtrrVO stockAtrrVO) throws Exception {
        Boolean setFlag = buMaterialGroupStockService.setMaterialAttribute(stockAtrrVO);
        return new Result<Boolean>().successResult(setFlag);
    }

    @PostMapping("/recoverAttribute")
    @ApiModelProperty(value = "恢复物资属性")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> recoverMaterialAttribute(@RequestParam @ApiParam(value = "班组库存ids,多个用逗号分隔", required = true) String ids,
                                                    @RequestParam @ApiParam(value = "班组groupIds,多个用逗号分隔", required = true) String groupIds) throws Exception {
        Boolean recoverFlag = buMaterialGroupStockService.recoverMaterialAttribute(ids, groupIds);
        return new Result<Boolean>().successResult(recoverFlag);
    }

    @PostMapping("/trans-to/turnover")
    @ApiOperation(value = "指定班组库存物资转入周转件")
    @OperationLog()
    public Result<String> transGroupStockToTurnover(@RequestParam @ApiParam(value = "班组库存id", required = true) String groupStockId,
                                                    @RequestParam @ApiParam(value = "转入数量", required = true) BigDecimal transAmount) throws Exception {
        String transInfo = buMaterialGroupStockService.transGroupStockToTurnover(groupStockId, transAmount);
        return new Result<String>().successResult(transInfo);
    }

}