package org.jeecg.modules.dispatch.serialplan.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskTool;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 任务工器具 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskToolMapper extends BaseMapper<BuRepairTaskTool> {

    /**
     * 批量插入
     *
     * @param list 任务工器具列表
     */
    void insertList(@Param("list") List<BuRepairTaskTool> list);

    /**
     * 根据列计划id查询所需工器具列表，按工器具类型id去重
     *
     * @param planId 列计划id
     * @return 所需工器具列表
     */
    List<BuRepairTaskTool> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询所需工器具列表
     *
     * @param planId 列计划id
     * @return 所需工器具列表
     */
    List<BuRepairTaskTool> selectListForGenerateOrderByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询所需工器具列表
     *
     * @param taskId 列计划任务id
     * @return 所需工器具列表
     */
    List<BuRepairTaskTool> selectListByTaskId(@Param("taskId") String taskId);

}
