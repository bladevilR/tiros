package org.jeecg.modules.material.tools.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsQueryVO;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsSettingVO;
import org.jeecg.modules.material.tools.service.BuMaterialToolsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * <p>
 * 工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Api(tags = "工艺装备管理")
@Slf4j
@RestController
@RequestMapping("/material/tools")
public class BuMaterialToolsController {

    private final BuMaterialToolsService buMaterialToolsService;

    public BuMaterialToolsController(BuMaterialToolsService buMaterialToolsService) {
        this.buMaterialToolsService = buMaterialToolsService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询工艺装备记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialTools>> page(@Validated BuMaterialToolsQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialTools> page = buMaterialToolsService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialTools>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增工艺装备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialTools buMaterialTools) {
        boolean flag = buMaterialToolsService.save(buMaterialTools);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改工艺装备")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuMaterialTools buMaterialTools) {
        boolean flag = buMaterialToolsService.updateById(buMaterialTools);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除工艺装备（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工艺装备ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialToolsService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/setNextCheckTime")
    @ApiOperation(value = "设置下次送检日期（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setNextCheckTime(@RequestParam @ApiParam(value = "工艺装备ids，多个逗号分隔", required = true) String ids,
                                            @RequestParam @ApiParam(value = "下次送检日期 格式 yyyy-MM-dd", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date nextCheckTime) throws Exception {
        boolean flag = buMaterialToolsService.setNextCheckTime(ids, nextCheckTime);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/setResponsible")
    @ApiOperation(value = "设置责任人（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setResponsible(@RequestParam @ApiParam(value = "工艺装备ids，多个逗号分隔", required = true) String ids,
                                          @RequestParam @ApiParam(value = "责任人id", required = true) String userId) throws Exception {
        boolean flag = buMaterialToolsService.setResponsible(ids, userId);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/setLastSelfCheckTime")
    @ApiOperation(value = "设置上次自检日期（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setLastSelfCheckTime(@RequestParam @ApiParam(value = "工艺装备ids，多个逗号分隔", required = true) String ids,
                                                @RequestParam @ApiParam(value = "上次自检日期 格式 yyyy-MM-dd", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date lastSelfCheckTime) throws Exception {
        boolean flag = buMaterialToolsService.setLastCheckTime(ids, lastSelfCheckTime);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/setSelfCheck")
    @ApiOperation(value = "设置是否自检")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setSelfCheck(@RequestParam @ApiParam(value = "工艺装备ids，多个逗号分隔", required = true) String ids,
                                        @RequestParam @ApiParam(value = "是否自检", required = true) Integer selfCheck) throws Exception {
        boolean flag = buMaterialToolsService.setSelfCheck(ids, selfCheck);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/setElectric")
    @ApiOperation(value = "设置是否电动工具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setElectric(@RequestBody BuMaterialToolsSettingVO toolsSettingVO) throws Exception {
        boolean flag = buMaterialToolsService.setElectric(toolsSettingVO);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/listNeedCheck")
    @ApiOperation(value = "查询待送检工艺装备列表(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialTools>> listNeedCheck(@RequestParam(required = false) @ApiParam(value = "名称或编码") String searchText,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialTools> page = buMaterialToolsService.listNeedCheck(searchText, pageNo, pageSize);
        return new Result<IPage<BuMaterialTools>>().successResult(page);
    }

    @GetMapping("/countNeedCheck")
    @ApiOperation(value = "查询待送检工艺装备个数")
    @OperationLog()
    public Result<Integer> countNeedCheck(@RequestParam(required = false) @ApiParam(value = "名称或编码") String searchText) throws Exception {
        Integer count = buMaterialToolsService.countNeedCheck(searchText);
        return new Result<Integer>().successResult(count);
    }

    @PostMapping("/flush-group-by-user")
    @ApiOperation(value = "根据责任人员刷新工器具的所属班组")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> flushToolGroupByUser() throws Exception {
        boolean flag = buMaterialToolsService.flushToolGroupByUser();
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/export")
    @ApiOperation(value = "工器具导出(excel)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> exportTools(@Validated BuMaterialToolsQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = buMaterialToolsService.exportTools(queryVO);
            String fileName = String.format("工器具-%s", new Date());
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

