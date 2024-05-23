package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.task.service.WorkOrderGenerateTaskService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 自动生成工单定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-30
 */
@Slf4j
@DisallowConcurrentExecution
public class WorkOrderGenerateJob implements Job {

    @Resource
    private WorkOrderGenerateTaskService workOrderGenerateTaskService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        Date now = new Date();
//        Date nextDay = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        String resultString = workOrderGenerateTaskService.generateOrder(now, true, null, true);
        log.info(resultString);

        long time2 = System.currentTimeMillis();
        log.info("定时任务【自动生成工单】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
