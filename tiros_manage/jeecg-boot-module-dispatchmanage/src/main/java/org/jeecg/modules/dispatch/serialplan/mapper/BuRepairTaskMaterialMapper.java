package org.jeecg.modules.dispatch.serialplan.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 任务物料 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskMaterialMapper extends BaseMapper<BuRepairTaskMaterial> {

    /**
     * 批量插入
     *
     * @param list 任务物料列表
     */
    void insertList(@Param("list") List<BuRepairTaskMaterial> list);

    /**
     * 根据列计划id查询所需物料列表，按物资类型id去重
     *
     * @param planId 列计划id
     * @return 所需物料列表
     */
    List<BuRepairTaskMaterial> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询所需物料列表
     *
     * @param planId 列计划id
     * @return 所需物料列表
     */
    List<BuRepairTaskMaterial> selectListForGenerateOrderByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询所需物料列表
     *
     * @param taskId 列计划任务id
     * @return 所需物料列表
     */
    List<BuRepairTaskMaterial> selectListByTaskId(@Param("taskId") String taskId);

}
