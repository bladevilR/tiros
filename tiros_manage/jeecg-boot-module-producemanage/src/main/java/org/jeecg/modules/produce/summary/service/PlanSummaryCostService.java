package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.cost.CostInfo;

/**
 * <p>
 * 维修成本情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryCostService {

    /**
     * 维修成本情况
     *
     * @param planId 列计划id
     * @return 成本情况
     * @throws Exception 异常
     */
    CostInfo getCostInfo(String planId) throws Exception;

}
