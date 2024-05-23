package org.jeecg.modules.tiros.rpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.rpt.service.PlanProgressUpdateService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlanTask;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairTaskTrack;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanTaskMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairTaskTrackMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTask;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderTaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 更新列计划进度 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class PlanProgressUpdateServiceImpl implements PlanProgressUpdateService {

    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuRepairPlanTaskMapper buRepairPlanTaskMapper;
    @Resource
    private BuRepairTaskTrackMapper buRepairTaskTrackMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuRepairExchangMapper buRepairExchangMapper;
    @Resource
    private HomepageItemRptService homepageItemRptService;

    private final static List<Integer> FINISHED_PLAN_PROGRESS_STATUS = Arrays.asList(3, 4, 5);


    /**
     * @see PlanProgressUpdateService#updatePlanTaskStart(List, Date)
     */
    @Override
    public boolean updatePlanTaskStart(List<String> planTaskIdList, Date now) throws RuntimeException {
        if (CollectionUtils.isEmpty(planTaskIdList)) {
            return true;
        }

        if (null == now) {
            now = new Date();
        }

        // 查询列计划任务
        List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectBatchIds(planTaskIdList);
        if (CollectionUtils.isEmpty(planTaskList)) {
            return true;
        }

        for (BuRepairPlanTask planTask : planTaskList) {
            // 更新列计划任务开始
            if (null == planTask.getActStart()) {
                planTask.setActStart(now);
            }
            buRepairPlanTaskMapper.updateById(planTask);

            // 如果工单任务对应的列计划任务有停放计划，则需要在任务开始的时候，自动将对应车辆的当前停放股道改成该股道
            LambdaQueryWrapper<BuRepairTaskTrack> planTaskTrackWrapper = new LambdaQueryWrapper<BuRepairTaskTrack>()
                    .eq(BuRepairTaskTrack::getTaskId, planTask.getTrackId());
            List<BuRepairTaskTrack> planTaskTrackList = buRepairTaskTrackMapper.selectList(planTaskTrackWrapper);
            if (CollectionUtils.isNotEmpty(planTaskTrackList)) {
                String trainNo = buRepairPlanTaskMapper.selectRepairTrainNoByPlanTaskId(planTask.getId());
                BuRepairTaskTrack planTaskTrack = planTaskTrackList.get(0);
                buRepairTaskTrackMapper.updateTrainTrack(trainNo, planTaskTrack.getTrackId());
            }
        }

        return true;
    }

    /**
     * @see PlanProgressUpdateService#updatePlanTaskFinish(List, Date)
     */
    @Override
    public boolean updatePlanTaskFinish(List<String> planTaskIdList, Date now) throws RuntimeException {
        if (CollectionUtils.isEmpty(planTaskIdList)) {
            return true;
        }

        if (null == now) {
            now = new Date();
        }

        // 查询列计划任务
        List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectBatchIds(planTaskIdList);
        if (CollectionUtils.isEmpty(planTaskList)) {
            return true;
        }

        // 查询工单任务
        LambdaQueryWrapper<BuWorkOrderTask> orderTaskOfPlanTaskWrapper = new LambdaQueryWrapper<BuWorkOrderTask>()
                .eq(BuWorkOrderTask::getTaskType, 1)
                .in(BuWorkOrderTask::getTaskObjId, planTaskIdList);
        List<BuWorkOrderTask> allOrderTaskListOfPlanTask = buWorkOrderTaskMapper.selectList(orderTaskOfPlanTaskWrapper);

        for (BuRepairPlanTask planTask : planTaskList) {
            List<BuWorkOrderTask> orderTaskList = allOrderTaskListOfPlanTask.stream()
                    .filter(orderTask -> planTask.getId().equals(orderTask.getTaskObjId()))
                    .collect(Collectors.toList());
            boolean orderTaskOfPlanTaskAllFinished = orderTaskAllFinished(orderTaskList);

            if (orderTaskOfPlanTaskAllFinished) {
                // 列计划任务关联的工单任务都已完成，修改列计划任务任务状态=已完成2
                planTask.setStatus(2)
                        .setActFinish(now)
                        .setProgress(100D);
                buRepairPlanTaskMapper.updateById(planTask);

                // 如果列计划任务的兄弟任务都已完成，更改父任务为已完成
                String parentId = planTask.getParentId();
                if (StringUtils.isNotBlank(parentId)) {
                    LambdaQueryWrapper<BuRepairPlanTask> brotherPlanTaskListWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                            .eq(BuRepairPlanTask::getParentId, parentId);
                    List<BuRepairPlanTask> brotherPlanTaskList = buRepairPlanTaskMapper.selectList(brotherPlanTaskListWrapper);
                    boolean brotherPlanTaskAllFinished = planTaskAllFinished(brotherPlanTaskList);
                    if (brotherPlanTaskAllFinished) {
                        BuRepairPlanTask parent = new BuRepairPlanTask()
                                .setId(parentId)
                                .setStatus(2)
                                .setActFinish(now)
                                .setProgress(100D);
                        buRepairPlanTaskMapper.updateById(parent);
                    }
                }

                // 如果工单任务对应的列计划任务有停放计划，则需要在任务完成后清空对应车辆的股道
                LambdaQueryWrapper<BuRepairTaskTrack> planTaskTrackWrapper = new LambdaQueryWrapper<BuRepairTaskTrack>()
                        .eq(BuRepairTaskTrack::getTaskId, planTask.getId());
                List<BuRepairTaskTrack> planTaskTrackList = buRepairTaskTrackMapper.selectList(planTaskTrackWrapper);
                if (CollectionUtils.isNotEmpty(planTaskTrackList)) {
                    String trainNo = buRepairPlanTaskMapper.selectRepairTrainNoByPlanTaskId(planTask.getId());

                    for (BuRepairTaskTrack planTaskTrack : planTaskTrackList) {
                        String trackId = planTaskTrack.getTrackId();
                        buRepairTaskTrackMapper.clearTrainTrack(trainNo, trackId);
                    }
                }
            }
        }

        return true;
    }

    /**
     * @see PlanProgressUpdateService#updatePlanStart(List, Date)
     */
    @Override
    public boolean updatePlanStart(List<String> planIdList, Date now) throws RuntimeException {
        if (CollectionUtils.isEmpty(planIdList)) {
            return true;
        }

        if (null == now) {
            now = new Date();
        }

        // 查询列计划
        List<BuRepairPlan> planList = buRepairPlanMapper.selectBatchIds(planIdList);

        for (BuRepairPlan plan : planList) {
            if (null == plan.getActStart()) {
                plan.setActStart(now);
            }
            if (null == plan.getProgressStatus() || 0 == plan.getProgressStatus()) {
                plan.setProgressStatus(1);
                Date finishDate = plan.getFinishDate();
                if (now.after(DateUtils.transToDayEnd(finishDate))) {
                    // 超过计划完成日期还未开始
                    plan.setProgressStatus(2);
                }
            }
            buRepairPlanMapper.updateById(plan);
        }

        return true;
    }

    /**
     * @see PlanProgressUpdateService#updatePlanProgressAndStatus(List, Date)
     */
    @Override
    public boolean updatePlanProgressAndStatus(List<String> planIdList, Date now) throws RuntimeException {
        if (null == now) {
            now = new Date();
        }

        // 查询列计划
        LambdaQueryWrapper<BuRepairPlan> planWrapper = new LambdaQueryWrapper<BuRepairPlan>()
                .in(BuRepairPlan::getProgressStatus, Arrays.asList(0, 1, 2));
        if (CollectionUtils.isNotEmpty(planIdList)) {
            planWrapper.in(BuRepairPlan::getId, planIdList);
        }
        List<BuRepairPlan> planList = buRepairPlanMapper.selectList(planWrapper);

        boolean hasFinishedPlan = false;
        // 更新进度及进度状态
        for (BuRepairPlan plan : planList) {
            String planId = plan.getId();
            // 更新由列计划生成的工单任务的任务对象id（如果任务对象id为空）
            updatePlanGenerateOrderTaskTaskObjIdIfNull(planId);

            // 查询列计划任务
            List<BuRepairPlanTask> planTaskList = buRepairPlanTaskMapper.selectListWithOrderTaskStatusByPlanId(plan.getId());
//            // 去掉委外劳务班的列计划任务，这里为硬编码
//            planTaskList.removeIf(planTask -> "wwlwb".equals(planTask.getWorkGroupId()));

            // 检查并设置列计划任务完成状态
            checkAndSetPlanTaskProgressAndStatus(now, planTaskList);

            setPlanProgressAndProgressStatus(plan, planTaskList, now);
            buRepairPlanMapper.updateById(plan);

            // 列计划完成时，修改对应的接车记录为“已维修”
            if (FINISHED_PLAN_PROGRESS_STATUS.contains(plan.getProgressStatus())) {
                String exchangeId = plan.getExchangeId();
                BuRepairExchang exchange = new BuRepairExchang()
                        .setId(exchangeId)
                        .setStatus(3);
                buRepairExchangMapper.updateById(exchange);

                hasFinishedPlan = true;
            }
        }

        if (hasFinishedPlan) {
            // 更新首页数据区数据
            homepageItemRptService.rewriteDataItem();
        }

        return true;
    }


    private void updatePlanGenerateOrderTaskTaskObjIdIfNull(String planId) {
        List<Map<String, Object>> orderTaskIdPlanTaskIdMapList = buWorkOrderTaskMapper.selectPlanGenerateOrderTaskAndTaskObjIdNullByPlanId(planId);
        List<BuWorkOrderTask> taskList = new ArrayList<>();
        for (Map<String, Object> orderTaskIdPlanTaskIdMap : orderTaskIdPlanTaskIdMapList) {
            String orderTaskId = (String) orderTaskIdPlanTaskIdMap.get("orderTaskId");
            String planTaskId = (String) orderTaskIdPlanTaskIdMap.get("planTaskId");

            if (StringUtils.isNotEmpty(orderTaskId) && StringUtils.isNotBlank(planTaskId)) {
                BuWorkOrderTask task = new BuWorkOrderTask()
                        .setId(orderTaskId)
                        .setTaskObjId(planTaskId);
                taskList.add(task);
            }
        }
        if (CollectionUtils.isNotEmpty(taskList)) {
            List<List<BuWorkOrderTask>> batchSubList = DatabaseBatchSubUtil.batchSubList(taskList);
            for (List<BuWorkOrderTask> batchSub : batchSubList) {
                buWorkOrderTaskMapper.updateListTaskObjId(batchSub);
            }
        }
    }

    private boolean orderTaskAllFinished(List<BuWorkOrderTask> taskList) {
        long finishCount = taskList.stream().filter(task -> task.getTaskStatus() == 2).count();
        return finishCount == taskList.size();
    }

    private boolean planTaskAllFinished(List<BuRepairPlanTask> taskList) {
        long finishCount = taskList.stream().filter(task -> task.getStatus() == 2).count();
        return finishCount == taskList.size();
    }

    private void checkAndSetPlanTaskProgressAndStatus(Date now, List<BuRepairPlanTask> planTaskList) {
        // 获取用于生成工单的列计划任务：所有叶子节点任务
        List<BuRepairPlanTask> planTaskLeafNodeList = extractPlanTaskLeafNodeList(null, planTaskList);
        // 更新叶子节点任务
        List<BuRepairPlanTask> needUpdatePlanTaskList = new ArrayList<>();
        for (BuRepairPlanTask planTask : planTaskLeafNodeList) {
            if (null != planTask.getStatus() && 2 != planTask.getStatus()
                    && null != planTask.getOrderTaskStatus() && 2 == planTask.getOrderTaskStatus()) {
                planTask.setStatus(2)
                        .setActFinish(planTask.getOrderTaskFinishTime())
                        .setProgress(100D)
                        .setUpdateTime(now)
                        .setUpdateBy("admin");
                needUpdatePlanTaskList.add(planTask);
            }
        }

        // 更新非叶子节点任务
        Set<String> leafPlanTaskIdSet = planTaskLeafNodeList.stream()
                .map(BuRepairPlanTask::getId)
                .collect(Collectors.toSet());
        for (BuRepairPlanTask planTask : planTaskList) {
            if (leafPlanTaskIdSet.contains(planTask.getId())) {
                continue;
            }
            if (2 == planTask.getStatus()) {
                continue;
            }

            // 根据任务下的叶子节点任务状态和进度，更新任务状态和进度
            setParentTaskProgressAndStatusByChildLeafNode(planTask, planTaskList, now, needUpdatePlanTaskList);
        }

        if (CollectionUtils.isNotEmpty(needUpdatePlanTaskList)) {
            List<List<BuRepairPlanTask>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdatePlanTaskList);
            for (List<BuRepairPlanTask> batchSub : batchSubList) {
                buRepairPlanTaskMapper.updateListStatusAndTime(batchSub);
            }
        }
    }

    private void setParentTaskProgressAndStatusByChildLeafNode(BuRepairPlanTask parent,
                                                               List<BuRepairPlanTask> planTaskList,
                                                               Date now,
                                                               List<BuRepairPlanTask> needUpdatePlanTaskList) {
        List<BuRepairPlanTask> planTaskLeafNodeList = extractPlanTaskLeafNodeList(parent, planTaskList);
        if (CollectionUtils.isNotEmpty(planTaskLeafNodeList)) {
            planTaskLeafNodeList.sort(Comparator.comparing(BuRepairPlanTask::getActFinish, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

            int total = 0;
            int finished = 0;
            Date lastActFinish = planTaskLeafNodeList.get(0).getActFinish();
            for (BuRepairPlanTask leafNode : planTaskLeafNodeList) {
                // 任务数量合计
                total++;
                if (2 == leafNode.getStatus()) {
                    finished++;
                    // 最晚时间
                    Date actFinish = leafNode.getActFinish();
                    if (null == lastActFinish || (null != actFinish && lastActFinish.before(actFinish))) {
                        lastActFinish = actFinish;
                    }
                }
            }

            // 根据叶子节点的任务数量合计计算进度
            Double progress = PercentUtils.percent(finished, total);
            if (100D == progress) {
                parent.setStatus(2)
                        .setActFinish(lastActFinish)
                        .setProgress(progress)
                        .setUpdateTime(now)
                        .setUpdateBy("admin");
            } else {
                parent.setProgress(progress)
                        .setUpdateTime(now)
                        .setUpdateBy("admin");
            }
            needUpdatePlanTaskList.add(parent);
        } else {
            parent.setStatus(2)
                    .setActFinish(now)
                    .setProgress(100D)
                    .setUpdateTime(now)
                    .setUpdateBy("admin");
        }
    }

    private void setPlanProgressAndProgressStatus(BuRepairPlan plan, List<BuRepairPlanTask> planTaskList, Date now) {
        // 获取用于生成工单的列计划任务：所有叶子节点任务
        List<BuRepairPlanTask> planTaskLeafNodeList = extractPlanTaskLeafNodeList(null, planTaskList);
        if (CollectionUtils.isEmpty(planTaskLeafNodeList)) {
            return;
        }

        // 当前进度：为1~100的数量，列计划中当前已完成任务的工期，占所有任务工期的比例，后端程序自动计算
        int progress;
        // 进度状态 字典dictCode=bu_progress_status
        int progressStatus = 0;

        // 计算列计划的当前进度，列计划中当前已完成任务的工时，占所有任务工时的比例
        int total = planTaskLeafNodeList.size();
        long finished = planTaskLeafNodeList.stream()
                .filter(task -> task.getStatus() == 2)
                .count();
        double finishWorkTimePercent = PercentUtils.percent(finished, total);

        // 计算列计划的进度状态，根据当前进度和时间去算
        Date finishDate = plan.getFinishDate();
        if (finishWorkTimePercent == 100D) {
            Date actFinish = plan.getActFinish();
            if (null == actFinish) {
                actFinish = now;
                plan.setActFinish(actFinish);
            }
            if (null == plan.getActDuration()) {
                if (null != plan.getActStart()) {
                    int dayDiff = DateUtils.dayDiff(plan.getActStart(), plan.getActFinish());
                    plan.setActDuration(dayDiff + 1);
                }
            }

            if (DateUtils.isSameDay(actFinish, finishDate)) {
                progressStatus = 3;
            } else {
                if (actFinish.after(DateUtils.transToDayEnd(finishDate))) {
                    progressStatus = 4;
                } else if (actFinish.before(DateUtils.transToDayStart(finishDate))) {
                    progressStatus = 5;
                }
            }
        } else if (finishWorkTimePercent > 0D && finishWorkTimePercent < 100D) {
            progressStatus = 1;
            if (now.after(DateUtils.transToDayEnd(finishDate))) {
                progressStatus = 2;
            }
        }
        progress = (int) finishWorkTimePercent;

        plan.setProgress(progress);
        plan.setProgressStatus(progressStatus);
    }

    private List<BuRepairPlanTask> extractPlanTaskLeafNodeList(BuRepairPlanTask parent, List<BuRepairPlanTask> planTaskList) {
        if (CollectionUtils.isEmpty(planTaskList)) {
            return new ArrayList<>();
        }

        List<BuRepairPlanTask> parentList;
        if (null == parent) {
            Set<String> planTaskIdSet = planTaskList.stream()
                    .map(BuRepairPlanTask::getId)
                    .collect(Collectors.toSet());
            // 父列计划任务=顶级列计划任务
            parentList = planTaskList.stream()
                    .filter(planTask -> !planTaskIdSet.contains(planTask.getParentId()))
                    .collect(Collectors.toList());
        } else {
            // 父列计划任务=指定列计划任务
            parentList = Collections.singletonList(parent);
        }

        Set<BuRepairPlanTask> resultSet = new HashSet<>();
        // 递归添加父列计划任务下的叶子节点
        for (BuRepairPlanTask parentTask : parentList) {
            addPlanTaskAllLeafNode(parentTask, planTaskList, resultSet);
        }

        List<BuRepairPlanTask> taskList = new ArrayList<>(resultSet);
        taskList.sort(Comparator.comparing(BuRepairPlanTask::getTaskNo, Comparator.nullsLast(Comparator.naturalOrder())));
        return taskList;
    }

    private void addPlanTaskAllLeafNode(BuRepairPlanTask parent, List<BuRepairPlanTask> planTaskList, Set<BuRepairPlanTask> resultSet) {
        String parentId = parent.getId();

        List<BuRepairPlanTask> children = planTaskList.stream()
                .filter(planTask -> parentId.equals(planTask.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(children)) {
            resultSet.add(parent);
        } else {
            for (BuRepairPlanTask child : children) {
                addPlanTaskAllLeafNode(child, planTaskList, resultSet);
            }
        }
    }

}
