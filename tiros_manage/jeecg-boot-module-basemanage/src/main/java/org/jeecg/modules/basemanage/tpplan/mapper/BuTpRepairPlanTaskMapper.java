package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTask;

import java.util.List;

/**
 * <p>
 * 计划任务明细 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanTaskMapper extends BaseMapper<BuTpRepairPlanTask> {

    /**
     * 批量插入
     *
     * @param list 计划模板任务列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanTask> list);

    /**
     * 批量更新任务序号
     *
     * @param taskList 任务列表
     */
    void updateListTaskNo(@Param("list") List<BuTpRepairPlanTask> taskList);

    /**
     * 根据任务id查询任务详情及关联信息
     *
     * @param taskId 任务id
     * @return 任务详情及关联信息
     */
    BuTpRepairPlanTask selectTpPlanTaskById(@Param("taskId") String taskId);

    /**
     * 根据任务id列表查询任务信息
     *
     * @param idList 任务id列表
     * @return 任务信息
     */
    List<BuTpRepairPlanTask> selectListByIdList(@Param("idList") List<String> idList);

    /**
     * 根据计划id列表查询任务信息
     *
     * @param planId 计划id
     * @return 任务信息
     */
    List<BuTpRepairPlanTask> selectListByPlanId(@Param("planId") String planId);

    List<BuTpRepairPlanTask> selectTaskListNotIncludeDay(@Param("planId") String planId);

    /**
     * 查询字段限制长度
     *
     * @return 字段限制长度
     */
    Integer selectSafeNoticeDataLength();

}
