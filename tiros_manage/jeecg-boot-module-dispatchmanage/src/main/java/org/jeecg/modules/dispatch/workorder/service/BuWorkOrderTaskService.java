package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderBookStep;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTask;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskFormInst;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTaskRelatedInfo;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskQueryVOForApp;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskSubmitVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -09-29
 */
public interface BuWorkOrderTaskService extends IService<BuWorkOrderTask> {

    /**
     * 根据条件查询工单任务
     *
     * @param queryVO 查询条件
     * @return 工单任务列表
     * @throws Exception 异常
     */
    List<BuWorkOrderTask> listTask(BuWorkOrderTaskQueryVO queryVO) throws Exception;

    /**
     * 根据条件查询工单任务--app使用
     *
     * @param queryVO 查询条件
     * @return 工单任务列表
     * @throws Exception 异常
     */
    List<BuWorkOrderTask> listTaskForApp(BuWorkOrderTaskQueryVOForApp queryVO) throws Exception;

    /**
     * 工单任务开始
     *
     * @param taskId 工单任务id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean startOrderTask(String taskId) throws Exception;

    /**
     * 提交工单任务
     *
     * @param submitVO 提交信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean submitTask(BuWorkOrderTaskSubmitVO submitVO) throws Exception;

    /**
     * APP作业填报提交工单(给工班长) = 提交工单任务
     * 修改工单状态、完成工单流程当前任务
     *
     * @param orderId 工单id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean submitWorkingOrderForApp(String orderId) throws Exception;

    /**
     * 根据工单任务id查询表单(列计划表单)
     *
     * @param taskId 工单任务id
     * @return 列计划表单
     * @throws Exception 异常
     */
    List<BuWorkOrderTaskFormInst> listPlanFormsByTaskId(String taskId) throws Exception;

    /**
     * 查询工单作业任务的关联信息
     *
     * @param workOrderTaskId 工单任务id
     * @return 关联信息
     * @throws Exception 异常
     */
    BuWorkOrderTaskRelatedInfo selectTaskRelatedInfo(String workOrderTaskId) throws Exception;

    /**
     * Select task related info of book steps list.
     *
     * @param id the id
     * @return the list
     */
    List<BuWorkOrderBookStep> selectTaskRelatedInfoOfBookSteps(String id);
}
