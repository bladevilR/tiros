package org.jeecg.modules.basemanage.quotabom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.quotabom.entity.BuQuotaBom;
import org.jeecg.modules.basemanage.quotabom.entity.vo.BuQuotaBomTreeNodeVO;
import org.jeecg.modules.basemanage.quotabom.mapper.BuQuotaBomMapper;
import org.jeecg.modules.basemanage.quotabom.service.IBuQuotaBomService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuQuotaBomServiceImpl extends ServiceImpl<BuQuotaBomMapper, BuQuotaBom> implements IBuQuotaBomService {

    @Override
    public IPage<BuQuotaBom> queryPage(BuQuotaBom query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuQuotaBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        if (query != null) {
            if (StringUtils.isNotBlank(query.getBomCode())) {
                queryWrapper.like("bom_code", query.getBomCode());
            }
            if (StringUtils.isNotBlank(query.getBomName())) {
                queryWrapper.like("bom_name", query.getBomName());
            }
            if (StringUtils.isNotBlank(query.getTrainType())) {
                queryWrapper.like("train_type", query.getTrainType());
            }
            if (StringUtils.isNotBlank(query.getLine())) {
                queryWrapper.like("line", query.getLine());
            }
            if (StringUtils.isNotBlank(query.getPosition())) {
                queryWrapper.like("position", query.getPosition());
            }
            if (StringUtils.isNotBlank(query.getSystem())) {
                queryWrapper.like("system", query.getSystem());
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<BuQuotaBom> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuQuotaBom record) {
        if (record == null) {
            throw new JeecgBootException("参数不能为空");
        }
        record.setCreateTime(new Date());
        if (record.getDelFlag() == null) {
            record.setDelFlag(0);
        }
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuQuotaBom record) {
        if (record == null || StringUtils.isBlank(record.getId())) {
            throw new JeecgBootException("定额BOM ID不能为空");
        }
        record.setUpdateTime(new Date());
        return this.updateById(record);
    }

    @Override
    public boolean deleteRecord(String ids) {
        List<String> idArray = parseIdList(ids);
        if (idArray.isEmpty()) {
            throw new JeecgBootException("定额BOM ID不能为空");
        }
        Date now = new Date();
        UpdateWrapper<BuQuotaBom> wrapper = new UpdateWrapper<>();
        wrapper.in("id", idArray)
                .eq("del_flag", 0)
                .set("del_flag", 1)
                .set("update_time", now);
        this.update(wrapper);
        return true;
    }

    @Override
    public List<BuQuotaBomTreeNodeVO> listTree(String trainType) {
        QueryWrapper<BuQuotaBom> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", 0);
        if (StringUtils.isNotBlank(trainType)) {
            wrapper.like("train_type", trainType);
        }
        wrapper.orderByAsc("train_type")
                .orderByAsc("line")
                .orderByAsc("position")
                .orderByAsc("system")
                .orderByAsc("module_level2")
                .orderByAsc("module_level3")
                .orderByAsc("bom_code");
        List<BuQuotaBom> list = this.list(wrapper);

        Map<String, BuQuotaBomTreeNodeVO> topLevel = new LinkedHashMap<>();
        Map<String, BuQuotaBomTreeNodeVO> all = new LinkedHashMap<>();

        for (BuQuotaBom item : list) {
            String l0 = safe(item.getTrainType(), "未分类车型");
            String l1 = safe(item.getLine(), "未分类线别");
            String l2 = safe(item.getPosition(), "未分类位置");
            String l3 = safe(item.getSystem(), "未分类系统");
            String l4 = safe(item.getModuleLevel2(), "未分类二级模块");
            String l5 = safe(item.getModuleLevel3(), "未分类三级模块");

            String k0 = "T:" + l0;
            String k1 = k0 + "|L:" + l1;
            String k2 = k1 + "|P:" + l2;
            String k3 = k2 + "|S:" + l3;
            String k4 = k3 + "|M2:" + l4;
            String k5 = k4 + "|M3:" + l5;
            String k6 = k5 + "|B:" + item.getId();

            BuQuotaBomTreeNodeVO n0 = createNode(all, topLevel, null, k0, l0, 0, null);
            BuQuotaBomTreeNodeVO n1 = createNode(all, null, n0, k1, l1, 1, null);
            BuQuotaBomTreeNodeVO n2 = createNode(all, null, n1, k2, l2, 2, null);
            BuQuotaBomTreeNodeVO n3 = createNode(all, null, n2, k3, l3, 3, null);
            BuQuotaBomTreeNodeVO n4 = createNode(all, null, n3, k4, l4, 4, null);
            BuQuotaBomTreeNodeVO n5 = createNode(all, null, n4, k5, l5, 5, null);

            String bomTitle = safe(item.getBomCode(), "-") + " - " + safe(item.getBomName(), "未命名BOM");
            createNode(all, null, n5, k6, bomTitle, 6, item.getId());
        }

        return topLevel.values().stream().collect(Collectors.toList());
    }

    private List<String> parseIdList(String ids) {
        if (StringUtils.isBlank(ids)) {
            return Collections.emptyList();
        }
        return Arrays.stream(ids.split(","))
                .map(StringUtils::trimToNull)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    private BuQuotaBomTreeNodeVO createNode(Map<String, BuQuotaBomTreeNodeVO> all,
                                            Map<String, BuQuotaBomTreeNodeVO> topLevel,
                                            BuQuotaBomTreeNodeVO parent,
                                            String key,
                                            String title,
                                            Integer level,
                                            String bomId) {
        BuQuotaBomTreeNodeVO node = all.get(key);
        if (node != null) {
            return node;
        }
        node = new BuQuotaBomTreeNodeVO();
        node.setKey(key);
        node.setTitle(title);
        node.setLevel(level);
        node.setBomId(bomId);
        if (parent != null) {
            node.setParentKey(parent.getKey());
            parent.getChildren().add(node);
        } else if (topLevel != null) {
            topLevel.put(key, node);
        }
        all.put(key, node);
        return node;
    }

    private String safe(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }
}
