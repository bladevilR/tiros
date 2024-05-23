package org.jeecg.modules.produce.summary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.produce.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.mapper.BuFaultInfoProduceMapper;
import org.jeecg.modules.produce.summary.bean.PlanBase;
import org.jeecg.modules.produce.summary.bean.fault.*;
import org.jeecg.modules.produce.summary.service.PlanSummaryBaseService;
import org.jeecg.modules.produce.summary.service.PlanSummaryFaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 维修质量控制情况 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/19
 */
@Slf4j
@Service
public class PlanSummaryFaultServiceImpl implements PlanSummaryFaultService {

    @Resource
    private PlanSummaryBaseService planSummaryBaseService;
    @Resource
    private BuFaultInfoProduceMapper buFaultInfoProduceMapper;

    private static final List<Integer> LEVEL_AB = Arrays.asList(1, 2);
    private static final List<Integer> STATUS_HANDLED = Arrays.asList(1, 2);
    private static final int STATUS_TRACKING = 3;
    private static final List<Integer> PHASE_REPAIR = Arrays.asList(1, 4);
    private static final int PHASE_WARRANTY = 2;


    /**
     * @see PlanSummaryFaultService#getFaultInfo(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public FaultInfo getFaultInfo(String planId) throws Exception {
        PlanBase planBase = planSummaryBaseService.getPlanBase(planId);
        List<PlanBase> planBaseList = planSummaryBaseService.listLinePlanBase(planBase);
        Map<String, PlanBase> idPlanBaseMap = new HashMap<>();
        planBaseList.forEach(base -> idPlanBaseMap.put(base.getPlanId(), base));
        Map<String, String> systemIdNameMap = planSummaryBaseService.getLineSystemIdNameMap(planBase);

        // 查询该线路、修程下的故障
        List<String> planIdList = new ArrayList<>(idPlanBaseMap.keySet());
        LambdaQueryWrapper<BuFaultInfo> faultWrapper = new LambdaQueryWrapper<BuFaultInfo>()
                .in(BuFaultInfo::getPlanId, planIdList);
        List<BuFaultInfo> faultList = buFaultInfoProduceMapper.selectList(faultWrapper);
        for (BuFaultInfo fault : faultList) {
            PlanBase base = idPlanBaseMap.get(fault.getPlanId());
            if (null != base) {
                fault.setRepairIndex(base.getRepairIndex());
            }
        }

        // 本列车
        List<BuFaultInfo> currentPlanFaultList = faultList.stream()
                .filter(fault -> planId.equals(fault.getPlanId()))
                .collect(Collectors.toList());
        FaultCurrent current = getFaultCurrent(currentPlanFaultList, systemIdNameMap);

        // 本线路惯性故障
        List<FaultLineOften> lineOftenList = getFaultLineOftenList(faultList, planBase.getRepairProgramId());

        // 本线路各车辆故障数量
        List<TrainFault> lineTrainList = getLineTrainList(faultList, planBaseList);

        // 质保期故障情况
        List<BuFaultInfo> warrantyFaultList = faultList.stream()
                .filter(fault -> PHASE_WARRANTY == fault.getPhase())
                .collect(Collectors.toList());
        FaultWarranty warranty = getFaultWarranty(warrantyFaultList, planBaseList, systemIdNameMap);

        return new FaultInfo()
                .setCurrent(current)
                .setLineOftenList(lineOftenList)
                .setLineTrainList(lineTrainList)
                .setWarranty(warranty);
    }


    private FaultCurrent getFaultCurrent(List<BuFaultInfo> faultList, Map<String, String> systemIdNameMap) {
        FaultCurrent current = new FaultCurrent()
                .setTotal(0)
                .setLevelAB(0)
                .setHandled(0)
                .setUnhandled(0)
                .setTracking(0)
                .setSystemList(new ArrayList<>())
                .setImportantFaultList(new ArrayList<>());
        if (CollectionUtils.isEmpty(faultList)) {
            return current;
        }

        faultList.sort(Comparator.comparing(BuFaultInfo::getHappenTime, Comparator.nullsLast(Comparator.naturalOrder())));

        int total = 0;
        int levelAB = 0;
        int handled = 0;
        int unhandled = 0;
        int tracking = 0;
        List<DetailFault> importantFaultList = new ArrayList<>();
        Map<String, SystemFault> systemNameFaultMap = getInitSystemNameFaultMap(systemIdNameMap);
        for (BuFaultInfo fault : faultList) {
            boolean isLevelAB = LEVEL_AB.contains(fault.getFaultLevel());
            boolean isHandled = STATUS_HANDLED.contains(fault.getStatus());
            boolean isTracking = STATUS_TRACKING == fault.getStatus();
            boolean isRepairPhase = PHASE_REPAIR.contains(fault.getPhase());

            // 总数统计
            total++;
            if (isLevelAB) {
                levelAB++;
            }
            if (isHandled) {
                handled++;
            } else {
                unhandled++;
            }
            if (isTracking) {
                tracking++;
            }

            // 各系统故障
            String systemName = systemIdNameMap.get(fault.getSysId());
            SystemFault systemFault = systemNameFaultMap.get(systemName);
            if (null == systemFault) {
                systemFault = new SystemFault()
                        .setSystemName(systemName)
                        .setTotal(0)
                        .setLevelAB(0)
                        .setUnhandled(0);
            }
            systemFault.setTotal(systemFault.getTotal() + 1);
            if (isLevelAB) {
                systemFault.setLevelAB(systemFault.getLevelAB() + 1);
            }
            if (!isHandled) {
                systemFault.setUnhandled(systemFault.getUnhandled() + 1);
            }
            systemNameFaultMap.put(systemName, systemFault);

            // 主要维修期故障
            if (isRepairPhase && isLevelAB) {
                DetailFault detailFault = new DetailFault()
                        .setFaultId(fault.getId())
                        .setRepairIndex(fault.getRepairIndex())
                        .setTrainNo(fault.getTrainNo())
                        .setSystemName(systemName)
                        .setHappenTime(fault.getHappenTime())
                        .setFaultConditions(fault.getFaultDesc());
                importantFaultList.add(detailFault);
            }
        }
        // 各系统故障排序
        List<SystemFault> systemList = new ArrayList<>(systemNameFaultMap.values());
        systemList.sort(Comparator.comparing(SystemFault::getSystemName, Comparator.nullsLast(Comparator.naturalOrder())));
        // 维修故障详情设置原因、措施描述
        setFaultReasonSolutionDesc(importantFaultList);

        return current.setTotal(total)
                .setLevelAB(levelAB)
                .setHandled(handled)
                .setUnhandled(unhandled)
                .setTracking(tracking)
                .setSystemList(systemList)
                .setImportantFaultList(importantFaultList);
    }

    private Map<String, SystemFault> getInitSystemNameFaultMap(Map<String, String> systemIdNameMap) {
        if (systemIdNameMap.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, SystemFault> systemNameFaultMap = new HashMap<>();
        for (Map.Entry<String, String> systemIdNameEntry : systemIdNameMap.entrySet()) {
            String systemName = systemIdNameEntry.getValue();
            SystemFault systemFault = new SystemFault()
                    .setSystemName(systemName)
                    .setTotal(0)
                    .setLevelAB(0)
                    .setUnhandled(0);
            systemNameFaultMap.put(systemName, systemFault);
        }
        return systemNameFaultMap;
    }

    private List<FaultLineOften> getFaultLineOftenList(List<BuFaultInfo> faultList, String repairProgramId) {
        if (CollectionUtils.isEmpty(faultList)) {
            return new ArrayList<>();
        }

        // 查询所有已维修的故障的原因描述和数量统计
        Map<String, Integer> reasonDescTotalCountMap = new HashMap<>();
        List<Map<String, Object>> reasonDescFaultCountList = buFaultInfoProduceMapper.selectReasonDescFaultCountByRepairProgram(repairProgramId);
        for (Map<String, Object> reasonDescFaultCount : reasonDescFaultCountList) {
            String reasonDesc = (String) reasonDescFaultCount.get("reasonDesc");
            BigDecimal count = DataTypeCastUtil.transNumber(reasonDescFaultCount.get("count"));
            reasonDescTotalCountMap.put(reasonDesc, count.intValue());
        }

        List<DetailFault> detailFaultList = new ArrayList<>();
        for (BuFaultInfo fault : faultList) {
            boolean isRepairPhase = Arrays.asList(1, 4).contains(fault.getPhase());

            // 故障列表
            if (isRepairPhase) {
                DetailFault detailFault = new DetailFault()
                        .setFaultId(fault.getId())
                        .setRepairIndex(fault.getRepairIndex())
                        .setTrainNo(fault.getTrainNo())
                        .setHappenTime(fault.getHappenTime())
                        .setFaultConditions(fault.getFaultDesc());
                detailFaultList.add(detailFault);
            }
        }

        List<FaultLineOften> lineOftenList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(detailFaultList)) {
            // 维修故障详情设置原因、措施描述
            setFaultReasonSolutionDesc(detailFaultList);

            // 按原因描述、车号分组统计
            Map<String, List<DetailFault>> reasonDescDetailFaultListMap = new HashMap<>();
            Map<String, String> reasonDescSolutionDescMap = new HashMap<>();
            for (DetailFault detailFault : detailFaultList) {
                String trainNo = detailFault.getTrainNo();
                String reasonDesc = detailFault.getReasonDesc();
                String solutionDesc = detailFault.getSolutionDesc();
                Date happenTime = detailFault.getHappenTime();

                reasonDescSolutionDescMap.put(reasonDesc, solutionDesc);

                if (!reasonDescDetailFaultListMap.containsKey(reasonDesc)) {
                    reasonDescDetailFaultListMap.put(reasonDesc, new ArrayList<>());
                }
                List<DetailFault> faultItemList = reasonDescDetailFaultListMap.get(reasonDesc);
                List<DetailFault> sameTrainList = faultItemList.stream()
                        .filter(faultItem -> trainNo.equals(faultItem.getTrainNo()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(sameTrainList)) {
                    DetailFault faultItem = new DetailFault()
                            .setRepairIndex(detailFault.getRepairIndex())
                            .setTrainNo(trainNo)
                            .setHappenTime(happenTime)
                            .setFaultConditions(detailFault.getFaultConditions())
                            .setFaultAssetCount(1);
                    faultItemList.add(faultItem);
                } else {
                    DetailFault faultItem = sameTrainList.get(0);
                    faultItem.setFaultAssetCount(faultItem.getFaultAssetCount() + 1);
                    if (faultItem.getHappenTime().before(happenTime)) {
                        faultItem.setHappenTime(happenTime);
                    }
                }
            }

            for (Map.Entry<String, List<DetailFault>> reasonDescDetailFaultListEntry : reasonDescDetailFaultListMap.entrySet()) {
                String reasonDesc = reasonDescDetailFaultListEntry.getKey();
                List<DetailFault> faultItemList = reasonDescDetailFaultListEntry.getValue();

                String solutionDesc = reasonDescSolutionDescMap.get(reasonDesc);
                Integer totalCount = reasonDescTotalCountMap.get(reasonDesc);
                faultItemList.sort(Comparator.comparing(DetailFault::getRepairIndex, Comparator.nullsLast(Comparator.naturalOrder())));

                int faultAssetCountSum = faultItemList.stream()
                        .mapToInt(DetailFault::getFaultAssetCount)
                        .sum();
                if (null == totalCount) {
                    totalCount = faultAssetCountSum;
                }
                String percent = PercentUtils.percentWithSign(faultAssetCountSum, totalCount);
                String faultAssetPercent = faultAssetCountSum + "/" + totalCount + System.lineSeparator() + "=" + percent;

                FaultLineOften lineOften = new FaultLineOften()
                        .setReasonDesc(reasonDesc)
                        .setSolutionDesc(solutionDesc)
                        .setFaultList(faultItemList)
                        .setFaultAssetPercent(faultAssetPercent);
                lineOftenList.add(lineOften);
            }
        }

        lineOftenList.sort(Comparator.comparing(FaultLineOften::getReasonDesc, Comparator.nullsLast(Comparator.naturalOrder())));
        return lineOftenList;
    }

    private void setFaultReasonSolutionDesc(List<DetailFault> detailFaultList) {
        if (CollectionUtils.isEmpty(detailFaultList)) {
            return;
        }

        // 根据故障查询处理记录
        List<String> faultIdList = detailFaultList.stream()
                .map(DetailFault::getFaultId)
                .collect(Collectors.toList());
        List<BuFaultHandleRecord> handleRecordList = new ArrayList<>();
        List<List<String>> faultIdBatchSubList = DatabaseBatchSubUtil.batchSubList(faultIdList);
        for (List<String> faultIdBatchSub : faultIdBatchSubList) {
            List<BuFaultHandleRecord> subHandleRecordList = buFaultInfoProduceMapper.selectHandleRecordListByFaultIdList(faultIdBatchSub);
            handleRecordList.addAll(subHandleRecordList);
        }

        for (DetailFault detailFault : detailFaultList) {
            String faultId = detailFault.getFaultId();

            List<BuFaultHandleRecord> matchHandleRecordList = handleRecordList.stream()
                    .filter(handleRecord -> faultId.equals(handleRecord.getFaultInfoId()))
                    .sorted(Comparator.comparing(BuFaultHandleRecord::getSolutionTime, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchHandleRecordList)) {
                BuFaultHandleRecord handleRecord = matchHandleRecordList.get(0);
                detailFault.setReasonDesc(handleRecord.getReasonDesc())
                        .setSolutionDesc(handleRecord.getSolutionDesc());
            }
        }
    }

    private List<TrainFault> getLineTrainList(List<BuFaultInfo> faultList, List<PlanBase> planBaseList) {
        if (CollectionUtils.isEmpty(planBaseList)) {
            return new ArrayList<>();
        }

        List<TrainFault> trainFaultList = new ArrayList<>();
        for (PlanBase planBase : planBaseList) {
            Integer repairIndex = planBase.getRepairIndex();
            String trainNo = planBase.getTrainNo();
            String planId = planBase.getPlanId();

            long count = faultList.stream()
                    .filter(fault -> planId.equals(fault.getPlanId()))
                    .count();

            TrainFault trainFault = new TrainFault()
                    .setRepairIndex(repairIndex)
                    .setTrainNo(trainNo)
                    .setTotal(Long.valueOf(count).intValue());
            trainFaultList.add(trainFault);
        }

        return trainFaultList;
    }

    private FaultWarranty getFaultWarranty(List<BuFaultInfo> faultList, List<PlanBase> planBaseList, Map<String, String> systemIdNameMap) {
        if (CollectionUtils.isEmpty(planBaseList)) {
            return new FaultWarranty()
                    .setTrainList(Arrays.asList(new TrainFaultWarranty(), new TrainFaultWarranty()))
                    .setImportantFaultList(Arrays.asList(new DetailFault(), new DetailFault()));
        }

        List<TrainFaultWarranty> trainFaultWarrantyList = new ArrayList<>();
        List<DetailFault> importantFaultList = new ArrayList<>();
        for (PlanBase planBase : planBaseList) {
            Integer repairIndex = planBase.getRepairIndex();
            String trainNo = planBase.getTrainNo();
            String planId = planBase.getPlanId();

            int total = 0;
            int levelAB = 0;
            int online = 0;

            List<BuFaultInfo> currentPlanFaultList = faultList.stream()
                    .filter(fault -> planId.equals(fault.getPlanId()))
                    .collect(Collectors.toList());
            for (BuFaultInfo fault : currentPlanFaultList) {
                boolean isLevelAB = LEVEL_AB.contains(fault.getFaultLevel());
                boolean isOnline = 1 == fault.getFaultOnline();

                String systemName = systemIdNameMap.get(fault.getSysId());

                total++;
                if (isLevelAB) {
                    levelAB++;
                }
                if (isOnline) {
                    online++;
                }

                // 主要质保期故障
                if (isLevelAB) {
                    DetailFault detailFault = new DetailFault()
                            .setFaultId(fault.getId())
                            .setRepairIndex(fault.getRepairIndex())
                            .setTrainNo(fault.getTrainNo())
                            .setSystemName(systemName)
                            .setHappenTime(fault.getHappenTime())
                            .setFaultConditions(fault.getFaultDesc());
                    importantFaultList.add(detailFault);
                }
            }

            // 各车辆故障
            TrainFaultWarranty faultWarranty = new TrainFaultWarranty();
            faultWarranty.setWarrantyAB(levelAB)
                    .setWarrantyOnline(online)
                    .setRepairIndex(repairIndex)
                    .setTrainNo(trainNo)
                    .setTotal(total);
            trainFaultWarrantyList.add(faultWarranty);
        }

        trainFaultWarrantyList.sort(Comparator.comparing(TrainFaultWarranty::getRepairIndex, Comparator.nullsLast(Comparator.naturalOrder())));
        importantFaultList.sort(Comparator.comparing(DetailFault::getRepairIndex, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(DetailFault::getSystemName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(DetailFault::getHappenTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return new FaultWarranty()
                .setTrainList(trainFaultWarrantyList)
                .setImportantFaultList(importantFaultList);
    }

}
