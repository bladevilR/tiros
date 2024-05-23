package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanTask;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskBookStep;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划任务 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanTaskMapper extends BaseMapper<BuRepairPlanTask> {

    /**
     * 批量插入
     *
     * @param list 列计划任务列表
     */
    void insertList(@Param("list") List<BuRepairPlanTask> list);

    /**
     * 批量更新任务序号
     *
     * @param taskList 任务列表
     */
    void updateListTaskNo(@Param("list") List<BuRepairPlanTask> taskList);

    /**
     * 批量更新任务状态和时间
     *
     * @param taskList 任务列表
     */
    void updateListStatusAndTime(@Param("list") List<BuRepairPlanTask> taskList);

    /**
     * 更新列计划任务为已生成工单
     *
     * @param planTaskIdList 列计划任务id列表
     */
    void updatePlanTaskGeneratedYes(@Param("planTaskIdList") List<String> planTaskIdList);

    /**
     * 更新列计划任务暂停属性
     *
     * @param taskList 任务列表
     */
    void updateListSuspendActivity(@Param("list") List<BuRepairPlanTask> taskList);

    /**
     * 根据计划id查询任务列表
     *
     * @param planId 计划id
     * @return 任务列表
     */
    List<BuRepairPlanTask> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划id查询任务列表-用来更新状态
     *
     * @param planId 计划id
     * @return 任务列表
     */
    List<BuRepairPlanTask> selectListWithOrderTaskStatusByPlanId(@Param("planId") String planId);

    /**
     * 查询工位名称
     *
     * @param id
     * @return
     */
    List<String> selectWorkStation(@Param("id") String id);

    /**
     * 查询工班名称
     *
     * @param id
     * @return
     */
    String selectWorkGroupName(@Param("id") String id);

    /**
     * 查询任务关联的信息
     *
     * @param taskId
     * @return
     */
    BuRepairPlanTask selectPlanTaskTaskId(@Param("taskId") String taskId);

    /**
     * 根据列计划任务id查询列计划任务关联规程
     *
     * @param id 列计划任务id
     * @return 列计划任务关联规程列表
     */
    List<BuRepairTaskRegu> selectTaskRepairPlanReguInfo(@Param("id") String id);

    /**
     * 规程没有关联具体任务
     *
     * @return
     */
    List<BuRepairPlanTask> noRelevanceDetail(@Param("id") String id);

    Integer selectMaxTaskNoByParentId(@Param("parentId") String parentId);

    /**
     * 根据列计划id，查询需要生成工单未生成工单、计划完成时间小于等于当前日期列计划任务
     *
     * @param planId 列计划id
     * @param date   查询时间
     * @return 列计划任务及关联信息
     */
    List<BuRepairPlanTask> selectListBeforeAndEqualDate(@Param("planId") String planId, @Param("date") Date date);

    /**
     * 根据列计划id，查询需要生成工单未生成工单、计划完成时间等于当前日期列计划任务
     *
     * @param planId 列计划id
     * @param date   查询时间
     * @return 列计划任务及关联信息
     */
    List<BuRepairPlanTask> selectListEqualDate(@Param("planId") String planId, @Param("date") Date date);

    /**
     * 查询今天的任务
     *
     * @return
     */
    List<BuRepairPlanTask> selectListByNow(@Param("planId") String planId);

    /**
     * 根据列计划id获取列计划任务中最晚的计划完成时间
     *
     * @param planId 列计划id
     * @return 任务最晚的计划完成时间
     */
    Date getTaskLatestFinishTimeByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id获取列计划任务工期之和
     *
     * @param planId 列计划id
     * @return 列计划任务工期之和
     */
    Integer getTaskTotalDurationByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id获取列计划任务关联的规程明细id列表
     *
     * @param planTaskId 列计划任务id
     * @return 列计划任务关联的规程明细id列表
     */
    List<String> selectReguDetailIdListByPlanTaskId(@Param("planTaskId") String planTaskId);

    /**
     * 根据列计划任务id查询维修车辆号
     *
     * @param planTaskId 列计划任务id
     * @return 维修车辆号
     */
    String selectRepairTrainNoByPlanTaskId(@Param("planTaskId") String planTaskId);

    /**
     * 根据工单id查询相应的可以暂停的列计划任务
     *
     * @param orderIdList 工单id列表
     * @return 列计划任务列表
     */
    List<BuRepairPlanTask> selectNeedSuspendListByOrderIdListAndPlanTaskStatus(@Param("orderIdList") List<String> orderIdList, @Param("planTaskStatus") Integer planTaskStatus);

    /**
     * 查询字段限制长度
     *
     * @return 字段限制长度
     */
    Integer selectSafeNoticeDataLength();

}
