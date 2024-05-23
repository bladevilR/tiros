package org.jeecg.modules.material.entry.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryConfirmVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryLevelFourDetailVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryQueryVO;
import org.jeecg.modules.material.entry.service.BuMaterialEntryDetailService;
import org.jeecg.modules.material.entry.service.BuMaterialEntryOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 入库单和入库明细 前端控制器
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@Api(tags = "入库管理")
@Slf4j
@RestController
@RequestMapping("/material/entry")
public class BuMaterialEntryController {

    private final BuMaterialEntryOrderService buMaterialEntryOrderService;
    private final BuMaterialEntryDetailService buMaterialEntryDetailService;

    public BuMaterialEntryController(BuMaterialEntryOrderService buMaterialEntryOrderService,
                                     BuMaterialEntryDetailService buMaterialEntryDetailService) {
        this.buMaterialEntryOrderService = buMaterialEntryOrderService;
        this.buMaterialEntryDetailService = buMaterialEntryDetailService;
    }


    @GetMapping("/get")
    @ApiOperation(value = "入库单-根据id查询入库单及所有明细")
    @OperationLog()
    public Result<BuMaterialEntryOrder> getEntryOrder(@RequestParam @ApiParam(value = "入库单id", required = true) String entryOrderId) throws Exception {
        BuMaterialEntryOrder entryOrder = buMaterialEntryOrderService.getEntryOrderById(entryOrderId);
        return new Result<BuMaterialEntryOrder>().successResult(entryOrder);
    }

    @PostMapping("/save")
    @ApiOperation(value = "入库单-新增/修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialEntryOrder entryOrder) throws Exception {
        boolean flag = buMaterialEntryOrderService.saveEntryOrder(entryOrder);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/detail/page")
    @ApiOperation(value = "明细-查询(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialEntryDetail>> page(@Validated BuMaterialEntryQueryVO queryVO,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialEntryDetail> page = buMaterialEntryDetailService.pageEntryDetail(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialEntryDetail>>().successResult(page);
    }

    @GetMapping("/detail/get")
    @ApiOperation(value = "明细--根据id查询")
    @OperationLog()
    public Result<BuMaterialEntryDetail> getEntryDetail(@RequestParam @ApiParam(value = "入库明细id", required = true) String entryDetailId) throws Exception {
        BuMaterialEntryDetail entryDetail = buMaterialEntryDetailService.getEntryDetailById(entryDetailId);
        return new Result<BuMaterialEntryDetail>().successResult(entryDetail);
    }

    @PostMapping("/detail/save")
    @ApiOperation(value = "明细-新增/修改", notes = "新增时必须传入entryOrderId入库单id")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveEntryDetail(@RequestBody @Validated BuMaterialEntryDetail entryDetail) throws Exception {
        boolean flag = buMaterialEntryDetailService.saveEntryDetail(entryDetail);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/detail/delete")
    @ApiOperation(value = "明细-删除(批量)", notes = "当入库单下没有明细数据时删除入库单")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteDetailBatch(@RequestParam @ApiParam(value = "入库明细ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialEntryDetailService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/detail/confirm")
    @ApiOperation(value = "明细-确认入库明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmEntryDetail(@RequestBody @Validated BuMaterialEntryConfirmVO confirmVO) throws Exception {
        boolean flag = buMaterialEntryDetailService.confirmEntry(confirmVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/confirm/levelFour")
    @ApiOperation(value = "明细-确认入库第四级levelFour库位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmEntryLevelFourDetail(@RequestBody @Validated BuMaterialEntryLevelFourDetailVO confirmVO) throws Exception {
        boolean flag = buMaterialEntryDetailService.confirmEntryLevelFourWarehouse(confirmVO);
        return new Result<Boolean>().successResult(flag);
    }
}

