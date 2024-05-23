package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanWorkstation;

import java.util.List;

/**
 * <p>
 * 任务工位 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanWorkstationMapper extends BaseMapper<BuTpRepairPlanWorkstation> {

    /**
     * 批量插入
     *
     * @param list 任务工位列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanWorkstation> list);

    /**
     * 根据计划模板id查询任务工位列表，按工位id去重
     *
     * @param planId 计划模板id
     * @return 任务工位列表
     */
    List<BuTpRepairPlanWorkstation> selectDistinctListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板id查询任务工位列表，按工位id去重
     *
     * @param planId 计划模板id
     * @return 任务工位列表
     */
    List<BuTpRepairPlanWorkstation> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询任务工位列表
     *
     * @param taskId 计划模板任务id
     * @return 任务工位列表
     */
    List<BuTpRepairPlanWorkstation> selectListByTaskId(@Param("taskId") String taskId);

}
