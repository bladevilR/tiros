package org.jeecg.modules.material.apply.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.apply.bean.BuMaterialApply;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyConfirmVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyQueryVO;
import org.jeecg.modules.material.apply.service.BuMaterialApplyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 物料申请(领用) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Api(tags = "领用管理")
@Slf4j
@RestController
@RequestMapping("/material/apply")
public class BuMaterialApplyController {

    private final BuMaterialApplyService buMaterialApplyService;

    public BuMaterialApplyController(BuMaterialApplyService buMaterialApplyService) {
        this.buMaterialApplyService = buMaterialApplyService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询物资领用记录(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialApply>> page(@Validated BuMaterialApplyQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialApply> page = buMaterialApplyService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialApply>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增物资领用")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialApply buMaterialApply) throws Exception {
        boolean flag = buMaterialApplyService.saveMaterialApply(buMaterialApply);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改物资领用")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateMaterialApplyById(@RequestBody @Validated BuMaterialApply buMaterialApply) throws Exception {
        boolean flag = buMaterialApplyService.updateMaterialApply(buMaterialApply);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除物资领用(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "物资领用记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialApplyService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/readyConfirm")
    @ApiOperation(value = "发料确认")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> readyConfirm(@RequestBody @Validated BuMaterialApplyConfirmVO confirmVO) throws Exception {
        boolean flag = buMaterialApplyService.readyConfirm(confirmVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/receive-confirm")
    @ApiOperation(value = "领料确认")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> receiveConfirm(@RequestParam @ApiParam(value = "ids类型 1托盘id 2领用明细id", required = true) Integer fromType,
                                        @RequestParam @ApiParam(value = "领用明细/托盘ids，多个逗号分隔", required = true) String ids,
                                        @RequestParam(required = false) @ApiParam(value = "是否只保存 true=只保存（只保存时不进行修改班组库存、消耗到maximo、更新统计数据等操作），false=保存加提交") Boolean onlySave) throws Exception {
        boolean flag = buMaterialApplyService.receiveConfirm(fromType, ids, onlySave);
        return new Result<Boolean>().successResult(flag);
    }

}

