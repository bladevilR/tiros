package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskBookStep;

import java.util.List;

/**
 * <p>
 * 列计划任务关联作业指导书明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-01
 */
public interface BuRepairTaskBookStepMapper extends BaseMapper<BuRepairTaskBookStep> {

    /**
     * 批量插入
     *
     * @param list 列计划任务关联作业指导书明细列表
     */
    void insertList(@Param("list") List<BuRepairTaskBookStep> list);

    /**
     * 根据列计划id查询作业指导书明细列表，按作业指导书明细id去重
     *
     * @param planId 列计划id
     * @return 作业指导书明细列表
     */
    List<BuRepairTaskBookStep> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据列计划任务id查询作业指导书明细列表
     *
     * @param taskId 列计划任务id
     * @return 作业指导书明细列表
     */
    List<BuRepairTaskBookStep> selectListByTaskId(@Param("taskId") String taskId);
    
}
