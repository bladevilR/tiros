package org.jeecg.common.tiros.cache.workstation;

import java.util.Map;

/**
 * <p>
 * 工位 缓存服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-07-31
 */

public interface WorkstationCacheService {

    /**
     * 获取工位缓存信息
     *
     * @param workshopId 车间id
     * @return 工位缓存信息
     */
    Map<String, WorkstationBO> map(String workshopId);

    /**
     * 获取工位缓存信息
     *
     * @param workshopId 车间id
     * @return 工位缓存信息
     */
    Map<String, WorkstationBO> mapKeyNo(String workshopId);

    /**
     * 获取工位缓存信息
     *
     * @param workshopId 车间id
     * @return 工位缓存信息
     */
    Map<String, WorkstationBO> mapKeyName(String workshopId);

    /**
     * 更新工位缓存信息
     *
     * @return 工位缓存信息
     */
    Map<String, Map<String, WorkstationBO>> update();

}
