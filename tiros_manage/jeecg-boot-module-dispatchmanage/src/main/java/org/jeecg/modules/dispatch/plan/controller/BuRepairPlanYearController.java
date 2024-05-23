package org.jeecg.modules.dispatch.plan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYear;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearVOWithGantt;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanYearService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 年计划 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Api(tags = "年计划管理")
@Slf4j
@RestController
@RequestMapping("/dispatch/plan/year")
public class BuRepairPlanYearController {

    private final BuRepairPlanYearService buRepairPlanYearService;

    public BuRepairPlanYearController(BuRepairPlanYearService buRepairPlanYearService) {
        this.buRepairPlanYearService = buRepairPlanYearService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询年计划记录（分页）")
    @OperationLog()
    public Result<IPage<BuRepairPlanYear>> pagePlanYear(@Validated BuRepairPlanFarYearQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairPlanYear> page = buRepairPlanYearService.pagePlanYear(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairPlanYear>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询年计划和明细")
    @OperationLog()
    public Result<BuRepairPlanYear> getPlanYear(@RequestParam @ApiParam(value = "年计划id", required = true) String id) throws Exception {
        BuRepairPlanYear repairPlanYear = buRepairPlanYearService.getPlanYearById(id);
        return new Result<BuRepairPlanYear>().successResult(repairPlanYear);
    }

    @GetMapping("/get-gantt")
    @ApiOperation(value = "根据id查询年计划和明细(甘特)")
    @OperationLog()
    public Result<BuRepairPlanYearVOWithGantt> getYearGanttWithDetail(@RequestParam @ApiParam(value = "年计划id", required = true) String planYearId) throws Exception {
        BuRepairPlanYearVOWithGantt yearVOWithGantt = buRepairPlanYearService.getYearGanttWithDetailGantt(planYearId);
        return new Result<BuRepairPlanYearVOWithGantt>().successResult(yearVOWithGantt);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增年计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuRepairPlanYearVOWithGantt yearVOWithGantt) throws Exception {
        boolean flag = buRepairPlanYearService.savePlanYear(yearVOWithGantt);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改年计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuRepairPlanYearVOWithGantt yearVOWithGantt) throws Exception {
        boolean flag = buRepairPlanYearService.updatePlanYear(yearVOWithGantt);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除年计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "年计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairPlanYearService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/sum-year-plan-detail-amount")
    @ApiOperation(value = "获取指定年份、指定修程的维修车辆数量")
    @OperationLog()
    public Result<Integer> sumRepairPlanYearDetailAmountByYearAndProgramId(@RequestParam @ApiParam(value = "年份", required = true) Integer year,
                                                                           @RequestParam @ApiParam(value = "修程id") String programId) throws Exception {
        int count = buRepairPlanYearService.sumRepairPlanYearDetailAmountByYearAndProgramId(year, programId);
        return new Result<Integer>().successResult(count);
    }

    @GetMapping("/yearPlanExport")
    @ApiOperation("年计划导出")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> yearPlanExport(@Validated BuRepairPlanFarYearQueryVO queryVO, HttpServletResponse httpServletResponse) throws Exception {
        HSSFWorkbook workbook = buRepairPlanYearService.exportYearPlanExport(queryVO);
        String fileName = String.format("年计划");
        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

        return null;
    }

}