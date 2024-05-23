package org.jeecg.common.tiros.task.service;

/**
 * <p>
 * 清理二维码定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
public interface QrcodeCleanTaskService {

    /**
     * 清理二维码(用于定时任务)
     *
     * @return 是否成功
     * @throws RuntimeException 异常信息
     */
    boolean clearQrcode() throws RuntimeException;
}
