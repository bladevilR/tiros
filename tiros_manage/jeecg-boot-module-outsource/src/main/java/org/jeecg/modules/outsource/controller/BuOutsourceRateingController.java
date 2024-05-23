package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuOutsourceRateing;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceRateingQueryVO;
import org.jeecg.modules.outsource.service.BuOutsourceRateingAnnexService;
import org.jeecg.modules.outsource.service.BuOutsourceRateingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 委外评分 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "委外评分")
@Slf4j
@RestController
@RequestMapping("/outsource/rateing/")
public class BuOutsourceRateingController {

    private final BuOutsourceRateingService rateingService;
    private final BuOutsourceRateingAnnexService annexService;

    public BuOutsourceRateingController(BuOutsourceRateingService rateingService, BuOutsourceRateingAnnexService annexService) {
        this.rateingService = rateingService;
        this.annexService = annexService;
    }


    @GetMapping("page")
    @ApiOperation(value = "查询委外评分（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceRateing>> page(@Validated BuOutsourceRateingQueryVO queryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceRateing> page = rateingService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceRateing>>().successResult(page);
    }

    @PostMapping("add")
    @ApiOperation(value = "添加委外评分")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuOutsourceRateing outsourceRateing) throws Exception {
        Boolean flag = rateingService.saveRateing(outsourceRateing);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改委外评分")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuOutsourceRateing outsourceRateing) throws Exception {
        Boolean flag = rateingService.editRateing(outsourceRateing);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除委外评分（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "评分ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = rateingService.removeBatch(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete/attachment")
    @ApiOperation(value = "删除委外评分附件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchAttachment(@RequestParam @ApiParam(value = "附件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = annexService.removeBatch(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}

