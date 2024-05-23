package org.jeecg.modules.material.alert.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertQueryVO;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertVO;
import org.jeecg.modules.material.alert.service.BuMaterialAlertService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 物资预警查看 APP端控制器
 * </p>
 *
 * @author lidafeng
 * @since 2021-03-01
 */
@Api(tags = "预警查看")
@AppController
@Slf4j
@Controller
@RequestMapping("/app/material/alert")
public class AppMaterialAlertController {
    private final BuMaterialAlertService buMaterialAlertService;

    public AppMaterialAlertController(BuMaterialAlertService buMaterialAlertService) {
        this.buMaterialAlertService = buMaterialAlertService;
    }

    @GetMapping("/page")
    @ResponseBody
    @ApiOperation(value = "查询默认库存预警记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialAlertVO>> page(@Validated BuMaterialAlertQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialAlertVO> page = buMaterialAlertService.appPageDefault(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialAlertVO>>().successResult(page);
    }

    @PostMapping("/read/{id}")
    @ResponseBody
    @ApiOperation(value = "设置已读")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> settingRead(@ApiParam(value = "预警id")@PathVariable String id) throws Exception {
        boolean flag = buMaterialAlertService.settingRead(id);
        return new Result<Boolean>().successResult(flag);
    }
}

