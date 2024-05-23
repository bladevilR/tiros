package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 生成首页统计数据项定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-30
 */
@Slf4j
@DisallowConcurrentExecution
public class HomepageDataGenerateJob implements Job {

    @Resource
    private HomepageItemRptService homepageItemRptService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        homepageItemRptService.rewriteDataItem();
        // 暂时不需要，因为生成预警记录的 AlertCheckServiceImpl.java 已经运行过了
        // homepageItemRptService.rewriteAlertItem();

        long time2 = System.currentTimeMillis();
        log.info("定时任务【生成首页统计数据项】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
