package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanSpeEq;

import java.util.List;

/**
 * <p>
 * 特种设备需求 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
public interface BuTpRepairPlanSpeEqMapper extends BaseMapper<BuTpRepairPlanSpeEq> {

    /**
     * 批量插入
     *
     * @param list 特种设备需求列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanSpeEq> list);

    /**
     * 根据计划模板id查询特种设备需求列表，按特种设备id去重
     *
     * @param planId 计划模板id
     * @return 特种设备需求列表
     */
    List<BuTpRepairPlanSpeEq> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询特种设备需求列表
     *
     * @param taskId 计划模板任务id
     * @return 特种设备需求列表
     */
    List<BuTpRepairPlanSpeEq> selectListByTaskId(@Param("taskId") String taskId);

}
