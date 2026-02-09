package org.jeecg.modules.basemanage.qualityvisual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.qualityvisual.entity.BuQualityVisual;
import org.jeecg.modules.basemanage.qualityvisual.mapper.BuQualityVisualMapper;
import org.jeecg.modules.basemanage.qualityvisual.service.IBuQualityVisualService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuQualityVisualServiceImpl extends ServiceImpl<BuQualityVisualMapper, BuQualityVisual> implements IBuQualityVisualService {

    @Resource
    private JdbcTemplate jdbcTemplate;

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
        Date now = new Date();
        UpdateWrapper<BuQualityVisual> wrapper = new UpdateWrapper<>();
        wrapper.in("id", idArray)
                .eq("del_flag", 0)
                .set("del_flag", 1)
                .set("update_time", now);
        this.update(wrapper);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BuQualityVisual refreshByPlanAndTrain(String planId, String trainNo, String trainType, String projectName) {
        if (StringUtils.isBlank(planId) || StringUtils.isBlank(trainNo)) {
            throw new JeecgBootException("计划ID和车号不能为空");
        }

        Integer totalProcesses = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_work_order WHERE plan_id = ? AND train_no = ? AND order_status <> 9",
                planId, trainNo));
        Integer completedProcesses = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_work_order WHERE plan_id = ? AND train_no = ? AND order_status = 4",
                planId, trainNo));

        Integer qualityIssues = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_fault_info WHERE plan_id = ? AND train_no = ? AND submit_status = 1",
                planId, trainNo));

        Integer openIssues = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_work_leave_record lr " +
                        "LEFT JOIN bu_work_order o ON o.id = lr.order_id " +
                        "WHERE o.plan_id = ? AND o.train_no = ? AND lr.status = 0",
                planId, trainNo));

        String progress = "0%";
        if (totalProcesses > 0) {
            progress = Math.round((completedProcesses * 100.0D) / totalProcesses) + "%";
        }

        String level;
        if (openIssues > 0) {
            level = "D";
        } else if (qualityIssues > 3) {
            level = "C";
        } else if (qualityIssues > 0) {
            level = "B";
        } else {
            level = "A";
        }

        QueryWrapper<BuQualityVisual> query = new QueryWrapper<>();
        query.eq("plan_id", planId)
                .eq("train_no", trainNo)
                .eq("del_flag", 0)
                .orderByDesc("create_time");
        List<BuQualityVisual> existingList = this.list(query);
        BuQualityVisual existing = existingList.isEmpty() ? null : existingList.get(0);

        Date now = new Date();
        if (existing == null) {
            BuQualityVisual visual = new BuQualityVisual();
            visual.setPlanId(planId);
            visual.setTrainNo(trainNo);
            visual.setTrainType(trainType);
            visual.setProjectName(StringUtils.isBlank(projectName) ? "列车质量可视化" : projectName);
            visual.setCompletedProcesses(completedProcesses);
            visual.setTotalProcesses(totalProcesses);
            visual.setProgress(progress);
            visual.setQualityIssues(qualityIssues);
            visual.setOpenIssues(openIssues);
            visual.setQualityLevel(level);
            visual.setProcessFlow(buildProcessFlowJson(planId, trainNo));
            visual.setQualityPoints(buildQualityPointsJson(planId, trainNo));
            visual.setCreateTime(now);
            visual.setUpdateTime(now);
            visual.setDelFlag(0);
            this.save(visual);
            return visual;
        }

        existing.setTrainType(StringUtils.isBlank(trainType) ? existing.getTrainType() : trainType);
        if (StringUtils.isNotBlank(projectName)) {
            existing.setProjectName(projectName);
        }
        existing.setCompletedProcesses(completedProcesses);
        existing.setTotalProcesses(totalProcesses);
        existing.setProgress(progress);
        existing.setQualityIssues(qualityIssues);
        existing.setOpenIssues(openIssues);
        existing.setQualityLevel(level);
        existing.setProcessFlow(buildProcessFlowJson(planId, trainNo));
        existing.setQualityPoints(buildQualityPointsJson(planId, trainNo));
        existing.setUpdateTime(now);
        this.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BuQualityVisual> batchRefreshByPlan(String planId) {
        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("计划ID不能为空");
        }
        List<String> trains = jdbcTemplate.queryForList(
                "SELECT DISTINCT train_no FROM bu_work_order WHERE plan_id = ? AND train_no IS NOT NULL AND train_no <> '' AND order_status <> 9",
                String.class,
                planId
        );
        return trains.stream()
                .map(trainNo -> refreshByPlanAndTrain(planId, trainNo, null, "列车质量可视化"))
                .collect(Collectors.toList());
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

    private Integer countInt(String sql, Object... args) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return result == null ? 0 : result;
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private String buildProcessFlowJson(String planId, String trainNo) {
        Integer total = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_work_order WHERE plan_id = ? AND train_no = ? AND order_status <> 9",
                planId, trainNo));
        Integer closed = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_work_order WHERE plan_id = ? AND train_no = ? AND order_status = 4",
                planId, trainNo));
        return "{\"total\":" + total + ",\"closed\":" + closed + "}";
    }

    private String buildQualityPointsJson(String planId, String trainNo) {
        Integer faultTotal = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_fault_info WHERE plan_id = ? AND train_no = ? AND submit_status = 1",
                planId, trainNo));
        Integer openTotal = defaultZero(countInt(
                "SELECT COUNT(1) FROM bu_work_leave_record lr " +
                        "LEFT JOIN bu_work_order o ON o.id = lr.order_id " +
                        "WHERE o.plan_id = ? AND o.train_no = ? AND lr.status = 0",
                planId, trainNo));
        return "{\"fault\":" + faultTotal + ",\"open\":" + openTotal + "}";
    }
}
