package org.jeecg.modules.produce.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.produce.cost.bean.BuMaterialType;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterialActs;
import org.jeecg.modules.produce.cost.mapper.BuWorkOrderMaterialProduceMapper;
import org.jeecg.modules.produce.fault.bean.BuFaultInfo;
import org.jeecg.modules.produce.fault.mapper.BuFaultInfoProduceMapper;
import org.jeecg.modules.produce.plan.bean.BuMaterialMustList;
import org.jeecg.modules.produce.plan.bean.BuRepairPlan;
import org.jeecg.modules.produce.plan.bean.BuRepairPlanTask;
import org.jeecg.modules.produce.plan.bean.bo.BuRepairReguDetailBO;
import org.jeecg.modules.produce.plan.bean.bo.BuTrainStructureDetailBO;
import org.jeecg.modules.produce.plan.bean.vo.*;
import org.jeecg.modules.produce.plan.mapper.BuMaterialMustListProduceMapper;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanProduceMapper;
import org.jeecg.modules.produce.plan.mapper.BuRepairPlanTaskProduceMapper;
import org.jeecg.modules.produce.plan.service.BuRepairPlanProduceService;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainInfoProduceMapper;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainStructureDetailProduceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 列计划 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-27
 */
@Slf4j
@Service
public class BuRepairPlanProduceServiceImpl extends ServiceImpl<BuRepairPlanProduceMapper, BuRepairPlan> implements BuRepairPlanProduceService {

    @Resource
    private BuRepairPlanProduceMapper buRepairPlanProduceMapper;
    @Resource
    private BuRepairPlanTaskProduceMapper buRepairPlanTaskProduceMapper;
    @Resource
    private BuFaultInfoProduceMapper buFaultInfoProduceMapper;
    @Resource
    private BuTrainInfoProduceMapper buTrainInfoProduceMapper;
    @Resource
    private BuTrainStructureDetailProduceMapper buTrainStructureDetailProduceMapper;
    @Resource
    private BuMaterialMustListProduceMapper buMaterialMustListProduceMapper;
    @Resource
    private BuWorkOrderMaterialProduceMapper buWorkOrderMaterialProduceMapper;


    /**
     * @see BuRepairPlanProduceService#listAllGroupByLine()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuLineRepairPlanVO> listAllGroupByLine() {
        List<BuRepairPlan> planList = buRepairPlanProduceMapper.selectApprovedNotFinishListFromRepairPlanVO();

        List<BuLineRepairPlanVO> linePlanVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(planList)) {
            Map<String, List<BuRepairPlan>> lineIdPlanListMap = planList.stream()
                    .collect(Collectors.groupingBy(BuRepairPlan::getLineId));
            for (Map.Entry<String, List<BuRepairPlan>> lineIdPlanListEntry : lineIdPlanListMap.entrySet()) {
                String lineId = lineIdPlanListEntry.getKey();
                List<BuRepairPlan> linePlanList = lineIdPlanListEntry.getValue();

                String lineName = null;
                if (CollectionUtils.isNotEmpty(linePlanList)) {
                    lineName = linePlanList.get(0).getLineName();
                }
                BuLineRepairPlanVO lineRepairPlanVO = new BuLineRepairPlanVO()
                        .setLineId(lineId)
                        .setLineName(lineName)
                        .setPlans(transformToPlanVOList(linePlanList));

                linePlanVOList.add(lineRepairPlanVO);
            }
        }

        return linePlanVOList;
    }

    /**
     * @see BuRepairPlanProduceService#listAllGroupByDepot()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuDepotRepairPlanVO> listAllGroupByDepot() {
        List<BuRepairPlan> planList = buRepairPlanProduceMapper.selectApprovedNotFinishListFromRepairPlanVO();

        List<BuDepotRepairPlanVO> depotPlanVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(planList)) {
            Map<String, List<BuRepairPlan>> depotIdPlanListMap = planList.stream()
                    .collect(Collectors.groupingBy(BuRepairPlan::getDepotId));
            for (Map.Entry<String, List<BuRepairPlan>> depotIdPlanListEntry : depotIdPlanListMap.entrySet()) {
                String depotId = depotIdPlanListEntry.getKey();
                List<BuRepairPlan> depotPlanList = depotIdPlanListEntry.getValue();

                String depotName = null;
                if (CollectionUtils.isNotEmpty(depotPlanList)) {
                    depotName = depotPlanList.get(0).getDepotName();
                }
                BuDepotRepairPlanVO depotRepairPlanVO = new BuDepotRepairPlanVO()
                        .setDepotId(depotId)
                        .setDepotName(depotName)
                        .setPlans(transformToPlanVOList(depotPlanList));

                depotPlanVOList.add(depotRepairPlanVO);
            }
        }

        return depotPlanVOList;
    }

    /**
     * @see BuRepairPlanProduceService#getRepairPlanVOById(String, boolean)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanVO getRepairPlanVOById(String planId, boolean needCountInfo) throws Exception {
        BuRepairPlanVO repairPlanVO = buRepairPlanProduceMapper.selectAsRepairPlanVOByPlanId(planId);
        BuRepairPlanRelationVO planRelationVO = buRepairPlanProduceMapper.selectPlanRelevanceInfoByPlanId(planId);
        List<BuRepairPlanTask> taskList = buRepairPlanTaskProduceMapper.selectListByPlanId(planId);

        if (needCountInfo) {
            repairPlanVO.setCountItems(getCountItemVOList(planId, planRelationVO, taskList));
        } else {
            repairPlanVO.setCountItems(new ArrayList<>());
        }

        //TODO-zhaiyantao 2021/7/21 0:20 计划当前工期第x天，暂时用当前日期-开始日期，日期差表示
        Date startDate = repairPlanVO.getStartDate();
        Date now = new Date();
        int dayDiff = DateUtils.dayDiff(startDate, now);
        repairPlanVO.setCurrentDay(dayDiff);

        return repairPlanVO;
    }

    /**
     * @see BuRepairPlanProduceService#listReguVOByPlanId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanReguVO> listReguVOByPlanId(String planId) throws Exception {
        // 根据列计划id查询列计划关联的规程下的明细信息树
        List<BuRepairReguDetailBO> allReguDetailList = buRepairPlanProduceMapper.selectAllReguDetailListByPlanId(planId);
        List<BuRepairReguDetailBO> topReguDetailList = allReguDetailList.stream()
                .filter(detail -> StringUtils.isBlank(detail.getParentId()))
                .collect(Collectors.toList());
        for (BuRepairReguDetailBO reguDetailBO : topReguDetailList) {
            Set<String> childIdSet = new HashSet<>();
            recurseAddReguDetailChildId(reguDetailBO, allReguDetailList, childIdSet);
            reguDetailBO.setChildIdList(new ArrayList<>(childIdSet));
        }

        // 查询列计划任务和规程明细的关联信息
        List<Map<String, Object>> planTaskIdReguDetailIdMapList = buRepairPlanProduceMapper.selectPlanTaskIdAndReguDetailIdByPlanId(planId);
        // 查询列计划任务和故障数量的关联信息
        List<Map<String, Object>> planTaskIdFaultCountMapList = buRepairPlanProduceMapper.selectPlanTaskIdAndFaultCountByPlanId(planId);

        Map<String, List<String>> reguDetailIdPlanTaskIdListMap = getReguDetailIdPlanTaskIdListMap(planTaskIdReguDetailIdMapList);
        Map<String, BigDecimal> planTaskIdFaultCountMap = getPlanTaskIdFaultCountMap(planTaskIdFaultCountMapList);

        // 查询列计划任务
        Set<String> planTaskIdSet = new HashSet<>();
        for (List<String> value : reguDetailIdPlanTaskIdListMap.values()) {
            planTaskIdSet.addAll(value);
        }
        List<BuRepairPlanTask> planTaskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(planTaskIdSet)) {
            planTaskList = buRepairPlanTaskProduceMapper.selectBatchIds(planTaskIdSet);
        }

        // 生成结果
        List<BuRepairPlanReguVO> repairPlanReguVOList = new ArrayList<>();
        for (BuRepairReguDetailBO repairReguDetailBO : topReguDetailList) {
            List<String> childIdList = repairReguDetailBO.getChildIdList();
            childIdList.add(repairReguDetailBO.getId());
            Set<String> childPlanTaskIdSet = new HashSet<>();
            for (String childId : childIdList) {
                List<String> planTaskIdList = reguDetailIdPlanTaskIdListMap.get(childId);
                if (CollectionUtils.isNotEmpty(planTaskIdList)) {
                    childPlanTaskIdSet.addAll(planTaskIdList);
                }
            }

            // 故障数
            long faultCountTotal = 0L;
            for (String planTaskId : childPlanTaskIdSet) {
                BigDecimal faultCount = planTaskIdFaultCountMap.get(planTaskId);
                if (null != faultCount) {
                    faultCountTotal = faultCountTotal + faultCount.longValue();
                }
            }

            // 列计划任务列表
            List<BuRepairPlanTask> childPlanTaskList = planTaskList.stream()
                    .filter(planTask -> childPlanTaskIdSet.contains(planTask.getId()))
                    .collect(Collectors.toList());
            // 列计划任务进度
            int planTaskCount = childPlanTaskList.size();
            long finishPlanTaskCount = childPlanTaskList.stream()
                    .filter(planTask -> planTask.getStatus() == 2)
                    .count();
            Double progress = PercentUtils.percent(finishPlanTaskCount, planTaskCount);
            progress = planTaskCount <= 0 ? -1D : progress;
            int progressStatus = computeProgressStatusByPlanTask(childPlanTaskList, progress);

            BuRepairPlanReguVO repairPlanReguVO = new BuRepairPlanReguVO()
                    .setNo(repairReguDetailBO.getNo())
                    .setTitle(repairReguDetailBO.getTitle())
                    .setProgress(progress)
                    .setProgressStatus(progressStatus)
                    .setFaultCount(new Long(faultCountTotal).intValue());
            repairPlanReguVOList.add(repairPlanReguVO);
        }

        return repairPlanReguVOList.stream()
                .sorted(Comparator.comparingInt(vo -> Integer.parseInt(vo.getNo())))
                .collect(Collectors.toList());
    }

    /**
     * @see BuRepairPlanProduceService#listTrainStructVOByPlanId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuRepairPlanTrainStructVO> listTrainStructVOByPlanId(String planId) throws Exception {
        // 根据列计划id查询列计划车辆的车辆结构明细信息树
        String trainStructId = buTrainInfoProduceMapper.selectTrainStructIdByPlanId(planId);
        List<BuTrainStructureDetailBO> allStructDetailList = buTrainStructureDetailProduceMapper.selectSimpleListByStructId(trainStructId);
        long time3 = System.currentTimeMillis();
        List<BuTrainStructureDetailBO> topStructDetailList = allStructDetailList.stream()
                .filter(detail -> StringUtils.isBlank(detail.getParentId()))
                .collect(Collectors.toList());
        for (BuTrainStructureDetailBO topStructDetail : topStructDetailList) {
            recurseAddChildAndAssetTypeId(topStructDetail, allStructDetailList);
        }
        long time4 = System.currentTimeMillis();
        log.warn("生成车辆结构明细信息树 && childIdList----------用时：" + (time4 - time3));

        // 查询列计划下工单和车辆结构明细的关联信息
        List<Map<String, Object>> orderIdAssetTypeIdMapList = buRepairPlanProduceMapper.selectOrderIdAndAssetTypeIdByPlanId(planId);
        long time5 = System.currentTimeMillis();
        Map<String, List<String>> assetTypeIdOrderIdListMap = getAssetTypeIdOrderIdListMap(orderIdAssetTypeIdMapList);
        long time6 = System.currentTimeMillis();
        log.warn("生成工单和设备类型的关联信息----------用时：" + (time6 - time5));

        // 查询工单信息
        List<String> orderIdList = new ArrayList<>();
        for (List<String> value : assetTypeIdOrderIdListMap.values()) {
            orderIdList.addAll(value);
        }
        List<BuWorkOrderVO> workOrderVOList = buRepairPlanProduceMapper.selectWorkOrderVOListByOrderIdList(orderIdList);

        // 查询列计划下故障
        //TODO-zhaiyantao 2021/8/27 10:59 使用新资产结构作为部件，无法确定故障部件对应的设备类型和车辆结构明细
        List<BuFaultInfo> faultInfoList = buFaultInfoProduceMapper.selectListForCountByPlanId(planId);

        // 生成结果
        long time1 = System.currentTimeMillis();
        List<BuRepairPlanTrainStructVO> trainStructVOList = getBuRepairPlanTrainStructVOList(topStructDetailList, assetTypeIdOrderIdListMap, workOrderVOList, faultInfoList);
        long time2 = System.currentTimeMillis();
        log.warn("生成返回结果------------用时：" + (time2 - time1));
        return trainStructVOList;
//        // 根据列计划id查询列计划车辆的车辆结构明细信息树
//        List<BuTrainStructureDetailBO> allStructureDetailList = buRepairPlanProduceMapper.selectAllStructureDetailListByPlanId(planId);
//        List<BuTrainStructureDetailBO> topStructureDetailList = allStructureDetailList.stream()
//                .filter(detail -> StringUtils.isBlank(detail.getParentId()))
//                .sorted(Comparator.comparing(BuTrainStructureDetailBO::getSortNo, Comparator.nullsLast(Comparator.naturalOrder())))
//                .collect(Collectors.toList());
//        for (BuTrainStructureDetailBO structureDetailBO : allStructureDetailList) {
//            Set<String> childIdSet = new HashSet<>();
//            recurseAddStructureDetailChildId(structureDetailBO, allStructureDetailList, childIdSet);
//            structureDetailBO.setChildIdList(new ArrayList<>(childIdSet));
//        }
//
//        // 查询列计划下工单和车辆结构明细的关联信息
//        List<Map<String, String>> orderIdTrainStructureDetailIdMapList = buRepairPlanProduceMapper.selectOrderIdAndTrainStructureDetailIdByPlanId(planId);
//        Map<String, List<String>> trainStructureDetailIdOrderIdListMap = getTrainStructureDetailIdOrderIdListMap(orderIdTrainStructureDetailIdMapList);
//
//        // 查询工单信息
//        List<String> orderIdList = new ArrayList<>();
//        for (List<String> value : trainStructureDetailIdOrderIdListMap.values()) {
//            orderIdList.addAll(value);
//        }
//        List<BuWorkOrderVO> workOrderVOList = buRepairPlanProduceMapper.selectWorkOrderVOListByOrderIdList(orderIdList);
//
//        // 查询工单下故障
//        List<BuFaultInfo> faultInfoList = buFaultInfoProduceMapper.selectListForCountByOrderIdList(orderIdList);
//
//        // 生成结果
//        return getBuRepairPlanTrainStructVOList(topStructureDetailList, trainStructureDetailIdOrderIdListMap, workOrderVOList, faultInfoList);
    }

    /**
     * @see BuRepairPlanProduceService#listWorkOrderVOByOrderIds(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderVO> listWorkOrderVOByOrderIds(String orderIds) throws Exception {
        List<BuWorkOrderVO> workOrderVOList = new ArrayList<>();

        if (StringUtils.isNotBlank(orderIds)) {
            List<String> orderIdList = Arrays.asList(orderIds.split(","));
            workOrderVOList = buRepairPlanProduceMapper.selectWorkOrderVOListByOrderIdList(orderIdList);
        }

        return workOrderVOList;
    }


    private List<BuRepairPlanVO> transformToPlanVOList(List<BuRepairPlan> planList) {
        List<BuRepairPlanVO> planVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(planList)) {
            for (BuRepairPlan plan : planList) {
                BuRepairPlanVO planVO = new BuRepairPlanVO()
                        .setId(plan.getId())
                        .setPlanName(plan.getPlanName())
                        .setTrainNo(plan.getTrainNo())
                        .setProgressStatus(plan.getProgressStatus())
                        .setLineId(plan.getLineId())
                        .setLineName(plan.getLineName())
                        .setDepotId(plan.getDepotId())
                        .setDepotName(plan.getDepotName())
                        .setWorkshopId(plan.getWorkshopId())
                        .setWorkshopName(plan.getWorkshopName());
                planVOList.add(planVO);
            }
        }

        return planVOList;
    }

    private List<BuRepairPlanCountItemVO> getCountItemVOList(String id, BuRepairPlanRelationVO planRelationVO, List<BuRepairPlanTask> taskList) {
        List<BuRepairPlanTask> finishTaskList = taskList.stream()
                .filter(task -> task.getStatus() == 2)
                .collect(Collectors.toList());

        // 必换
        BuRepairPlanCountItemVO mustReplaceItem = new BuRepairPlanCountItemVO()
                .setTitle("必换件")
                .setCount(0D)
                .setFinish(0D);
        // 总数=对应的必换件清单
        LambdaQueryWrapper<BuMaterialMustList> mustListWrapper = new LambdaQueryWrapper<BuMaterialMustList>()
                .eq(BuMaterialMustList::getStatus, 1)
                .eq(BuMaterialMustList::getLineId, planRelationVO.getLineId())
                .eq(BuMaterialMustList::getRepairProgramId, planRelationVO.getRepairProgramId());
        List<BuMaterialMustList> mustList = buMaterialMustListProduceMapper.selectList(mustListWrapper);
        // 已完成=该列计划下已提交和已关闭的工单物料实际消耗
        List<BuWorkOrderMaterialActs> actList = buWorkOrderMaterialProduceMapper.selectActListOfSubmitOrderByPlanId(planRelationVO.getId());
        Set<String> mustMaterialTypeIdSet = new HashSet<>();
        mustList.forEach(must -> mustMaterialTypeIdSet.add(must.getMaterialTypeId()));
        for (BuWorkOrderMaterialActs act : actList) {
            if (null != act.getUseCategory() && 1 == act.getUseCategory()) {
                mustMaterialTypeIdSet.add(act.getMaterialTypeId());
            }
        }
        if (CollectionUtils.isNotEmpty(mustMaterialTypeIdSet)) {
            Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
            List<String> materialTypeIdList = new ArrayList<>(mustMaterialTypeIdSet);
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                List<BuMaterialType> subMaterialTypeList = buWorkOrderMaterialProduceMapper.selectMaterialListByMaterialTypeIdList(materialTypeIdBatchSub);
                for (BuMaterialType materialType : subMaterialTypeList) {
                    idMaterialTypeMap.put(materialType.getId(), materialType);
                }
            }

            double count = 0D;
            double finish = 0D;
            List<BuRepairMustCompareVO> mustCompareList = new ArrayList<>();
            for (String materialTypeId : mustMaterialTypeIdSet) {
                double mustSum = mustList.stream()
                        .filter(must -> materialTypeId.equals(must.getMaterialTypeId()))
                        .mapToDouble(BuMaterialMustList::getNeedAmount)
                        .sum();
                double actSum = actList.stream()
                        .filter(act -> materialTypeId.equals(act.getMaterialTypeId()))
                        .mapToDouble(BuWorkOrderMaterialActs::getActAmount)
                        .sum();

                count = count + mustSum;
                finish = finish + actSum;

                BuRepairMustCompareVO mustCompare = new BuRepairMustCompareVO()
                        .setMaterialTypeId(materialTypeId)
                        .setNeedAmount(BigDecimal.valueOf(mustSum))
                        .setConsumeAmount(BigDecimal.valueOf(actSum));
                if (idMaterialTypeMap.containsKey(materialTypeId)) {
                    BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                    mustCompare.setMaterialTypeCode(materialType.getCode())
                            .setMaterialTypeName(materialType.getName())
                            .setMaterialTypeSpec(materialType.getSpec());
                }
                mustCompareList.add(mustCompare);
            }
            mustReplaceItem.setCount(count)
                    .setFinish(finish)
                    .setDetails(mustCompareList);
        }

        // 委外
        BuRepairPlanCountItemVO outsourceItem = new BuRepairPlanCountItemVO()
                .setTitle("委外任务")
                .setCount(0D)
                .setFinish(0D);
        if (CollectionUtils.isNotEmpty(taskList)) {
            outsourceItem
                    .setCount(new Long(taskList.stream()
                            .filter(task -> task.getOutsource() == 1)
                            .count()).doubleValue())
                    .setFinish(new Long(finishTaskList.stream()
                            .filter(task -> task.getOutsource() == 1)
                            .count()).doubleValue());
        }

        // 工时
        BuRepairPlanCountItemVO workTimeItem = new BuRepairPlanCountItemVO()
                .setTitle("总工时")
                .setCount(0D)
                .setFinish(0D);
        if (CollectionUtils.isNotEmpty(taskList)) {
            workTimeItem
                    .setCount(taskList.stream()
                            .mapToDouble(BuRepairPlanTask::getWorkTime)
                            .sum())
                    .setFinish(finishTaskList.stream()
                            .mapToDouble(BuRepairPlanTask::getWorkTime)
                            .sum());
        }

        // 故障
        Integer faultCount = buRepairPlanProduceMapper.selectFaultCountByPlanId(id);
        Integer finishFaultCount = buRepairPlanProduceMapper.selectFinishFaultCountByPlanId(id);
        BuRepairPlanCountItemVO faultItem = new BuRepairPlanCountItemVO()
                .setTitle("故障数")
                .setCount(faultCount.doubleValue())
                .setFinish(finishFaultCount.doubleValue());

        return Arrays.asList(mustReplaceItem, outsourceItem, workTimeItem, faultItem);
    }

    private void recurseAddReguDetailChildId(BuRepairReguDetailBO reguDetailBO, List<BuRepairReguDetailBO> allReguDetailList, Set<String> childIdSet) {
        if (null == reguDetailBO) {
            return;
        }

        String id = reguDetailBO.getId();
        List<BuRepairReguDetailBO> children = allReguDetailList.stream()
                .filter(detail -> StringUtils.isNotBlank(id) && id.equals(detail.getParentId()))
                .collect(Collectors.toList());
        reguDetailBO.setChildren(children);
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuRepairReguDetailBO child : children) {
                childIdSet.add(child.getId());
                recurseAddReguDetailChildId(child, allReguDetailList, childIdSet);
            }
        }
    }

    private void recurseAddChildAndAssetTypeId(BuTrainStructureDetailBO parent, List<BuTrainStructureDetailBO> structDetailList) {
        if (null == parent) {
            return;
        }

        String parentId = parent.getId();
        List<BuTrainStructureDetailBO> children = structDetailList.stream()
                .filter(detail -> parentId.equals(detail.getParentId()))
                .collect(Collectors.toList());
        parent.setChildren(children);

        if (CollectionUtils.isNotEmpty(children)) {
            parent.setHasChildren(true);

            String parentWbs = parent.getWbs();
            int length = parentWbs.length();
            List<String> childAssetTypeIdList = structDetailList.stream()
                    .filter(detail -> null != detail.getWbs()
                            && detail.getWbs().length() > length
                            && detail.getWbs().substring(0, length).equals(parentWbs))
                    .map(BuTrainStructureDetailBO::getAssetTypeId)
                    .distinct()
                    .collect(Collectors.toList());
            parent.setChildAssetTypeIdList(childAssetTypeIdList);

            for (BuTrainStructureDetailBO child : children) {
                recurseAddChildAndAssetTypeId(child, structDetailList);
            }
        } else {
            parent.setHasChildren(false)
                    .setChildAssetTypeIdList(new ArrayList<>());
        }
    }

    private Map<String, List<String>> getReguDetailIdPlanTaskIdListMap(List<Map<String, Object>> planTaskIdReguDetailIdMapList) {
        if (CollectionUtils.isEmpty(planTaskIdReguDetailIdMapList)) {
            return new HashMap<>();
        }

        Map<String, List<String>> resultMap = new HashMap<>();
        for (Map<String, Object> planTaskIdReguDetailIdMap : planTaskIdReguDetailIdMapList) {
            String planTaskId = (String) planTaskIdReguDetailIdMap.get("planTaskId");
            String reguDetailId = (String) planTaskIdReguDetailIdMap.get("reguDetailId");
            if (StringUtils.isBlank(planTaskId) || StringUtils.isBlank(reguDetailId)) {
                continue;
            }

            List<String> planTaskIdList = new ArrayList<>();
            if (resultMap.containsKey(reguDetailId)) {
                planTaskIdList = resultMap.get(reguDetailId);
            }
            if (!planTaskIdList.contains(planTaskId)) {
                planTaskIdList.add(planTaskId);
            }
            resultMap.put(reguDetailId, planTaskIdList);
        }

        return resultMap;
    }

    private Map<String, BigDecimal> getPlanTaskIdFaultCountMap(List<Map<String, Object>> planTaskIdFaultCountMapList) {
        if (CollectionUtils.isEmpty(planTaskIdFaultCountMapList)) {
            return new HashMap<>();
        }

        Map<String, BigDecimal> resultMap = new HashMap<>();
        for (Map<String, Object> planTaskIdFaultCountMap : planTaskIdFaultCountMapList) {
            String planTaskId = (String) planTaskIdFaultCountMap.get("planTaskId");
            BigDecimal faultCount = DataTypeCastUtil.transNumber(planTaskIdFaultCountMap.get("faultCount"));
            if (StringUtils.isBlank(planTaskId) || null == faultCount || BigDecimal.ZERO.compareTo(faultCount) == 0) {
                continue;
            }

            BigDecimal count = BigDecimal.ZERO;
            if (resultMap.containsKey(planTaskId)) {
                count = resultMap.get(planTaskId);
            }
            resultMap.put(planTaskId, count.add(faultCount));
        }

        return resultMap;
    }

    private Map<String, List<String>> getAssetTypeIdOrderIdListMap(List<Map<String, Object>> orderIdAssetTypeIdMapList) {
        if (CollectionUtils.isEmpty(orderIdAssetTypeIdMapList)) {
            return new HashMap<>();
        }

        Map<String, List<String>> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(orderIdAssetTypeIdMapList)) {
            for (Map<String, Object> orderIdAssetTypeIdMap : orderIdAssetTypeIdMapList) {
                String orderId = (String) orderIdAssetTypeIdMap.get("orderId");
                String assetTypeId = (String) orderIdAssetTypeIdMap.get("assetTypeId");
                if (StringUtils.isBlank(orderId) || StringUtils.isBlank(assetTypeId)) {
                    continue;
                }

                List<String> orderIdList = new ArrayList<>();
                if (resultMap.containsKey(assetTypeId)) {
                    orderIdList = resultMap.get(assetTypeId);
                }
                if (!orderIdList.contains(orderId)) {
                    orderIdList.add(orderId);
                }
                resultMap.put(assetTypeId, orderIdList);
            }
        }

        return resultMap;
    }

    private int computeProgressStatusByPlanTask(List<BuRepairPlanTask> planTaskList, Double progress) {
        // 进度状态 字典dictCode=bu_progress_status
        int progressStatus = 0;
        if (progress <= 0) {
            return progressStatus;
        }

        Date now = new Date();
        Date latestFinishTime = null;
        Date latestActFinish = null;
        for (BuRepairPlanTask planTask : planTaskList) {
            Date finishTime = planTask.getFinishTime();
            Date actFinish = planTask.getActFinish();

            if (finishTime != null) {
                if (latestFinishTime == null) {
                    latestFinishTime = finishTime;
                }
                if (finishTime.after(latestFinishTime)) {
                    latestFinishTime = finishTime;
                }
            }
            if (actFinish != null) {
                if (latestActFinish == null) {
                    latestActFinish = actFinish;
                }
                if (actFinish.after(latestActFinish)) {
                    latestActFinish = actFinish;
                }
            }
        }

        // 计算进度状态，根据当前进度和时间去算
        if (progress == 100) {
            progressStatus = 3;
            if (latestActFinish != null && latestFinishTime != null) {
                if (latestActFinish.after(latestFinishTime)) {
                    progressStatus = 4;
                }
                if (latestActFinish.before(latestFinishTime)) {
                    progressStatus = 5;
                }
            }
        } else if (progress > 0 && progress < 100) {
            progressStatus = 1;
            if (latestFinishTime != null && now.after(latestFinishTime)) {
                progressStatus = 2;
            }
        }

        return progressStatus;
    }

    private int computeProgressStatus(List<BuWorkOrderVO> childOrderList, Double progress) {
        // 进度状态 字典dictCode=bu_progress_status
        int progressStatus = 0;
        if (progress <= 0) {
            return progressStatus;
        }

        Date now = new Date();
        Date latestFinishTime = null;
        Date latestActFinish = null;
        for (BuWorkOrderVO workOrderVO : childOrderList) {
            Date finishTime = workOrderVO.getFinishTime();
            Date actFinish = workOrderVO.getActFinish();

            if (finishTime != null) {
                if (latestFinishTime == null) {
                    latestFinishTime = finishTime;
                }
                if (finishTime.after(latestFinishTime)) {
                    latestFinishTime = finishTime;
                }
            }
            if (actFinish != null) {
                if (latestActFinish == null) {
                    latestActFinish = actFinish;
                }
                if (actFinish.after(latestActFinish)) {
                    latestActFinish = actFinish;
                }
            }
        }

        // 计算进度状态，根据当前进度和时间去算
        if (progress == 100) {
            progressStatus = 3;
            if (latestActFinish != null && latestFinishTime != null) {
                if (latestActFinish.after(latestFinishTime)) {
                    progressStatus = 4;
                }
                if (latestActFinish.before(latestFinishTime)) {
                    progressStatus = 5;
                }
            }
        } else if (progress > 0 && progress < 100) {
            progressStatus = 1;
            if (latestFinishTime != null && now.after(latestFinishTime)) {
                progressStatus = 2;
            }
        }

        return progressStatus;
    }

    private List<BuRepairPlanTrainStructVO> getBuRepairPlanTrainStructVOList(List<BuTrainStructureDetailBO> structureDetailList,
                                                                             Map<String, List<String>> assetTypeIdOrderIdListMap,
                                                                             List<BuWorkOrderVO> workOrderVOList,
                                                                             List<BuFaultInfo> faultInfoList) {
        if (CollectionUtils.isEmpty(structureDetailList)) {
            return new ArrayList<>();
        }

        List<BuRepairPlanTrainStructVO> repairPlanTrainStructVOList = new ArrayList<>();
        for (BuTrainStructureDetailBO structureDetailBO : structureDetailList) {
            List<String> childAssetTypeIdList = structureDetailBO.getChildAssetTypeIdList();
            childAssetTypeIdList.add(structureDetailBO.getAssetTypeId());
            List<String> childOrderIdList = new ArrayList<>();
            for (String childAssetTypeId : childAssetTypeIdList) {
                List<String> structDetailAssetTypeChildOrderIdList = assetTypeIdOrderIdListMap.get(childAssetTypeId);
                if (CollectionUtils.isNotEmpty(structDetailAssetTypeChildOrderIdList)) {
                    childOrderIdList.addAll(structDetailAssetTypeChildOrderIdList);
                }
            }

            // 故障数
            long faultCount = faultInfoList.stream()
                    .filter(fault -> childOrderIdList.contains(fault.getWorkOrderId()))
                    .filter(fault -> childAssetTypeIdList.contains(fault.getFaultAssetTypeId()))
                    .count();

            // 工单列表
            List<BuWorkOrderVO> childOrderList = workOrderVOList.stream()
                    .filter(order -> childOrderIdList.contains(order.getId()))
                    .collect(Collectors.toList());
            // 工单进度
            int orderCount = childOrderList.size();
            long finishOrderCount = childOrderList.stream()
                    .filter(order -> order.getWorkStatus() == 2)
                    .count();
            Double progress = PercentUtils.percent(finishOrderCount, orderCount);
            progress = orderCount <= 0 ? -1D : progress;
            int progressStatus = computeProgressStatus(childOrderList, progress);

            // 子节点转换
            List<BuTrainStructureDetailBO> structureDetailBOChildren = structureDetailBO.getChildren();
            List<BuRepairPlanTrainStructVO> childRepairPlanTrainStructVOList = getBuRepairPlanTrainStructVOList(structureDetailBOChildren, assetTypeIdOrderIdListMap, workOrderVOList, faultInfoList);

            BuRepairPlanTrainStructVO repairPlanTrainStructVO = new BuRepairPlanTrainStructVO()
                    .setId(structureDetailBO.getId())
                    .setName(structureDetailBO.getName())
                    .setWbs(structureDetailBO.getWbs())
                    .setSortNo(structureDetailBO.getSortNo())
                    .setParentId(structureDetailBO.getParentId())
                    .setProgress(progress)
                    .setProgressStatus(progressStatus)
                    .setFaultCount(new Long(faultCount).intValue())
                    .setCurrentOrderCount(orderCount - new Long(finishOrderCount).intValue())
                    .setOrderCount(orderCount)
                    .setOrderIds(StringUtils.join(childOrderIdList, ","))
                    .setHasChildren(structureDetailBO.getHasChildren())
                    .setChildren(childRepairPlanTrainStructVOList);

            repairPlanTrainStructVOList.add(repairPlanTrainStructVO);
        }
        repairPlanTrainStructVOList.sort(Comparator.comparing(BuRepairPlanTrainStructVO::getSortNo, Comparator.nullsLast(Comparator.naturalOrder())));

        return repairPlanTrainStructVOList;
    }

}
