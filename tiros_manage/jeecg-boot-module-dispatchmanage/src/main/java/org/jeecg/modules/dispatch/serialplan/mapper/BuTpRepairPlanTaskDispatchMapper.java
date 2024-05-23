package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.serialplan.bean.tp.*;

import java.util.List;

/**
 * <p>
 * 维修计划模版 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-11
 */
public interface BuTpRepairPlanTaskDispatchMapper extends BaseMapper<BuTpRepairPlanTask> {

    /**
     * 根据计划模板id查询计划模板任务信息及任务关联信息
     *
     * @param tpPlanId 计划模板id
     * @return 计划模板任务信息及任务关联信息
     */
    List<BuTpRepairPlanTask> selectTpPlanTaskByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanReguInfo> selectTaskRepairPlanReguInfoByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanBookStep> selectTaskBookStepByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanSpeEq> selectTaskSpecAssetByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanWorkstation> selectTaskWorkstationsByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanMaterial> selectTaskMaterialsByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanTool> selectTaskToolsByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanPerson> selectTaskPersonsByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanMustReplace> selectTaskMustReplacesByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanTaskPre> selectTaskTaskPresByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanTaskEqu> selectTaskEquipmentByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanTaskForms> selectTaskFormByTpPlanId(@Param("tpPlanId") String tpPlanId);

    BuTpRepairPlan selectTpPlanByTpPlanId(@Param("tpPlanId") String tpPlanId);

    List<BuTpRepairPlanForms> selectTpPlanFormsByTpPlanId(@Param("tpPlanId") String tpPlanId);

}