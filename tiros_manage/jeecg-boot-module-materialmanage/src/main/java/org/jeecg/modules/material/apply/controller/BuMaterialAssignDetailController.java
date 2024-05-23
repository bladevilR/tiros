package org.jeecg.modules.material.apply.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAssignSaveVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAutoAssignResultVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAutoAssignVO;
import org.jeecg.modules.material.apply.service.BuMaterialAssignDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 物料分配明细 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Api(tags = "领用管理")
@Slf4j
@RestController
@RequestMapping("/material/apply/assign")
public class BuMaterialAssignDetailController {

    private final BuMaterialAssignDetailService buMaterialAssignDetailService;

    public BuMaterialAssignDetailController(BuMaterialAssignDetailService buMaterialAssignDetailService) {
        this.buMaterialAssignDetailService = buMaterialAssignDetailService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "分配明细-查询(列表)")
    @OperationLog()
    public Result<List<BuMaterialAssignDetail>> list(@RequestParam @ApiParam(value = "领用明细id", required = true) String applyDetailId) throws Exception {
        List<BuMaterialAssignDetail> list = buMaterialAssignDetailService.listAssignDetail(applyDetailId);
        return new Result<List<BuMaterialAssignDetail>>().successResult(list);
    }

    @PostMapping("/auto-assign")
    @ApiOperation(value = "自动分配物料（只生成分配明细，不保存）")
    @OperationLog()
    public Result<BuMaterialAutoAssignResultVO> autoAssignAndSave(@RequestBody @Validated BuMaterialAutoAssignVO autoAssignVO) throws Exception {
        BuMaterialAutoAssignResultVO assignResultVO = buMaterialAssignDetailService.autoAssignAndSave(autoAssignVO);
        return new Result<BuMaterialAutoAssignResultVO>().successResult(assignResultVO);
    }

}
