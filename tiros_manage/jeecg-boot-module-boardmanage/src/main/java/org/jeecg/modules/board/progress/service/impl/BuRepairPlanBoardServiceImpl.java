package org.jeecg.modules.board.progress.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.homepage.bean.vo.BuRepairPlanVOWithTaskGantt;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;
import org.jeecg.modules.board.progress.bean.BuRepairPlanTask;
import org.jeecg.modules.board.progress.mapper.BuRepairPlanBoardMapper;
import org.jeecg.modules.board.progress.service.BuRepairPlanBoardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 列计划 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
@Slf4j
@Service
public class BuRepairPlanBoardServiceImpl extends ServiceImpl<BuRepairPlanBoardMapper, BuRepairPlan> implements BuRepairPlanBoardService {

    @Resource
    private BuRepairPlanBoardMapper buRepairPlanBoardMapper;


    /**
     * @see BuRepairPlanBoardService#listAllRepairPlanBasicGantt()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanVOWithTaskGantt> listAllRepairPlanBasicGantt() throws Exception {
        // List<BuRepairPlan> repairPlanList = buRepairPlanBoardMapper.selectNotFinishRepairPlanList();
        List<BuRepairPlan> repairPlanList = buRepairPlanBoardMapper.selectNotFinishRepairPlanListNoTask();

        List<BuRepairPlanVOWithTaskGantt> resultList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(repairPlanList)) {
            for (BuRepairPlan repairPlan : repairPlanList) {
                if (null != repairPlan) {
                    resultList.add(transformToGantt(repairPlan));
                }
            }
        }

        return resultList;
    }


    private BuRepairPlanVOWithTaskGantt transformToGantt(BuRepairPlan plan) {
        BuRepairPlanVOWithTaskGantt planGantt = new BuRepairPlanVOWithTaskGantt();
        BeanUtils.copyProperties(plan, planGantt);

        List<BuRepairPlanTask> tasks = plan.getTasks();

        /*long finishTaskCount = tasks.stream().filter(task -> 2 == task.getStatus()).count();
        Double progress = PercentUtils.percent(new Long(finishTaskCount).intValue(), tasks.size());*/
        Double progress = Double.parseDouble(plan.getProgress().toString());

        planGantt.setUID(plan.getId());
        planGantt.setID(Integer.valueOf(plan.getCode()));
        planGantt.setOutlineNumber(plan.getCode());
        planGantt.setName(plan.getPlanName());
        planGantt.setCritical(1);
        planGantt.setDuration(plan.getDuration());
        planGantt.setStart(plan.getStartDate());
        planGantt.setFinish(getGanttFinishTime(plan.getFinishDate()));
        planGantt.setActualStart(plan.getActStart());
        planGantt.setActualFinish(plan.getActFinish()!=null?getGanttFinishTime(plan.getActFinish()):null);
        planGantt.setNotes(plan.getRemark());
        planGantt.setOutlineLevel(1);
        planGantt.setPercentComplete(progress);

        return planGantt;
    }

    private Date getGanttFinishTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (0 == calendar.get(Calendar.HOUR_OF_DAY)
                && 0 == calendar.get(Calendar.MINUTE)
                && 0 == calendar.get(Calendar.SECOND)) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }

        return calendar.getTime();
    }

}
