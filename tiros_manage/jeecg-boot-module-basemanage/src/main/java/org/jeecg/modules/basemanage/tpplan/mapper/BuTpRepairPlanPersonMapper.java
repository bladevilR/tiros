package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanPerson;

import java.util.List;

/**
 * <p>
 * 所需人员 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanPersonMapper extends BaseMapper<BuTpRepairPlanPerson> {

    /**
     * 批量插入
     *
     * @param list 所需人员列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanPerson> list);

    /**
     * 根据计划模板id查询所需人员列表
     *
     * @param planId 计划模板id
     * @return 所需人员列表
     */
    List<BuTpRepairPlanPerson> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询所需人员列表
     *
     * @param taskId 计划模板任务id
     * @return 所需人员列表
     */
    List<BuTpRepairPlanPerson> selectListByTaskId(@Param("taskId") String taskId);

}
