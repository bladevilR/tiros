package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskBookStep;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.bo.BuRepairReguDetailBO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskQueryVO;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderTaskQueryVOForApp;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
public interface BuWorkOrderTaskMapper extends BaseMapper<BuWorkOrderTask> {

    /**
     * 批量插入
     *
     * @param list 工单任务列表
     */
    void insertList(@Param("list") List<BuWorkOrderTask> list);

    /**
     * 批量更新任务对象id
     *
     * @param list 工单任务列表
     */
    void updateListTaskObjId(@Param("list") List<BuWorkOrderTask> list);

    /**
     * 根据工单id删除工单任务
     *
     * @param orderIdList 工单id列表
     */
    int deleteByOrderIdList(@Param("orderIdList") List<String> orderIdList);

    /**
     * 所有任务
     *
     * @param queryVO
     * @return
     */
    List<BuWorkOrderTask> selectTaskList(@Param("queryVO") BuWorkOrderTaskQueryVO queryVO);

    /**
     * 根据条件查询工单任务--app使用
     *
     * @param queryVO 查询条件
     * @return 工单任务列表
     */
    List<BuWorkOrderTask> selectTaskListForApp(@Param("queryVO") BuWorkOrderTaskQueryVOForApp queryVO);

    /**
     * 周转件更换时间
     *
     * @param changeId
     * @return
     */

    Date selectTurnoverReplaceTime(@Param("changeId") String changeId);

    /**
     * 部件更换时间
     *
     * @param changeId
     * @return
     */
    Date selectAssetReplaceTime(@Param("changeId") String changeId);

    /**
     * 查询工单任务工艺文件
     *
     * @param workOrderTaskId 工单任务id
     * @return 工单任务工艺文件
     */
    List<BuOtherData> selectWorkOrderTaskFile(@Param("workOrderTaskId") String workOrderTaskId);

    /**
     * 查询工单任务工艺文件
     *
     * @param workOrderTaskId 工单任务id
     * @return 工单任务工艺文件
     */
    List<BuOtherData> selectReguDetailFile(@Param("workOrderTaskId") String workOrderTaskId);

    /**
     * 根据工单任务id获取任务关联的规程明细（包含所有上级规程明细）
     *
     * @param taskId 工单任务id
     * @return 规程明细（包含所有上级规程明细）
     */
    List<BuRepairReguDetailBO> selectTaskRelativeReguDetailList(@Param("taskId") String taskId);

    /**
     * 根据工单id获取工单任务关联的规程明细（包含所有上级规程明细）
     *
     * @param orderId 工单id
     * @return 规程明细（包含所有上级规程明细）
     */
    List<BuRepairReguDetailBO> selectOrderRelativeReguDetailList(@Param("orderId") String orderId);

    /**
     * 根据工单任务id获取工单任务信息
     *
     * @param taskId 工单任务id
     * @return 工单任务信息
     */
    BuWorkOrderTask selectTaskByTaskId(@Param("taskId") String taskId);

    List<JobRequirement> selectJobRequirement(@Param("taskObjId") String taskObjId);

    /**
     * 工班最后一条安全预想内容
     *
     * @return
     */
    Map<String, String> selectLastSafeAssumeByGroupId(@Param("groupId") String groupId);

    /**
     * @param id
     * @return
     */
    List<JobRequirement> selectJobRequirementByParent(@Param("id") String id);

    /**
     * 根据工单任务id查询表单id列表
     * 工单任务找到工位->工位关联的表单
     *
     * @param taskId 工单任务id
     * @return 表单id列表
     */
    List<String> selectFormIdListByTaskId(@Param("taskId") String taskId);

    /**
     * 根据工单任务id查询工单是否自动生成
     *
     * @param taskId 工单任务id
     * @return 工单是否自动生成
     */
    Integer selectWorkOrderGenerateByTaskId(@Param("taskId") String taskId);

    /**
     * 根据规程明细id查询规程明细信息，返回列计划任务关联规程
     *
     * @param reguDetailId 规程明细id
     * @return 列计划任务关联规程列表
     */
    List<BuRepairTaskRegu> selectAsPlanTaskRepairPlanReguInfoByReguDetailId(@Param("reguDetailId") String reguDetailId);

    /**
     * 根据规程明细id查询规程明细关联作业指导书明细，返回列计划任务关联作业指导书明细
     *
     * @param reguDetailId 规程明细id
     * @return 列计划任务关联作业指导书明细列表
     */
    List<BuRepairTaskBookStep> selectAsPlanTaskBookStepsByReguDetailId(@Param("reguDetailId") String reguDetailId);

    /**
     * 根据工单任务id查询工单任务关联表单实例列表
     *
     * @param taskId 工单任务id
     * @return 工单任务关联表单实例列表
     */
    List<BuWorkOrderTaskFormInst> selectOrderTaskFormInstListByOrderTaskId(@Param("taskId") String taskId);

    /**
     * 查询工单任务的作业指导步骤，包含工单任务自身的、列计划任务中的
     *
     * @param taskId 任务id
     * @return 作业指导步骤
     */
    List<BuWorkOrderBookStep> selectTaskAllBookSteps(@Param("taskId") String taskId);

    /**
     * 查询由列计划生成的、任务对象id为空、的工单任务
     *
     * @param planId 列计划id
     * @return 工单任务和列计划任务
     */
    List<Map<String, Object>> selectPlanGenerateOrderTaskAndTaskObjIdNullByPlanId(@Param("planId") String planId);

    /**
     * 查询无物料的工单任务及计划模板任务信息
     *
     * @param planId  列计划id
     * @param groupId 班组id
     * @return 工单任务及计划模板任务信息
     */
    List<Map<String, Object>> selectNullMaterialOrderTaskAndTpPlanTask(@Param("planId") String planId, @Param("groupId") String groupId);

    /**
     * 根据工单id查询工单任务的作业车辆设备编码
     *
     * @param orderId 工单id
     * @return 工单任务的作业车辆设备编码
     */
    List<String> selectTrainAssetCodeByOrderId(@Param("orderId") String orderId);

    /**
     * 查询字段限制长度
     *
     * @return 字段限制长度
     */
    Integer selectSafeNoticeDataLength();

    /**
     * 查询已关闭工单中，时间异常的工单任务
     *
     * @return 工单任务列表
     */
    List<BuWorkOrderTask> selectErrorTimeListOfCloseOrder();

}
