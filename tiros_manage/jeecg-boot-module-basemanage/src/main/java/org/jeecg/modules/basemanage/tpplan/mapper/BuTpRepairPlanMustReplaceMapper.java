package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanMustReplace;

import java.util.List;

/**
 * <p>
 * 必换件清单，根据任何对应的所有规程的设备类型是否为必换件自动导入，导入后可以手动调整 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanMustReplaceMapper extends BaseMapper<BuTpRepairPlanMustReplace> {

    /**
     * 批量插入
     *
     * @param list 必换件清单列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanMustReplace> list);

    /**
     * 根据计划模板id查询所需物料列表
     *
     * @param planId 计划模板id
     * @return 所需物料列表
     */
    List<BuTpRepairPlanMustReplace> selectListByPlanId(@Param("planId") String planId);

    /**
     * 根据计划模板任务id查询必换件清单列表
     *
     * @param taskId 计划模板任务id
     * @return 必换件清单列表
     */
    List<BuTpRepairPlanMustReplace> selectListByTaskId(@Param("taskId") String taskId);

    List<BuTpRepairPlanMustReplace> selectListAll();

}
