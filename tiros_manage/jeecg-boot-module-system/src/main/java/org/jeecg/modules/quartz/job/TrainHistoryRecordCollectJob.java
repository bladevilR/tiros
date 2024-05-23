package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.task.service.TrainHistoryRecordCollectTaskService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 归档车辆履历定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-05
 */
@Slf4j
@DisallowConcurrentExecution
public class TrainHistoryRecordCollectJob implements Job {

    @Resource
    private TrainHistoryRecordCollectTaskService trainHistoryRecordCollectTaskService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        boolean flag = trainHistoryRecordCollectTaskService.generateTrainHistoryRecord();

        long time2 = System.currentTimeMillis();
        log.info("定时任务【归档车辆履历】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
