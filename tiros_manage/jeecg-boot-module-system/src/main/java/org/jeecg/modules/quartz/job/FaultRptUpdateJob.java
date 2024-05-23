package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.rpt.service.FaultRptService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * <p>
 * 重新统计故障统计数据定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-08-26
 */
@Slf4j
@DisallowConcurrentExecution
public class FaultRptUpdateJob implements Job {

    @Resource
    private FaultRptService faultRptService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        String rebuildInfo = faultRptService.rebuildFaultRpt(new ArrayList<>());

        long time2 = System.currentTimeMillis();
        log.info("定时任务【重新统计故障统计数据】执行完毕，耗时" + (time2 - time1) + "毫秒。" + rebuildInfo);
    }

}
