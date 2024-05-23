package org.jeecg.modules.basemanage.regu.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguInfo;
import org.jeecg.modules.basemanage.regu.bean.ImportTechBook;
import org.jeecg.modules.basemanage.regu.bean.vo.RepairReguDetailQueryVO;
import org.jeecg.modules.basemanage.regu.service.BuRepairReguDetailService;
import org.jeecg.modules.basemanage.regu.service.BuRepairReguInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 规程信息 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
@Api(tags = "架修规程管理")
@Slf4j
@RestController
@RequestMapping("/regu/reguInfo")
public class BuRepairReguInfoController {

    private final BuRepairReguInfoService buRepairReguInfoService;
    private final BuRepairReguDetailService buRepairReguDetailService;

    public BuRepairReguInfoController(BuRepairReguDetailService buRepairReguDetailService,
                                      BuRepairReguInfoService buRepairReguInfoService) {
        this.buRepairReguDetailService = buRepairReguDetailService;
        this.buRepairReguInfoService = buRepairReguInfoService;
    }


    @GetMapping("/list/reguInfo")
    @ApiOperation("查询规程列表")
    @OperationLog()
    public Result<List<BuRepairReguInfo>> listReguInfo(@RequestParam(required = false) @ApiParam(value = "车型id") String trainTypeId) throws Exception {
        List<BuRepairReguInfo> reguInfos = buRepairReguInfoService.listReguInfo(trainTypeId);
        return new Result<List<BuRepairReguInfo>>().successResult(reguInfos);
    }

    @GetMapping("/get/{reguInfoId}")
    @ApiOperation("根据id查询规程")
    @OperationLog()
    public Result<BuRepairReguInfo> getReGuInfo(@PathVariable @ApiParam(value = "规程id", required = true) String reguInfoId) throws Exception {
        BuRepairReguInfo reguInfo = buRepairReguInfoService.getReguInfoById(reguInfoId);
        return new Result<BuRepairReguInfo>().successResult(reguInfo);
    }

    @PostMapping("/copy/reguInfo/{reguInfoId}")
    @ApiOperation("复制规程")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> copyReGuInfo(@PathVariable @ApiParam(value = "需复制的规程id", required = true) String reguInfoId) throws Exception {
        boolean flag = buRepairReguInfoService.copyReguInfoByReguInfoId(reguInfoId);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/list/reguDetail")
    @ApiOperation("查询规程明细列表")
    @OperationLog()
    public Result<List<BuRepairReguDetail>> listReguDetail(@Validated RepairReguDetailQueryVO detailQueryVO) throws Exception {
        List<BuRepairReguDetail> reguDetails = buRepairReguDetailService.listReguDetail(detailQueryVO);
        return new Result<List<BuRepairReguDetail>>().successResult(reguDetails);
    }

    @GetMapping("/detail/get")
    @ApiOperation("根据id查询规程明细及关联信息")
    @OperationLog()
    public Result<BuRepairReguDetail> getReguDetail(@RequestParam @ApiParam(value = "规程明细id", required = true) String id) throws Exception {
        BuRepairReguDetail reguDetail = buRepairReguDetailService.getReguDetailById(id);
        return new Result<BuRepairReguDetail>().successResult(reguDetail);
    }

    @PostMapping("/saveOrUpdate/reguInfo")
    @ApiOperation("新增或修改规程")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdateReguInfo(@Validated @RequestBody BuRepairReguInfo reguInfo) throws Exception {
        boolean flag = buRepairReguInfoService.saveReguInfo(reguInfo);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除规程")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteReguInfo(@PathVariable @ApiParam(value = "规程id") String id) throws Exception {
        boolean relativeWithTpPlan = buRepairReguInfoService.isRelativeWithTpPlan(id);
        if (relativeWithTpPlan) {
            throw new JeecgBootException("规程已关联计划模板，不能删除");
        }
        boolean flag2 = buRepairReguDetailService.deleteByReguId(id);
        boolean flag1 = buRepairReguInfoService.deleteById(id);
        return new Result<Boolean>().successResult(flag1 && flag2);
    }

    @PostMapping("/saveOrUpdate/reguDetail")
    @ApiOperation("新增或者修改规程明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveOrUpdateReguDetail(@Validated @RequestBody BuRepairReguDetail reguDetail) throws Exception {
        boolean flag = buRepairReguDetailService.saveReguDetail(reguDetail);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/deleteBatch/reguDetail")
    @ApiOperation("批量删除规程明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatchReguDetail(@RequestParam @ApiParam(value = "规程明细信息ids，多个逗号分割") String ids) throws Exception {
        boolean flag = buRepairReguDetailService.deleteBatchByDetailIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/importTechBook")
    @ApiOperation("规程作业项导入作业指导书")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> importTechBook(@Validated @RequestBody ImportTechBook importTechBook) throws Exception {
        boolean flag = buRepairReguDetailService.importTechBook(importTechBook);
        return new Result<Boolean>().successResult(flag);
    }

}
