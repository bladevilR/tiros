package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskWorkstation;

import java.util.List;

/**
 * <p>
 * 任务工位 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskWorkstationMapper extends BaseMapper<BuRepairTaskWorkstation> {

    /**
     * 批量插入
     *
     * @param list 任务工位列表
     */
    void insertList(@Param("list") List<BuRepairTaskWorkstation> list);

    /**
     * 根据列计划id查询任务工位列表，按工位id去重
     *
     * @param planId 列计划id
     * @return 任务工位列表
     */
    List<BuRepairTaskWorkstation> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询任务工位列表
     *
     * @param planId 列计划id
     * @return 任务工位列表
     */
    List<BuRepairTaskWorkstation> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询任务工位列表
     *
     * @param planId 列计划id
     * @return 任务工位列表
     */
    List<BuRepairTaskWorkstation> selectListForGenerateOrderByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询任务工位列表
     *
     * @param taskId 列计划任务id
     * @return 任务工位列表
     */
    List<BuRepairTaskWorkstation> selectListByTaskId(@Param("taskId") String taskId);

}
