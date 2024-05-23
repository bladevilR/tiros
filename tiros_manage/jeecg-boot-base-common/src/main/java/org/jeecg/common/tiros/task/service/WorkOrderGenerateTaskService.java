package org.jeecg.common.tiros.task.service;

import java.util.Date;

/**
 * <p>
 * 自动生成工单定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface WorkOrderGenerateTaskService {

    /**
     * 自动生成工单(用于定时任务)
     *
     * @param date                 日期，生成该日期的工单
     * @param generateEarlierOrder 是否生成日期之前列计划任务应生成但未生成的工单
     * @param planId               列计划id =null默认所有列计划
     * @param startFlow            是否启动流程
     * @return 生成工单结果提示信息
     * @throws RuntimeException 异常信息
     */
    String generateOrder(Date date, Boolean generateEarlierOrder, String planId, Boolean startFlow) throws RuntimeException;

}
