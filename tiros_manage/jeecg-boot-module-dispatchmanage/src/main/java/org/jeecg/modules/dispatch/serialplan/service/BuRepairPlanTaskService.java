package org.jeecg.modules.dispatch.serialplan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanTask;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanTaskNoUpdateVO;
import org.jeecg.modules.dispatch.serialplan.bean.vo.BuRepairPlanTaskVOGantt;

import java.util.List;

/**
 * <p>
 * 列计划任务 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
public interface BuRepairPlanTaskService extends IService<BuRepairPlanTask> {

    /**
     * 根据列计划任务id查询任务及关联信息
     *
     * @param taskId 列计划任务id
     * @return 任务及关联信息
     * @throws Exception 异常信息
     */
    BuRepairPlanTaskVOGantt selectTaskDetail(String taskId) throws Exception;

    /**
     * 新增或更新列计划任务
     *
     * @param taskVOWithGantt 列计划任务及关联信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveOrUpdatePlanTask(BuRepairPlanTaskVOGantt taskVOWithGantt) throws Exception;

    /**
     * 更新计划模板任务序号
     *
     * @param taskNoUpdateVOS 更新信息(id+序号+计划模板id)
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateTaskNoAndWbs(List<BuRepairPlanTaskNoUpdateVO> taskNoUpdateVOS) throws Exception;

    /**
     * 批量删除任务
     *
     * @param ids 任务ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
