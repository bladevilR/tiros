package org.jeecg.modules.material.cost.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.vo.CheckQueryVO;
import org.jeecg.modules.material.cost.service.PlanMaterialCheckService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 列计划物料核实 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/15
 */
@Api(tags = "列计划物料核实")
@Slf4j
@RestController
@RequestMapping("/material/plan-cost")
public class PlanMaterialCheckController {

    private final PlanMaterialCheckService planMaterialCheckService;

    public PlanMaterialCheckController(PlanMaterialCheckService planMaterialCheckService) {
        this.planMaterialCheckService = planMaterialCheckService;
    }


//    @GetMapping("/page")
//    @ApiOperation(value = "查询工单物料(分页)")
//    @OperationLog()
//    public Result<IPage<BuWorkOrderMaterial>> pageOrderMaterial(@Validated CheckQueryVO queryVO,
//                                                                @RequestParam(defaultValue = "1") Integer pageNo,
//                                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
//        IPage<BuWorkOrderMaterial> page = planMaterialCheckService.pageOrderMaterial(queryVO, pageNo, pageSize);
//        return new Result<IPage<BuWorkOrderMaterial>>().successResult(page);
//    }
//
//    @GetMapping("/get")
//    @ApiOperation(value = "查询工单物料详情")
//    @OperationLog()
//    public Result<BuWorkOrderMaterial> getOrderMaterialDetail(@RequestParam @ApiParam(value = "工单物料id", required = true) String orderMaterialId) throws Exception {
//        BuWorkOrderMaterial orderMaterial = planMaterialCheckService.getOrderMaterialDetail(orderMaterialId);
//        return new Result<BuWorkOrderMaterial>().successResult(orderMaterial);
//    }
//
//    @PostMapping("/save")
//    @ApiOperation(value = "保存工单物料")
//    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
//    public Result<Boolean> saveOrderMaterial(@RequestBody BuWorkOrderMaterial orderMaterial) throws Exception {
//        boolean flag = planMaterialCheckService.saveOrderMaterial(orderMaterial);
//        return new Result<Boolean>().successResult(flag);
//    }

}
