package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.task.service.ContractRefundCheckTaskService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 委外合同质保金退款检查
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/17
 */
@Slf4j
@DisallowConcurrentExecution
public class ContractRefundCheckJob implements Job {

    @Resource
    private ContractRefundCheckTaskService contractRefundCheckTaskService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        boolean flag = contractRefundCheckTaskService.checkContractRefund();

        long time2 = System.currentTimeMillis();
        log.info("定时任务【委外合同质保金退款检查】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
