package org.jeecg.modules.produce.plan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.produce.plan.bean.BuRepairPlanTask;
import org.jeecg.modules.produce.plan.bean.vo.BuRepairPlanTaskVOGantt;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanTaskProduceMapper;
import org.jeecg.modules.produce.plan.service.BuRepairPlanTaskProduceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * <p>
 * 列计划任务 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Service
public class BuRepairPlanTaskProduceServiceImpl extends ServiceImpl<BuRepairPlanTaskProduceMapper, BuRepairPlanTask> implements BuRepairPlanTaskProduceService {

    @Resource
    private BuRepairPlanTaskProduceMapper buRepairPlanTaskProduceMapper;


    /**
     * @see BuRepairPlanTaskProduceService#listByPlanId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanTaskVOGantt> listByPlanId(String planId) throws Exception {
        List<BuRepairPlanTask> taskList = buRepairPlanTaskProduceMapper.selectListByPlanId(planId);

        return transformToGantt(taskList);
    }

    /**
     * @see BuRepairPlanTaskProduceService#selectTaskDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanTaskVOGantt selectTaskDetail(String id) throws Exception {
        BuRepairPlanTask task = buRepairPlanTaskProduceMapper.selectTaskAndDetailByTaskId(id);
        return transformToGantt(task);
    }


    private List<BuRepairPlanTaskVOGantt> transformToGantt(List<BuRepairPlanTask> tasks) {
        List<BuRepairPlanTaskVOGantt> taskGantts = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(tasks)) {
            for (BuRepairPlanTask task : tasks) {
                taskGantts.add(transformToGantt(task));
            }
        }

        return taskGantts;
    }

    private BuRepairPlanTaskVOGantt transformToGantt(BuRepairPlanTask task) {
        if (null == task) {
            return null;
        }

        if (null != task.getFinishTime()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getFinishTime());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            task.setFinishTime(calendar.getTime());
        }

        BuRepairPlanTaskVOGantt taskGantt = new BuRepairPlanTaskVOGantt();
        BeanUtils.copyProperties(task, taskGantt);
        taskGantt.setTaskId(task.getId());
        taskGantt.setTaskParentId(task.getParentId());
        taskGantt.setTaskDuration(task.getDuration());

        taskGantt.setUID(task.getId());
        taskGantt.setParentId(task.getParentId());
        taskGantt.setID(task.getTaskNo());
        taskGantt.setOutlineNumber(task.getTaskWbs());
        taskGantt.setName(task.getTaskName());
        taskGantt.setCritical(task.getImportant());
        taskGantt.setWork(task.getWorkTime().intValue());
        taskGantt.setDuration(task.getDuration().intValue());
        taskGantt.setStart(task.getStartTime());
        taskGantt.setFinish(task.getFinishTime());
        taskGantt.setNotes(task.getRemark());
        taskGantt.setOutlineLevel(countPointSplit(task.getTaskWbs()) + 1);
        taskGantt.setDepartment(task.getWorkGroupName());

        return taskGantt;
    }

    private int countPointSplit(String var) {
        if (StringUtils.isBlank(var)) {
            return 0;
        }
        String replaceAll = var.replaceAll("\\.", "");
        return var.length() - replaceAll.length();
    }

}
