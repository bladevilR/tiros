package org.jeecg.modules.third.maximo.service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * maximo自定义 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-01
 */
public interface CustomMaximoService {

    /**
     * 查询队列表堵塞错误数据
     *
     * @return 队列表堵塞错误数据
     */
    List<Map<String, Object>> listBlockingErrorInQueue();

}
