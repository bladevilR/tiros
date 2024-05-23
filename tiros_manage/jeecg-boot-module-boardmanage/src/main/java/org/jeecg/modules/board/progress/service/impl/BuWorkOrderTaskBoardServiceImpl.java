package org.jeecg.modules.board.progress.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.LineChartVO;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.progress.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.board.progress.bean.BuRepairPlanTask;
import org.jeecg.modules.board.progress.bean.BuWorkOrderTask;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkGroupTaskProgressVO;
import org.jeecg.modules.board.progress.bean.vo.BuWorkshopProgressTaskTrendVO;
import org.jeecg.modules.board.progress.mapper.BuMtrWorkshopGroupBoardMapper;
import org.jeecg.modules.board.progress.mapper.BuRepairPlanTaskBoardMapper;
import org.jeecg.modules.board.progress.mapper.BuWorkOrderTaskBoardMapper;
import org.jeecg.modules.board.progress.service.BuWorkOrderTaskBoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
@Slf4j
@Service
public class BuWorkOrderTaskBoardServiceImpl extends ServiceImpl<BuWorkOrderTaskBoardMapper, BuWorkOrderTask> implements BuWorkOrderTaskBoardService {

    @Resource
    private BuRepairPlanTaskBoardMapper buRepairPlanTaskBoardMapper;
    @Resource
    private BuWorkOrderTaskBoardMapper buWorkOrderTaskBoardMapper;
    @Resource
    private BuMtrWorkshopGroupBoardMapper buMtrWorkshopGroupBoardMapper;
    @Resource
    private SysConfigService sysConfigService;


    /**
     * @see BuWorkOrderTaskBoardService#listCurrentMonthTaskFinishTrend(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LineChartVO> listCurrentMonthTaskFinishTrend(BuBoardProgressQueryVO queryVO) throws Exception {
        // 设置本月时间
        queryVO.setCurrentMonthQueryDate();

        // 获取计划任务
        List<BuRepairPlanTask> planTaskList = buRepairPlanTaskBoardMapper.listPlanTaskByCondition(queryVO);
        // 过滤实际的列计划任务，也就是会用来生成工单任务的列计划任务
        planTaskList = extractPlanTaskLeafNodeList(null, planTaskList);
        // 获取完成的工单任务
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskBoardMapper.listFinishWorkOrderTaskByCondition(queryVO);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(queryVO.getStartDate());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(queryVO.getEndDate());

        List<BuWorkshopProgressTaskTrendVO> taskTrendVOList = new ArrayList<>();
        while (!endCalendar.before(startCalendar)) {
            Date date = startCalendar.getTime();
            int dayOfMonth = startCalendar.get(Calendar.DAY_OF_MONTH);

            long planTaskCount = planTaskList.stream()
                    .filter(planTask -> DateUtils.isSameDay(planTask.getFinishTime(), date))
                    .count();
            long orderTaskCount = orderTaskList.stream()
                    .filter(orderTask -> DateUtils.isSameDay(orderTask.getTaskFinish(), date))
                    .count();

            BuWorkshopProgressTaskTrendVO trendVO = new BuWorkshopProgressTaskTrendVO()
                    .setDay(dayOfMonth)
                    .setPlan(new Long(planTaskCount).intValue())
                    .setActual(new Long(orderTaskCount).intValue());
            taskTrendVOList.add(trendVO);

            startCalendar.add(Calendar.DATE, 1);
        }
//        int maxDayOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
//        for (int i = 1; i <= maxDayOfMonth; i++) {
//            int dayOfMonth = i;
//            long planTaskCount = planTaskList.stream()
//                    .filter(planTask -> dayOfMonth == planTask.getFinishTime().getDate()).count();
//            long orderTaskCount = orderTaskList.stream()
//                    .filter(orderTask -> dayOfMonth == orderTask.getTaskFinish().getDate()).count();
//
//            taskTrendVOList.add(
//                    new BuWorkshopProgressTaskTrendVO()
//                            .setDay(dayOfMonth)
//                            .setPlan(new Long(planTaskCount).intValue())
//                            .setActual(new Long(orderTaskCount).intValue())
//            );
//        }

        return transformToLineChartVOList(taskTrendVOList);
    }

    /**
     * @see BuWorkOrderTaskBoardService#listWorkGroupTaskProgress(BuBoardProgressQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkGroupTaskProgressVO> listWorkGroupTaskProgress(BuBoardProgressQueryVO queryVO) throws Exception {
        // 如果手动控制显示监控大屏显示数据，包括列计划进度、工班任务进度、委外任务进度
        String monitorScreenControlDataManual = sysConfigService.getConfigValueByCode("MonitorScreenControl_DataManual");
        boolean manualControl = StringUtils.isNotBlank(monitorScreenControlDataManual) && "1".equals(monitorScreenControlDataManual);
        if (manualControl) {
            String data = sysConfigService.getConfigValueByCode("MonitorScreenControl_GroupData");
            if (StringUtils.isBlank(data)) {
                return new ArrayList<>();
            } else {
                return JSON.parseArray(data, BuWorkGroupTaskProgressVO.class);
            }
        }

        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        // 查询工单任务
        List<BuWorkOrderTask> orderTaskList = buWorkOrderTaskBoardMapper.listIssuedWorkOrderTaskByCondition(queryVO);
        // 查询工单任务人员安排
        Map<String, Set<String>> taskIdUserIdSetMap = new HashMap<>();
        List<String> orderTaskIdList = orderTaskList.stream()
                .map(BuWorkOrderTask::getId)
                .collect(Collectors.toList());
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskIdList);
        for (List<String> batchSub : batchSubList) {
            List<Map<String, Object>> taskIdUserIdMapList = buWorkOrderTaskBoardMapper.selectOrderTaskUserByOrderTaskIdList(batchSub);
            if (CollectionUtils.isNotEmpty(taskIdUserIdMapList)) {
                for (Map<String, Object> taskIdUserIdMap : taskIdUserIdMapList) {
                    String taskId = (String) taskIdUserIdMap.get("taskId");
                    String userId = (String) taskIdUserIdMap.get("userId");
                    Set<String> userIdSet = taskIdUserIdSetMap.getOrDefault(taskId, new HashSet<>());
                    userIdSet.add(userId);
                    taskIdUserIdSetMap.put(taskId, userIdSet);
                }
            }
        }

        // 查询班组
        List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupBoardMapper.selectAllList();
        Map<String, BuMtrWorkshopGroup> idGroupList = new HashMap<>();
        groupList.forEach(group -> idGroupList.put(group.getId(), group));

        Map<String, List<BuWorkOrderTask>> longGroupNameOrderTaskListMap = orderTaskList.stream()
                .filter(task -> StringUtils.isNotBlank(task.getGroupName()))
                .collect(Collectors.groupingBy(task -> task.getDepotName() + "-" + task.getGroupName()));
        List<BuWorkGroupTaskProgressVO> groupTaskProgressVOList = new ArrayList<>();
        for (Map.Entry<String, List<BuWorkOrderTask>> longGroupNameOrderTaskListEntry : longGroupNameOrderTaskListMap.entrySet()) {
            String longGroupName = longGroupNameOrderTaskListEntry.getKey();
            List<BuWorkOrderTask> taskList = longGroupNameOrderTaskListEntry.getValue();
            String groupId = taskList.get(0).getGroupId();

            String monitorName = null;
            BuMtrWorkshopGroup group = idGroupList.get(groupId);
            if (null != group) {
                if (StringUtils.isNotBlank(group.getMonitorName())) {
                    monitorName = group.getMonitorName();
                } else {
                    monitorName = group.getMonitor2Name();
                }
            }

//            int staffArrangeUserIdCount = taskList.stream()
//                    .mapToInt(BuWorkOrderTask::getStaffArrangeUserIdCount)
//                    .sum();
            Set<String> userIdSet = new HashSet<>();
            for (BuWorkOrderTask task : taskList) {
                Set<String> taskUserIdSet = taskIdUserIdSetMap.get(task.getId());
                if (CollectionUtils.isNotEmpty(taskUserIdSet)) {
                    userIdSet.addAll(taskUserIdSet);
                }
            }
            int staffArrangeUserIdCount = userIdSet.size();
            int taskCount = taskList.size();
            long finishTaskCount = taskList.stream()
                    .filter(task -> 2 == task.getTaskStatus())
                    .count();
            Double progress = PercentUtils.percent(finishTaskCount, taskCount);

            groupTaskProgressVOList.add(
                    new BuWorkGroupTaskProgressVO()
                            .setGroupId(groupId)
                            .setGroupName(longGroupName)
                            .setGroupMonitorName(monitorName)
                            .setWorkerQuantity(staffArrangeUserIdCount)
                            .setProgress(progress)
            );
        }

        return groupTaskProgressVOList;
    }

    /**
     * @see BuWorkOrderTaskBoardService#listOrderTask(String, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderTask> listOrderTask(String orderId, Integer taskStatus) throws Exception {
        return buWorkOrderTaskBoardMapper.listByOrderIdAndTaskStatus(orderId, taskStatus);
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

    private List<LineChartVO> transformToLineChartVOList(List<BuWorkshopProgressTaskTrendVO> trendVOList) {
        List<LineChartVO> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trendVOList)) {
            for (BuWorkshopProgressTaskTrendVO trendVO : trendVOList) {
                LineChartVO lineChartVO = new LineChartVO()
                        .setType(trendVO.getDay().toString())
                        .setJeecg(trendVO.getPlan().doubleValue())
                        .setJeebt(trendVO.getActual().doubleValue());
                result.add(lineChartVO);
            }
        }

        return result;
    }

}
