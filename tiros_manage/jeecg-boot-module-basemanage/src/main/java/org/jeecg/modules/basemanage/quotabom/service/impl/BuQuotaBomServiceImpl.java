package org.jeecg.modules.basemanage.quotabom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.basemanage.quotabom.entity.BuQuotaBom;
import org.jeecg.modules.basemanage.quotabom.mapper.BuQuotaBomMapper;
import org.jeecg.modules.basemanage.quotabom.service.IBuQuotaBomService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class BuQuotaBomServiceImpl extends ServiceImpl<BuQuotaBomMapper, BuQuotaBom> implements IBuQuotaBomService {

    @Override
    public IPage<BuQuotaBom> queryPage(BuQuotaBom query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuQuotaBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        queryWrapper.orderByDesc("create_time");
        Page<BuQuotaBom> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuQuotaBom record) {
        record.setCreateTime(new Date());
        record.setDelFlag(0);
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuQuotaBom record) {
        record.setUpdateTime(new Date());
        return this.updateById(record);
    }

    @Override
    public boolean deleteRecord(String ids) {
        if (StringUtils.isBlank(ids)) {
            return false;
        }
        String[] idArray = ids.split(",");
        return this.removeByIds(Arrays.asList(idArray));
    }
}
