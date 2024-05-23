package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.task.service.SupplierAppraiseTaskService;
import org.jeecg.modules.outsource.service.BuOutsourceRateingService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 计算厂商分数定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-30
 */
@Slf4j
@DisallowConcurrentExecution
public class SupplierAppraiseComputeJob implements Job {

    @Resource
    private SupplierAppraiseTaskService supplierAppraiseTaskService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        boolean flag = supplierAppraiseTaskService.computeSupplierAppraise(null);

        long time2 = System.currentTimeMillis();
        log.info("定时任务【计算厂商分数】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
