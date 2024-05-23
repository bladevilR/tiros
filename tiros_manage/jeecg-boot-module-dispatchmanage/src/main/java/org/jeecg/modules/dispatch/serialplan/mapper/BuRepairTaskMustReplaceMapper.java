package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskMustReplace;

import java.util.List;

/**
 * <p>
 * 任务必换件，根据任何对应的所有规程的设备类型是否为必换件自动导入，导入后可以手动调整 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairTaskMustReplaceMapper extends BaseMapper<BuRepairTaskMustReplace> {

    /**
     * 批量插入
     *
     * @param list 任务必换件列表
     */
    void insertList(@Param("list") List<BuRepairTaskMustReplace> list);

    /**
     * 根据列计划id查询必换件清单列表，按必换件清单id去重
     *
     * @param planId 列计划id
     * @return 必换件清单列表
     */
    List<BuRepairTaskMustReplace> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划id查询必换件清单列表
     *
     * @param planId 列计划id
     * @return 必换件清单列表
     */
    List<BuRepairTaskMustReplace> selectListForGenerateOrderByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询必换件清单列表
     *
     * @param taskId 列计划任务id
     * @return 必换件清单列表
     */
    List<BuRepairTaskMustReplace> selectListByTaskId(@Param("taskId") String taskId);

}
