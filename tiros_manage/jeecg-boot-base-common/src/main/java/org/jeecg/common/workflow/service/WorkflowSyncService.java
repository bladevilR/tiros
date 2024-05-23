package org.jeecg.common.workflow.service;

import org.jeecg.common.workflow.bean.WorkFlowGroup;

/**
 * <p>
 * 工作流 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/28
 */
public interface WorkflowSyncService {

    /**
     * 同步所有部门信息(含子部门和人员)到工作流
     *
     * @return 是否操作成功
     * @throws RuntimeException 异常信息
     */
    WorkFlowGroup syncDepartInfoToWorkflow() throws RuntimeException;

    /**
     * 同步所有角色信息(含人员)到工作流
     *
     * @return 是否操作成功
     * @throws RuntimeException 异常信息
     */
    WorkFlowGroup syncRoleInfoToWorkflow() throws RuntimeException;

}
