package org.jeecg.modules.material.returned.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.returned.bean.BuMaterialReturned;
import org.jeecg.modules.material.returned.bean.vo.BuMaterialReturnedQueryVO;
import org.jeecg.modules.material.returned.service.BuMaterialReturnedService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 退料 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Api(tags = "退料管理")
@Slf4j
@RestController
@RequestMapping("/material/returned")
public class BuMaterialReturnedController {

    private final BuMaterialReturnedService buMaterialReturnedService;

    public BuMaterialReturnedController(BuMaterialReturnedService buMaterialReturnedService) {
        this.buMaterialReturnedService = buMaterialReturnedService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询退料单记录(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialReturned>> pageReturned(@Validated BuMaterialReturnedQueryVO queryVO,
                                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialReturned> page = buMaterialReturnedService.pageReturned(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialReturned>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询退料单及明细")
    @OperationLog()
    public Result<BuMaterialReturned> getReturnedById(@RequestParam @ApiParam(value = "退料单id", required = true) String id) throws Exception {
        BuMaterialReturned returned = buMaterialReturnedService.getReturnedById(id);
        return new Result<BuMaterialReturned>().successResult(returned);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增/修改退料单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialReturned returned) throws Exception {
        boolean flag = buMaterialReturnedService.saveReturned(returned);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除退料单(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "退料单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialReturnedService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/confirm")
    @ApiOperation(value = "确认退料(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmBatch(@RequestParam @ApiParam(value = "退料单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialReturnedService.confirmBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/confirm-onlyToEbs")
    @ApiOperation(value = "确认退料(批量)（不扣减班组库存不处理退料回写）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmBatchOnlyToEbs(@RequestParam @ApiParam(value = "退料单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialReturnedService.confirmBatchOnlyToEbs(ids);
        return new Result<Boolean>().successResult(flag);
    }

}