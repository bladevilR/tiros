package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanSpeEq;

import java.util.List;

/**
 * <p>
 * 列计划任务特种设备需求 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-03
 */
public interface BuRepairPlanSpeEqMapper extends BaseMapper<BuRepairPlanSpeEq> {

    /**
     * 批量插入
     *
     * @param list 列计划任务特种设备需求列表
     */
    void insertList(@Param("list") List<BuRepairPlanSpeEq> list);

    /**
     * 根据列计划id查询特种设备需求列表，按特种设备id去重
     *
     * @param planId 列计划id
     * @return 特种设备需求列表
     */
    List<BuRepairPlanSpeEq> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询特种设备需求列表
     *
     * @param taskId 列计划任务id
     * @return 特种设备需求列表
     */
    List<BuRepairPlanSpeEq> selectListByTaskId(@Param("taskId") String taskId);

}
