package org.jeecg.modules.tiros.cache.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.jeecg.common.tiros.config.bean.SysConfig;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.util.MapSizeUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.jeecg.modules.tiros.config.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 仓库信息 缓存服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/26
 */
@Service
public class WarehouseCacheServiceImpl implements WarehouseCacheService {

    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;
    @Resource
    private SysConfigMapper sysConfigMapper;
    @Resource
    private RedisUtil redisUtil;


    /**
     * @see WarehouseCacheService#map()
     */
    @Override
    public Map<String, CacheWarehouseBO> map() {
        Map<String, CacheWarehouseBO> warehouseBOMap = new HashMap<>();

        SysConfig sysConfig = sysConfigMapper.selectById(TirosConstant.SYS_CONFIG_UPDATE_WAREHOUSE_CACHE);
        if (null == sysConfig) {
            sysConfig = new SysConfig()
                    .setConfigCode(TirosConstant.SYS_CONFIG_UPDATE_WAREHOUSE_CACHE)
                    .setConfigValue("true")
                    .setConfigName("是否需要更新仓库缓存信息")
                    .setConfigRemark(null);
            sysConfigMapper.insert(sysConfig);
        }
        if ("true".equals(sysConfig.getConfigValue())) {
            warehouseBOMap = update();
            sysConfig.setConfigValue("false");
            sysConfigMapper.updateById(sysConfig);
        } else {
            Object cacheObject = redisUtil.get(CacheConstant.WAREHOUSE_CACHE_ALL);
            if (null != cacheObject) {
                if (cacheObject instanceof String) {
                    String jsonString = (String) cacheObject;
                    warehouseBOMap = JSON.parseObject(jsonString, new TypeReference<Map<String, CacheWarehouseBO>>() {
                    });
                }
            } else {
                warehouseBOMap = update();
            }
        }

        return warehouseBOMap;
    }

    /**
     * @see WarehouseCacheService#update()
     */
    @Override
    public Map<String, CacheWarehouseBO> update() {
        List<CacheWarehouseBO> warehouseBOList = buMtrWarehouseMapper.selectAllSimpleInfoListForCache();
        Map<String, CacheWarehouseBO> warehouseBOMap = transformToMap(warehouseBOList);

        redisUtil.del(CacheConstant.WAREHOUSE_CACHE_ALL);
        String warehouseBOMapJson = JSON.toJSONString(warehouseBOMap);
        redisUtil.set(CacheConstant.WAREHOUSE_CACHE_ALL, warehouseBOMapJson);

        return warehouseBOMap;
    }


    private Map<String, CacheWarehouseBO> transformToMap(List<CacheWarehouseBO> warehouseBOList) {
        if (CollectionUtils.isEmpty(warehouseBOList)) {
            return new HashMap<>();
        }

        int mapSize = MapSizeUtil.tableSizeFor(warehouseBOList.size());
        Map<String, CacheWarehouseBO> warehouseIdBOMap = new HashMap<>(mapSize);

        for (CacheWarehouseBO warehouseBO : warehouseBOList) {
            warehouseIdBOMap.put(warehouseBO.getId(), warehouseBO);
        }

        return warehouseIdBOMap;
    }

}
