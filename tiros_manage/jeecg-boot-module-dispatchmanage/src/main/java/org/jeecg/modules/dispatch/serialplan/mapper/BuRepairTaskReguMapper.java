package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskRegu;

import java.util.List;

/**
 * <p>
 * 关联规程 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskReguMapper extends BaseMapper<BuRepairTaskRegu> {

    /**
     * 批量插入
     *
     * @param list 关联规程列表
     */
    void insertList(@Param("list") List<BuRepairTaskRegu> list);

    /**
     * 根据列计划id查询任务关联规程列表，按规程明细id去重
     *
     * @param planId 列计划id
     * @return 任务关联规程列表
     */
    List<BuRepairTaskRegu> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询任务关联规程列表
     *
     * @param planId 列计划id
     * @return 任务关联规程列表
     */
    List<BuRepairTaskRegu> selectListForGenerateOrderByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询任务关联规程列表
     *
     * @param taskId 列计划任务id
     * @return 任务关联规程列表
     */
    List<BuRepairTaskRegu> selectListByTaskId(@Param("taskId") String taskId);

}
