package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanTaskPre;

import java.util.List;

/**
 * <p>
 * 列计划任务前置任务 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanTaskPreMapper extends BaseMapper<BuRepairPlanTaskPre> {

    /**
     * 批量插入
     *
     * @param list 列计划任务前置任务列表
     */
    void insertList(@Param("list") List<BuRepairPlanTaskPre> list);

    /**
     * 根据列计划任务id查询列计划任务前置任务列表
     *
     * @param taskId 列计划任务id
     * @return 列计划任务前置任务列表
     */
    List<BuRepairPlanTaskPre> selectListByTaskId(@Param("taskId") String taskId);

}
