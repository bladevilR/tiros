package org.jeecg.modules.basemanage.standardprocess.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.basemanage.standardprocess.entity.BuStandardProcess;
import org.jeecg.modules.basemanage.standardprocess.mapper.BuStandardProcessMapper;
import org.jeecg.modules.basemanage.standardprocess.service.IBuStandardProcessService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class BuStandardProcessServiceImpl extends ServiceImpl<BuStandardProcessMapper, BuStandardProcess> implements IBuStandardProcessService {

    @Override
    public IPage<BuStandardProcess> queryPage(BuStandardProcess query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuStandardProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        queryWrapper.orderByDesc("create_time");
        Page<BuStandardProcess> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuStandardProcess record) {
        record.setCreateTime(new Date());
        record.setDelFlag(0);
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuStandardProcess record) {
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
