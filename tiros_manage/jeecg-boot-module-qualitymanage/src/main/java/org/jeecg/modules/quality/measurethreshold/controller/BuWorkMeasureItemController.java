package org.jeecg.modules.quality.measurethreshold.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quality.measurethreshold.bean.BuWorkMeasureItem;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuWorkMeasureItemQueryVO;
import org.jeecg.modules.quality.measurethreshold.service.BuWorkMeasureItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 测量项定义 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Api(tags = "测量阈值设置")
@Slf4j
@RestController
@RequestMapping("/quality/measurethreshold/threshold")
public class BuWorkMeasureItemController {

    private final BuWorkMeasureItemService buWorkMeasureItemService;

    public BuWorkMeasureItemController(BuWorkMeasureItemService buWorkMeasureItemService) {
        this.buWorkMeasureItemService = buWorkMeasureItemService;
    }


    @GetMapping("/list/{formId}")
    @ApiOperation(value = "根据表单id获取测量项")
    @OperationLog()
    public Result<List<BuWorkMeasureItem>> listMeasureItemByFormId(@PathVariable @ApiParam(value = "在线表单(数据记录表)id") String formId) throws Exception {
        List<BuWorkMeasureItem> measureItemList = buWorkMeasureItemService.listMeasureItemByFormId(formId);
        return new Result<List<BuWorkMeasureItem>>().successResult(measureItemList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取表单指定行和列的测量项")
    @OperationLog()
    public Result<BuWorkMeasureItem> getMeasureItemByFormIdAndRow(BuWorkMeasureItemQueryVO queryVO) throws Exception {
        BuWorkMeasureItem measureItem = buWorkMeasureItemService.getMeasureItemByFormIdAndRow(queryVO);
        return new Result<BuWorkMeasureItem>().successResult(measureItem);
    }

    @PostMapping("/save")
    @ApiOperation(value = "设置测量项")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuWorkMeasureItem buWorkMeasureItem) throws Exception {
        boolean flag = buWorkMeasureItemService.saveMeasureItem(buWorkMeasureItem);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改测量项")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuWorkMeasureItem buWorkMeasureItem) throws Exception {
        boolean flag = buWorkMeasureItemService.updateMeasureItem(buWorkMeasureItem);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除测量项")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> delete(@RequestParam(name = "id") @ApiParam(name = "id", value = "测量项id", required = true) String id) {
        boolean flag = buWorkMeasureItemService.removeById(id);
        return new Result<Boolean>().successResult(flag);
    }

}

