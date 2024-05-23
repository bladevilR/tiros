package org.jeecg.modules.third.task;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.third.jdx.service.BuFaultInfoThirdService;
import org.jeecg.modules.third.utils.DateUtils;
import org.jeecg.modules.third.workshopdb.bean.WSFault;
import org.jeecg.modules.third.workshopdb.service.WSFaultService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 从车间车辆故障数据库中同步所有质保期故障 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
@Slf4j
@Component
@EnableScheduling
public class SyncFaultFromWorkshopDbTask {

    @Resource
    private WSFaultService wsFaultService;
    @Resource
    private BuFaultInfoThirdService buFaultInfoThirdService;

    @Value("${serviceTask.transFromWorkshopDb.fault.enable}")
    private String enableTask;


    @Scheduled(fixedDelayString = "${serviceTask.transFromWorkshopDb.fault.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            Date now = new Date();
            String nowStr = DateUtils.datetimeFormat.get().format(now);
            Map<String, String> lineNameIdMap = new HashMap<>();
            lineNameIdMap.put("1号线", "1");
            lineNameIdMap.put("2号线", "2");

            // 从车间车辆数据库库查询
            List<WSFault> wsFaultList = wsFaultService.selectAllList(lineNameIdMap);
            // 保存到架大修数据库
            String result = buFaultInfoThirdService.initSaveWorkshopFaultData(wsFaultList, lineNameIdMap);

            log.error("从车间车辆故障数据库中同步所有质保期故障：定时任务" + nowStr + "执行成功：" + result);
        } catch (Exception ex) {
            log.error("从车间车辆故障数据库中同步所有质保期故障：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
