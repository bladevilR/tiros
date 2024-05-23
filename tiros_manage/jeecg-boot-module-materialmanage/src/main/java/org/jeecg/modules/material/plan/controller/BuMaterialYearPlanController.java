package org.jeecg.modules.material.plan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.plan.bean.BuMaterialYearPlan;
import org.jeecg.modules.material.plan.bean.vo.BuMaterialYearPlanQueryVO;
import org.jeecg.modules.material.plan.service.BuMaterialYearPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2021-05-01
 */
@Slf4j
@Api(tags = "物资计划管理")
@RestController
@RequestMapping("/material/plan")
public class BuMaterialYearPlanController {

    private final BuMaterialYearPlanService materialYearPlanService;

    public BuMaterialYearPlanController(BuMaterialYearPlanService materialYearPlanService) {
        this.materialYearPlanService = materialYearPlanService;
    }

    /* @GetMapping("/list/all")
     @ApiOperation("查询物资计划-物资用(分页)")
     public Result<Page<BuMaterialYearPlan>> list(@Validated BuMaterialYearPlanQueryVO queryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
         Page<BuMaterialYearPlan> page = materialYearPlanService.materialYearPlanPage(queryVO, new Page<>(pageNo, pageSize),false);
         return new Result<Page<BuMaterialYearPlan>>().successResult(page);
     }*/
    @GetMapping("/list/part")
    @ApiOperation("查询物资计划-个人和班组用(分页)")
    @OperationLog()
    public Result<Page<BuMaterialYearPlan>> userOrGroup(@Validated BuMaterialYearPlanQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuMaterialYearPlan> page = materialYearPlanService.materialYearPlanPage(queryVO, new Page<>(pageNo, pageSize));
        return new Result<Page<BuMaterialYearPlan>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation("新增物资计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialYearPlan materialYearPlan) throws Exception {
        boolean flag = materialYearPlanService.addMaterialYearPlan(materialYearPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation("编辑物资计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuMaterialYearPlan materialYearPlan) throws Exception {
        boolean flag = materialYearPlanService.updateMaterialYearPlan(materialYearPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除物资计划（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "物资计划ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = materialYearPlanService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/export")
    @ApiOperation("导出物资计划")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> export(@Validated BuMaterialYearPlanQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = materialYearPlanService.exportMaterialYearPlan(queryVO);
            String fileName = String.format("物资计划");
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

    @GetMapping(value = "/materialYearTrain")
    @ApiOperation("获取系统配置车辆数量")
    @OperationLog()
    public Result<Integer> getMaterialYearTrain() throws Exception {
        Integer materialYearTrain = materialYearPlanService.getMaterialYearTrain();
        return new Result<Integer>().successResult(materialYearTrain);
    }

}
