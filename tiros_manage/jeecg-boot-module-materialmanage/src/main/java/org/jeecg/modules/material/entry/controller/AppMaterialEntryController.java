package org.jeecg.modules.material.entry.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryConfirmVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryQueryVO;
import org.jeecg.modules.material.entry.service.BuMaterialEntryDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 入库单和入库明细 app前端控制器
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
@AppController
@Api(tags = "入库管理-物料")
@Slf4j
@RestController
@RequestMapping("/app/material/entry")
public class AppMaterialEntryController {
    private final BuMaterialEntryDetailService buMaterialEntryDetailService;

    public AppMaterialEntryController(BuMaterialEntryDetailService buMaterialEntryDetailService) {
        this.buMaterialEntryDetailService = buMaterialEntryDetailService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询入库记录(分页)")
    @OperationLog()
    public Result<IPage<BuMaterialEntryDetail>> page(@Validated BuMaterialEntryQueryVO queryVO,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialEntryDetail> page = buMaterialEntryDetailService.pageEntryDetail(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialEntryDetail>>().successResult(page);
    }

    @PostMapping("/confirm")
    @ApiOperation(value = "确认入库")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmEntry(@RequestBody @Validated BuMaterialEntryConfirmVO confirmVO) throws Exception {
        boolean flag = buMaterialEntryDetailService.confirmEntry(confirmVO);
        return new Result<Boolean>().successResult(flag);
    }

}

