package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.outsource.bean.BuOutsourceAsset;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceAssetQueryVO;
import org.jeecg.modules.outsource.service.BuContractInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 委外质保 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "委外部件统计")
@Slf4j
@RestController
@RequestMapping("/outsource/part/")
public class BuOutsourcePartsController {

    private final BuContractInfoService contractInfoService;

    public BuOutsourcePartsController(BuContractInfoService contractInfoService) {
        this.contractInfoService = contractInfoService;
    }


    @GetMapping("page")
    @ApiOperation(value = "委外质保（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceAsset>> page(@Validated BuOutsourceAssetQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceAsset> page = contractInfoService.partsPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceAsset>>().successResult(page);
    }

}

