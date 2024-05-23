package org.jeecg.modules.basemanage.tpplan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTask;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanTaskNoUpdateVO;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanTaskVOGantt;

import java.util.List;

/**
 * <p>
 * 计划任务明细 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-03
 */
public interface BuTpRepairPlanTaskService extends IService<BuTpRepairPlanTask> {

    /**
     * 根据任务id查询任务详情及关联信息
     *
     * @param taskId 任务id
     * @return 任务详情及关联信息
     * @throws Exception 异常
     */
    BuTpRepairPlanTaskVOGantt selectTaskDetail(String taskId) throws Exception;

    /**
     * 新增或者修改计划模板任务
     *
     * @param taskVOWithGantt 计划模板任务
     * @throws Exception 异常
     */
    boolean saveOrUpdatePlanTask(BuTpRepairPlanTaskVOGantt taskVOWithGantt) throws Exception;

    /**
     * 更新计划模板任务序号
     *
     * @param taskNoUpdateVOS 更新信息(id+序号+计划模板id)
     * @return 是否更新成功
     * @throws Exception 异常
     */
    boolean updateTaskNoAndWbs(List<BuTpRepairPlanTaskNoUpdateVO> taskNoUpdateVOS) throws Exception;

    /**
     * 批量删除任务
     *
     * @param ids 任务ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatchPlanTaskByIds(String ids) throws Exception;

}
