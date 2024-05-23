package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.storage.AliyunStorage;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceRepairRecord;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourcePerformQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.DelayReasonVO;
import org.jeecg.modules.dispatch.workorder.service.BuOutsourceOutinService;
import org.jeecg.modules.outsource.bean.BuOutsourceRateingAnnex;
import org.jeecg.modules.outsource.bean.BuOutsourceResource;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;
import org.jeecg.modules.outsource.bean.vo.SystemContractVO;
import org.jeecg.modules.outsource.service.BuBaseSupplierService;
import org.jeecg.modules.outsource.service.BuOutsourceRateingAnnexService;
import org.jeecg.modules.outsource.service.BuOutsourceResourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 委外执行前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
@Api(tags = "委外执行")
@Slf4j
@RestController
@RequestMapping("/outsource/perform")
public class BuOutsourcePerformController {

    private final BuOutsourceOutinService buOutsourceOutinService;
    private final BuBaseSupplierService buBaseSupplierService;
    private final BuOutsourceResourceService buOutsourceResourceService;
    private final BuOutsourceRateingAnnexService buOutsourceRateingAnnexService;

    public BuOutsourcePerformController(BuOutsourceOutinService buOutsourceOutinService,
                                        BuBaseSupplierService buBaseSupplierService,
                                        BuOutsourceResourceService buOutsourceResourceService,
                                        BuOutsourceRateingAnnexService buOutsourceRateingAnnexService) {
        this.buOutsourceOutinService = buOutsourceOutinService;
        this.buBaseSupplierService = buBaseSupplierService;
        this.buOutsourceResourceService = buOutsourceResourceService;
        this.buOutsourceRateingAnnexService = buOutsourceRateingAnnexService;
    }


    @GetMapping("/vendor/tree")
    @ApiOperation(value = "查询厂商树(系统-合同)")
    @OperationLog()
    public Result<List<SystemContractVO>> vendorTree(@RequestParam @ApiParam(value = "车型id", required = true) String trainTypeId) throws Exception {
        List<SystemContractVO> systemContractVOList = buBaseSupplierService.vendorTree(trainTypeId);
        return new Result<List<SystemContractVO>>().successResult(systemContractVOList);
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询执行(分页)")
    @OperationLog()
    public Result<IPage<BuOutsourceRepairRecord>> page(@Validated BuOutsourcePerformQueryVO queryVO,
                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceRepairRecord> page = buOutsourceOutinService.repairRecordPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceRepairRecord>>().successResult(page);
    }

    @PostMapping("/delayReason")
    @ApiOperation(value = "设置逾期原因")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> delayReason(@RequestBody @Validated DelayReasonVO delayReason) {
        boolean flag = buOutsourceOutinService.settingDelayReason(delayReason);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/resource")
    @ApiOperation(value = "查询委外过程资料(分页)")
    @OperationLog()
    public Result<IPage<BuOutsourceResource>> resourcePage(@Validated BuOutsourceResourceQueryVO queryVO,
                                                           @RequestParam(defaultValue = "1") Integer pageNo,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuOutsourceResource> page = buOutsourceResourceService.resourcePage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceResource>>().successResult(page);
    }

    @PostMapping("/resource/add")
    @ApiOperation(value = "添加委外过程资料(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> resourceAdd(@RequestBody @Validated List<BuOutsourceResource> outsourceResources) throws Exception {
        boolean flag = buOutsourceResourceService.saveBatchResource(outsourceResources);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/rate/addFile")
    @ApiOperation(value = "添加评分附件(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> rateAddFile(@RequestBody @Validated List<BuOutsourceRateingAnnex> rateingAnnexes) {

        Boolean saveFlag = buOutsourceRateingAnnexService.saveBatch(rateingAnnexes);
        return new Result<Boolean>().successResult(saveFlag);
    }

    @PostMapping("/resource/edit")
    @ApiOperation(value = "修改委外过程资料")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> resourceEdit(@RequestBody @Validated BuOutsourceResource outsourceResources) {
        Boolean saveFlag = buOutsourceResourceService.saveOrUpdate(outsourceResources);
        return new Result<Boolean>().successResult(saveFlag);
    }

    @PostMapping("/delete/resource")
    @ApiOperation(value = "删除委外过程资料(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "资料ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buOutsourceResourceService.removeByIds(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "上传资料")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2, saveParam = false)
    public Result<List<String>> upload(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        List<String> saveUrl = new ArrayList<>();
        for (MultipartFile file : files) {
            String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());
            saveUrl.add(AliyunStorage.storeFile(file, baseName));
        }
        return new Result<List<String>>().successResult(saveUrl);
    }

}

