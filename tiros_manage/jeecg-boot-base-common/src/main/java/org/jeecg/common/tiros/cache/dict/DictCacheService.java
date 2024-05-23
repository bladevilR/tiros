package org.jeecg.common.tiros.cache.dict;

import java.util.Map;

/**
 * <p>
 * 字典sys_dict 缓存服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface DictCacheService {

    /**
     * 获取所有字典数据缓存信息
     *
     * @return 所有字典数据缓存信息
     */
    Map<String, Map<String, String>> mapAll();

    /**
     * 更新数据字典缓存信息
     */
    Map<String, Map<String, String>> update();

}
