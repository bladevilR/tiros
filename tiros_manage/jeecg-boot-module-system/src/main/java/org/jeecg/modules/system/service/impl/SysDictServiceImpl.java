package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.DictQuery;
import org.jeecg.modules.system.entity.SysDict;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.mapper.SysDictItemMapper;
import org.jeecg.modules.system.mapper.SysDictMapper;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
//    @Cacheable(value = CacheConstant.SYS_DICT_TYPE_CACHE, key = "#code")
    public Integer queryDictTypeByCode(String code) {
//        log.info("无缓存dictTypeCache的时候调用这里！");
        return sysDictMapper.queryDictTypeByCode(code);
    }

    @Override
    public Integer queryDictTypeById(String id) {

        return sysDictMapper.queryDictTypeById(id);
    }

    /**
     * 通过查询指定code 获取字典
     *
     * @param code
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_CACHE, key = "#code")
    public List<DictModel> queryDictItemsByCode(String code) {
        log.info("无缓存dictCache的时候调用这里！");
        return sysDictMapper.queryDictItemsByCode(code);
    }

    @Override
    public Map<String, Map<String, String>> queryDictItemsByCodeList(List<String> codeList) {
        Map<String, Map<String, String>> dictCodeValuesMap = new HashMap<>(8);

//        for (String code : codeList) {
//            Map<String, String> valueTextMap = new HashMap<>(4);
//            List<DictModel> dictModels = queryDictItemsByCode(code);
//            for (DictModel dictModel : dictModels) {
//                valueTextMap.put(dictModel.getValue(), dictModel.getText());
//            }
//            dictCodeValuesMap.put(code, valueTextMap);
//        }
        if (CollectionUtils.isEmpty(codeList)) {
            return dictCodeValuesMap;
        }

        List<DictModel> dictModelList = sysDictMapper.queryDictItemsByCodeList(codeList);
        if (CollectionUtils.isNotEmpty(dictModelList)) {
            Map<String, List<DictModel>> dictCodeModelListMap = dictModelList.stream()
                    .filter(dictModel -> StringUtils.isNotBlank(dictModel.getDictCode()))
                    .collect(Collectors.groupingBy(DictModel::getDictCode));
            for (Map.Entry<String, List<DictModel>> dictCodeModelListEntry : dictCodeModelListMap.entrySet()) {
                String dictCode = dictCodeModelListEntry.getKey();
                List<DictModel> modelList = dictCodeModelListEntry.getValue();

                Map<String, String> valueTextMap = new HashMap<>(8);
                for (DictModel model : modelList) {
                    valueTextMap.put(model.getValue(), model.getText());
                }
                dictCodeValuesMap.put(dictCode, valueTextMap);
            }
        }

        return dictCodeValuesMap;
    }

    @Override
    public Map<String, List<DictModel>> queryAllDictItems() {
        Map<String, List<DictModel>> res = new HashMap<String, List<DictModel>>();
        List<SysDict> ls = sysDictMapper.selectList(null);
        LambdaQueryWrapper<SysDictItem> queryWrapper = new LambdaQueryWrapper<SysDictItem>();
        queryWrapper.eq(SysDictItem::getStatus, 1);
        queryWrapper.orderByAsc(SysDictItem::getSortOrder);
        List<SysDictItem> sysDictItemList = sysDictItemMapper.selectList(queryWrapper);

        for (SysDict d : ls) {
            List<DictModel> dictModelList = sysDictItemList.stream().filter(s -> d.getId().equals(s.getDictId())).map(item -> {
                DictModel dictModel = new DictModel();
                dictModel.setText(item.getItemText());
                dictModel.setValue(item.getItemValue());
                return dictModel;
            }).collect(Collectors.toList());
            if (StringUtils.isNotBlank(d.getDictCode())) {
                res.put(d.getDictCode(), dictModelList);
            }
        }
        log.debug("-------登录加载系统字典-----" + res.toString());
        return res;
    }

    /**
     * 通过查询指定code 获取字典值text
     *
     * @param code
     * @param key
     * @return
     */

    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_CACHE, key = "#code+':'+#key")
    public String queryDictTextByKey(String code, String key) {
        log.info("无缓存dictText的时候调用这里！");
        return sysDictMapper.queryDictTextByKey(code, key);
    }

    /**
     * 通过查询指定table的 text code 获取字典
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @return
     */
    @Override
    // @Cacheable(cacheNames=CacheConstant.SYS_DICT_TABLE_CACHE, key="#table+'-'+#text+'-'+#code") // add by jakgong
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        log.info("无缓存dictTableList的时候调用这里！");
        return sysDictMapper.queryTableDictItemsByCode(table, text, code);
    }

    @Override
    // @Cacheable(cacheNames=CacheConstant.SYS_DICT_TABLE_CACHE, key="#table+'-'+#text+'-'+#code+'-'+#filterSql") // add by jakgong
    public List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql) {
        log.info("无缓存dictTableList的时候调用这里！");
        return sysDictMapper.queryTableDictItemsByCodeAndFilter(table, text, code, filterSql);
    }

    @Override
    // @Cacheable(cacheNames=CacheConstant.SYS_DICT_TABLE_CACHE, key="#table+'-'+#text+'-'+#code+'-'+#extendFieldsStr+'-'+#filterSql") // add by jakgong
    public List<DictModel> queryTableDictItemsAndExtendFieldsByCodeAndFilter(String table, String text, String code, String extendFieldsStr, String filterSql) {
        List<Map> mapList = sysDictMapper.queryTableDictItemsAndExtendFieldsByCodeAndFilter(table, text, code, extendFieldsStr, filterSql);

        List<DictModel> resultList = new ArrayList<>();
        for (Map map : mapList) {
            Map<String, Object> fieldMap = map;

            DictModel dictModel = new DictModel();
            dictModel.setValue(fieldMap.get("value").toString());
            dictModel.setText(fieldMap.get("text").toString());

            fieldMap.remove("value");
            fieldMap.remove("text");
            if (!fieldMap.isEmpty()) {
                Map<String, Object> lowerCaseMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                    String key = entry.getKey();
                    String lowerCaseKey = key.toLowerCase();

                    lowerCaseMap.put(lowerCaseKey, entry.getValue());
                }
                fieldMap = lowerCaseMap;
            }

            dictModel.setExtFields(fieldMap);
            resultList.add(dictModel);
        }

        return resultList;
    }

    /**
     * 通过查询指定table的 text code 获取字典值text
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
    public String queryTableDictTextByKey(String table, String text, String code, String key) {
        log.info("无缓存dictTable的时候调用这里！");
        return sysDictMapper.queryTableDictTextByKey(table, text, code, key);
    }

    /**
     * 通过查询指定table的 text code 获取字典，包含text和value
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @param keyArray
     * @return
     */
    @Override
//    @Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
        List<DictModel> dicts = sysDictMapper.queryTableDictByKeys(table, text, code, keyArray);
        List<String> texts = new ArrayList<>(dicts.size());
        // 查询出来的顺序可能是乱的，需要排个序
        for (String key : keyArray) {
            for (DictModel dict : dicts) {
                if (key.equals(dict.getValue())) {
                    texts.add(dict.getText());
                    break;
                }
            }
        }
        return texts;
    }

    /**
     * 根据字典类型id删除关联表中其对应的数据
     */
    @Override
    public boolean deleteByDictId(SysDict sysDict) {
        sysDict.setDelFlag(CommonConstant.DEL_FLAG_1);
        return this.updateById(sysDict);
    }

    @Override
    @Transactional
    public Integer saveMain(SysDict sysDict, List<SysDictItem> sysDictItemList) {
        int insert = 0;
        try {
            insert = sysDictMapper.insert(sysDict);
            if (sysDictItemList != null) {
                for (SysDictItem entity : sysDictItemList) {
                    entity.setDictId(sysDict.getId());
                    entity.setStatus(1);
                    sysDictItemMapper.insert(entity);
                }
            }
        } catch (Exception e) {
            return insert;
        }
        return insert;
    }

    @Override
    public List<DictModel> queryAllDepartBackDictModel() {
        return baseMapper.queryAllDepartBackDictModel();
    }

    @Override
    public List<DictModel> queryAllUserBackDictModel() {
        return baseMapper.queryAllUserBackDictModel();
    }

    @Override
    public List<DictModel> queryTableDictItems(String table, String text, String code, String keyword) {
        return baseMapper.queryTableDictItems(table, text, code, "%" + keyword + "%");
    }

    @Override
    public List<TreeSelectModel> queryTreeList(Map<String, String> query, String table, String text, String code, String pidField, String pid, String hasChildField) {
        return baseMapper.queryTreeList(query, table, text, code, pidField, pid, hasChildField);
    }

    @Override
    public void deleteOneDictPhysically(String id) {
        this.baseMapper.deleteOneById(id);
        this.sysDictItemMapper.delete(new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictId, id));
    }

    @Override
    public void updateDictDelFlag(int delFlag, String id) {
        baseMapper.updateDictDelFlag(delFlag, id);
    }

    @Override
    public List<SysDict> queryDeleteList() {
        return baseMapper.queryDeleteList();
    }

    @Override
    public List<DictModel> queryDictTablePageList(DictQuery query, int pageSize, int pageNo) {
        Page page = new Page(pageNo, pageSize, false);
        Page<DictModel> pageList = baseMapper.queryDictTablePageList(page, query);
        return pageList.getRecords();
    }
}
