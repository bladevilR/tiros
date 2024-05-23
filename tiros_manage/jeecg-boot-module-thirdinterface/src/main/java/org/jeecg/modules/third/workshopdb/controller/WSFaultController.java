package org.jeecg.modules.third.workshopdb.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.third.common.api.vo.Result;
import org.jeecg.modules.third.jdx.service.BuFaultInfoThirdService;
import org.jeecg.modules.third.workshopdb.bean.WSFault;
import org.jeecg.modules.third.workshopdb.service.WSFaultService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车间故障 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-03-23
 */
@Api(tags = "从车间故障sqlserver中获取")
@Slf4j
@RestController
@RequestMapping("/ws-fault")
public class WSFaultController {

    private final WSFaultService wsFaultService;
    private final BuFaultInfoThirdService buFaultInfoThirdService;

    public WSFaultController(WSFaultService wsFaultService,
                             BuFaultInfoThirdService buFaultInfoThirdService) {
        this.wsFaultService = wsFaultService;
        this.buFaultInfoThirdService = buFaultInfoThirdService;
    }


    @GetMapping("/count/all")
    @ApiOperation(value = "统计-从车间车辆故障数据库中读取所有质保期故障数量")
    public Result<String> countWorkshopFault() {
        Map<String, String> lineNameIdMap = new HashMap<>();
        lineNameIdMap.put("1号线", "1");
        lineNameIdMap.put("2号线", "2");

        String count = wsFaultService.countAll(lineNameIdMap);
        return new Result<String>().successResult(count);
    }

    @GetMapping("/read/all")
    @ApiOperation(value = "查询-从车间车辆故障数据库中读取所有质保期故障")
    public Result<List<WSFault>> readWorkshopFault() {
        Map<String, String> lineNameIdMap = new HashMap<>();
        lineNameIdMap.put("1号线", "1");
        lineNameIdMap.put("2号线", "2");

        List<WSFault> wsFaultList = wsFaultService.selectAllList(lineNameIdMap);
        return new Result<List<WSFault>>().successResult(wsFaultList);
    }

    @PostMapping("/sync/all")
    @ApiOperation(value = "同步-从车间车辆故障数据库中同步所有质保期故障（慎用！）（会清空旧数据）")
    public Result<String> syncWorkshopFault() {
        Map<String, String> lineNameIdMap = new HashMap<>();
        lineNameIdMap.put("1号线", "1");
        lineNameIdMap.put("2号线", "2");

        // 从车间车辆数据库库查询
        List<WSFault> wsFaultList = wsFaultService.selectAllList(lineNameIdMap);
        // 保存到架大修数据库
        String result = buFaultInfoThirdService.initSaveWorkshopFaultData(wsFaultList, lineNameIdMap);

        return new Result<String>().successResult(result);
    }


}
