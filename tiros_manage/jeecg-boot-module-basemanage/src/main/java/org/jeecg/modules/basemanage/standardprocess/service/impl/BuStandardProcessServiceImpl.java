package org.jeecg.modules.basemanage.standardprocess.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.standardprocess.entity.BuStandardProcess;
import org.jeecg.modules.basemanage.standardprocess.mapper.BuStandardProcessMapper;
import org.jeecg.modules.basemanage.standardprocess.service.IBuStandardProcessService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuStandardProcessServiceImpl extends ServiceImpl<BuStandardProcessMapper, BuStandardProcess> implements IBuStandardProcessService {

    @Override
    public IPage<BuStandardProcess> queryPage(BuStandardProcess query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuStandardProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        if (query != null) {
            if (StringUtils.isNotBlank(query.getProcessNo())) {
                queryWrapper.like("process_no", query.getProcessNo());
            }
            if (StringUtils.isNotBlank(query.getProcessName())) {
                queryWrapper.like("process_name", query.getProcessName());
            }
            if (StringUtils.isNotBlank(query.getProcessType())) {
                queryWrapper.eq("process_type", query.getProcessType());
            }
            if (StringUtils.isNotBlank(query.getTrainType())) {
                queryWrapper.like("train_type", query.getTrainType());
            }
            if (query.getStandardDuration() != null) {
                queryWrapper.eq("standard_duration", query.getStandardDuration());
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<BuStandardProcess> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuStandardProcess record) {
        if (record == null) {
            throw new JeecgBootException("参数不能为空");
        }
        record.setCreateTime(new Date());
        if (record.getDelFlag() == null) {
            record.setDelFlag(0);
        }
        if (record.getStandardDuration() != null && record.getStandardDuration() < 0) {
            throw new JeecgBootException("标准工时不能小于0");
        }
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuStandardProcess record) {
        if (record == null || StringUtils.isBlank(record.getId())) {
            throw new JeecgBootException("标准工序ID不能为空");
        }
        if (record.getStandardDuration() != null && record.getStandardDuration() < 0) {
            throw new JeecgBootException("标准工时不能小于0");
        }
        record.setUpdateTime(new Date());
        return this.updateById(record);
    }

    @Override
    public boolean deleteRecord(String ids) {
        List<String> idArray = parseIdList(ids);
        if (idArray.isEmpty()) {
            throw new JeecgBootException("标准工序ID不能为空");
        }
        Date now = new Date();
        UpdateWrapper<BuStandardProcess> wrapper = new UpdateWrapper<>();
        wrapper.in("id", idArray)
                .eq("del_flag", 0)
                .set("del_flag", 1)
                .set("update_time", now);
        this.update(wrapper);
        return true;
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
