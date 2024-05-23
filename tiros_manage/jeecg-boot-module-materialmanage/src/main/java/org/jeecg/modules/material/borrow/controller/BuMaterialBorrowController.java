package org.jeecg.modules.material.borrow.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrow;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrowDetail;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowReturnVO;
import org.jeecg.modules.material.borrow.service.BuMaterialBorrowService;
import org.jeecg.modules.material.plan.bean.vo.BuMaterialYearPlanQueryVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 物资借用 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-17
 */
@Api(tags = "借用管理")
@Slf4j
@RestController
@RequestMapping("/material/borrow")
public class BuMaterialBorrowController {
    private final BuMaterialBorrowService buMaterialBorrowService;

    public BuMaterialBorrowController(BuMaterialBorrowService buMaterialBorrowService) {
        this.buMaterialBorrowService = buMaterialBorrowService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询物资借用记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialBorrow>> page(@Validated BuMaterialBorrowQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialBorrow> page = buMaterialBorrowService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialBorrow>>().successResult(page);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "借用物资明细列表")
    @OperationLog()
    public Result<List<BuMaterialBorrowDetail>> borrowDetailList(@ApiParam(value = "物资借用id", required = true) @PathVariable String id) throws Exception {
        List<BuMaterialBorrowDetail> borrowDetailList = buMaterialBorrowService.borrowDetailList(id);
        return new Result<List<BuMaterialBorrowDetail>>().successResult(borrowDetailList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增物资借用记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialBorrow buMaterialBorrow) throws Exception {
        boolean flag = buMaterialBorrowService.saveMaterialBorrow(buMaterialBorrow);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改物资借用记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuMaterialBorrow buMaterialBorrow) throws Exception {
        boolean flag = buMaterialBorrowService.updateMaterialBorrow(buMaterialBorrow);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除物资借用记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "物资借用记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialBorrowService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/return")
    @ApiOperation(value = "设置物资借用记录已归还（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setReturn(@RequestBody List<BuMaterialBorrowReturnVO> borrowReturnVOs) throws Exception {
        boolean flag = buMaterialBorrowService.setReturn(borrowReturnVOs);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/export")
    @ApiOperation("导出台账(借用物资)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> export(@Validated BuMaterialBorrowQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialBorrowService.exportMaterialBorrow(queryVO);
            String fileName = String.format("借用物资");
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

    @GetMapping("/borrowExport")
    @ApiOperation("借用物资导出")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> borrowExport(@Validated BuMaterialBorrowQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialBorrowService.exportDetailList(queryVO);
            String fileName = String.format("借用物资");
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

