package org.jeecg.modules.group.measurealert.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.group.measurealert.bean.vo.BuWorkMeasureAlertCloseVO;
import org.jeecg.modules.group.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;
import org.jeecg.modules.group.measurealert.service.BuPlanFormValuesGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 测量修复 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Api(tags = "测量修复")
@Slf4j
@RestController
@RequestMapping("/group/measurealert")
public class BuMeasureAlertGroupController {

    private final BuPlanFormValuesGroupService buPlanFormValuesGroupService;

    public BuMeasureAlertGroupController(BuPlanFormValuesGroupService buPlanFormValuesGroupService) {
        this.buPlanFormValuesGroupService = buPlanFormValuesGroupService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询测量预警记录（分页）")
    @OperationLog()
    public Result<IPage<BuPlanFormValues>> page(@Validated BuWorkMeasureAlertQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuPlanFormValues> page = buPlanFormValuesGroupService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuPlanFormValues>>().successResult(page);
    }

    @PostMapping("/close")
    @ApiOperation(value = "测量预警修复（修复关闭）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> close(@RequestBody @Validated BuWorkMeasureAlertCloseVO closeVO) throws Exception {
        boolean flag = buPlanFormValuesGroupService.close(closeVO);
        return new Result<Boolean>().successResult(flag);
    }

}

