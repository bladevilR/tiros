package org.jeecg.modules.basemanage.repair.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttribute;
import org.jeecg.modules.basemanage.repair.bean.vo.RepairAttributeQueryVO;
import org.jeecg.modules.basemanage.repair.service.BuRepairAttributeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 检修属性 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
@Api(tags = "检修属性")
@Slf4j
@RestController
@RequestMapping("/base/repair/attr")
public class BuRepairAttributeController {

    private final BuRepairAttributeService buRepairAttributeService;

    public BuRepairAttributeController(BuRepairAttributeService buRepairAttributeService) {
        this.buRepairAttributeService = buRepairAttributeService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    @OperationLog()
    public Result<IPage<BuRepairAttribute>> page(@Validated RepairAttributeQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairAttribute> page = buRepairAttributeService.pageRepairAttribute(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairAttribute>>().successResult(page);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存(新增/修改)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveRepairAttribute(@RequestBody @Validated BuRepairAttribute repairAttribute) throws Exception {
        boolean flag = buRepairAttributeService.saveRepairAttribute(repairAttribute);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "检修属性ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairAttributeService.deleteRepairAttribute(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
