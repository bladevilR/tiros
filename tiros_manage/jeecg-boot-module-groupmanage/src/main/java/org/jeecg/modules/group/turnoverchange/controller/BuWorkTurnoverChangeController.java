package org.jeecg.modules.group.turnoverchange.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.turnoverchange.bean.BuWorkTurnoverChange;
import org.jeecg.modules.group.turnoverchange.bean.vo.BuWorkTurnoverChangeQueryVO;
import org.jeecg.modules.group.turnoverchange.service.BuWorkTurnoverChangeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 周转件更换，如果换上件没有记录，应先建立记录 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Api(tags = "周转件更换")
@Slf4j
@RestController
@RequestMapping("/group/turnoverchange")
public class BuWorkTurnoverChangeController {

    private final BuWorkTurnoverChangeService buWorkTurnoverChangeService;

    public BuWorkTurnoverChangeController(BuWorkTurnoverChangeService buWorkTurnoverChangeService) {
        this.buWorkTurnoverChangeService = buWorkTurnoverChangeService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询周转件更换记录（分页）")
    @OperationLog()
    public Result<IPage<BuWorkTurnoverChange>> page(@Validated BuWorkTurnoverChangeQueryVO queryVO,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuWorkTurnoverChange> page = buWorkTurnoverChangeService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuWorkTurnoverChange>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据id查询周转件更换记录详情")
    @OperationLog()
    public Result<BuWorkTurnoverChange> get(@RequestParam @ApiParam(value = "周转件更换记录id", required = true) String id) throws Exception {
        BuWorkTurnoverChange turnoverChange = buWorkTurnoverChangeService.selectById(id);
        return new Result<BuWorkTurnoverChange>().successResult(turnoverChange);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改周转件更换记录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updatePlanFar(@RequestBody @Validated BuWorkTurnoverChange buWorkTurnoverChange) {
        boolean flag = buWorkTurnoverChangeService.updateById(buWorkTurnoverChange);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除周转件更换记录（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "周转件更换记录ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buWorkTurnoverChangeService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

