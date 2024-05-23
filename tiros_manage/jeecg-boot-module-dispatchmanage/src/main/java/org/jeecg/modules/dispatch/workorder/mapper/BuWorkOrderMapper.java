package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.bean.WorkOrderRelevanceInfo;
import org.jeecg.modules.dispatch.workorder.bean.bo.SysUserBO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderQueryVO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderMapper extends BaseMapper<BuWorkOrder> {

    /**
     * 批量插入
     *
     * @param list 工单列表
     */
    void insertList(@Param("list") List<BuWorkOrder> list);

    /**
     * 暂停或激活工单
     *
     * @param orderId   工单id
     * @param state     状态
     * @param isSuspend 是否暂停操作
     */
    void updateSuspend(@Param("orderId") String orderId, @Param("state") Integer state, @Param("date") Date date, @Param("isSuspend") Boolean isSuspend);

    /**
     * 更新修改工单为未下发
     *
     * @param orderId   工单id
     */
    void updateUnIssuing(@Param("orderId") String orderId);

    /**
     * 根据条件分页查询工单
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 工单分页结果
     */
    IPage<BuWorkOrder> selectPageByCondition(Page<BuWorkOrder> page, @Param("queryVO") BuWorkOrderQueryVO queryVO);

    /**
     * 根据条件分页查询工单(工班工单)
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 工单分页结果
     */
    IPage<BuWorkOrder> selectPageByConditionForGroup(Page<BuWorkOrder> page, @Param("queryVO") BuWorkOrderQueryVO queryVO);

    /**
     * 根据条件分页查询领用
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 领用分页结果
     */
    IPage<BuWorkOrder> selectApplyPageByCondition(Page<BuWorkOrder> page, @Param("queryVO") BuWorkOrderQueryVO queryVO);

    /**
     * 根据条件分页查询领用
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 领用分页结果
     */
    IPage<BuWorkOrder> selectApplyPageByConditionForGroup(Page<BuWorkOrder> page, @Param("queryVO") BuWorkOrderQueryVO queryVO);

    /**
     * 根据工单id查询工单关联信息
     *
     * @param id 工单id
     * @return 工单关联信息
     */
    WorkOrderRelevanceInfo selectWorkOrderRelevanceInfo(@Param("id") String id);

    /**
     * 最大的任务序号
     *
     * @param id 工单id
     * @return
     */
    Integer selectMaxTaskNo(@Param("id") String id);

    BuWorkOrder selectByTaskId(@Param("workOrderTaskId") String workOrderTaskId);

    /**
     * 根据列计划任务id查询关联规程明细id列表
     *
     * @param planTaskId 列计划任务id
     * @return 列计划任务关联规程明细id列表
     */
    List<String> selectReguDetailIdListByPlanTaskId(@Param("planTaskId") String planTaskId);

    /**
     * 根据列计划id列表查询列计划下的工单(包含工单任务)
     *
     * @param planIdList 列计划id列表
     * @return 列计划下的工单(包含工单任务)
     */
    List<BuWorkOrder> selectListByPlanIdList(@Param("planIdList") List<String> planIdList);

    /**
     * 根据id查询工单信息
     *
     * @param orderId 工单id
     * @return 工单信息
     */
    BuWorkOrder selectOrderById(@Param("orderId") String orderId);

    /**
     * 根据id查询工单信息
     *
     * @param orderIdList 工单id列表
     * @return 工单信息
     */
    List<BuWorkOrder> selectOrderForMaximoByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 根据id查询工班长
     *
     * @param groupId 工班id
     * @return 用户id
     */
    List<String> selectUserNameByGroupId(@Param("groupId") String groupId);

    /**
     * 根据工班id列表查询工班长信息
     *
     * @param groupIdList 工班id列表
     * @return 工班长信息
     */
    List<SysUserBO> selectMonitorUserNameByGroupIdList(@Param("groupIdList") List<String> groupIdList);

    /**
     * 根据工单id查询表单id列表
     * 工单任务找到工位->工位关联的表单
     *
     * @param orderId 工单id
     * @return 表单id列表
     */
    List<String> selectFormIdListByOrderId(@Param("orderId") String orderId);

    /**
     * 根据工单编号找工单
     *
     * @param workOrderCodeList 工单编号集合
     * @return 工单列表
     */
    List<BuWorkOrder> selectListByCode(@Param("list") Set<String> workOrderCodeList);

    /**
     * 获取流程实例id根据工单id
     *
     * @param id
     * @return
     */
    String selectProcessInstanceId(@Param("id") String id);
}
