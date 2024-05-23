package org.jeecg.modules.basemanage.tpplan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlan;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanCopyVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanQueryVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanRelationVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanVOGantt;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 计划模版 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
@Api(tags = "计划模板")
@Slf4j
@RestController
@RequestMapping("/tp-plan")
public class BuTpRepairPlanController {

    private final BuTpRepairPlanService buTpRepairPlanService;

    public BuTpRepairPlanController(BuTpRepairPlanService buTpRepairPlanService) {
        this.buTpRepairPlanService = buTpRepairPlanService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "计划-查询分页")
    @OperationLog()
    public Result<IPage<BuTpRepairPlan>> pageTpPlan(@Validated BuTpRepairPlanQueryVO queryVO,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuTpRepairPlan> page = buTpRepairPlanService.pageTpPlan(queryVO, pageNo, pageSize);
        return new Result<IPage<BuTpRepairPlan>>().successResult(page);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "计划-根据id查详情(含任务)")
    @OperationLog()
    public Result<BuTpRepairPlanVOGantt> selectPlanAndTask(@PathVariable @ApiParam(value = "计划模板id") String id) throws Exception {
        BuTpRepairPlanVOGantt planGantt = buTpRepairPlanService.selectPlanAndTask(id);
        return new Result<BuTpRepairPlanVOGantt>().successResult(planGantt);
    }

    @GetMapping("/relation/{id}")
    @ApiOperation(value = "计划-根据id查关联信息")
    @OperationLog()
    public Result<BuTpRepairPlanRelationVO> selectPlanRelation(@PathVariable @ApiParam(value = "计划模板id") String id) throws Exception {
        BuTpRepairPlanRelationVO planRelation = buTpRepairPlanService.selectPlanRelation(id);
        return new Result<BuTpRepairPlanRelationVO>().successResult(planRelation);
    }

    @GetMapping("/not-related-regu/count")
    @ApiOperation(value = "计划-根据id查未关联的规程明细数量")
    @OperationLog()
    public Result<Integer> noRelevanceCount(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                            @RequestParam @ApiParam(value = "规程id", required = true) String reguId) throws Exception {
        Integer noRelevanceCount = buTpRepairPlanService.noRelevanceCount(planId, reguId);
        return new Result<Integer>().successResult(noRelevanceCount);
    }

    @GetMapping("/not-related-regu/detail")
    @ApiOperation(value = "计划-根据id查未关联的规程明细数据列表")
    @OperationLog()
    public Result<Page<BuRepairReguDetail>> noRelevanceDetail(@RequestParam @ApiParam(value = "列计划id", required = true) String planId,
                                                              @RequestParam @ApiParam(value = "规程id", required = true) String reguId,
                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuRepairReguDetail> pageList = buTpRepairPlanService.noRelevanceDetail(new Page<>(pageNo, pageSize), planId, reguId);
        return new Result<Page<BuRepairReguDetail>>().successResult(pageList);
    }

    @PostMapping("/save")
    @ApiOperation(value = "计划-保存（新增/修改）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdatePlan(@RequestBody @Validated BuTpRepairPlan repairPlan) throws Exception {
        boolean flag = buTpRepairPlanService.saveOrUpdatePlan(repairPlan);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "计划-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchPlan(@RequestParam @ApiParam(value = "计划模板id,多个用英文逗号隔开") String ids) throws Exception {
        boolean flag = buTpRepairPlanService.deleteByIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/valid")
    @ApiOperation(value = "计划-设置有效")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> validPlan(@RequestParam @ApiParam(value = "计划模板ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buTpRepairPlanService.validPlan(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/invalid")
    @ApiOperation(value = "计划-设置无效")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> invalidPlan(@RequestParam @ApiParam(value = "计划模板ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buTpRepairPlanService.invalidPlan(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/copy")
    @ApiOperation(value = "计划-复制创建")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> copyPlan(@RequestBody @Validated BuTpRepairPlanCopyVO planCopyVO) throws Exception {
        boolean flag = buTpRepairPlanService.copyPlan(planCopyVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/flush-regu-relation")
    @ApiOperation(value = "计划-刷新规程的物料工器具作业指导书到计划模板中")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> flushReguRelationToTpPlan(@RequestParam(required = false) @ApiParam(value = "计划模板id") String tpPlanId) throws Exception {
        boolean flag = buTpRepairPlanService.flushReguRelationToTpPlan(tpPlanId);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/planInfo/export")
    @ApiOperation("计划模板关联数据导出")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> planInfoExport(@RequestParam @ApiParam(value = "计划模板id") String tpPlanId, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buTpRepairPlanService.exportPlanInfo(tpPlanId);
            String fileName = "计划模板关联数据";
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

    @GetMapping("/sync/mustReplace")
    @ApiOperation(value = "同步必换件到物资表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> syncMustReplace() throws Exception {
        Boolean flag = buTpRepairPlanService.syncMustReplace();
        return new Result<Boolean>().successResult(flag);
    }

}