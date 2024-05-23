package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date: 2019-04-28
 * @Version: V1.1
 */
public interface IQuartzJobService extends IService<QuartzJob> {

    List<QuartzJob> findByJobClassName(String jobClassName);

    boolean saveAndScheduleJob(QuartzJob quartzJob);

    boolean editAndScheduleJob(QuartzJob quartzJob) throws SchedulerException;

    boolean deleteAndStopJob(QuartzJob quartzJob);

    boolean pauseJob(QuartzJob quartzJob) throws SchedulerException;

    boolean resumeJob(QuartzJob quartzJob);

    /**
     * 立即执行一次任务
     *
     * @param quartzJob 定时任务
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean executeJobOnce(QuartzJob quartzJob) throws Exception;

}
