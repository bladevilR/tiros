package org.jeecg.modules.quality.measurethreshold.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.quality.measurethreshold.bean.BuBaseFormTemplate;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuBaseFormTemplateQueryVO;
import org.jeecg.modules.quality.measurethreshold.service.BuBaseFormTemplateQualityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 数据记录表模版信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Api(tags = "测量阈值设置")
@Slf4j
@RestController
@RequestMapping("/quality/measurethreshold/form")
public class BuBaseFormTemplateQualityController {

    private final BuBaseFormTemplateQualityService buBaseFormTemplateQualityService;

    public BuBaseFormTemplateQualityController(BuBaseFormTemplateQualityService buBaseFormTemplateQualityService) {
        this.buBaseFormTemplateQualityService = buBaseFormTemplateQualityService;
    }


    @GetMapping("/page-data-form")
    @ApiOperation(value = "查询数据记录表单(分页)")
    @OperationLog()
    public Result<IPage<BuBaseFormTemplate>> pageDataRecordForm(@Validated BuBaseFormTemplateQueryVO queryVO,
                                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuBaseFormTemplate> formTemplatePage = buBaseFormTemplateQualityService.pageDataRecordForm(queryVO, pageNo, pageSize);
        return new Result<IPage<BuBaseFormTemplate>>().successResult(formTemplatePage);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取测量表单详情")
    @OperationLog()
    public Result<BuBaseFormTemplate> get(@RequestParam @ApiParam(value = "表单id", readOnly = true) String id) {
        BuBaseFormTemplate formTemplate = buBaseFormTemplateQualityService.getById(id);
        return new Result<BuBaseFormTemplate>().successResult(formTemplate);
    }

}

