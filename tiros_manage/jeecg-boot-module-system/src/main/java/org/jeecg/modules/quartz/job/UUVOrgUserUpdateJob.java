package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.tiros.uuv.service.UUVService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * UUV组织人员同步 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-30
 */
@Slf4j
@DisallowConcurrentExecution
public class UUVOrgUserUpdateJob implements Job {

    @Resource
    private UUVService uuvService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        String syncDeptAndUserInfo = uuvService.syncDeptAndUser(true, "admin", null);

        long time2 = System.currentTimeMillis();
        log.info("定时任务【UUV组织人员同步】执行完毕，耗时" + (time2 - time1) + "毫秒");
        log.info("定时任务【UUV组织人员同步】执行信息如下：" + syncDeptAndUserInfo);
    }

}
