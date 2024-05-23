package org.jeecg.common.tiros.cache.assettype;

import java.util.Map;

/**
 * <p>
 * 设备类型 缓存服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-16
 */
public interface AssetTypeCacheService {

    /**
     * 获取设备类型缓存信息
     *
     * @param trainTypeId 车型id
     * @return 设备类型缓存信息
     */
    Map<String, BuTrainAssetTypeBO> map(String trainTypeId);

    /**
     * 更新设备类型缓存信息
     *
     * @return 设备类型缓存信息
     */
    Map<String, BuTrainAssetTypeBO> update();

    /**
     * 获取设备类型中系统列表
     *
     * @param trainTypeId 车型id
     * @return 设备类型缓存信息
     */
    Map<String, BuTrainAssetTypeBO> mapSys(String trainTypeId);

    /**
     * 更新设备类型缓存信息
     *
     * @return 设备类型缓存信息
     */
    Map<String, BuTrainAssetTypeBO> updateSys(String trainTypeId);

}
