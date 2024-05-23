package org.jeecg.common.tiros.rpt.service;

/**
 * <p>
 * 绩效统计 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
public interface KpiRptService {

    /**
     * 添加绩效：人工成本到对应工时
     *
     * @param orderId 工单id
     * @return 是否成功
     */
    boolean increaseTimeKpiByOrder(String orderId);

    /**
     * 添加绩效：故障到对应故障数
     * @param faultId 故障id
     * @return 是否成功
     */
    boolean increaseFaultKpiByFault(String faultId);

    /**
     * 减少绩效：故障到对应故障数
     * @param faultId 故障id
     * @return 是否成功
     */
    boolean decreaseFaultKpiByFault(String faultId);

}
