package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.fault.FaultInfo;

/**
 * <p>
 * 维修质量控制情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryFaultService {

    /**
     * 维修质量控制情况
     *
     * @param planId 列计划id
     * @return 故障情况
     * @throws Exception 异常
     */
    FaultInfo getFaultInfo(String planId) throws Exception;

}
