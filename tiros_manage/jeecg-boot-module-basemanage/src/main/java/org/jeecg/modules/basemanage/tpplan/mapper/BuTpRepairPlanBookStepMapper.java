package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanBookStep;

import java.util.List;

/**
 * <p>
 * 计划模板任务和作业指导书明细关联 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-01
 */
public interface BuTpRepairPlanBookStepMapper extends BaseMapper<BuTpRepairPlanBookStep> {

    /**
     * 批量插入
     *
     * @param list 计划模板任务和作业指导书明细关联列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanBookStep> list);

    /**
     * 根据计划模板id查询作业指导书明细列表，按作业指导书明细id去重
     *
     * @param planId 计划模板id
     * @return 作业指导书明细列表
     */
    List<BuTpRepairPlanBookStep> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询作业指导书明细列表
     *
     * @param taskId 计划模板任务id
     * @return 作业指导书明细列表
     */
    List<BuTpRepairPlanBookStep> selectListByTaskId(@Param("taskId") String taskId);

}
