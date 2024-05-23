package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.workflow.bean.WorkFlowGroup;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.common.workflow.service.WorkflowSyncService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 同步组织角色人员数据到工作流
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-02-02
 */
@Slf4j
@DisallowConcurrentExecution
public class SynDataToWorkflowJob implements Job {

    @Resource
    private WorkflowSyncService workflowSyncService;
    @Resource
    private WorkflowForwardService workflowForwardService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        long time1 = System.currentTimeMillis();

        WorkFlowGroup topDepartGroup = workflowSyncService.syncDepartInfoToWorkflow();
        WorkFlowGroup topRoleGroup = workflowSyncService.syncRoleInfoToWorkflow();

        workflowForwardService.postDataToApi(topDepartGroup);
        workflowForwardService.postDataToApi(topRoleGroup);

        long time2 = System.currentTimeMillis();
        log.info("定时任务【同步组织角色人员数据到工作流】执行完毕，耗时" + (time2 - time1) + "毫秒");
    }

}
