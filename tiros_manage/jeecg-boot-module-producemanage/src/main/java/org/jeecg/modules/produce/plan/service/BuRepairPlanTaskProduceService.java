package org.jeecg.modules.produce.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.plan.bean.BuRepairPlanTask;
import org.jeecg.modules.produce.plan.bean.vo.BuRepairPlanTaskVOGantt;

import java.util.List;

/**
 * <p>
 * 列计划任务 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanTaskProduceService extends IService<BuRepairPlanTask> {

    /**
     * 根据列计划id获取列计划任务列表
     *
     * @param planId 列计划id
     * @return 列计划任务列表
     * @throws Exception 异常信息
     */
    List<BuRepairPlanTaskVOGantt> listByPlanId(String planId) throws Exception;

    /**
     * 根据列计划任务id查询任务详情(包含关联信息)
     *
     * @param id 任务id
     * @return 任务详情(包含关联信息)
     * @throws Exception 异常信息
     */
    BuRepairPlanTaskVOGantt selectTaskDetail(String id) throws Exception;

}
