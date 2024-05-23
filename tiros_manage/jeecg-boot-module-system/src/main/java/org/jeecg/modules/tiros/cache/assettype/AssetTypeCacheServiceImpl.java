package org.jeecg.modules.tiros.cache.assettype;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.tiros.cache.assettype.AssetTypeCacheService;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.common.util.MapSizeUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备类型 缓存服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-16
 */
@Service
public class AssetTypeCacheServiceImpl implements AssetTypeCacheService {

    @Resource
    private BuTrainAssetTypeMapper buTrainAssetTypeMapper;
    @Resource
    private RedisUtil redisUtil;


    /**
     * @see AssetTypeCacheService#map(String)
     */
    @Override
    public Map<String, BuTrainAssetTypeBO> map(String trainTypeId) {
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = new HashMap<>();

        Object cacheObject = redisUtil.get(CacheConstant.TRAIN_ASSET_TYPE_CACHE_ALL);
        if (null != cacheObject) {
            if (cacheObject instanceof String) {
                String jsonString = (String) cacheObject;
                assetTypeIdBOMap = JSON.parseObject(jsonString, new TypeReference<Map<String, BuTrainAssetTypeBO>>() {
                });
            }
        } else {
            assetTypeIdBOMap = update();
        }

        if (StringUtils.isNotBlank(trainTypeId)) {
            Map<String, BuTrainAssetTypeBO> filterAssetTypeIdBOMap = new HashMap<>();
            for (Map.Entry<String, BuTrainAssetTypeBO> assetTypeIdBOEntry : assetTypeIdBOMap.entrySet()) {
                String key = assetTypeIdBOEntry.getKey();
                BuTrainAssetTypeBO assetTypeBO = assetTypeIdBOEntry.getValue();

                if (trainTypeId.equals(assetTypeBO.getTrainTypeId())) {
                    filterAssetTypeIdBOMap.put(key, assetTypeBO);
                }
            }
            return filterAssetTypeIdBOMap;
        }

        return assetTypeIdBOMap;
    }

    /**
     * @see AssetTypeCacheService#update()
     */
    @Override
    public Map<String, BuTrainAssetTypeBO> update() {
        List<BuTrainAssetTypeBO> assetTypeBOList = buTrainAssetTypeMapper.selectAllSimpleInfoListForCache();
        for (BuTrainAssetTypeBO assetTypeBO : assetTypeBOList) {
            // 短名称为空则用原来的名称
            if (StringUtils.isBlank(assetTypeBO.getShortName())) {
                assetTypeBO.setShortName(assetTypeBO.getName());
            }
        }
        setSysIdName(assetTypeBOList);
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = transformToMap(assetTypeBOList);

        redisUtil.del(CacheConstant.TRAIN_ASSET_TYPE_CACHE_ALL);
        String assetTypeIdBOMapJson = JSON.toJSONString(assetTypeIdBOMap);
        redisUtil.set(CacheConstant.TRAIN_ASSET_TYPE_CACHE_ALL, assetTypeIdBOMapJson);

        return assetTypeIdBOMap;
    }

    /**
     * @see AssetTypeCacheService#mapSys(String)
     */
    @Override
    public Map<String, BuTrainAssetTypeBO> mapSys(String trainTypeId) {
        Map<String, BuTrainAssetTypeBO> idSysBOMap = new HashMap<>();

        Object cacheObject = redisUtil.get(CacheConstant.TRAIN_ASSET_TYPE_CACHE_SYS);
        if (null != cacheObject) {
            if (cacheObject instanceof String) {
                String jsonString = (String) cacheObject;
                idSysBOMap = JSON.parseObject(jsonString, new TypeReference<Map<String, BuTrainAssetTypeBO>>() {
                });
            }
        } else {
            idSysBOMap = updateSys(trainTypeId);
        }

        return idSysBOMap;
    }

    /**
     * @see AssetTypeCacheService#updateSys(String)
     */
    @Override
    public Map<String, BuTrainAssetTypeBO> updateSys(String trainTypeId) {
        Map<String, BuTrainAssetTypeBO> idSysBOMap = new HashMap<>();

        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = map(trainTypeId);
        if (null != assetTypeIdBOMap && assetTypeIdBOMap.size() != 0) {
            List<BuTrainAssetTypeBO> assetTypeBOList = new ArrayList<>(assetTypeIdBOMap.values());
            List<BuTrainAssetTypeBO> sysBOList = assetTypeBOList.stream()
                    .filter(assetTypeBO -> assetTypeBO.getId().equals(assetTypeBO.getSysId()))
                    .collect(Collectors.toList());

            for (BuTrainAssetTypeBO sysBO : sysBOList) {
                setSysChildrenIdList(sysBO, assetTypeBOList);
                idSysBOMap.put(sysBO.getId(), sysBO);
            }

            redisUtil.del(CacheConstant.TRAIN_ASSET_TYPE_CACHE_SYS);
            String idSysBOMapJson = JSON.toJSONString(idSysBOMap);
            redisUtil.set(CacheConstant.TRAIN_ASSET_TYPE_CACHE_SYS, idSysBOMapJson);
        }

        return idSysBOMap;
    }


    private Map<String, BuTrainAssetTypeBO> transformToMap(List<BuTrainAssetTypeBO> assetTypeBOList) {
        if (CollectionUtils.isEmpty(assetTypeBOList)) {
            return new HashMap<>();
        }

        int mapSize = MapSizeUtil.tableSizeFor(assetTypeBOList.size());
        Map<String, BuTrainAssetTypeBO> assetTypeIdBOMap = new HashMap<>(mapSize);

        for (BuTrainAssetTypeBO assetTypeBO : assetTypeBOList) {
            assetTypeIdBOMap.put(assetTypeBO.getId(), assetTypeBO);
        }

        return assetTypeIdBOMap;
    }

    private void setSysIdName(List<BuTrainAssetTypeBO> assetTypeBOList) {
        if (CollectionUtils.isEmpty(assetTypeBOList)) {
            return;
        }

        for (BuTrainAssetTypeBO assetTypeBO : assetTypeBOList) {
            BuTrainAssetTypeBO sysBO = getSysBO(assetTypeBO, assetTypeBOList);
            if (null != sysBO) {
                assetTypeBO.setSysId(sysBO.getId())
                        .setSysName(sysBO.getName())
                        .setSysShortName(sysBO.getShortName());
            }

            BuTrainAssetTypeBO subSysBO = getSubSysBO(assetTypeBO, assetTypeBOList);
            if (null != subSysBO) {
                assetTypeBO.setSubSysId(subSysBO.getId())
                        .setSubSysName(subSysBO.getName())
                        .setSubSysShortName(subSysBO.getShortName());
            }
        }
    }

    private BuTrainAssetTypeBO getSysBO(BuTrainAssetTypeBO assetTypeBO, List<BuTrainAssetTypeBO> assetTypeBOList) {
        if (null == assetTypeBO) {
            return null;
        }

        Integer structType = assetTypeBO.getStructType();
        String parentId = assetTypeBO.getParentId();
        if (structType == 1 && StringUtils.isBlank(parentId)) {
            return assetTypeBO;
        } else {
            Optional<BuTrainAssetTypeBO> parentOptional = assetTypeBOList.stream()
                    .filter(assetType -> assetType.getId().equals(parentId))
                    .findFirst();

            return parentOptional.map(buTrainAssetTypeBO -> getSysBO(buTrainAssetTypeBO, assetTypeBOList))
                    .orElse(null);
        }
    }

    private BuTrainAssetTypeBO getSubSysBO(BuTrainAssetTypeBO assetTypeBO, List<BuTrainAssetTypeBO> assetTypeBOList) {
        if (null == assetTypeBO) {
            return null;
        }

        Integer structType = assetTypeBO.getStructType();
        String parentId = assetTypeBO.getParentId();
        if (structType == 1) {
            return null;
        } else if (structType == 2) {
            return assetTypeBO;
        } else {
            Optional<BuTrainAssetTypeBO> parentOptional = assetTypeBOList.stream()
                    .filter(assetType -> assetType.getId().equals(parentId))
                    .findFirst();

            return parentOptional.map(buTrainAssetTypeBO -> getSubSysBO(buTrainAssetTypeBO, assetTypeBOList))
                    .orElse(null);
        }
    }

    private void setSysChildrenIdList(BuTrainAssetTypeBO sysBO, List<BuTrainAssetTypeBO> assetTypeBOList) {
        Set<String> childrenIdSet = new HashSet<>(256);

        recurseAddChildIdSet(sysBO, assetTypeBOList, childrenIdSet);
        sysBO.setChildrenIdList(new ArrayList<>(childrenIdSet));
    }

    private void recurseAddChildIdSet(BuTrainAssetTypeBO parent, List<BuTrainAssetTypeBO> assetTypeBOList, Set<String> childrenIdSet) {
        String parentId = parent.getId();
        List<BuTrainAssetTypeBO> children = assetTypeBOList.stream()
                .filter(assetTypeBO -> parentId.equals(assetTypeBO.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuTrainAssetTypeBO child : children) {
                childrenIdSet.add(child.getId());
                recurseAddChildIdSet(child, assetTypeBOList, childrenIdSet);
            }
        }
    }

}
