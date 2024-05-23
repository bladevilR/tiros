package org.jeecg.modules.dispatch.serialplan.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskStaffRequire;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 作业人员需求 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskStaffRequireMapper extends BaseMapper<BuRepairTaskStaffRequire> {

    /**
     * 批量插入
     *
     * @param list 作业人员需求列表
     */
    void insertList(@Param("list") List<BuRepairTaskStaffRequire> list);

    /**
     * 根据列计划id查询所需人员列表
     *
     * @param planId 列计划id
     * @return 所需人员列表
     */
    List<BuRepairTaskStaffRequire> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询所需人员列表
     *
     * @param taskId 列计划任务id
     * @return 所需人员列表
     */
    List<BuRepairTaskStaffRequire> selectListByTaskId(@Param("taskId") String taskId);

}
