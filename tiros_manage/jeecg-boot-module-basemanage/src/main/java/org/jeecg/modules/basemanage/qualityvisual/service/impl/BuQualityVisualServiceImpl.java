package org.jeecg.modules.basemanage.qualityvisual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.basemanage.qualityvisual.entity.BuQualityVisual;
import org.jeecg.modules.basemanage.qualityvisual.mapper.BuQualityVisualMapper;
import org.jeecg.modules.basemanage.qualityvisual.service.IBuQualityVisualService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class BuQualityVisualServiceImpl extends ServiceImpl<BuQualityVisualMapper, BuQualityVisual> implements IBuQualityVisualService {

    @Override
    public IPage<BuQualityVisual> queryPage(BuQualityVisual query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuQualityVisual> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        queryWrapper.orderByDesc("create_time");
        Page<BuQualityVisual> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuQualityVisual record) {
        record.setCreateTime(new Date());
        record.setDelFlag(0);
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuQualityVisual record) {
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
