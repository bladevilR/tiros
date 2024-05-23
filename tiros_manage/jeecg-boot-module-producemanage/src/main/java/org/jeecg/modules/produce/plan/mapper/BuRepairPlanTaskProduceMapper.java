package org.jeecg.modules.produce.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.plan.bean.BuRepairPlanTask;

import java.util.List;

/**
 * <p>
 * 列计划任务 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanTaskProduceMapper extends BaseMapper<BuRepairPlanTask> {

    /**
     * 根据计划id获取计划任务列表
     *
     * @param planId 计划id
     * @return 计划任务列表
     */
    List<BuRepairPlanTask> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询任务详情(包含关联信息)
     *
     * @param planTaskId 列计划任务id
     * @return 任务详情(包含关联信息)
     */
    BuRepairPlanTask selectTaskAndDetailByTaskId(@Param("planTaskId") String planTaskId);

}
