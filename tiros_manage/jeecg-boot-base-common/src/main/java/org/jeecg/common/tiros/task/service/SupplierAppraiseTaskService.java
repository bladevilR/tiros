package org.jeecg.common.tiros.task.service;

/**
 * <p>
 * 计算厂商分数定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface SupplierAppraiseTaskService {

    /**
     * 计算厂商分数(用于定时任务)
     *
     * @param id 厂商id，为空计算所有厂商
     * @return 是否执行成功
     * @throws RuntimeException 异常信息
     */
    boolean computeSupplierAppraise(String id) throws RuntimeException;

}
