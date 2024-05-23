package org.jeecg.modules.basemanage.tpplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTaskPre;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanWorkstation;

import java.util.List;

public interface BuTpRepairPlanTaskPreMapper extends BaseMapper<BuTpRepairPlanTaskPre> {

    /**
     * 批量插入
     *
     * @param list 计划模板任务前置任务列表
     */
    void insertList(@Param("list") List<BuTpRepairPlanTaskPre> list);

    /**
     * 根据计划模板任务id查询计划模板任务前置任务列表
     *
     * @param taskId 计划模板任务id
     * @return 计划模板任务前置任务列表
     */
    List<BuTpRepairPlanTaskPre> selectListByTaskId(@Param("taskId") String taskId);

}
