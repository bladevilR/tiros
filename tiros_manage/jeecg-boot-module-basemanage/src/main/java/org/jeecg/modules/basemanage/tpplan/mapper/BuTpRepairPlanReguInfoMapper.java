package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanReguInfo;

import java.util.List;

/**
 * <p>
 * 任务规程关联 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanReguInfoMapper extends BaseMapper<BuTpRepairPlanReguInfo> {

    /**
     * 批量插入
     *
     * @param list 任务规程关联列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanReguInfo> list);

    /**
     * 根据计划模板id查询任务关联规程列表
     *
     * @param planId 计划模板id
     * @return 任务关联规程列表
     */
    List<BuTpRepairPlanReguInfo> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询任务关联规程列表
     *
     * @param taskId 计划模板任务id
     * @return 任务关联规程列表
     */
    List<BuTpRepairPlanReguInfo> selectListByTaskId(@Param("taskId") String taskId);

    /**
     * 根据规程id和计划模板id查询计划模板任务关联规程列表
     *
     * @param reguId 规程id
     * @param planId 计划模板id
     * @return 计划模板任务关联规程列表
     */
    List<BuTpRepairPlanReguInfo> selectTpPlanRelatedList(@Param("reguId") String reguId, @Param("planId") String planId);

}
