package org.jeecg.common.workflow.service;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.workflow.bean.WorkFlowGroup;
import org.jeecg.common.workflow.bean.vo.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程接口转发 服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
public interface WorkflowForwardService {

    /**
     * 启动指定业务的流程
     *
     * @param startVO 流程方案启动参数
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean startSolution(StartVO startVO) throws RuntimeException;

    /**
     * 获取当前用户指定流程的可办理业务
     *
     * @param queryCandidateVO 参数
     * @return 任务列表
     * @throws RuntimeException 异常
     */
    List<TaskInfoVO> listCurrentUserProcessCanHandleTask(QueryCandidateVO queryCandidateVO) throws RuntimeException;

    /**
     * 发送信号
     *
     * @param signalVO 信号参数
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean signal(SignalVO signalVO) throws RuntimeException;

    /**
     * 完成指定任务
     *
     * @param taskCompleteVO 任务完成参数
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean completeTask(TaskCompleteVO taskCompleteVO) throws RuntimeException;

    /**
     * 查询用户的工单的流程可办理业务任务，并提交任务列表第一个
     *
     * @param orderId   工单id
     * @param orderCode 工单code
     * @param sysUser   用户
     * @param scene     场景，用于流程记录message
     * @param vars      流程变量：writeSubmit 1填报提交（到工班长那里） 2临时领料提交（到物料那里）
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean completeOrderFirstWorkflowTask(String orderId, String orderCode, LoginUser sysUser, String scene, Map<String, Object> vars) throws RuntimeException;

    /**
     * 查询用户的故障的流程可办理业务任务，并提交任务列表第一个
     *
     * @param faultId 故障id
     * @param faultSn 故障编码code
     * @param sysUser 用户
     * @param scene   场景，用于流程记录message
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean completeFaultFirstWorkflowTask(String faultId, String faultSn, LoginUser sysUser, String scene) throws RuntimeException;

    /**
     * 停止/取消 流程流转，直接到结束节点
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    boolean stopProcess(String processInstanceId, String taskId);

    /**
     * 查询任务状态
     *
     * @param orderId
     * @param orderType
     * @return
     */
    Integer getTaskAttributes(String orderId, Integer orderType);

    /**
     * 查询用户的领用单的流程可办理业务任务，并提交任务列表第一个
     *
     * @param id        工单id
     * @param orderCode 工单code
     * @param sysUser   用户
     * @param scene     场景，用于流程记录message
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean completeMaterialApplyFirstWorkflowTask(String id, String orderCode, LoginUser sysUser, String scene, Map<String, Object> vars);

    /**
     * 查询用户的领用单的流程可办理业务任务，并提交任务列表第一个
     *
     * @param id        工单id
     * @param orderCode 工单code
     * @param sysUser   用户
     * @param scene     场景，用于流程记录message
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean completeWorkshopConsumeFirstWorkflowTask(String id, String orderCode, LoginUser sysUser, String scene, Map<String, Object> vars);

    /**
     * 暂停流程服务
     *
     * @param processInstanceId 实例id
     * @return
     */
    boolean suspendInstance(String processInstanceId);

    /**
     * 激活流程服务
     *
     * @param processInstanceId 实例id
     * @return
     */
    boolean activateInstance(String processInstanceId);

    /**
     * 删除流程服务
     *
     * @param processInstanceId 实例id
     * @return
     */
    boolean deleteInstance(String processInstanceId);

    /**
     * 同步组织角色人员数据到工作流
     *
     * @param workFlowGroup 组织角色人员数据
     */
    boolean postDataToApi(WorkFlowGroup workFlowGroup);

}
