package org.jeecg.modules.basemanage.holiday.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.basemanage.holiday.entity.BuBaseHoliday;
import org.jeecg.modules.basemanage.holiday.entity.vo.BuBaseHolidayQueryVO;
import org.jeecg.modules.basemanage.holiday.service.IBuBaseHolidayService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 节假日信息表，用于存放节假日信息，包括要上班的特殊日期 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-06
 */
@Api(tags = "节日管理")
@Slf4j
@RestController
@RequestMapping("/holiday")
public class BuBaseHolidayController {

    private final IBuBaseHolidayService buBaseHolidayService;

    public BuBaseHolidayController(IBuBaseHolidayService buBaseHolidayService) {
        this.buBaseHolidayService = buBaseHolidayService;
    }


    @GetMapping(value = "/list")
    @ApiOperation("查询节日")
    @OperationLog()
    public Result<IPage<BuBaseHoliday>> queryPageList(@Validated BuBaseHolidayQueryVO buBaseHolidayQueryVO,
                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        BuBaseHoliday buBaseHoliday = new BuBaseHoliday();
        buBaseHoliday.setName(buBaseHolidayQueryVO.getName());
        buBaseHoliday.setYear(buBaseHolidayQueryVO.getYear());
        QueryWrapper<BuBaseHoliday> queryWrapper = QueryGenerator.initQueryWrapper(buBaseHoliday, req.getParameterMap());
        queryWrapper.orderByDesc("year");
        queryWrapper.orderByAsc("start_time");
        Page<BuBaseHoliday> page = new Page<BuBaseHoliday>(pageNo, pageSize);
        IPage<BuBaseHoliday> pageList = buBaseHolidayService.page(page, queryWrapper);

        return new Result<IPage<BuBaseHoliday>>().successResult(pageList);
    }

    @PostMapping("/add")
    @ApiOperation("新增节日")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuBaseHoliday buBaseHoliday) throws Exception {
        boolean flag = buBaseHolidayService.saveHoliday(buBaseHoliday);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/edit")
    @ApiOperation("修改节日")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@Validated @RequestBody BuBaseHoliday buBaseHoliday) throws Exception {
        boolean flag = buBaseHolidayService.updateHoliday(buBaseHoliday);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除节日")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "节日id，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buBaseHolidayService.deleteHolidayBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    /*@GetMapping("/addByNetWork")
    @ApiOperation("从网络获取新增节日")
    public Result<Boolean> saveByNetWork(@RequestParam(required = false) @ApiParam(value = "年份，可不传参默认当年") Integer year) throws Exception {
        boolean flag = buBaseHolidayService.saveDayByNetWork(year);
        return new Result<Boolean>().successResult(flag);
    }*/

    @PostMapping("/addByNetWork")
    @ApiOperation("从网络获取新增节日")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3, saveParam = false)
    public Result<Boolean> getJsonVal(@RequestBody @ApiParam(value = "传入JSON格式的数据") Map<String, Object> data) throws Exception {
        boolean flag = buBaseHolidayService.saveDayByNetWork(new JSONObject(data).toJSONString());
        return new Result<Boolean>().successResult(flag);
    }


}
