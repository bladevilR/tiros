package org.jeecg.modules.produce.summary.service;

import org.jeecg.modules.produce.summary.bean.outsource.OutsourceInfo;

/**
 * <p>
 * 委外维修部件维修进度控制情况 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
public interface PlanSummaryOutsourceService {

    /**
     * 委外维修部件维修进度控制情况
     *
     * @param planId 列计划id
     * @return 委外情况
     * @throws Exception 异常
     */
    OutsourceInfo getOutsourceInfo(String planId) throws Exception;

}
