package org.jeecg.modules.quality.measurealert.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.quality.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.quality.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;
import org.jeecg.modules.quality.measurealert.service.BuPlanFormValuesQualityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测量预警 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Api(tags = "测量预警")
@Slf4j
@RestController
@RequestMapping("/quality/measurealert")
public class BuMeasureAlertQualityController {

    private final BuPlanFormValuesQualityService buPlanFormValuesQualityService;

    public BuMeasureAlertQualityController(BuPlanFormValuesQualityService buPlanFormValuesQualityService) {
        this.buPlanFormValuesQualityService = buPlanFormValuesQualityService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询测量预警记录（分页）")
    @OperationLog()
    public Result<IPage<BuPlanFormValues>> page(@Validated BuWorkMeasureAlertQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuPlanFormValues> page = buPlanFormValuesQualityService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuPlanFormValues>>().successResult(page);
    }

}

