package org.jeecg.modules.basemanage.techbook.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetail;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * app作业指导书(工艺) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@AppController
@Api(tags = "作业指导书")
@Slf4j
@RestController
@RequestMapping("/app/base/tech-book")
public class AppRepairTechBookController {

    private final BuRepairTechBookDetailService buRepairTechBookDetailService;

    public AppRepairTechBookController(BuRepairTechBookDetailService buRepairTechBookDetailService) {
        this.buRepairTechBookDetailService = buRepairTechBookDetailService;
    }


    @GetMapping("/detail/page")
    @ApiOperation(value = "指导书明细-根据指导书id查明细")
    @OperationLog()
    public Result<List<BuRepairTechBookDetail>> listDetail(@RequestParam @ApiParam(value = "作业指导书id", required = true) String bookId) throws Exception {
        List<BuRepairTechBookDetail> detailList = buRepairTechBookDetailService.listDetail(bookId);
        return new Result<List<BuRepairTechBookDetail>>().successResult(detailList);
    }

    @GetMapping("/detail/get")
    @ApiOperation(value = "指导书明细-根据明细id查询明细及关联信息")
    @OperationLog()
    public Result<BuRepairTechBookDetail> getDetailAndRelation(@RequestParam @ApiParam(value = "指导书明细id", required = true) String detailId) throws Exception {
        BuRepairTechBookDetail detail = buRepairTechBookDetailService.getDetailAndRelation(detailId);
        return new Result<BuRepairTechBookDetail>().successResult(detail);
    }

}

