package org.jeecg.modules.produce.summary.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.produce.plan.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.produce.plan.bean.bo.WorkTimeSumBO;
import org.jeecg.modules.produce.plan.mapper.BuRepairTaskStaffArrangeProduceMapper;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.worktime.*;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryWorkTimeService;
import org.jeecg.modules.produce.workshop.mapper.BuMtrWorkstationProduceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 维修作业工时控制情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryWorkTimeServiceImpl implements PlanSummaryWorkTimeService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;
    @Resource
    private BuRepairTaskStaffArrangeProduceMapper buRepairTaskStaffArrangeProduceMapper;
    @Resource
    private BuMtrWorkstationProduceMapper buMtrWorkstationProduceMapper;


    /**
     * @see PlanSummaryWorkTimeService#getWorkTimeInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public WorkTimeInfo getWorkTimeInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);
        List<PlanBase> planBaseList = planSummaryBaseService.listLinePlanBase(planBase);

        // 查询该线路、修程下的工时
        List<WorkTimeSumBO> workTimeSumBOList = buRepairTaskStaffArrangeProduceMapper.selectWorkTimeSumBOListByLineAndProgram(planBase.getLineId(), planBase.getRepairProgramId());
        // 查询班组+工位
        List<Map<String, Object>> groupWorkstationNameMapList = buMtrWorkstationProduceMapper.selectGroupWorkstationNameMapList();
        List<GroupWorkstation> groupWorkstationList = transToGroupWorkstationList(groupWorkstationNameMapList);

        // 本列车
        List<WorkTimeSumBO> currentPlanWorkTimeSumBOList = workTimeSumBOList.stream()
                .filter(workTime -> planId.equals(workTime.getPlanId()))
                .collect(Collectors.toList());
        WorkTimeCurrent current = getWorkTimeCurrent(currentPlanWorkTimeSumBOList, planBase, groupWorkstationList);

        // 近三列车
        List<WorkTimeLast> lastThree = getWorkTimeLastList(workTimeSumBOList, planBaseList, groupWorkstationList);

        // 本线路各车辆工时
        List<WorkTimeLine> lineTrainList = getWorkTimeLine(workTimeSumBOList, planBaseList);

        return new WorkTimeInfo()
                .setCurrent(current)
                .setLastThree(lastThree)
                .setLineTrainList(lineTrainList);
    }


    private List<GroupWorkstation> transToGroupWorkstationList(List<Map<String, Object>> groupWorkstationNameMapList) {
        if (CollectionUtils.isEmpty(groupWorkstationNameMapList)) {
            return new ArrayList<>();
        }

        Map<String, Set<String>> groupNameWorkstationNameSetMap = new HashMap<>();
        for (Map<String, Object> groupWorkstationNameMap : groupWorkstationNameMapList) {
            String groupName = (String) groupWorkstationNameMap.get("groupName");
            String workstationName = (String) groupWorkstationNameMap.get("workstationName");

            Set<String> workstationNameSet = groupNameWorkstationNameSetMap.getOrDefault(groupName, new HashSet<>());
            workstationNameSet.add(workstationName);
            groupNameWorkstationNameSetMap.put(groupName, workstationNameSet);
        }

        List<GroupWorkstation> groupWorkstationList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> groupNameWorkstationNameSetEntry : groupNameWorkstationNameSetMap.entrySet()) {
            String groupName = groupNameWorkstationNameSetEntry.getKey();
            Set<String> workstationNameSet = groupNameWorkstationNameSetEntry.getValue();

            ArrayList<String> workstationNameList = new ArrayList<>(workstationNameSet);
            workstationNameList.sort(Comparator.comparing(String::toString, Comparator.nullsLast(Comparator.naturalOrder())));

            GroupWorkstation groupWorkstation = new GroupWorkstation()
                    .setGroupName(groupName)
                    .setWorkstationNameList(workstationNameList);
            groupWorkstationList.add(groupWorkstation);
        }

        groupWorkstationList.sort(Comparator.comparing(GroupWorkstation::getGroupName, Comparator.nullsLast(Comparator.naturalOrder())));
        return groupWorkstationList;
    }

    private WorkTimeCurrent getWorkTimeCurrent(List<WorkTimeSumBO> workTimeList, PlanBase planBase, List<GroupWorkstation> groupWorkstationList) {
        if (CollectionUtils.isEmpty(workTimeList)) {
            return new WorkTimeCurrent()
                    .setRepairIndex(planBase.getRepairIndex())
                    .setTrainNo(planBase.getTrainNo())
                    .setWorkDays(planBase.getRepairDays())
                    .setWorkHours(0D)
                    .setGroupHoursList(new ArrayList<>());
        }

        List<GroupHours> groupHoursList = getGroupHoursList(workTimeList, groupWorkstationList, true);
        double totalHours = groupHoursList.stream()
                .mapToDouble(GroupHours::getHours)
                .sum();

        return new WorkTimeCurrent()
                .setRepairIndex(planBase.getRepairIndex())
                .setTrainNo(planBase.getTrainNo())
                .setWorkDays(planBase.getRepairDays())
                .setWorkHours(totalHours)
                .setGroupHoursList(groupHoursList);
    }

    private List<WorkTimeLast> getWorkTimeLastList(List<WorkTimeSumBO> workTimeList, List<PlanBase> planBaseList, List<GroupWorkstation> groupWorkstationList) {
        if (CollectionUtils.isEmpty(planBaseList)) {
            return new ArrayList<>();
        }

        int resultSize = 3;
        int planSize = planBaseList.size();
        int size = Math.min(resultSize, planSize);
        List<WorkTimeLast> workTimeLastList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PlanBase planBase = planBaseList.get(size - i - 1);
            List<WorkTimeSumBO> planWorkTimeList = workTimeList.stream()
                    .filter(workTime -> planBase.getPlanId().equals(workTime.getPlanId()))
                    .collect(Collectors.toList());
            List<GroupHours> groupHoursList = getGroupHoursList(planWorkTimeList, groupWorkstationList, false);

            WorkTimeLast workTimeLast = new WorkTimeLast()
                    .setRepairIndex(planBase.getRepairIndex())
                    .setTrainNo(planBase.getTrainNo())
                    .setGroupHoursList(groupHoursList);
            workTimeLastList.add(workTimeLast);
        }
        return workTimeLastList;
    }

    private List<GroupHours> getGroupHoursList(List<WorkTimeSumBO> workTimeList, List<GroupWorkstation> groupWorkstationList, boolean needDetails) {
        double totalHours = workTimeList.stream()
                .map(WorkTimeSumBO::getSumWorkTime)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        List<GroupHours> groupHoursList = new ArrayList<>();
        for (GroupWorkstation groupWorkstation : groupWorkstationList) {
            String groupName = groupWorkstation.getGroupName();
            double totalGroupHours = 0D;
            List<WorkstationHours> workstationHoursList = new ArrayList<>();

            if (needDetails) {
                List<String> workstationNameList = groupWorkstation.getWorkstationNameList();
                for (String workstationName : workstationNameList) {
                    double hours = workTimeList.stream()
                            .filter(workTime -> groupName.equals(workTime.getGroupName())
                                    && workstationName.equals(workTime.getWorkstationName()))
                            .map(WorkTimeSumBO::getSumWorkTime)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .doubleValue();

                    totalGroupHours = totalGroupHours + hours;

                    WorkstationHours workstationHours = new WorkstationHours()
                            .setWorkstationName(workstationName)
                            .setHours(hours);
                    workstationHoursList.add(workstationHours);
                }
            } else {
                totalGroupHours = workTimeList.stream()
                        .filter(workTime -> groupName.equals(workTime.getGroupName()))
                        .map(WorkTimeSumBO::getSumWorkTime)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .doubleValue();
            }

            Double groupPercent = PercentUtils.percent(totalGroupHours, totalHours);
            GroupHours groupHours = new GroupHours()
                    .setGroupName(groupName)
                    .setHours(totalGroupHours)
                    .setPercent(groupPercent)
                    .setWorkstationHoursList(workstationHoursList);
            groupHoursList.add(groupHours);
        }
        return groupHoursList;
    }

    private List<WorkTimeLine> getWorkTimeLine(List<WorkTimeSumBO> workTimeList, List<PlanBase> planBaseList) {
        if (CollectionUtils.isEmpty(planBaseList)) {
            return new ArrayList<>();
        }

        int size = planBaseList.size();
        List<WorkTimeLine> workTimeLineList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PlanBase planBase = planBaseList.get(size - i - 1);
            double totalHours = workTimeList.stream()
                    .filter(workTime -> planBase.getPlanId().equals(workTime.getPlanId()))
                    .map(WorkTimeSumBO::getSumWorkTime)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();

            WorkTimeLine workTimeLine = new WorkTimeLine()
                    .setRepairIndex(planBase.getRepairIndex())
                    .setTrainNo(planBase.getTrainNo())
                    .setHours(totalHours);
            workTimeLineList.add(workTimeLine);
        }
        return workTimeLineList;
    }

}
