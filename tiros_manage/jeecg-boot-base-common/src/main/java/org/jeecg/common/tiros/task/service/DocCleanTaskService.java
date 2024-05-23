package org.jeecg.common.tiros.task.service;

/**
 * <p>
 * 文件清理定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface DocCleanTaskService {

    /**
     * 文件清理(用于定时任务)
     *
     * @return 是否成功
     */
    boolean cleanDoc() throws RuntimeException;

}
