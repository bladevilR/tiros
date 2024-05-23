package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanTaskEqu;

import java.util.List;

/**
 * <p>
 * 列计划任务目标设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
public interface BuRepairPlanTaskEquMapper extends BaseMapper<BuRepairPlanTaskEqu> {

    /**
     * 批量插入
     *
     * @param list 列计划任务目标设备列表
     */
    void insertList(@Param("list") List<BuRepairPlanTaskEqu> list);

    /**
     * 根据列计划id查询目标设备列表，按车辆结构明细id去重
     *
     * @param planId 列计划id
     * @return 目标设备列表
     */
    List<BuRepairPlanTaskEqu> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询目标设备列表
     *
     * @param taskId 列计划任务id
     * @return 目标设备列表
     */
    List<BuRepairPlanTaskEqu> selectListByTaskId(@Param("taskId") String taskId);

    /**
     * 根据列计划id查询目标设备列表
     *
     * @param planId 列计划id
     * @return 目标设备列表
     */
    List<BuRepairPlanTaskEqu> selectListForGenerateOrderByPlanId(@Param("planId") String planId);

}
