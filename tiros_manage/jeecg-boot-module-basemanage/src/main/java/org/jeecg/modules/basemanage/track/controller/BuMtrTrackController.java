package org.jeecg.modules.basemanage.track.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.track.entity.BuMtrTrack;
import org.jeecg.modules.basemanage.track.entity.vo.BuMtrTrackQueryVO;
import org.jeecg.modules.basemanage.track.service.IBuMtrTrackService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 股道信息 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
@Slf4j
@Api(tags = "股道信息")
@RestController
@RequestMapping("/track")
public class BuMtrTrackController {

    private final IBuMtrTrackService buMtrTrackService;

    public BuMtrTrackController(IBuMtrTrackService buMtrTrackService) {
        this.buMtrTrackService = buMtrTrackService;
    }


    @GetMapping(value = "/list")
    @ApiOperation("查询股道信息")
    @OperationLog()
    public Result<Page<BuMtrTrack>> queryPageList(@Validated BuMtrTrackQueryVO trackQueryVO,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuMtrTrack> page = buMtrTrackService.selectTrackPage(new Page<>(pageNo, pageSize), trackQueryVO);
        return new Result<Page<BuMtrTrack>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation("新增股道信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuMtrTrack buMtrTrack) throws Exception {
        boolean flag = buMtrTrackService.saveTrack(buMtrTrack);
        return new Result<Boolean>().successResult(flag);
    }


    @PutMapping("/edit")
    @ApiOperation("修改股道信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@Validated @RequestBody BuMtrTrack buMtrTrack) throws Exception {
        boolean flag = buMtrTrackService.updateTrack(buMtrTrack);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除股道信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "股道ids，多个逗号分隔") String ids) throws Exception {
        boolean flag = buMtrTrackService.deleteBatchByTrackIds(ids);
        return new Result<Boolean>().successResult(flag);
    }

}
