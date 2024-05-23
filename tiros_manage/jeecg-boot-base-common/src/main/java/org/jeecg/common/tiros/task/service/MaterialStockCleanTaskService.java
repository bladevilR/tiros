package org.jeecg.common.tiros.task.service;

/**
 * <p>
 * 清理物资库存表定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface MaterialStockCleanTaskService {

    /**
     * 清理物资库存表(用于定时任务)
     *
     * @return 是否成功
     * @throws RuntimeException 异常信息
     */
    boolean clearInvalidStock() throws RuntimeException;

}
