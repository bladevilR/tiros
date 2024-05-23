package org.jeecg.modules.outsource.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutin;
import org.jeecg.modules.dispatch.workorder.bean.BuOutsourceOutinDetail;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOutsourceOutinQueryVO;
import org.jeecg.modules.dispatch.workorder.service.BuOutsourceOutinService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 委外出入库单(交接) 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
@Api(tags = "委外交接单")
@Slf4j
@RestController
@RequestMapping("/outsource/outin")
public class BuOutsourceOutinController {

    private final BuOutsourceOutinService buOutsourceOutinService;

    public BuOutsourceOutinController(BuOutsourceOutinService buOutsourceOutinService) {
        this.buOutsourceOutinService = buOutsourceOutinService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "交接单-查询（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceOutin>> pageOutIn(@Validated BuOutsourceOutinQueryVO queryVO,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuOutsourceOutin> page = buOutsourceOutinService.pageOutIn(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceOutin>>().successResult(page);
    }

    @GetMapping("/page/detail")
    @ApiOperation(value = "交接单明细-查询（分页）")
    @OperationLog()
    public Result<IPage<BuOutsourceOutinDetail>> pageOutInDetail(@Validated BuOutsourceOutinQueryVO queryVO,
                                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuOutsourceOutinDetail> page = buOutsourceOutinService.pageOutInDetail(queryVO, pageNo, pageSize);
        return new Result<IPage<BuOutsourceOutinDetail>>().successResult(page);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "交接单-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteOutinBatch(@RequestParam @ApiParam(value = "交接单ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buOutsourceOutinService.deleteOutinBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/detail/delete")
    @ApiOperation(value = "交接单明细-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteOutinDetailBatch(@RequestParam @ApiParam(value = "交接单明细ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buOutsourceOutinService.deleteOutinDetailBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

