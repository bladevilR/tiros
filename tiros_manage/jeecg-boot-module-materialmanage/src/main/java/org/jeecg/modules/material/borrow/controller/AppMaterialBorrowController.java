package org.jeecg.modules.material.borrow.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrow;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrowDetail;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowReturnVO;
import org.jeecg.modules.material.borrow.service.BuMaterialBorrowService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 物资借用 App端控制器
 * </p>
 *
 * @author lidafeng
 * @since 2021-03-01
 */
@Api(tags = "借用管理")
@AppController
@Slf4j
@RestController
@RequestMapping("/app/material/borrow")
public class AppMaterialBorrowController {
    private final BuMaterialBorrowService buMaterialBorrowService;

    public AppMaterialBorrowController(BuMaterialBorrowService buMaterialBorrowService) {
        this.buMaterialBorrowService = buMaterialBorrowService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询物资借用记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialBorrow>> page(@Validated BuMaterialBorrowQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialBorrow> page = buMaterialBorrowService.appPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialBorrow>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查看借用记录明细")
    @OperationLog()
    public Result<List<BuMaterialBorrowDetail>> get(@RequestParam @ApiParam(value = "物资借用记录id", required = true) String id) throws Exception {
        List<BuMaterialBorrowDetail> materialBorrowDetailList = buMaterialBorrowService.borrowDetailList(id);
        return new Result<List<BuMaterialBorrowDetail>>().successResult(materialBorrowDetailList);
    }

    @PostMapping("/return")
    @ApiOperation(value = "设置物资借用记录已归还")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setReturn(@RequestBody BuMaterialBorrowReturnVO borrowReturnVO) throws Exception {
        boolean flag = buMaterialBorrowService.setReturn(Arrays.asList(borrowReturnVO));
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/detail/return")
    @ApiOperation(value = "设置物资借用记录明细已归还")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setDetailReturn(@RequestBody BuMaterialBorrowReturnVO borrowReturnVO) throws Exception {
        boolean flag = buMaterialBorrowService.setDetailReturn(borrowReturnVO);
        return new Result<Boolean>().successResult(flag);
    }

}

