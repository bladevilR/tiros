package org.jeecg.modules.tiros.cache.dict;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.tiros.cache.dict.DictCacheService;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.mapper.SysDictItemMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典sys_dict 缓存服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Service
public class DictCacheServiceImpl implements DictCacheService {

    @Resource
    private SysDictItemMapper sysDictItemMapper;
    @Resource
    private RedisUtil redisUtil;


    /**
     * @see DictCacheService#mapAll()
     */
    @Override
    public Map<String, Map<String, String>> mapAll() {
        Map<String, Map<String, String>> dictCodeItemMap = new HashMap<>();

        Object cacheObject = redisUtil.get(CacheConstant.DICT_ITEM_CACHE);
        if (null == cacheObject) {
            dictCodeItemMap = update();
        } else {
            if (cacheObject instanceof String) {
                String jsonString = (String) cacheObject;
                dictCodeItemMap = JSON.parseObject(jsonString, new TypeReference<Map<String, Map<String, String>>>() {
                });
            }
        }

        return dictCodeItemMap;
    }

    /**
     * @see DictCacheService#update()
     */
    @Override
    public Map<String, Map<String, String>> update() {
        List<SysDictItem> dictItemList = sysDictItemMapper.selectAllList();
        Map<String, Map<String, String>> dictCodeItemMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(dictItemList)) {
            for (SysDictItem dictItem : dictItemList) {
                String dictCode = dictItem.getDictCode();
                String itemValue = dictItem.getItemValue();
                String itemText = dictItem.getItemText();

                Map<String, String> itemValueTextMap = dictCodeItemMap.get(dictCode);
                if (null == itemValueTextMap) {
                    itemValueTextMap = new HashMap<>(4);
                }
                itemValueTextMap.put(itemValue, itemText);
                dictCodeItemMap.put(dictCode, itemValueTextMap);
            }
        }

        String jsonString = JSON.toJSONString(dictCodeItemMap);
        redisUtil.set(CacheConstant.DICT_ITEM_CACHE, jsonString);

        return dictCodeItemMap;
    }

}
