package org.jeecg.common.tiros.third.maximo.service;

import java.util.Timer;

/**
 * <p>
 * 从maximo读取数据 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-16
 */
public interface TransFromMaximoService {

    /**
     * 同步库存变动
     *
     * @param timer 定时器，执行完成后关闭该定时器
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean readMaterialStockChange(Timer timer);

    /**
     * 同步消耗回写
     *
     * @param timer 定时器，执行完成后关闭该定时器
     * @return 是否成功
     * @throws RuntimeException 异常
     */
    boolean readMaterialReWrite(Timer timer);

}
