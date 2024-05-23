package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.worktime.WorkTimeInfo;

/**
 * <p>
 * 维修作业工时控制情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryWorkTimeService {

    /**
     * 维修作业工时控制情况
     *
     * @param planId 列计划id
     * @return 工时情况
     * @throws Exception 异常
     */
    WorkTimeInfo getWorkTimeInfo(String planId) throws Exception;

}
