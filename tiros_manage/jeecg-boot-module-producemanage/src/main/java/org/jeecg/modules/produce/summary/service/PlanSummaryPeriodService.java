package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.period.PeriodInfo;

/**
 * <p>
 * 维修周期控制情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryPeriodService {

    /**
     * 维修周期控制情况
     *
     * @param planId 列计划id
     * @return 周期情况
     * @throws Exception 异常
     */
    PeriodInfo getPeriodInfo(String planId) throws Exception;

}
