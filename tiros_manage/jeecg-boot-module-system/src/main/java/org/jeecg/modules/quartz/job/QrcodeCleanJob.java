package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.task.service.QrcodeCleanTaskService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 清理二维码定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Slf4j
@DisallowConcurrentExecution
public class QrcodeCleanJob implements Job {

    @Resource
    private QrcodeCleanTaskService qrcodeCleanTaskService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        boolean flag = qrcodeCleanTaskService.clearQrcode();

        long time2 = System.currentTimeMillis();
        log.info("定时任务【清理二维码】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
