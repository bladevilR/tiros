package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTaskEqu;

import java.util.List;

/**
 * <p>
 * 计划模板任务目标设备 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
public interface BuTpRepairPlanTaskEquMapper extends BaseMapper<BuTpRepairPlanTaskEqu> {

    /**
     * 批量插入
     *
     * @param list 目标设备列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanTaskEqu> list);

    /**
     * 根据计划模板id查询目标设备列表
     *
     * @param planId 计划模板id
     * @return 目标设备列表
     */
    List<BuTpRepairPlanTaskEqu> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询目标设备列表
     *
     * @param taskId 计划模板任务id
     * @return 目标设备列表
     */
    List<BuTpRepairPlanTaskEqu> selectListByTaskId(@Param("taskId") String taskId);

}
