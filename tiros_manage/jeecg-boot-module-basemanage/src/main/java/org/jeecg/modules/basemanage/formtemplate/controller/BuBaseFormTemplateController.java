package org.jeecg.modules.basemanage.formtemplate.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.formtemplate.entity.BuBaseFormTemplate;
import org.jeecg.modules.basemanage.formtemplate.entity.vo.FormTemplateQueryVO;
import org.jeecg.modules.basemanage.formtemplate.service.IBuBaseFormTemplateService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 数据记录表模版信息 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@RestController
@RequestMapping("/formTemplate")
@Api(tags = "数据记录表")
@Slf4j
public class BuBaseFormTemplateController {

    private final IBuBaseFormTemplateService buBaseFormTemplateService;

    public BuBaseFormTemplateController(IBuBaseFormTemplateService buBaseFormTemplateService) {
        this.buBaseFormTemplateService = buBaseFormTemplateService;
    }


    @GetMapping(value = "/page")
    @ApiOperation("分页查询表单模板")
    @OperationLog()
    public Result<IPage<BuBaseFormTemplate>> queryPageList(@Validated FormTemplateQueryVO formTemplateQueryVO,
                                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuBaseFormTemplate> page = buBaseFormTemplateService.selectFormTemplatePage(new Page<>(pageNo, pageSize), formTemplateQueryVO);
        return new Result<IPage<BuBaseFormTemplate>>().successResult(page);
    }

    @GetMapping(value = "/getContent/{id}")
    @ApiOperation("根据表单id获取content")
    @OperationLog()
    public Result<String> getContentByFormId(@PathVariable @ApiParam(value = "表单id") String id) throws Exception {
        String content = buBaseFormTemplateService.getContentByFormId(id);
        return new Result<String>().successResult(content);
    }

    @GetMapping(value = "/getDataJson/{id}")
    @ApiOperation("根据表单id获取dataJson")
    @OperationLog()
    public Result<String> getDataJsonByFormId(@PathVariable @ApiParam(value = "表单id") String id) throws Exception {
        String dataJson = buBaseFormTemplateService.getDataJsonByFormId(id);
        return new Result<String>().successResult(dataJson);
    }

    @PostMapping("/add")
    @ApiOperation("新增表单")
    @OperationLog(saveParam = false)
    public Result<Boolean> save(@RequestBody @Validated BuBaseFormTemplate formTemplate) {
        buBaseFormTemplateService.save(formTemplate);
        return new Result<Boolean>().successResult(true);
    }

    @PutMapping("/edit")
    @ApiOperation("修改表单")
    @OperationLog(saveParam = false)
    public Result<Boolean> edit(@Validated @RequestBody BuBaseFormTemplate formTemplate) {
        buBaseFormTemplateService.updateById(formTemplate);
        return new Result<Boolean>().successResult(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除表单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "表单ids，多个逗号分割") String ids) {
        boolean flag = buBaseFormTemplateService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/valid")
    @ApiOperation(value = "设置有效")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> validPlan(@RequestParam @ApiParam(value = "表单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buBaseFormTemplateService.validFormTemplate(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/invalid")
    @ApiOperation(value = "设置无效")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> invalidPlan(@RequestParam @ApiParam(value = "表单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buBaseFormTemplateService.invalidFormTemplate(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
