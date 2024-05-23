package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.TotalInfo;

/**
 * <p>
 * 总体情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryTotalService {

    /**
     * 获取总体情况
     *
     * @param planId 列计划id
     * @return 总体情况
     * @throws Exception 异常
     */
    TotalInfo getTotalInfo(String planId) throws Exception;

}
