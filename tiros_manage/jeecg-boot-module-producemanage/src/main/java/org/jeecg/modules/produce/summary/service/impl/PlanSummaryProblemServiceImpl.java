package org.jeecg.modules.produce.summary.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.problem.RepairProblem;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryProblemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 维修过程中存在的问题及处理措施 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryProblemServiceImpl implements PlanSummaryProblemService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;


    /**
     * @see PlanSummaryProblemService#getRepairProblem(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<RepairProblem> getRepairProblem(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);

        return Arrays.asList(new RepairProblem(), new RepairProblem());
    }


}
