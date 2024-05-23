package org.jeecg.common.tiros.cache.warehouse;

import java.util.Map;

/**
 * <p>
 * 仓库信息 缓存服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/26
 */
public interface WarehouseCacheService {

    /**
     * 获取仓库缓存信息
     *
     * @return 仓库缓存信息
     */
    Map<String, CacheWarehouseBO> map();

    /**
     * 更新仓库缓存信息
     *
     * @return 仓库缓存信息
     */
    Map<String, CacheWarehouseBO> update();

}
