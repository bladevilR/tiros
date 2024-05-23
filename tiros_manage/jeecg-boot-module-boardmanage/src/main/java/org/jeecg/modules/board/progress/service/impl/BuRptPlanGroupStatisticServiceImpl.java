package org.jeecg.modules.board.progress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.board.progress.bean.*;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticQueryVO;
import org.jeecg.modules.board.progress.bean.vo.PlanGroupStatisticVO;
import org.jeecg.modules.board.progress.mapper.*;
import org.jeecg.modules.board.progress.service.BuRptPlanGroupStatisticService;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.quality.mapper.BuFaultInfoBoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 列计划班组工单故障填写统计 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-01-04
 */
@Service
public class BuRptPlanGroupStatisticServiceImpl extends ServiceImpl<BuRptPlanGroupStatisticMapper, BuRptPlanGroupStatistic> implements BuRptPlanGroupStatisticService {

    @Resource
    private BuRptPlanGroupStatisticMapper buRptPlanGroupStatisticMapper;
    @Resource
    private BuRepairPlanBoardMapper buRepairPlanBoardMapper;
    @Resource
    private BuRepairPlanTaskBoardMapper buRepairPlanTaskBoardMapper;
    @Resource
    private BuWorkOrderTaskBoardMapper buWorkOrderTaskBoardMapper;
    @Resource
    private BuMtrWorkshopGroupBoardMapper buMtrWorkshopGroupBoardMapper;
    @Resource
    private BuFaultInfoBoardMapper buFaultInfoBoardMapper;

    private final static List<Integer> WORKING_PLAN_PROGRESS_STATUS = Arrays.asList(1, 2);
    private final static List<Integer> FINISHED_PLAN_PROGRESS_STATUS = Arrays.asList(3, 4, 5);
    private final String NO_GROUP_ID = "ALL";


    /**
     * @see BuRptPlanGroupStatisticService#getLastWorkingPlan()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BuRepairPlan getLastWorkingPlan() throws Exception {
        LambdaQueryWrapper<BuRepairPlan> planWrapper = new LambdaQueryWrapper<BuRepairPlan>()
                .in(BuRepairPlan::getProgressStatus, WORKING_PLAN_PROGRESS_STATUS);
        List<BuRepairPlan> planList = buRepairPlanBoardMapper.selectList(planWrapper);
        if (CollectionUtils.isEmpty(planList)) {
            planList = buRepairPlanBoardMapper.selectList(Wrappers.emptyWrapper());
        }

        planList.sort(Comparator.comparing(BuRepairPlan::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return planList.get(0);
    }

    /**
     * @see BuRptPlanGroupStatisticService#listPlanGroupStatistic(PlanGroupStatisticQueryVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PlanGroupStatisticVO listPlanGroupStatistic(PlanGroupStatisticQueryVO queryVO) throws Exception {
        Date now = new Date();

        // 查询列计划
        List<BuRepairPlan> planList;
        if (StringUtils.isNotBlank(queryVO.getPlanId())) {
            BuRepairPlan plan = buRepairPlanBoardMapper.selectById(queryVO.getPlanId());
            if (null == plan) {
                throw new JeecgBootException("列计划不存在");
            }
            planList = Collections.singletonList(plan);
        } else if (StringUtils.isNotBlank(queryVO.getTrainNo())) {
            LambdaQueryWrapper<BuRepairPlan> planWrapper = new LambdaQueryWrapper<BuRepairPlan>()
                    .eq(BuRepairPlan::getTrainNo, queryVO.getTrainNo());
            planList = buRepairPlanBoardMapper.selectList(planWrapper);
            if (CollectionUtils.isEmpty(planList)) {
                throw new JeecgBootException("该车辆没有列计划");
            }
        } else {
            planList = buRepairPlanBoardMapper.selectList(Wrappers.emptyWrapper());
        }

        // 根据单个列计划处理统计数据
        List<BuRptPlanGroupStatistic> planStatisticList = new ArrayList<>();
        List<BuRptPlanGroupStatistic> groupStatisticList = new ArrayList<>();
        for (BuRepairPlan plan : planList) {
            queryVO.setPlanId(plan.getId());

            // 查询统计表数据
            List<BuRptPlanGroupStatistic> statisticList = buRptPlanGroupStatisticMapper.selectListByQueryVO(queryVO);
            Integer progressStatus = plan.getProgressStatus();
            // 列计划未完成或统计表数据为空，更新统计表数据，再重新查询统计表数据
            if (!FINISHED_PLAN_PROGRESS_STATUS.contains(progressStatus) || CollectionUtils.isEmpty(statisticList)) {
                // 删除旧的
                LambdaQueryWrapper<BuRptPlanGroupStatistic> deleteWrapper = new LambdaQueryWrapper<BuRptPlanGroupStatistic>()
                        .eq(BuRptPlanGroupStatistic::getPlanId, plan.getId());
                buRptPlanGroupStatisticMapper.delete(deleteWrapper);

                // 生成新的
                List<BuRptPlanGroupStatistic> planGroupStatisticList = generatePlanGroupStatistic(plan, now);
                // 插入新的
                if (CollectionUtils.isNotEmpty(planGroupStatisticList)) {
                    List<List<BuRptPlanGroupStatistic>> batchSubList = DatabaseBatchSubUtil.batchSubList(planGroupStatisticList);
                    for (List<BuRptPlanGroupStatistic> batchSub : batchSubList) {
                        buRptPlanGroupStatisticMapper.insertList(batchSub);
                    }
                }

                // 重新查询
                statisticList = buRptPlanGroupStatisticMapper.selectListByQueryVO(queryVO);
            }

            for (BuRptPlanGroupStatistic statistic : statisticList) {
                if (NO_GROUP_ID.equals(statistic.getGroupId())) {
                    planStatisticList.add(statistic);
                } else {
                    groupStatisticList.add(statistic);
                }
            }
        }

        planStatisticList.sort(Comparator.comparing(BuRptPlanGroupStatistic::getPlanStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                .thenComparing(BuRptPlanGroupStatistic::getGroupId, Comparator.nullsLast(Comparator.naturalOrder())));
        groupStatisticList.sort(Comparator.comparing(BuRptPlanGroupStatistic::getPlanStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                .thenComparing(BuRptPlanGroupStatistic::getGroupId, Comparator.nullsLast(Comparator.naturalOrder())));
        return new PlanGroupStatisticVO()
                .setPlanList(planStatisticList)
                .setGroupList(groupStatisticList);
    }


    private List<BuRptPlanGroupStatistic> generatePlanGroupStatistic(BuRepairPlan plan, Date now) {
        if (null == plan) {
            return new ArrayList<>();
        }
        String planId = plan.getId();
        String trainNo = plan.getTrainNo();
        String remark = String.format("%s更新数据", DateUtils.datetimeFormat.get().format(now));

        // 获取计划任务
        LambdaQueryWrapper<BuRepairPlanTask> planTaskWrapper = new LambdaQueryWrapper<BuRepairPlanTask>()
                .eq(BuRepairPlanTask::getPlanId, planId);
        List<BuRepairPlanTask> planTaskList = buRepairPlanTaskBoardMapper.selectList(planTaskWrapper);
        List<String> finishedPlanTaskIdList = planTaskList.stream()
                .filter(planTask -> 2 == planTask.getStatus())
                .map(BuRepairPlanTask::getId)
                .collect(Collectors.toList());
        // 去掉委外劳务班的列计划任务，这里为硬编码
        planTaskList.removeIf(planTask -> "wwlwb".equals(planTask.getWorkGroupId()));
        // 过滤实际的列计划任务，也就是会用来生成工单任务的列计划任务
        planTaskList = extractPlanTaskLeafNodeList(null, planTaskList);
        // 获取工单任务
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskBoardMapper.selectPlanOrderTaskListByPlanId(planId);

        // 获取已提交的故障
        LambdaQueryWrapper<BuFaultInfo> faultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                .eq(BuFaultInfo::getPlanId, planId)
                .eq(BuFaultInfo::getSubmitStatus, 1);
        List<BuFaultInfo> faultList = buFaultInfoBoardMapper.selectList(faultWrapper);

        // 获取所有班组
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupBoardMapper.selectAllList();
        // 去掉委外劳务班，这里为硬编码
        groupList.removeIf(group -> "wwlwb".equals(group.getId()));

        List<Integer> noNeedWriteOrderStatus = Arrays.asList(0, 9);
        List<Integer> handledFaultStatus = Arrays.asList(1, 2);
        List<BuRptPlanGroupStatistic> statisticList = new ArrayList<>();

        // 列计划统计
        int taskTotal = planTaskList.size();
        int taskIssued = (int) orderTaskList.stream()
                .filter(orderTask -> !noNeedWriteOrderStatus.contains(orderTask.getOrderStatus()))
                .count();
        int taskSuspended = (int) orderTaskList.stream()
                .filter(orderTask -> 5 == orderTask.getOrderStatus())
                .count();
        int taskClosed = (int) orderTaskList.stream()
                .filter(orderTask -> 4 == orderTask.getOrderStatus())
                .count();
        int taskSubmitted = (int) orderTaskList.stream()
                .filter(orderTask -> 3 == orderTask.getOrderStatus())
                .count();
        int orderTaskNotFinishedButPlanTaskFinished = (int) orderTaskList.stream()
                .filter(orderTask -> 3 != orderTask.getOrderStatus() && 4 != orderTask.getOrderStatus()
                        && finishedPlanTaskIdList.contains(orderTask.getTaskObjId()))
                .count();
        double taskFinishPercent = PercentUtils.percent(taskClosed + taskSubmitted + orderTaskNotFinishedButPlanTaskFinished, taskTotal);
        taskFinishPercent = Math.min(taskFinishPercent, 100D);
        int faultFound = faultList.size();
        int faultHandled = (int) faultList.stream()
                .filter(fault -> handledFaultStatus.contains(fault.getStatus()))
                .count();
        BuRptPlanGroupStatistic planStatistic = new BuRptPlanGroupStatistic()
                .setId(UUIDGenerator.generate())
                .setPlanId(planId)
                .setTrainNo(trainNo)
                .setGroupId(NO_GROUP_ID)
                .setTaskTotal(taskTotal)
                .setTaskIssued(taskIssued)
                .setTaskSuspended(taskSuspended)
                .setTaskClosed(taskClosed)
                .setTaskSubmitted(taskSubmitted)
                .setTaskFinishPercent(taskFinishPercent)
                .setGroupTaskNeedWrite(0)
                .setGroupTaskWrote(0)
                .setGroupTaskSuspended(0)
                .setGroupTaskClosed(0)
                .setGroupTaskFinishPercent(0D)
                .setFaultFound(faultFound)
                .setFaultHandled(faultHandled)
                .setGroupFaultFound(0)
                .setGroupFaultHandled(0)
                .setRemark(remark)
                .setCompanyId(plan.getCompanyId())
                .setWorkshopId(plan.getWorkshopId())
                .setLineId(plan.getLineId());
        statisticList.add(planStatistic);
        // 各班组统计
        for (BuMtrWorkshopGroup group : groupList) {
            String groupId = group.getId();

            int groupTaskTotal = (int) planTaskList.stream()
                    .filter(planTask -> groupId.equals(planTask.getWorkGroupId()))
                    .count();
            int groupTaskNeedWrite = (int) orderTaskList.stream()
                    .filter(orderTask -> groupId.equals(orderTask.getGroupId())
                            && !noNeedWriteOrderStatus.contains(orderTask.getOrderStatus()))
                    .count();
            int groupTaskWrote = (int) orderTaskList.stream()
                    .filter(orderTask -> groupId.equals(orderTask.getGroupId())
                            && 2 == orderTask.getTaskStatus())
                    .count();
            int groupTaskSuspended = (int) orderTaskList.stream()
                    .filter(orderTask -> groupId.equals(orderTask.getGroupId())
                            && 5 == orderTask.getOrderStatus())
                    .count();
            int groupTaskClosed = (int) orderTaskList.stream()
                    .filter(orderTask -> groupId.equals(orderTask.getGroupId())
                            && 4 == orderTask.getOrderStatus())
                    .count();
            int groupTaskSubmitted = (int) orderTaskList.stream()
                    .filter(orderTask -> groupId.equals(orderTask.getGroupId())
                            && 3 == orderTask.getOrderStatus())
                    .count();
            int groupOrderTaskNotFinishedButPlanTaskFinished = (int) orderTaskList.stream()
                    .filter(orderTask -> groupId.equals(orderTask.getGroupId()) &&
                            3 != orderTask.getOrderStatus() && 4 != orderTask.getOrderStatus()
                            && finishedPlanTaskIdList.contains(orderTask.getTaskObjId()))
                    .count();
            double groupTaskFinishPercent = PercentUtils.percent(groupTaskClosed + groupTaskSubmitted + groupOrderTaskNotFinishedButPlanTaskFinished, groupTaskTotal);
            groupTaskFinishPercent = Math.min(groupTaskFinishPercent, 100D);
            int groupFaultFound = (int) faultList.stream()
                    .filter(fault -> groupId.equals(fault.getReportGroupId()))
                    .count();
            int groupFaultHandled = (int) faultList.stream()
                    .filter(fault -> groupId.equals(fault.getReportGroupId()) && handledFaultStatus.contains(fault.getStatus()))
                    .count();
            BuRptPlanGroupStatistic groupStatistic = new BuRptPlanGroupStatistic()
                    .setId(UUIDGenerator.generate())
                    .setPlanId(planId)
                    .setTrainNo(trainNo)
                    .setGroupId(groupId)
                    .setTaskTotal(0)
                    .setTaskIssued(0)
                    .setTaskSuspended(0)
                    .setTaskClosed(0)
                    .setTaskSubmitted(0)
                    .setTaskFinishPercent(0D)
                    .setGroupTaskNeedWrite(groupTaskNeedWrite)
                    .setGroupTaskWrote(groupTaskWrote)
                    .setGroupTaskSuspended(groupTaskSuspended)
                    .setGroupTaskClosed(groupTaskClosed)
                    .setGroupTaskFinishPercent(groupTaskFinishPercent)
                    .setFaultFound(0)
                    .setFaultHandled(0)
                    .setGroupFaultFound(groupFaultFound)
                    .setGroupFaultHandled(groupFaultHandled)
                    .setRemark(remark)
                    .setCompanyId(plan.getCompanyId())
                    .setWorkshopId(plan.getWorkshopId())
                    .setLineId(plan.getLineId());
            statisticList.add(groupStatistic);
        }

        return statisticList;
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
