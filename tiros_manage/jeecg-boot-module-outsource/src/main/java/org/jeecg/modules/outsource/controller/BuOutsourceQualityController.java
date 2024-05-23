package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuOutsourceQuality;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceOutInQualityVO;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceQualityQueryVO;
import org.jeecg.modules.outsource.service.BuContractInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 委外质保 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "委外质保")
@Slf4j
@RestController
@RequestMapping("/outsource/quality/")
public class BuOutsourceQualityController {

    private final BuContractInfoService contractInfoService;

    public BuOutsourceQualityController(BuContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }


    @GetMapping("page")
    @ApiOperation(value = "委外质保（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceQuality>> page(@Validated BuOutsourceQualityQueryVO queryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceQuality> page = contractInfoService.qualityPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceQuality>>().successResult(page);
    }

    @PostMapping("edit")
    @ApiOperation(value = "设置质保日期、天数")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody BuOutsourceOutInQualityVO qualityVO ) throws Exception {
        return new Result<Boolean>().successResult(contractInfoService.updateQuality(qualityVO));
    }
}

