package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.rpt.service.AlertCheckService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 生成预警信息定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-24
 */
@Slf4j
@DisallowConcurrentExecution
public class AlertCheckJob implements Job {

    @Resource
    private AlertCheckService alertCheckService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        boolean flag = alertCheckService.generateAlertInfo(null);

        long time2 = System.currentTimeMillis();
        log.info("定时任务【生成预警信息】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
