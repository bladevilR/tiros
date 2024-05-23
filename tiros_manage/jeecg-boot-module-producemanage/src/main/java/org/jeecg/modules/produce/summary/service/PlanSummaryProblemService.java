package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.problem.RepairProblem;

import java.util.List;

/**
 * <p>
 * 维修过程中存在的问题及处理措施 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryProblemService {

    /**
     * 维修过程中存在的问题及处理措施
     *
     * @param planId 列计划id
     * @return 问题及处理措施
     * @throws Exception 异常
     */
    List<RepairProblem> getRepairProblem(String planId) throws Exception;

}
