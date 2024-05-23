package org.jeecg.modules.tiros.wbs.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.modules.tiros.wbs.bean.WbsPO;
import org.jeecg.modules.tiros.wbs.mapper.WbsMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * wbs更新工具
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-29
 */
@Component
public class WbsServiceImpl implements WbsService {

    @Resource
    private WbsMapper wbsMapper;


    /**
     * @see WbsService#updateWbs(WbsConf)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWbs(WbsConf wbsConf) {
//        List<WbsPO> wbsPOTree = wbsMapper.selectWbsPOTree(wbsConf);
//        // 去掉所有子节点
//        Set<String> idSet = new HashSet<>();
//        recurseAddId(wbsPOTree, idSet);
//        List<WbsPO> topList = wbsPOTree.stream()
//                .filter(wbsPO -> !idSet.contains(wbsPO.getParentId()))
//                .collect(Collectors.toList());
//        setTopParentWbs(topList, wbsConf);
        List<WbsPO> wbsPOList = wbsMapper.selectWbsPOList(wbsConf);
        Set<String> idSet = wbsPOList.stream()
                .map(WbsPO::getId)
                .collect(Collectors.toSet());
        List<WbsPO> topList = wbsPOList.stream()
                .filter(wbsPO -> !idSet.contains(wbsPO.getParentId()))
                .collect(Collectors.toList());
        buildTree(topList, wbsPOList);
        setTopParentWbs(topList, wbsConf);

        // 获取新wbs
        Integer wbsType = wbsConf.getWbsType();
        if (null == wbsType || wbsType == 1) {
            for (WbsPO top : topList) {
                recurseSetCodeWbs(top, top.getParentWbs());
            }
        } else if (wbsType == 2) {
            AtomicInteger index = new AtomicInteger(1);
            for (WbsPO top : topList) {
                recurseSetNumberWbs(top, top.getParentWbs(), index.getAndIncrement());
            }
        }

        // 更新需要更新的
        List<WbsPO> allWbsPOList = new ArrayList<>();
        extractToOneLayer(topList, allWbsPOList);
        List<WbsPO> needUpdateWbsPOList = filterNeedUpdateList(allWbsPOList);
        if (CollectionUtils.isNotEmpty(needUpdateWbsPOList)) {
            // 报异常：不允许多sql(multi-statement not allow)，需修改配置如下：
            // 1、DB的URL参数加上&allowMultiQueries=true
            // 2、pring.datasource.druid.filters=stat,wall 删除wall
            List<List<WbsPO>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateWbsPOList);
            for (List<WbsPO> batchSub : batchSubList) {
                wbsMapper.updateWbsList(batchSub, wbsConf.getTable(), wbsConf.getId(), wbsConf.getWbs());
            }
        }

        return true;
    }


    private void recurseAddId(List<WbsPO> wbsPOList, Set<String> idSet) {
        if (CollectionUtils.isNotEmpty(wbsPOList)) {
            for (WbsPO wbsPO : wbsPOList) {
                idSet.add(wbsPO.getId());
                List<WbsPO> children = wbsPO.getChildren();
                recurseAddId(children, idSet);
            }
        }
    }

    private void buildTree(List<WbsPO> parentList, List<WbsPO> wbsPOList) {
        for (WbsPO parent : parentList) {
            List<WbsPO> children = wbsPOList.stream()
                    .filter(wbsPO -> parent.getId().equals(wbsPO.getParentId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(children)) {
                buildTree(children, wbsPOList);
            }
            parent.setChildren(children);
        }
    }

    private void setTopParentWbs(List<WbsPO> topList, WbsConf wbsConf) {
        if (CollectionUtils.isNotEmpty(topList)) {
            List<String> parentIdList = topList.stream()
                    .map(WbsPO::getParentId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            List<WbsPO> wbsPOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(parentIdList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(parentIdList);
                for (List<String> batchSub : batchSubList) {
                    List<WbsPO> subWbsPOList = wbsMapper.selectByIdList(batchSub, wbsConf);
                    wbsMapper.selectByIdList(batchSub, wbsConf);
                    wbsPOList.addAll(subWbsPOList);
                }
            }
            Map<String, String> idWbsMap = new HashMap<>();
            wbsPOList.forEach(wbsPO -> idWbsMap.put(wbsPO.getId(), wbsPO.getWbs()));

            for (WbsPO wbsPO : topList) {
                String parentId = wbsPO.getParentId();
                if (StringUtils.isBlank(parentId)) {
                    wbsPO.setParentWbs("");
                } else {
                    String parentWbs = idWbsMap.get(parentId);
                    if (null == parentWbs) {
                        wbsPO.setParentWbs("");
                    } else {
                        wbsPO.setParentWbs(parentWbs);
                    }
                }
            }
        }
    }

    private void recurseSetCodeWbs(WbsPO wbsPO, String parentWbs) {
        if (null != wbsPO) {
            String newWbs;
            if (StringUtils.isBlank(parentWbs)) {
                newWbs = wbsPO.getCode();
            } else {
                newWbs = parentWbs + "." + wbsPO.getCode();
            }
            wbsPO.setNewWbs(newWbs);
            List<WbsPO> children = wbsPO.getChildren();
            for (WbsPO child : children) {
                recurseSetCodeWbs(child, newWbs);
            }
        }
    }

    private void recurseSetNumberWbs(WbsPO wbsPO, String parentWbs, int index) {
        if (null != wbsPO) {
            String newWbs;
            if (StringUtils.isBlank(parentWbs)) {
                newWbs = index + "";
            } else {
                newWbs = parentWbs + "." + index;
            }
            wbsPO.setNewWbs(newWbs);
            List<WbsPO> children = wbsPO.getChildren();
            AtomicInteger childIndex = new AtomicInteger(1);
            for (WbsPO child : children) {
                recurseSetNumberWbs(child, newWbs, childIndex.getAndIncrement());
            }
        }
    }

    private void extractToOneLayer(List<WbsPO> wbsPOList, List<WbsPO> allWbsPOList) {
        if (CollectionUtils.isNotEmpty(wbsPOList)) {
            for (WbsPO wbsPO : wbsPOList) {
                List<WbsPO> children = wbsPO.getChildren();
                extractToOneLayer(children, allWbsPOList);

                wbsPO.setChildren(null);
                allWbsPOList.add(wbsPO);
            }
        }
    }

    private List<WbsPO> filterNeedUpdateList(List<WbsPO> allWbsPOList) {
        List<WbsPO> needUpdateWbsPOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(allWbsPOList)) {
            for (WbsPO wbsPO : allWbsPOList) {
                if (null == wbsPO.getWbs() || !wbsPO.getWbs().equals(wbsPO.getNewWbs())) {
                    wbsPO.setWbs(wbsPO.getNewWbs());
                    needUpdateWbsPOList.add(wbsPO);
                }
            }
        }

        return needUpdateWbsPOList;
    }

}
