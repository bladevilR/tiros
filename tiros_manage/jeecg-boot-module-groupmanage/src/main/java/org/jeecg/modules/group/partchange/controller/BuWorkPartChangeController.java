package org.jeecg.modules.group.partchange.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.partchange.bean.BuWorkPartChange;
import org.jeecg.modules.group.partchange.bean.vo.BuWorkPartChangeQueryVO;
import org.jeecg.modules.group.partchange.service.BuWorkPartChangeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 部件更换 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Api(tags = "部件更换")
@Slf4j
@RestController
@RequestMapping("/group/partchange")
public class BuWorkPartChangeController {

    private final BuWorkPartChangeService buWorkPartChangeService;

    public BuWorkPartChangeController(BuWorkPartChangeService buWorkPartChangeService) {
        this.buWorkPartChangeService = buWorkPartChangeService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询部件更换记录（分页）")
    @OperationLog()
    public Result<IPage<BuWorkPartChange>> page(@Validated BuWorkPartChangeQueryVO queryVO,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkPartChange> page = buWorkPartChangeService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkPartChange>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询部件更换记录详情")
    @OperationLog()
    public Result<BuWorkPartChange> get(@RequestParam @ApiParam(value = "部件更换记录id", required = true) String id) throws Exception {
        BuWorkPartChange partChange = buWorkPartChangeService.selectById(id);
        return new Result<BuWorkPartChange>().successResult(partChange);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改部件更换记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuWorkPartChange buWorkPartChange) {
        boolean flag = buWorkPartChangeService.updateById(buWorkPartChange);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除部件更换记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "部件更换记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkPartChangeService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

