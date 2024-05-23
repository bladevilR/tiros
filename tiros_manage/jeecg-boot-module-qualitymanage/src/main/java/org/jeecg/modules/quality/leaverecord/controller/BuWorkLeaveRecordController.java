package org.jeecg.modules.quality.leaverecord.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quality.leaverecord.bean.BuWorkLeaveRecord;
import org.jeecg.modules.quality.leaverecord.bean.vo.BuWorkLeaveRecordQueryVO;
import org.jeecg.modules.quality.leaverecord.service.BuWorkLeaveRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 开口项 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Api(tags = "开口项管理")
@Slf4j
@RestController
@RequestMapping("/quality/leaverecord")
public class BuWorkLeaveRecordController {

    private final BuWorkLeaveRecordService buWorkLeaveRecordService;

    public BuWorkLeaveRecordController(BuWorkLeaveRecordService buWorkLeaveRecordService) {
        this.buWorkLeaveRecordService = buWorkLeaveRecordService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询开口项记录（分页）")
    @OperationLog()
    public Result<IPage<BuWorkLeaveRecord>> page(@Validated BuWorkLeaveRecordQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkLeaveRecord> page = buWorkLeaveRecordService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkLeaveRecord>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增开口项记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuWorkLeaveRecord buWorkLeaveRecord) throws Exception {
        boolean flag = buWorkLeaveRecordService.saveWorkLeaveRecord(buWorkLeaveRecord);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改开口项记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuWorkLeaveRecord buWorkLeaveRecord) {
        boolean flag = buWorkLeaveRecordService.updateById(buWorkLeaveRecord);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除开口项记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") @ApiParam(name = "ids", value = "开口项记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkLeaveRecordService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/close")
    @ApiOperation(value = "关闭开口项（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> closeBatch(@RequestParam(name = "ids") @ApiParam(name = "ids", value = "开口项记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkLeaveRecordService.closeBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/workLeaveRecordExport")
    @ApiOperation("开口项导出")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> workLeaveRecordExport(@Validated BuWorkLeaveRecordQueryVO queryVO, HttpServletResponse httpServletResponse) throws Exception {
        HSSFWorkbook workbook = buWorkLeaveRecordService.workLeaveRecordExport(queryVO);
        String fileName = String.format("开口项");
        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

        return null;
    }

}
