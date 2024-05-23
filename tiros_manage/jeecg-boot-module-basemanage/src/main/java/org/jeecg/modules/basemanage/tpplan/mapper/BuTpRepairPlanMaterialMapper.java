package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanMaterial;

import java.util.List;

/**
 * <p>
 * 所需物料 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanMaterialMapper extends BaseMapper<BuTpRepairPlanMaterial> {

    /**
     * 批量插入
     *
     * @param list 所需物料列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanMaterial> list);

    /**
     * 根据计划模板id查询所需物料列表
     *
     * @param planId 计划模板id
     * @return 所需物料列表
     */
    List<BuTpRepairPlanMaterial> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询所需物料列表
     *
     * @param taskId 计划模板任务id
     * @return 所需物料列表
     */
    List<BuTpRepairPlanMaterial> selectListByTaskId(@Param("taskId") String taskId);

}
