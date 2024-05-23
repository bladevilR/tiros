package org.jeecg.common.tiros.rpt.service;

/**
 * <p>
 * 生成预警信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-24
 */
public interface AlertCheckService {

    /**
     * 生成预警信息
     *
     * @param alertType 预警类型，不传参生成所有
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean generateAlertInfo(Integer alertType) throws RuntimeException;

}
