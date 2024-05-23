package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTaskForms;

import java.util.List;

/**
 * <p>
 * 任务与模板表单的关联关系表 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-07
 */
public interface BuTpRepairPlanTaskFormsMapper extends BaseMapper<BuTpRepairPlanTaskForms> {

    /**
     * 批量插入
     *
     * @param list 任务模板表单关联列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanTaskForms> list);

    /**
     * 根据计划模板任务id查询任务与模板表单的关联列表
     *
     * @param taskId 计划模板任务id
     * @return 任务与模板表单的关联
     */
    List<BuTpRepairPlanTaskForms> selectListByTaskId(@Param("taskId") String taskId);

}
