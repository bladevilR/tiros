package org.jeecg.modules.quartz.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 生成安全运营天数定时任务
 * </p>
 *
 * @author yyg
 * @since 2021-07-21
 */
@Slf4j
@DisallowConcurrentExecution
public class SafeOperationDaysJob implements Job {
    @Resource
    private SysConfigService sysConfigService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        long time1 = System.currentTimeMillis();

        Date startDate = DateUtil.parse("2014-01-23", "yyyy-mm-dd");
        // 获取上次更新天数
        String configCode = "SafeServiceDays";
        //Integer days = Integer.valueOf(sysConfigService.getScheduleTaskLastDays(configCode));
        // 本次更新时间
        long newDays = DateUtil.betweenDay(startDate, DateUtil.date(), false);
        sysConfigService.updateScheduleTaskLastDays(configCode, Long.toString(newDays));

        long time2 = System.currentTimeMillis();
        log.info("定时任务【生成安全运营天数】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }
}
