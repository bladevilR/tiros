package org.jeecg.common.tiros.wbs.service;

import org.jeecg.common.tiros.wbs.entity.WbsConf;

/**
 * <p>
 * wbs更新服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-06
 */
public interface WbsService {

    /**
     * 更新wbs
     *
     * @param wbsConf 更新配置
     * @return 是否成功
     * @throws RuntimeException 异常信息
     */
    boolean updateWbs(WbsConf wbsConf) throws RuntimeException;

}
