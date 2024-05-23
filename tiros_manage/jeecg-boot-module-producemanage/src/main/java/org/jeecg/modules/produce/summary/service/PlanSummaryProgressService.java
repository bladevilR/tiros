package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.progress.ProgressInfo;

/**
 * <p>
 * 生产进度控制情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryProgressService {

    /**
     * 生产进度控制情况
     *
     * @param planId 列计划id
     * @return 生产进度
     * @throws Exception 异常
     */
    ProgressInfo getProgressInfo(String planId) throws Exception;

}
