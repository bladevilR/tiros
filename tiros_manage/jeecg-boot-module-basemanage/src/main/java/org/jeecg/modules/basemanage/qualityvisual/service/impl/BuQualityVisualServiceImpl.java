package org.jeecg.modules.basemanage.qualityvisual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.qualityvisual.entity.BuQualityVisual;
import org.jeecg.modules.basemanage.qualityvisual.mapper.BuQualityVisualMapper;
import org.jeecg.modules.basemanage.qualityvisual.service.IBuQualityVisualService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuQualityVisualServiceImpl extends ServiceImpl<BuQualityVisualMapper, BuQualityVisual> implements IBuQualityVisualService {

    @Override
    public IPage<BuQualityVisual> queryPage(BuQualityVisual query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuQualityVisual> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        if (query != null) {
            if (StringUtils.isNotBlank(query.getProjectName())) {
                queryWrapper.like("project_name", query.getProjectName());
            }
            if (StringUtils.isNotBlank(query.getTrainType())) {
                queryWrapper.like("train_type", query.getTrainType());
            }
            if (StringUtils.isNotBlank(query.getTrainNo())) {
                queryWrapper.like("train_no", query.getTrainNo());
            }
            if (StringUtils.isNotBlank(query.getQualityLevel())) {
                queryWrapper.eq("quality_level", query.getQualityLevel());
            }
            if (StringUtils.isNotBlank(query.getPlanId())) {
                queryWrapper.eq("plan_id", query.getPlanId());
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<BuQualityVisual> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuQualityVisual record) {
        if (record == null) {
            throw new JeecgBootException("参数不能为空");
        }
        if (record.getCompletedProcesses() != null && record.getCompletedProcesses() < 0) {
            throw new JeecgBootException("已完成工序数不能小于0");
        }
        if (record.getTotalProcesses() != null && record.getTotalProcesses() < 0) {
            throw new JeecgBootException("总工序数不能小于0");
        }
        if (record.getQualityIssues() != null && record.getQualityIssues() < 0) {
            throw new JeecgBootException("质量问题数不能小于0");
        }
        if (record.getOpenIssues() != null && record.getOpenIssues() < 0) {
            throw new JeecgBootException("未关闭问题数不能小于0");
        }
        record.setCreateTime(new Date());
        if (record.getDelFlag() == null) {
            record.setDelFlag(0);
        }
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuQualityVisual record) {
        if (record == null || StringUtils.isBlank(record.getId())) {
            throw new JeecgBootException("质量可视化ID不能为空");
        }
        if (record.getCompletedProcesses() != null && record.getCompletedProcesses() < 0) {
            throw new JeecgBootException("已完成工序数不能小于0");
        }
        if (record.getTotalProcesses() != null && record.getTotalProcesses() < 0) {
            throw new JeecgBootException("总工序数不能小于0");
        }
        if (record.getQualityIssues() != null && record.getQualityIssues() < 0) {
            throw new JeecgBootException("质量问题数不能小于0");
        }
        if (record.getOpenIssues() != null && record.getOpenIssues() < 0) {
            throw new JeecgBootException("未关闭问题数不能小于0");
        }
        record.setUpdateTime(new Date());
        return this.updateById(record);
    }

    @Override
    public boolean deleteRecord(String ids) {
        List<String> idArray = parseIdList(ids);
        if (idArray.isEmpty()) {
            throw new JeecgBootException("质量可视化ID不能为空");
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
