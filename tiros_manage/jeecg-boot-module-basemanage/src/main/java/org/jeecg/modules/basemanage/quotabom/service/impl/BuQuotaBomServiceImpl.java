package org.jeecg.modules.basemanage.quotabom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.quotabom.entity.BuQuotaBom;
import org.jeecg.modules.basemanage.quotabom.mapper.BuQuotaBomMapper;
import org.jeecg.modules.basemanage.quotabom.service.IBuQuotaBomService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
        return this.removeByIds(idArray);
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
}
