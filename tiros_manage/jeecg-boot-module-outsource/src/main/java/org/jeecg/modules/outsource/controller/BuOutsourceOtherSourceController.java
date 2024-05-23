package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.outsource.bean.BuOutsourceOtherSource;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;
import org.jeecg.modules.outsource.service.BuOutsourceOtherSourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 委外文档资料，设备质量保证书、测试报告、监修日志、过程记录、质量报告 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Api(tags = "委外文档资料")
@Slf4j
@RestController
@RequestMapping("/outsource/otherSource/")
public class BuOutsourceOtherSourceController {

    private final BuOutsourceOtherSourceService otherSourceService;

    public BuOutsourceOtherSourceController(BuOutsourceOtherSourceService otherSourceService) {
        this.otherSourceService = otherSourceService;
    }


    @GetMapping("page")
    @ApiOperation(value = "查询委外文档资料（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceOtherSource>> page(@Validated BuOutsourceResourceQueryVO queryVO,
                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceOtherSource> page = otherSourceService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceOtherSource>>().successResult(page);
    }

    @PostMapping("add")
    @ApiOperation(value = "添加委外文档资料")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated List<BuOutsourceOtherSource> otherSource) throws Exception {
        Boolean saveFlag = otherSourceService.saveBatchOtherSource(otherSource);
        return new Result<Boolean>().successResult(saveFlag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改委外文档资料")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuOutsourceOtherSource otherSource) {
        Boolean flag = otherSourceService.updateById(otherSource);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除委外文档资料（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "文档资料ids，多个逗号分隔", required = true) String ids) {
        boolean flag = otherSourceService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}

