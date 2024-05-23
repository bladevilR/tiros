package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanTaskForms;

import java.util.List;

/**
 * <p>
 * 任务与表单的关联关系表 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-12
 */
public interface BuRepairPlanTaskFormsMapper extends BaseMapper<BuRepairPlanTaskForms> {

    /**
     * 批量插入
     *
     * @param list 任务列计划表单关联列表
     */
    void insertList(@Param("list") List<BuRepairPlanTaskForms> list);

    /**
     * 根据列计划任务id查询任务与表单的关联列表
     *
     * @param taskId 计划任务id
     * @return 任务与表单的关联列表
     */
    List<BuRepairPlanTaskForms> selectListByTaskId(@Param("taskId") String taskId);

}
