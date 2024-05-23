package org.jeecg.modules.quality.recordconfirm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormDataRecord;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormWorkRecord;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormCheckRecordVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormInstanceVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormWorkRecordCheckVO;
import org.jeecg.modules.quality.recordconfirm.service.BuPlanFormCheckRecordQualityService;
import org.jeecg.modules.quality.recordconfirm.service.BuPlanFormWorkRecordQualityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 作业检查 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/11
 */
@Api(tags = "作业检查")
@Slf4j
@RestController
@RequestMapping("/quality/record-confirm")
public class BuOrderRecordConfirmController {

    private final BuPlanFormWorkRecordQualityService buPlanFormWorkRecordQualityService;
    private final BuPlanFormCheckRecordQualityService buPlanFormCheckRecordQualityService;

    public BuOrderRecordConfirmController(BuPlanFormWorkRecordQualityService buPlanFormWorkRecordQualityService,
                                          BuPlanFormCheckRecordQualityService buPlanFormCheckRecordQualityService) {
        this.buPlanFormWorkRecordQualityService = buPlanFormWorkRecordQualityService;
        this.buPlanFormCheckRecordQualityService = buPlanFormCheckRecordQualityService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询表单实例(分页)")
    @OperationLog()
    public Result<IPage<BuFormInstanceVO>> pageFormInstanceRecord(@Validated BuFormRecordQueryVO queryVO,
                                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFormInstanceVO> page = buPlanFormWorkRecordQualityService.pageFormInstanceRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFormInstanceVO>>().successResult(page);
    }

    @GetMapping("/page/check-record")
    @ApiOperation(value = "查询检查表单(分页)")
    @OperationLog()
    public Result<IPage<BuFormCheckRecordVO>> pageFormCheckRecord(@Validated BuFormRecordQueryVO queryVO,
                                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuFormCheckRecordVO> page = buPlanFormCheckRecordQualityService.pageFormCheckRecord(queryVO, pageNo, pageSize);
        return new Result<IPage<BuFormCheckRecordVO>>().successResult(page);
    }

    @GetMapping("/work-record/get")
    @ApiOperation(value = "根据记录表实列id查询详情(记录、分类、明细、检查、计量器具)")
    @OperationLog()
    public Result<BuPlanFormWorkRecord> getFormWorkRecordById(@RequestParam @ApiParam(value = "记录表实例id", required = true) String recordFormId) throws Exception {
        BuPlanFormWorkRecord workOrderRecord = buPlanFormWorkRecordQualityService.getFormWorkRecordById(recordFormId);
        return new Result<BuPlanFormWorkRecord>().successResult(workOrderRecord);
    }

    @GetMapping("/online-form/get")
    @ApiOperation(value = "根据数据表实列id查询")
    @OperationLog()
    public Result<BuPlanFormDataRecord> getFormDataRecordFormById(@RequestParam @ApiParam(value = "在线表单(数据表)实列id", required = true) String dataFormId) throws Exception {
        BuPlanFormDataRecord formDataRecord = buPlanFormWorkRecordQualityService.getFormDataRecordById(dataFormId);
        return new Result<BuPlanFormDataRecord>().successResult(formDataRecord);
    }

    @PostMapping("/confirm")
    @ApiOperation(value = "检查确认记录/数据表")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> checkWorkOrderRecord(@RequestBody @Validated BuFormWorkRecordCheckVO checkVO) throws Exception {
        boolean flag = buPlanFormWorkRecordQualityService.checkFormWorkRecord(checkVO);
        return new Result<Boolean>().successResult(flag);
    }

}