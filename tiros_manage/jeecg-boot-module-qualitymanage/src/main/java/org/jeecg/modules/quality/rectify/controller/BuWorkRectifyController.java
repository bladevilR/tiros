package org.jeecg.modules.quality.rectify.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.modules.quality.rectify.bean.BuWorkRectify;
import org.jeecg.modules.quality.rectify.bean.vo.BuWorkRectifyQueryVO;
import org.jeecg.modules.quality.rectify.service.BuWorkRectifyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 整改信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Api(tags = "整改管理")
@Slf4j
@RestController
@RequestMapping("/quality/rectify")
public class BuWorkRectifyController {

    private final BuWorkRectifyService buWorkRectifyService;

    private final SerialNumberGenerate serialNumberGenerate;

    public BuWorkRectifyController(BuWorkRectifyService buWorkRectifyService, SerialNumberGenerate serialNumberGenerate) {
        this.buWorkRectifyService = buWorkRectifyService;
        this.serialNumberGenerate = serialNumberGenerate;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询整改记录(分页)")
    @OperationLog()
    public Result<IPage<BuWorkRectify>> page(@Validated BuWorkRectifyQueryVO queryVO,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkRectify> page = buWorkRectifyService.pageRectify(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkRectify>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询整改信息")
    @OperationLog()
    public Result<BuWorkRectify> getRectifyById(@RequestParam @ApiParam(value = "整改信息id", required = true) String id) throws Exception {
        BuWorkRectify rectify = buWorkRectifyService.getRectifyById(id);
        return new Result<BuWorkRectify>().successResult(rectify);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增整改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuWorkRectify buWorkRectify) throws Exception {
        boolean flag = buWorkRectifyService.saveRectify(buWorkRectify);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改整改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuWorkRectify buWorkRectify) throws Exception {
        boolean flag = buWorkRectifyService.updateRectify(buWorkRectify);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除整改(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "整改信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkRectifyService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/close")
    @ApiOperation(value = "关闭整改(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> closeBatch(@RequestParam @ApiParam(value = "整改信息ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkRectifyService.closeBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/getCode")
    @ApiOperation(value = "获取整改随机编码")
    @OperationLog()
    public Result<String> getCode() {
        return new Result<String>().successResult(serialNumberGenerate.generateSerialNumberByCode("JDXZG"));
    }

}

