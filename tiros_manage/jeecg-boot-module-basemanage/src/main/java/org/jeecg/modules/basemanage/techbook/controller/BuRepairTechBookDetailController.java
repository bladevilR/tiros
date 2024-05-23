package org.jeecg.modules.basemanage.techbook.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetail;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 作业指导书(工艺)明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Api(tags = "作业指导书")
@Slf4j
@RestController
@RequestMapping("/base/tech-book/detail")
public class BuRepairTechBookDetailController {

    private final BuRepairTechBookDetailService buRepairTechBookDetailService;

    public BuRepairTechBookDetailController(BuRepairTechBookDetailService buRepairTechBookDetailService) {
        this.buRepairTechBookDetailService = buRepairTechBookDetailService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "指导书明细-根据指导书id查明细")
    @OperationLog()
    public Result<List<BuRepairTechBookDetail>> listDetail(@RequestParam @ApiParam(value = "作业指导书id", required = true) String bookId) throws Exception {
        List<BuRepairTechBookDetail> detailList = buRepairTechBookDetailService.listDetail(bookId);
        return new Result<List<BuRepairTechBookDetail>>().successResult(detailList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "指导书明细-根据明细id查询明细及关联信息")
    @OperationLog()
    public Result<BuRepairTechBookDetail> getDetailAndRelation(@RequestParam @ApiParam(value = "指导书明细id", required = true) String detailId) throws Exception {
        BuRepairTechBookDetail detail = buRepairTechBookDetailService.getDetailAndRelation(detailId);
        return new Result<BuRepairTechBookDetail>().successResult(detail);
    }

    @PostMapping("/save")
    @ApiOperation(value = "指导书明细-新增/修改", notes = "包括物料及工器具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7, saveParam = false)
    public Result<Boolean> save(@RequestBody @Validated BuRepairTechBookDetail detail) throws Exception {
        boolean flag = buRepairTechBookDetailService.saveDetail(detail);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "指导书明细-删除(批量)", notes = "包括物料及工器具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "指导书明细ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairTechBookDetailService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

