package org.jeecg.modules.basemanage.qualityvisual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.qualityvisual.entity.BuQualityVisual;
import org.jeecg.modules.basemanage.qualityvisual.entity.vo.BuQualityPlanningItemVO;
import org.jeecg.modules.basemanage.qualityvisual.entity.vo.BuQualityPlanningSummaryVO;
import org.jeecg.modules.basemanage.qualityvisual.entity.vo.BuQualityVisualProcessStepVO;
import org.jeecg.modules.basemanage.qualityvisual.mapper.BuQualityVisualMapper;
import org.jeecg.modules.basemanage.qualityvisual.service.IBuQualityVisualService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public List<BuQualityVisualProcessStepVO> listProcessSteps(String planId, String trainNo) {
        if (StringUtils.isBlank(planId)) {
            throw new JeecgBootException("计划ID不能为空");
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT o.id AS order_id, o.order_code, o.order_name, o.order_status, ")
                .append("o.start_time, o.finish_time, ")
                .append("t.id AS task_id, t.task_no, t.task_name, ")
                .append("COUNT(DISTINCT d.id) AS total_points, ")
                .append("COUNT(DISTINCT CASE WHEN r.id IS NOT NULL THEN d.id END) AS checked_points, ")
                .append("COUNT(DISTINCT CASE WHEN f.submit_status = 1 THEN f.id END) AS quality_issue_count, ")
                .append("COUNT(DISTINCT CASE WHEN l.status = 0 THEN l.id END) AS open_item_count ")
                .append("FROM bu_work_order o ")
                .append("LEFT JOIN bu_work_order_task t ON t.order_id = o.id ")
                .append("LEFT JOIN bu_work_order_task_form_inst fi ON fi.work_order_task_id = t.id AND fi.inst_type = 3 ")
                .append("LEFT JOIN bu_pl_work_record_detail d ON d.work_record_id = fi.form_inst_id ")
                .append("LEFT JOIN bu_pl_work_record_result r ON r.record_detail_id = d.id ")
                .append("LEFT JOIN bu_fault_info f ON f.work_order_id = o.id AND f.order_task_id = t.id ")
                .append("LEFT JOIN bu_work_leave_record l ON l.order_id = o.id AND l.order_task_id = t.id ")
                .append("WHERE o.plan_id = ? AND o.order_status <> 9 ");

        Object[] args;
        int[] argTypes;
        if (StringUtils.isNotBlank(trainNo)) {
            sqlBuilder.append("AND o.train_no = ? ");
            args = new Object[]{planId, trainNo};
            argTypes = new int[]{Types.VARCHAR, Types.VARCHAR};
        } else {
            args = new Object[]{planId};
            argTypes = new int[]{Types.VARCHAR};
        }

        sqlBuilder.append("GROUP BY o.id, o.order_code, o.order_name, o.order_status, o.start_time, o.finish_time, t.id, t.task_no, t.task_name ")
                .append("ORDER BY o.order_code, t.task_no");

        return jdbcTemplate.query(sqlBuilder.toString(), args, argTypes, (rs, rowNum) -> {
            BuQualityVisualProcessStepVO item = new BuQualityVisualProcessStepVO();
            item.setOrderId(rs.getString("order_id"));
            item.setOrderCode(rs.getString("order_code"));
            item.setOrderName(rs.getString("order_name"));
            item.setOrderStatus(rs.getObject("order_status") == null ? null : rs.getInt("order_status"));
            item.setTaskId(rs.getString("task_id"));
            item.setTaskNo(rs.getObject("task_no") == null ? null : rs.getInt("task_no"));
            item.setTaskName(rs.getString("task_name"));
            item.setStartTime(rs.getDate("start_time"));
            item.setFinishTime(rs.getDate("finish_time"));

            int totalPoints = rs.getInt("total_points");
            int checkedPoints = rs.getInt("checked_points");
            int qualityIssueCount = rs.getInt("quality_issue_count");
            int openItemCount = rs.getInt("open_item_count");

            item.setTotalPoints(totalPoints);
            item.setCheckedPoints(checkedPoints);
            item.setQualityIssueCount(qualityIssueCount);
            item.setOpenItemCount(openItemCount);

            int progressPercent = 0;
            if (totalPoints > 0) {
                progressPercent = (int) Math.round((checkedPoints * 100.0D) / totalPoints);
            } else if (Objects.equals(item.getOrderStatus(), 4)) {
                progressPercent = 100;
            }
            item.setProgressPercent(progressPercent);

            String color = "yellow";
            if (openItemCount > 0) {
                color = "red";
            } else if (progressPercent >= 100 && qualityIssueCount == 0) {
                color = "green";
            }
            item.setStatusColor(color);

            String level;
            if (openItemCount > 0) {
                level = "D";
            } else if (qualityIssueCount > 3) {
                level = "C";
            } else if (qualityIssueCount > 0) {
                level = "B";
            } else {
                level = "A";
            }
            item.setQualityLevel(level);
            return item;
        });
    }

    @Override
    public BuQualityPlanningSummaryVO extractQualityPlanning(String planId, String trainNo, Boolean excludeNoNeed) {
        if (StringUtils.isBlank(planId) || StringUtils.isBlank(trainNo)) {
            throw new JeecgBootException("计划ID和车号不能为空");
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT o.id AS order_id, o.order_code, o.order_name, ")
                .append("t.id AS task_id, t.task_name, fi.form_inst_id, d.id AS record_detail_id, ")
                .append("d.work_content, d.tech_require, d.check_level, d.is_ignore, ")
                .append("CASE WHEN MAX(r.id) IS NULL THEN 0 ELSE 1 END AS filled ")
                .append("FROM bu_work_order o ")
                .append("LEFT JOIN bu_work_order_task t ON t.order_id = o.id ")
                .append("LEFT JOIN bu_work_order_task_form_inst fi ON fi.work_order_task_id = t.id AND fi.inst_type = 3 ")
                .append("LEFT JOIN bu_pl_work_record_detail d ON d.work_record_id = fi.form_inst_id ")
                .append("LEFT JOIN bu_pl_work_record_result r ON r.record_detail_id = d.id ")
                .append("WHERE o.plan_id = ? AND o.train_no = ? AND o.order_status <> 9 AND d.id IS NOT NULL ");

        if (Boolean.TRUE.equals(excludeNoNeed)) {
            sqlBuilder.append("AND COALESCE(d.is_ignore, 0) = 0 ");
        }

        sqlBuilder.append("GROUP BY o.id, o.order_code, o.order_name, t.id, t.task_no, t.task_name, fi.form_inst_id, d.id, d.work_content, d.tech_require, d.check_level, d.is_ignore ")
                .append("ORDER BY o.order_code, t.task_no, d.id");

        List<BuQualityPlanningItemVO> items = jdbcTemplate.query(
                sqlBuilder.toString(),
                new Object[]{planId, trainNo},
                new int[]{Types.VARCHAR, Types.VARCHAR},
                (rs, rowNum) -> {
                    BuQualityPlanningItemVO item = new BuQualityPlanningItemVO();
                    item.setOrderId(rs.getString("order_id"));
                    item.setOrderCode(rs.getString("order_code"));
                    item.setOrderName(rs.getString("order_name"));
                    item.setTaskId(rs.getString("task_id"));
                    item.setTaskName(rs.getString("task_name"));
                    item.setFormInstId(rs.getString("form_inst_id"));
                    item.setRecordDetailId(rs.getString("record_detail_id"));
                    item.setWorkContent(rs.getString("work_content"));
                    item.setTechRequire(rs.getString("tech_require"));
                    Integer checkLevel = rs.getObject("check_level") == null ? null : rs.getInt("check_level");
                    item.setCheckLevel(checkLevel);
                    item.setCheckLevelText(parseCheckLevelText(checkLevel));
                    item.setNoNeedFill(rs.getObject("is_ignore") == null ? 0 : rs.getInt("is_ignore"));
                    item.setFilled(rs.getInt("filled"));
                    return item;
                });

        BuQualityPlanningSummaryVO summary = new BuQualityPlanningSummaryVO();
        summary.setPlanId(planId);
        summary.setTrainNo(trainNo);
        summary.setItems(items);

        int total = items.size();
        int noNeed = (int) items.stream().filter(e -> Objects.equals(e.getNoNeedFill(), 1)).count();
        int filled = (int) items.stream().filter(e -> Objects.equals(e.getFilled(), 1)).count();
        int pending = (int) items.stream().filter(e -> !Objects.equals(e.getNoNeedFill(), 1) && !Objects.equals(e.getFilled(), 1)).count();
        summary.setTotalItems(total);
        summary.setNoNeedFillItems(noNeed);
        summary.setFilledItems(filled);
        summary.setPendingItems(pending);
        return summary;
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

    private String parseCheckLevelText(Integer checkLevel) {
        if (checkLevel == null) {
            return "";
        }
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "W点");
        map.put(2, "H点");
        map.put(3, "R点");
        return map.getOrDefault(checkLevel, "");
    }
}
