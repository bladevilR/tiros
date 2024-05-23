package org.jeecg.modules.tiros.task.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.task.service.WorkOrderGenerateTaskService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.bean.vo.StartVO;
import org.jeecg.common.workflow.constant.WfConstant;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanForms;
import org.jeecg.modules.dispatch.planform.bean.BuRepairPlanTaskForms;
import org.jeecg.modules.dispatch.planform.bean.vo.BuPlanFormChoiceVO;
import org.jeecg.modules.dispatch.planform.mapper.BuRepairPlanFormsMapper;
import org.jeecg.modules.dispatch.planform.mapper.BuRepairPlanTaskFormsMapper;
import org.jeecg.modules.dispatch.serialplan.bean.*;
import org.jeecg.modules.dispatch.serialplan.mapper.*;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.mapper.*;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.mapper.BuMaterialToolsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 自动生成工单定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Slf4j
@Service
public class WorkOrderGenerateTaskServiceImpl implements WorkOrderGenerateTaskService {

    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;
    @Resource
    private BuRepairPlanTaskMapper buRepairPlanTaskMapper;
    @Resource
    private BuRepairTaskWorkstationMapper buRepairTaskWorkstationMapper;
    @Resource
    private BuRepairTaskMaterialMapper buRepairTaskMaterialMapper;
    @Resource
    private BuRepairTaskMustReplaceMapper buRepairTaskMustReplaceMapper;
    @Resource
    private BuRepairTaskToolMapper buRepairTaskToolMapper;
    @Resource
    private BuRepairReguTechFileDispatchMapper buRepairReguTechFileDispatchMapper;
    @Resource
    private BuRepairTaskReguMapper buRepairTaskReguMapper;
    @Resource
    private BuRepairPlanTaskEquMapper buRepairPlanTaskEquMapper;
    @Resource
    private BuRepairPlanFormsMapper buRepairPlanFormsMapper;
    @Resource
    private BuRepairPlanTaskFormsMapper buRepairPlanTaskFormsMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuWorkOrderTaskWorkstationMapper buWorkOrderTaskWorkstationMapper;
    @Resource
    private BuWorkOrderMaterialMapper buWorkOrderMaterialMapper;
    @Resource
    private BuWorkOrderToolMapper buWorkOrderToolMapper;
    @Resource
    private BuWorkOrderTechFileMapper buWorkOrderTechFileMapper;
    @Resource
    private BuWorkOrderTaskEquMapper buWorkOrderTaskEquMapper;
    @Resource
    private BuWorkOrderTaskFormInstMapper buWorkOrderTaskFormInstMapper;
    @Resource
    private BuMaterialToolsMapper buMaterialToolsMapper;
    @Resource
    private BuTrainAssetDispatchMapper buTrainAssetDispatchMapper;
    @Resource
    private BuMaterialMustListMapper buMaterialMustListMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private WorkflowForwardService workflowForwardService;

    private final String outSuffix = "(委外出库)";
    private final String inSuffix = "(委外入库)";
    private final int outRepairNeedDay = 10;


    /**
     * @see WorkOrderGenerateTaskService#generateOrder(Date, Boolean, String, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String generateOrder(Date date, Boolean generateEarlierOrder, String planId, Boolean startFlow) throws RuntimeException {
        Calendar calendar = Calendar.getInstance();
        if (null != date) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();

        String resultMessage;
        List<BuRepairPlan> planList;

        if (StringUtils.isBlank(planId)) {
            planList = buRepairPlanMapper.selectApprovedNotFinishedPlanList(null);
            if (CollectionUtils.isEmpty(planList)) {
                resultMessage = "未找到需要生成工单的列计划，没有生成工单";
                log.warn(resultMessage);
                return resultMessage;
            }
        } else {
            BuRepairPlan plan = buRepairPlanMapper.selectById(planId);
            planList = Collections.singletonList(plan);

            if (2 != plan.getStatus()) {
                resultMessage = "列计划[" + plan.getPlanName() + "]未完成审批，不能生成工单";
                log.warn(resultMessage);
                return resultMessage;
            }
            if (Arrays.asList(3, 4, 5).contains(plan.getProgressStatus())) {
                resultMessage = "列计划[" + plan.getPlanName() + "]已完成，不能生成工单";
                log.warn(resultMessage);
                return resultMessage;
            }
        }

        // 获取班组信息
        List<BuMtrWorkshopGroup> groupList = buRepairPlanMapper.selectAllWorkGroupList();
        Map<String, BuMtrWorkshopGroup> idGroupMap = new HashMap<>();
        groupList.forEach(group -> idGroupMap.put(group.getId(), group));

        List<BuWorkOrder> allOrderList = new ArrayList<>();
        List<BuWorkOrderTask> allOrderTaskList = new ArrayList<>();
        List<BuWorkOrderTaskWorkstation> allOrderTaskWorkstationList = new ArrayList<>();
        List<BuWorkOrderMaterial> allOrderTaskMaterialList = new ArrayList<>();
        List<BuWorkOrderTool> allOrderTaskToolList = new ArrayList<>();
        List<BuWorkOrderTechFile> allOrderTaskTechFileList = new ArrayList<>();
        // 计划模板和列计划关联的车辆结构在生成工单时清空，因为工单需要关联maximo资产设备，现在无法根据车辆结构找到maximo资产设备
//        List<BuWorkOrderTaskEqu> allOrderTaskEquipmentList = new ArrayList<>();

        List<String> planTaskIdListToUpdate = new ArrayList<>();

        for (BuRepairPlan plan : planList) {
            List<BuRepairPlanTask> selectPlanTaskList;
            if (null != generateEarlierOrder && generateEarlierOrder) {
                selectPlanTaskList = buRepairPlanTaskMapper.selectListBeforeAndEqualDate(plan.getId(), date);
            } else {
                selectPlanTaskList = buRepairPlanTaskMapper.selectListEqualDate(plan.getId(), date);
            }

            // 去掉委外劳务班的列计划任务，这里为硬编码
            selectPlanTaskList.removeIf(planTask -> "wwlwb".equals(planTask.getWorkGroupId()));

            if (CollectionUtils.isEmpty(selectPlanTaskList)) {
                String dateString = DateUtils.date_sdf.get().format(date);
                resultMessage = "列计划[" + plan.getPlanName() + "]在" + dateString + "日期没有任务需要生成工单";
                log.warn(resultMessage);
                continue;
            }

            // 没有工期序号，设置工期序号为0
            for (BuRepairPlanTask planTask : selectPlanTaskList) {
                if (null == planTask.getDayIndex()) {
                    planTask.setDayIndex(0);
                }
            }

            // 获取列计划任务关联信息
            List<BuRepairTaskWorkstation> planTaskWorkstationList = buRepairTaskWorkstationMapper.selectListForGenerateOrderByPlanId(plan.getId());
            List<BuRepairTaskMaterial> planTaskMaterialList = buRepairTaskMaterialMapper.selectListForGenerateOrderByPlanId(plan.getId());
//            List<BuRepairTaskMustReplace> planTaskMustReplaceList = buRepairTaskMustReplaceMapper.selectListForGenerateOrderByPlanId(plan.getId());
            List<BuRepairTaskTool> planTaskToolList = buRepairTaskToolMapper.selectListForGenerateOrderByPlanId(plan.getId());
            List<BuRepairTaskRegu> planTaskReguList = buRepairTaskReguMapper.selectListForGenerateOrderByPlanId(plan.getId());
            List<BuRepairReguTechFile> reguTechFileList = new ArrayList<>();
            List<String> reguDetailIdList = planTaskReguList.stream()
                    .map(BuRepairTaskRegu::getReguDetailId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(reguDetailIdList)) {
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(reguDetailIdList);
                for (List<String> batchSub : batchSubList) {
                    LambdaQueryWrapper<BuRepairReguTechFile> reguTechFileWrapper = new LambdaQueryWrapper<BuRepairReguTechFile>()
                            .in(BuRepairReguTechFile::getReguDetailId, batchSub);
                    List<BuRepairReguTechFile> subReguTechFileList = buRepairReguTechFileDispatchMapper.selectList(reguTechFileWrapper);
                    reguTechFileList.addAll(subReguTechFileList);
                }
            }
            List<BuRepairPlanTaskEqu> planTaskEquipmentList = buRepairPlanTaskEquMapper.selectListForGenerateOrderByPlanId(plan.getId());

            // 获取每个工班具体工器具信息
            Map<String, List<BuMaterialTools>> groupIdToolList = new HashMap<>();
            List<String> toolTypeIdList = planTaskToolList.stream()
                    .map(BuRepairTaskTool::getToolTypeId)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(toolTypeIdList)) {
                List<BuMaterialTools> toolsList = new ArrayList<>();
                List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(toolTypeIdList);
                for (List<String> batchSub : batchSubList) {
                    List<BuMaterialTools> subToolsList = buMaterialToolsMapper.selectUseableToolListByTypeIdList(batchSub);
                    toolsList.addAll(subToolsList);
                }
                for (BuMtrWorkshopGroup group : groupList) {
                    String groupId = group.getId();
                    List<BuMaterialTools> groupToolsList = toolsList.stream()
                            .filter(tool -> groupId.equals(tool.getGroupId()))
                            .collect(Collectors.toList());
                    groupIdToolList.put(groupId, groupToolsList);
                }
            }

            // 提取设置列计划任务关联信息
//            extractPlanTaskRelation(selectPlanTaskList, planTaskWorkstationList, planTaskMaterialList, planTaskMustReplaceList, planTaskToolList, planTaskReguList, reguTechFileList, planTaskEquipmentList);
            extractPlanTaskRelation(selectPlanTaskList, planTaskWorkstationList, planTaskMaterialList, planTaskToolList, planTaskReguList, reguTechFileList, planTaskEquipmentList);

            // 有可能需要生成之前几天的列计划任务的工单，对列计划任务按天分组
            Map<Integer, List<BuRepairPlanTask>> dayIndexPlanTaskListMap = selectPlanTaskList.stream()
                    .collect(Collectors.groupingBy(BuRepairPlanTask::getDayIndex));
            for (Map.Entry<Integer, List<BuRepairPlanTask>> dayIndexPlanTaskListEntry : dayIndexPlanTaskListMap.entrySet()) {
                List<BuRepairPlanTask> dayPlanTaskList = dayIndexPlanTaskListEntry.getValue();

                // 获取用于生成工单的列计划任务
                List<BuRepairPlanTask> planTaskList = extractPlanTaskLeafNodeList(null, dayPlanTaskList);
                // 过滤出需要生成工单且未生成的列计划任务
                planTaskList.removeIf(planTask -> !(1 == planTask.getGenOrder() && 0 == planTask.getHasGen()));
                if (CollectionUtils.isEmpty(planTaskList)) {
                    continue;
                }

                // 分组委外任务和计划任务
                List<BuRepairPlanTask> groupOutPlanTaskList = new ArrayList<>();
                List<BuRepairPlanTask> groupPlanPlanTaskList = new ArrayList<>();
                for (BuRepairPlanTask planTask : planTaskList) {
                    planTaskIdListToUpdate.add(planTask.getId());

                    if (1 == planTask.getOutsource()) {
                        groupOutPlanTaskList.add(planTask);
                    } else {
                        groupPlanPlanTaskList.add(planTask);
                    }
                }

                // 生成委外工单：根据任务的是否委外来确定是不是委外任务，根据委外任务名称中“出库”“入库”来确定委外任务类型
                if (CollectionUtils.isNotEmpty(groupOutPlanTaskList)) {
//                    // 查询委外维修所需天数：根据设备类型找到对应的最新的合同，取合同所需天数
//                    Set<String> assetTypeIdSet = groupOutPlanTaskList.stream()
//                            .map(BuRepairPlanTask::getAssetTypeId)
//                            .filter(StringUtils::isNotBlank)
//                            .collect(Collectors.toSet());
//                    Map<String, Integer> assetTypeIdOutRepairNeedDayMap = new HashMap<>();
//                    if (CollectionUtils.isNotEmpty(assetTypeIdSet)) {
//                        List<BuContractInfo> contractInfoList = new ArrayList<>();
//                        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(assetTypeIdSet));
//                        for (List<String> batchSub : batchSubList) {
//                            LambdaQueryWrapper<BuContractInfo> contractWrapper = new LambdaQueryWrapper<BuContractInfo>()
//                                    .in(BuContractInfo::getAssetTypeId, batchSub);
//                            List<BuContractInfo> subContractInfoList = buContractInfoMapper.selectList(contractWrapper);
//                            contractInfoList.addAll(subContractInfoList);
//                        }
//
//                        for (String assetTypeId : assetTypeIdSet) {
//                            List<BuContractInfo> matchContractList = contractInfoList.stream()
//                                    .filter(contract -> assetTypeId.equals(contract.getAssetTypeId()))
//                                    .collect(Collectors.toList());
//                            if (CollectionUtils.isEmpty(matchContractList)) {
//                                assetTypeIdOutRepairNeedDayMap.put(assetTypeId, outRepairNeedDay);
//                                continue;
//                            }
//
//                            matchContractList.sort(Comparator.comparing(BuContractInfo::getSignDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
//                            Integer needDay = matchContractList.get(0).getNeedDay();
//                            if (null == needDay) {
//                                assetTypeIdOutRepairNeedDayMap.put(assetTypeId, outRepairNeedDay);
//                                continue;
//                            }
//                            assetTypeIdOutRepairNeedDayMap.put(assetTypeId, needDay);
//                        }
//                    }

                    // 按最小一级生成工单：一条列计划任务对应一个工单
                    for (BuRepairPlanTask planTask : groupOutPlanTaskList) {
                        String groupId = planTask.getWorkGroupId();
                        BuMtrWorkshopGroup group = idGroupMap.get(groupId);
                        List<BuMaterialTools> groupToolsList = groupIdToolList.get(groupId);
                        String groupMonitor = getGroupMonitor(group);

                        // 根据委外任务名称中“出库”“入库”来确定委外任务类型
                        String taskName = planTask.getTaskName();
                        int outTaskType = 6;
                        if (StringUtils.isNotBlank(taskName) && taskName.contains("入库")) {
                            outTaskType = 7;
                        }

                        if (outTaskType == 6) {
                            BuWorkOrder outOrder = createOutOrder(plan, planTask, groupMonitor);
                            BuWorkOrderTask outOrderTask = createOutOrderTaskAndRelation(plan, planTask, outOrder.getId(), outOrder.getTrainNo(), groupToolsList);

                            // 添加到列表，统一保存
                            allOrderList.add(outOrder);
                            allOrderTaskList.add(outOrderTask);
                            allOrderTaskWorkstationList.addAll(outOrderTask.getWorkstations());
                            allOrderTaskMaterialList.addAll(outOrderTask.getMaterials());
                            allOrderTaskToolList.addAll(outOrderTask.getTools());
                            allOrderTaskTechFileList.addAll(outOrderTask.getTechFiles());
                            // 计划模板和列计划关联的车辆结构在生成工单时清空，因为工单需要关联maximo资产设备，现在无法根据车辆结构找到maximo资产设备
//                            allOrderTaskEquipmentList.addAll(outOrderTask.getEquipments());
                        } else {
                            BuWorkOrder inOrder = createInOrder(plan, planTask, groupMonitor);
                            BuWorkOrderTask inOrderTask = createInOrderTaskAndRelation(plan, planTask, inOrder.getId(), inOrder.getTrainNo(), groupToolsList);

                            // 添加到列表，统一保存
                            allOrderList.add(inOrder);
                            allOrderTaskList.add(inOrderTask);
                            allOrderTaskWorkstationList.addAll(inOrderTask.getWorkstations());
                            allOrderTaskMaterialList.addAll(inOrderTask.getMaterials());
                            allOrderTaskToolList.addAll(inOrderTask.getTools());
                            allOrderTaskTechFileList.addAll(inOrderTask.getTechFiles());
                            // 计划模板和列计划关联的车辆结构在生成工单时清空，因为工单需要关联maximo资产设备，现在无法根据车辆结构找到maximo资产设备
//                            allOrderTaskEquipmentList.addAll(inOrderTask.getEquipments());
                        }
                    }
                }

                // 生成计划工单：一个班组id生成一张工单(同班组id的多个计划任务生成同工单下的多个工单任务)
                if (CollectionUtils.isNotEmpty(groupPlanPlanTaskList)) {
                    // 按最小一级生成工单：一条列计划任务对应一个工单
                    for (BuRepairPlanTask planTask : groupPlanPlanTaskList) {
                        String groupId = planTask.getWorkGroupId();
                        BuMtrWorkshopGroup group = idGroupMap.get(groupId);
                        List<BuMaterialTools> groupToolsList = groupIdToolList.get(groupId);
                        String groupMonitor = getGroupMonitor(group);

                        BuWorkOrder order = createWorkOrder(plan, planTask, groupMonitor);
                        BuWorkOrderTask orderTask = createWorkOrderTaskAndRelation(plan, planTask, order.getId(), order.getTrainNo(), groupToolsList);

                        // 添加到列表，统一保存
                        allOrderList.add(order);
                        allOrderTaskList.add(orderTask);
                        allOrderTaskWorkstationList.addAll(orderTask.getWorkstations());
                        allOrderTaskMaterialList.addAll(orderTask.getMaterials());
                        allOrderTaskToolList.addAll(orderTask.getTools());
                        allOrderTaskTechFileList.addAll(orderTask.getTechFiles());
                        // 计划模板和列计划关联的车辆结构在生成工单时清空，因为工单需要关联maximo资产设备，现在无法根据车辆结构找到maximo资产设备
//                        allOrderTaskEquipmentList.addAll(orderTask.getEquipments());
                    }
                }
            }
        }

        // 保存工单及任务及关联信息
        // 计划模板和列计划关联的车辆结构在生成工单时清空，因为工单需要关联maximo资产设备，现在无法根据车辆结构找到maximo资产设备
//        saveOrderAndTaskAndRelation(allOrderList, allOrderTaskList, allOrderTaskWorkstationList, allOrderTaskMaterialList, allOrderTaskToolList, allOrderTaskTechFileList, allOrderTaskEquipmentList);
        saveOrderAndTaskAndRelation(allOrderList, allOrderTaskList, allOrderTaskWorkstationList, allOrderTaskMaterialList, allOrderTaskToolList, allOrderTaskTechFileList, new ArrayList<>());
        // 建立列计划表单实例和工单任务的关联
        insertFormInstByPlanTaskAndWorkstationRef(allOrderList, allOrderTaskList);
        // 更新列计划任务为已生成工单
        buRepairPlanTaskMapper.updatePlanTaskGeneratedYes(planTaskIdListToUpdate);

//            // 暂时不启动流程
//            if (null != startFlow && startFlow) {
//                for (BuWorkOrder order : allOrderList) {
//                    startOrderWorkflow(order);
//                }
//            }

        List<String> planNameList = planList.stream()
                .map(BuRepairPlan::getPlanName)
                .collect(Collectors.toList());
        resultMessage = "由列计划[" + String.join(",", planNameList) + "]" + "成功生成" + allOrderList.size() + "个工单";
        log.info(resultMessage);

        return resultMessage;
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

    private BuWorkOrder createWorkOrder(BuRepairPlan plan,
                                        BuRepairPlanTask planTask,
                                        String groupMonitor) {
        String code = serialNumberGenerate.generateOrderCode(plan.getLineId());
//        String orderName = plan.getTrainNo() + "车" + group.getGroupName() + "第" + planTask.getDayIndex() + "天作业";
        String orderName = plan.getTrainNo() + "车" + "第" + planTask.getDayIndex() + "天作业" + "-" + planTask.getTaskName();

        return new BuWorkOrder()
                .setId(UUIDGenerator.generate())
                .setOrderCode(code)
                .setOrderName(orderName)
                .setOrderType(1)
                .setGenerate(1)
                .setFromType(1)
                .setPlanId(plan.getId())
                .setStartTime(planTask.getStartTime())
                .setFinishTime(planTask.getFinishTime())
                .setDuration(null == planTask.getDuration() ? 0 : planTask.getDuration().intValue())
                .setGroupId(planTask.getWorkGroupId())
                .setMonitor(groupMonitor)
                .setWorkshopId(plan.getWorkshopId())
                .setLineId(plan.getLineId())
                .setTrainNo(plan.getTrainNo())
                .setOrderStatus(0)
                .setWorkStatus(0)
                .setRemark("由列计划" + plan.getPlanName() + "自动生成的计划工单")
                .setFdProject(plan.getFdProject())
                .setFdTask(plan.getFdTask())
                .setFdCostType(plan.getFdCostType())
                .setCreateTime(new Date())
                .setCompanyId(plan.getCompanyId());
    }

    private BuWorkOrder createOutOrder(BuRepairPlan plan,
                                       BuRepairPlanTask planTask,
                                       String groupMonitor) {
        String code = serialNumberGenerate.generateOrderCode(plan.getLineId());
//        String orderName = plan.getTrainNo() + "车" + group.getGroupName() + "第" + planTask.getDayIndex() + "天作业" + outSuffix;
        String orderName = plan.getTrainNo() + "车" + "第" + planTask.getDayIndex() + "天" + outSuffix + "作业" + "-" + planTask.getTaskName();
//        String orderName = plan.getTrainNo() + "车" + "第" + planTask.getDayIndex() + "天作业" + "-" + planTask.getTaskName();

        return new BuWorkOrder()
                .setId(UUIDGenerator.generate())
                .setOrderCode(code)
                .setOrderName(orderName)
                .setOrderType(1)
                .setGenerate(1)
                .setPlanId(plan.getId())
                .setStartTime(planTask.getStartTime())
                .setFinishTime(planTask.getFinishTime())
                .setDuration(null == planTask.getDuration() ? 0 : planTask.getDuration().intValue())
                .setGroupId(planTask.getWorkGroupId())
                .setMonitor(groupMonitor)
                .setWorkshopId(plan.getWorkshopId())
                .setLineId(plan.getLineId())
                .setTrainNo(plan.getTrainNo())
                .setOrderStatus(0)
                .setWorkStatus(0)
                .setRemark("由列计划" + plan.getPlanName() + "自动生成的" + outSuffix + "工单")
                .setFdProject(plan.getFdProject())
                .setFdTask(plan.getFdTask())
                .setFdCostType(plan.getFdCostType())
                .setCreateTime(new Date())
                .setCompanyId(plan.getCompanyId());
    }

    private BuWorkOrder createInOrder(BuRepairPlan plan,
                                      BuRepairPlanTask planTask,
                                      String groupMonitor) {
        String code = serialNumberGenerate.generateOrderCode(plan.getLineId());
//        String orderName = plan.getTrainNo() + "车" + group.getGroupName() + "第" + planTask.getDayIndex() + "天作业" + inSuffix;
        String orderName = plan.getTrainNo() + "车" + "第" + planTask.getDayIndex() + "天" + inSuffix + "作业" + "-" + planTask.getTaskName();

//        // 计算出计划任务的返厂日期，作为工单的日期
//        String assetTypeId = planTask.getAssetTypeId();
//        // 获取委外维修所需天数
//        int needDay = outRepairNeedDay;
//        if (null != assetTypeIdOutRepairNeedDayMap.get(assetTypeId)) {
//            needDay = assetTypeIdOutRepairNeedDayMap.get(assetTypeId);
//        }
//        Calendar startCalendar = Calendar.getInstance();
//        startCalendar.setTime(planTask.getStartTime());
//        startCalendar.add(Calendar.DATE, needDay);
//        Date returnStartTime = startCalendar.getTime();
//        Calendar finishCalendar = Calendar.getInstance();
//        finishCalendar.setTime(planTask.getFinishTime());
//        finishCalendar.add(Calendar.DATE, needDay);
//        Date returnFinishTime = finishCalendar.getTime();

        return new BuWorkOrder()
                .setId(UUIDGenerator.generate())
                .setOrderCode(code)
                .setOrderName(orderName)
                .setOrderType(1)
                .setGenerate(1)
                .setPlanId(plan.getId())
                .setStartTime(planTask.getStartTime())
                .setFinishTime(planTask.getFinishTime())
                .setDuration(null == planTask.getDuration() ? 0 : planTask.getDuration().intValue())
                .setGroupId(planTask.getWorkGroupId())
                .setMonitor(groupMonitor)
                .setWorkshopId(plan.getWorkshopId())
                .setLineId(plan.getLineId())
                .setTrainNo(plan.getTrainNo())
                .setOrderStatus(0)
                .setWorkStatus(0)
                .setRemark("由列计划" + plan.getPlanName() + "自动生成的" + inSuffix + "工单")
                .setFdProject(plan.getFdProject())
                .setFdTask(plan.getFdTask())
                .setFdCostType(plan.getFdCostType())
                .setCreateTime(new Date())
                .setCompanyId(plan.getCompanyId());
    }

    private String getGroupMonitor(BuMtrWorkshopGroup group) {
        if (null == group) {
            return null;
        }

        String monitor = null;
        if (StringUtils.isNotBlank(group.getMonitor())) {
            monitor = group.getMonitor();
        } else if (StringUtils.isNotBlank(group.getMonitor2())) {
            monitor = group.getMonitor2();
        }
        return monitor;
    }

    private void extractPlanTaskRelation(List<BuRepairPlanTask> planTaskList,
                                         List<BuRepairTaskWorkstation> planTaskWorkstationList,
                                         List<BuRepairTaskMaterial> planTaskMaterialList,
//                                         List<BuRepairTaskMustReplace> planTaskMustReplaceList,
                                         List<BuRepairTaskTool> planTaskToolList,
                                         List<BuRepairTaskRegu> planTaskReguList,
                                         List<BuRepairReguTechFile> reguTechFileList,
                                         List<BuRepairPlanTaskEqu> planTaskEquipmentList) {
        if (CollectionUtils.isEmpty(planTaskList)) {
            return;
        }

        for (BuRepairPlanTask task : planTaskList) {
            String taskId = task.getId();

            List<BuRepairTaskWorkstation> workstationList = planTaskWorkstationList.stream()
                    .filter(item -> taskId.equals(item.getTaskId()))
                    .collect(Collectors.toList());
            List<BuRepairTaskMaterial> materialList = planTaskMaterialList.stream()
                    .filter(item -> taskId.equals(item.getTaskId()))
                    .collect(Collectors.toList());
//            List<BuRepairTaskMustReplace> mustReplaceList = planTaskMustReplaceList.stream()
//                    .filter(item -> taskId.equals(item.getTaskId()))
//                    .collect(Collectors.toList());
            List<BuRepairTaskTool> toolList = planTaskToolList.stream()
                    .filter(item -> taskId.equals(item.getTaskId()))
                    .collect(Collectors.toList());

            List<String> reguDetailIdList = planTaskReguList.stream()
                    .filter(item -> taskId.equals(item.getTaskId()))
                    .map(BuRepairTaskRegu::getReguDetailId)
                    .collect(Collectors.toList());
            List<BuRepairReguTechFile> techFileList = reguTechFileList.stream()
                    .filter(item -> reguDetailIdList.contains(item.getReguDetailId()))
                    .collect(Collectors.toList());
            List<BuRepairPlanTaskEqu> equipmentList = planTaskEquipmentList.stream()
                    .filter(item -> taskId.equals(item.getPlanTaskId()))
                    .collect(Collectors.toList());

            task.setWorkstations(workstationList)
                    .setMaterials(materialList)
//                    .setMustReplaces(mustReplaceList)
                    .setTools(toolList)
                    .setTechFiles(techFileList)
                    .setEquipments(equipmentList);
        }
    }

    private BuWorkOrderTask createWorkOrderTaskAndRelation(BuRepairPlan plan,
                                                           BuRepairPlanTask planTask,
                                                           String orderId,
                                                           String trainNo,
                                                           List<BuMaterialTools> groupToolsList) {
        String orderTaskId = UUIDGenerator.generate();
        return new BuWorkOrderTask()
                .setId(orderTaskId)
                .setOrderId(orderId)
                .setTaskType(1)
                .setTaskObjId(planTask.getId())
                .setTaskNo(planTask.getTaskNo())
                .setTaskName(planTask.getTaskName())
                .setTaskContent(planTask.getTaskName())
                .setWorkTime(null == planTask.getWorkTime() ? 0 : planTask.getWorkTime().intValue())
                .setRemark(planTask.getRemark())
                .setTaskStart(null)
                .setTaskFinish(null)
                .setTaskTime(0)
                .setReportTime(0)
                .setTaskStatus(0)
                .setOutTask(null == planTask.getOutsource() ? 0 : planTask.getOutsource())
                .setAssetTypeId(planTask.getAssetTypeId())
                //TODO-zhaiyantao 2020/12/28 18:30 生成工单任务时是否需要对应找到具体车辆设备？
                .setStructDetailId(null)
                .setTrainAssetId(null)
                .setImportant(null == planTask.getImportant() ? 0 : planTask.getImportant())
                .setMethod(null == planTask.getMethod() ? 1 : planTask.getMethod())
                .setSafeNotice(planTask.getSafeNotice())
                .setWorkstations(createOrderTaskWorkstations(planTask.getWorkstations(), orderId, orderTaskId, planTask.getWorkTime()))
                .setMaterials(createOrderTaskMaterials(plan, planTask, orderId, orderTaskId))
                .setTools(createOrderTaskTools(planTask.getTools(), orderId, orderTaskId, groupToolsList))
                .setTechFiles(createOrderTaskTechFiles(planTask.getTechFiles(), orderId, orderTaskId))
                .setEquipments(createOrderTaskEquipments(planTask.getEquipments(), orderId, orderTaskId, trainNo));
    }

    private void insertFormInstByPlanTaskAndWorkstationRef(List<BuWorkOrder> orderList, List<BuWorkOrderTask> orderTaskList) {
        if (CollectionUtils.isEmpty(orderTaskList)) {
            return;
        }

        List<String> orderIdList = new ArrayList<>();
        List<String> planIdList = new ArrayList<>();
        Map<String, BuWorkOrder> idOrderMap = new HashMap<>();
        for (BuWorkOrder order : orderList) {
            if (StringUtils.isNotBlank(order.getId()) && !orderIdList.contains(order.getId())) {
                orderIdList.add(order.getId());
            }
            if (StringUtils.isNotBlank(order.getPlanId()) && !planIdList.contains(order.getPlanId())) {
                planIdList.add(order.getPlanId());
            }
            idOrderMap.put(order.getId(), order);
        }

        // 查询列计划任务和表单关联
        List<String> planTaskIdList = orderTaskList.stream()
                .map(BuWorkOrderTask::getTaskObjId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<BuRepairPlanTaskForms> planTaskFormList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(planTaskIdList)) {
            LambdaQueryWrapper<BuRepairPlanTaskForms> planTaskFormWrapper = new LambdaQueryWrapper<BuRepairPlanTaskForms>()
                    .in(BuRepairPlanTaskForms::getTaskId, planTaskIdList);
            planTaskFormList = buRepairPlanTaskFormsMapper.selectList(planTaskFormWrapper);
        }
        // 查询工单任务关联工位及工位关联表单
        List<BuWorkOrderTaskWorkstation> workstationList = buWorkOrderTaskWorkstationMapper.selectListAndRefFormListByOrderIdList(orderIdList);
        // 查询属于列计划的表单
        List<BuRepairPlanForms> planFormList = buRepairPlanFormsMapper.selectPlanFormsListWithChoiceByPlanIdList(planIdList);
        Map<String, BuRepairPlanForms> idPlanFormMap = planFormList.stream()
                .collect(Collectors.toMap(BuRepairPlanForms::getId, Function.identity()));

        // 关联表单实例和任务
        List<BuWorkOrderTaskFormInst> formInstList = new ArrayList<>();
        for (BuWorkOrderTask orderTask : orderTaskList) {
            String taskId = orderTask.getId();
            String orderId = orderTask.getOrderId();
            BuWorkOrder order = idOrderMap.get(orderId);
            String planId = order.getPlanId();
            String planTaskId = orderTask.getTaskObjId();

            List<BuRepairPlanTaskForms> planTaskFormMatchList = planTaskFormList.stream()
                    .filter(planTaskForm -> planTaskId.equals(planTaskForm.getTaskId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(planTaskFormMatchList)) {
                // 如果有列计划任务和表单关联，根据“列计划任务和表单关联”关联实例
                for (BuRepairPlanTaskForms planTaskForm : planTaskFormMatchList) {
                    BuRepairPlanForms planForm = idPlanFormMap.get(planTaskForm.getPlanFormId());
                    List<BuWorkOrderTaskFormInst> orderTaskFormInstList = createOrderTaskFormInst(planForm, planId, orderId, taskId);
                    formInstList.addAll(orderTaskFormInstList);
                }
            } else {
                // 如果没有列计划任务和表单关联，根据“工单任务关联工位及工位关联表单”关联实例
                List<BuWorkOrderTaskWorkstation> orderTaskWorkstationList = workstationList.stream()
                        .filter(workstation -> taskId.equals(workstation.getOrderTaskId()))
                        .collect(Collectors.toList());
                for (BuWorkOrderTaskWorkstation workstation : orderTaskWorkstationList) {
                    List<String> refFormIdList = workstation.getRefFormIdList();
                    List<BuRepairPlanForms> workstationMatchPlanFormList = planFormList.stream()
                            .filter(planForm -> refFormIdList.contains(planForm.getObjId()))
                            .collect(Collectors.toList());
                    for (BuRepairPlanForms planForm : workstationMatchPlanFormList) {
                        List<BuWorkOrderTaskFormInst> orderTaskFormInstList = createOrderTaskFormInst(planForm, planId, orderId, taskId);
                        formInstList.addAll(orderTaskFormInstList);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(formInstList)) {
            List<List<BuWorkOrderTaskFormInst>> batchSubList = DatabaseBatchSubUtil.batchSubList(formInstList);
            for (List<BuWorkOrderTaskFormInst> batchSub : batchSubList) {
                buWorkOrderTaskFormInstMapper.insertList(batchSub);
            }
        }
    }

    private List<BuWorkOrderTaskFormInst> createOrderTaskFormInst(BuRepairPlanForms planForm, String planId, String orderId, String taskId) {
        if (null == planForm || CollectionUtils.isEmpty(planForm.getChoiceList())) {
            return new ArrayList<>();
        }
        List<BuWorkOrderTaskFormInst> formInstList = new ArrayList<>();
        for (BuPlanFormChoiceVO choiceVO : planForm.getChoiceList()) {
            BuWorkOrderTaskFormInst formInst = new BuWorkOrderTaskFormInst()
                    .setId(UUIDGenerator.generate())
                    .setInstType(choiceVO.getFormType())
                    .setFormInstId(choiceVO.getId())
                    .setPlanId(planId)
                    .setWorkOrderId(orderId)
                    .setWorkOrderTaskId(taskId);
            formInstList.add(formInst);
        }
        return formInstList;
    }

    private BuWorkOrderTask createOutOrderTaskAndRelation(BuRepairPlan plan,
                                                          BuRepairPlanTask planTask,
                                                          String orderId,
                                                          String trainNo,
                                                          List<BuMaterialTools> groupToolsList) {
        String orderTaskId = UUIDGenerator.generate();
        return new BuWorkOrderTask()
                .setId(orderTaskId)
                .setOrderId(orderId)
                .setTaskType(6)
                .setTaskObjId(planTask.getId())
                .setTaskNo(planTask.getTaskNo())
                .setTaskName(planTask.getTaskName() + outSuffix)
                .setTaskContent(planTask.getTaskName())
                .setWorkTime(null == planTask.getWorkTime() ? 0 : planTask.getWorkTime().intValue())
                .setRemark(planTask.getRemark())
                .setTaskStart(null)
                .setTaskFinish(null)
                .setTaskTime(0)
                .setReportTime(0)
                .setTaskStatus(0)
                .setOutTask(1)
                .setAssetTypeId(planTask.getAssetTypeId())
                //TODO-zhaiyantao 2020/12/28 18:30 生成工单任务时是否需要对应找到具体车辆设备？
                .setStructDetailId(null)
                .setTrainAssetId(null)
                .setImportant(null == planTask.getImportant() ? 0 : planTask.getImportant())
                .setMethod(null == planTask.getMethod() ? 1 : planTask.getMethod())
                .setSafeNotice(planTask.getSafeNotice())
                .setWorkstations(createOrderTaskWorkstations(planTask.getWorkstations(), orderId, orderTaskId, planTask.getWorkTime()))
                .setMaterials(createOrderTaskMaterials(plan, planTask, orderId, orderTaskId))
                .setTools(createOrderTaskTools(planTask.getTools(), orderId, orderTaskId, groupToolsList))
                .setTechFiles(createOrderTaskTechFiles(planTask.getTechFiles(), orderId, orderTaskId))
                .setEquipments(createOrderTaskEquipments(planTask.getEquipments(), orderId, orderTaskId, trainNo));
    }

    private BuWorkOrderTask createInOrderTaskAndRelation(BuRepairPlan plan,
                                                         BuRepairPlanTask planTask,
                                                         String orderId,
                                                         String trainNo,
                                                         List<BuMaterialTools> groupToolsList) {
        String orderTaskId = UUIDGenerator.generate();
        return new BuWorkOrderTask()
                .setId(orderTaskId)
                .setOrderId(orderId)
                .setTaskType(7)
                .setTaskObjId(planTask.getId())
                .setTaskNo(planTask.getTaskNo())
                .setTaskName(planTask.getTaskName() + inSuffix)
                .setTaskContent(planTask.getTaskName())
                .setWorkTime(null == planTask.getWorkTime() ? 0 : planTask.getWorkTime().intValue())
                .setRemark(planTask.getRemark())
                .setTaskStart(null)
                .setTaskFinish(null)
                .setTaskTime(0)
                .setReportTime(0)
                .setTaskStatus(0)
                .setOutTask(2)
                .setAssetTypeId(planTask.getAssetTypeId())
                //TODO-zhaiyantao 2020/12/28 18:30 生成工单任务时是否需要对应找到具体车辆设备？
                .setStructDetailId(null)
                .setTrainAssetId(null)
                .setImportant(null == planTask.getImportant() ? 0 : planTask.getImportant())
                .setMethod(null == planTask.getMethod() ? 1 : planTask.getMethod())
                .setSafeNotice(planTask.getSafeNotice())
                .setWorkstations(createOrderTaskWorkstations(planTask.getWorkstations(), orderId, orderTaskId, planTask.getWorkTime()))
                .setMaterials(createOrderTaskMaterials(plan, planTask, orderId, orderTaskId))
                .setTools(createOrderTaskTools(planTask.getTools(), orderId, orderTaskId, groupToolsList))
                .setTechFiles(createOrderTaskTechFiles(planTask.getTechFiles(), orderId, orderTaskId))
                .setEquipments(createOrderTaskEquipments(planTask.getEquipments(), orderId, orderTaskId, trainNo));
    }

    private List<BuWorkOrderTaskWorkstation> createOrderTaskWorkstations(List<BuRepairTaskWorkstation> planTaskWorkstationList,
                                                                         String orderId,
                                                                         String orderTaskId,
                                                                         Double workTime) {
        List<BuWorkOrderTaskWorkstation> orderTaskWorkstationList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(planTaskWorkstationList)) {
            for (BuRepairTaskWorkstation planTaskWorkstation : planTaskWorkstationList) {
                BuWorkOrderTaskWorkstation orderTaskWorkstation = new BuWorkOrderTaskWorkstation()
                        .setId(UUIDGenerator.generate())
                        .setOrderId(orderId)
                        .setOrderTaskId(orderTaskId)
                        .setWorkTime(null == workTime ? 0 : workTime.intValue())
                        .setWorkstationId(planTaskWorkstation.getWorkstationId());
                orderTaskWorkstationList.add(orderTaskWorkstation);
            }
        }

        return orderTaskWorkstationList;
    }

    private List<BuWorkOrderMaterial> createOrderTaskMaterials(BuRepairPlan plan,
                                                               BuRepairPlanTask planTask,
                                                               String orderId,
                                                               String orderTaskId) {
        if (planTask.getTaskName().equals("①电气连接线维护")) {
            System.out.println(JSON.toJSONString(planTask));
        }

        List<BuRepairTaskMaterial> planTaskMaterialList = planTask.getMaterials();
//        List<BuRepairTaskMustReplace> planTaskMustReplaceList = planTask.getMustReplaces();

        String defaultWorkstationId = null;
        List<BuRepairTaskWorkstation> planTaskWorkstationList = planTask.getWorkstations();
        if (CollectionUtils.isNotEmpty(planTaskWorkstationList)) {
            defaultWorkstationId = planTaskWorkstationList.get(0).getWorkstationId();
        }

        List<BuWorkOrderMaterial> orderTaskMaterialList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(planTaskMaterialList)) {
            for (BuRepairTaskMaterial planTaskMaterial : planTaskMaterialList) {
                // 列计划物料中用途类型
                Integer useCategory = planTaskMaterial.getUseCategory();
                if (null == useCategory) {
                    // 列计划物料中用途类型为空，使用物资类型中的类型
                    useCategory = planTaskMaterial.getCategory1();
                    if (null == useCategory) {
                        // 物资类型中的类型为空，默认-1未知
                        useCategory = -1;
                    }
                }

                BuWorkOrderMaterial orderTaskMaterial = new BuWorkOrderMaterial()
                        .setId(UUIDGenerator.generate())
                        .setOrderId(orderId)
                        .setOrderTaskId(orderTaskId)
                        .setMaterialTypeId(planTaskMaterial.getMaterialTypeId())
                        .setAmount(planTaskMaterial.getAmount())
                        .setActAmount(0D)
                        .setRemark(planTaskMaterial.getRemark())
                        .setUseCategory(useCategory)
                        .setIsVerify(0)
                        .setOpType(1)
                        .setIsGenOrder(0)
                        .setPlanAmount(planTaskMaterial.getAmount())
                        .setNeedDispatchin(0)
                        .setSystemId(planTask.getSystemId())
                        .setWorkstationId(defaultWorkstationId);
                orderTaskMaterialList.add(orderTaskMaterial);
            }

            // 如果是必换件，使用必换件清单中的系统和工位
            List<BuWorkOrderMaterial> mustMaterialList = orderTaskMaterialList.stream()
                    .filter(orderTaskMaterial -> 1 == orderTaskMaterial.getUseCategory())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(mustMaterialList)) {
                Set<String> materialTypeIdSet = mustMaterialList.stream()
                        .map(BuWorkOrderMaterial::getMaterialTypeId)
                        .collect(Collectors.toSet());
                LambdaQueryWrapper<BuMaterialMustList> mustListWrapper = new LambdaQueryWrapper<BuMaterialMustList>()
                        .in(BuMaterialMustList::getMaterialTypeId, materialTypeIdSet)
                        .eq(BuMaterialMustList::getLineId, plan.getLineId())
                        .eq(BuMaterialMustList::getRepairProgramId, plan.getRepairProgramId());
                List<BuMaterialMustList> mustList = buMaterialMustListMapper.selectList(mustListWrapper);
                mustList.sort(Comparator.comparing(BuMaterialMustList::getStatus).reversed());

                for (BuWorkOrderMaterial mustMaterial : mustMaterialList) {
                    String materialTypeId = mustMaterial.getMaterialTypeId();

                    List<BuMaterialMustList> matchMustList = mustList.stream()
                            .filter(must -> materialTypeId.equals(must.getMaterialTypeId())
                                    && planTask.getWorkGroupId().equals(must.getGroupId()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(matchMustList)) {
                        matchMustList = mustList.stream()
                                .filter(must -> materialTypeId.equals(must.getMaterialTypeId()))
                                .collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(matchMustList)) {
                        BuMaterialMustList must = matchMustList.get(0);
                        mustMaterial.setSystemId(must.getSysId())
                                .setWorkstationId(must.getWorkstationId());
                    }
                }
            }
        }
//        if (CollectionUtils.isNotEmpty(planTaskMustReplaceList)) {
//            for (BuRepairTaskMustReplace planTaskMustReplace : planTaskMustReplaceList) {
//                BuWorkOrderMaterial orderTaskMaterial = new BuWorkOrderMaterial()
//                        .setId(UUIDGenerator.generate())
//                        .setOrderId(orderId)
//                        .setOrderTaskId(orderTaskId)
//                        .setMaterialTypeId(planTaskMustReplace.getMaterialTypeId())
//                        .setAmount(planTaskMustReplace.getAmount())
//                        .setActAmount(0D)
//                        .setRemark(planTaskMustReplace.getRemark())
//                        .setUseCategory(1)
//                        .setIsVerify(0)
//                        .setOpType(1)
//                        .setIsGenOrder(0)
//                        .setPlanAmount(planTaskMustReplace.getAmount())
//                        .setNeedDispatchin(0)
//                        .setSystemId(StringUtils.isBlank(planTaskMustReplace.getSysId()) ? planTask.getSystemId() : planTaskMustReplace.getSysId())
//                        .setWorkstationId(planTaskMustReplace.getWorkstationId());
//                orderTaskMaterialList.add(orderTaskMaterial);
//            }
//        }

        return orderTaskMaterialList;
    }

    private List<BuWorkOrderTool> createOrderTaskTools(List<BuRepairTaskTool> planTaskToolList,
                                                       String orderId,
                                                       String orderTaskId,
                                                       List<BuMaterialTools> groupToolsList) {
        List<BuWorkOrderTool> orderTaskToolList = new ArrayList<>();
        if (groupToolsList == null) {
            groupToolsList = new ArrayList<>();
        }

        if (CollectionUtils.isNotEmpty(planTaskToolList)) {
            for (BuRepairTaskTool planTaskTool : planTaskToolList) {
                // 工器具要找出具体的工器具：查出来该工班的具体的工器具(过滤报废的)，根据需求数量生成多条记录
                List<BuMaterialTools> needToolsList = groupToolsList.stream()
                        .filter(tool -> tool.getMaterialTypeId().equals(planTaskTool.getToolTypeId()))
                        .collect(Collectors.toList());
                int needAmount = planTaskTool.getAmount().intValue();
                int lack = 1;
                if (needToolsList.size() >= needAmount) {
                    needToolsList = needToolsList.subList(0, needAmount);
                    lack = 0;
                }

                if (needToolsList.size() > 0) {
                    for (BuMaterialTools materialTools : needToolsList) {
                        BuWorkOrderTool orderTaskTool = new BuWorkOrderTool()
                                .setId(UUIDGenerator.generate())
                                .setOrderId(orderId)
                                .setOrderTaskId(orderTaskId)
                                .setToolTypeId(planTaskTool.getToolTypeId())
                                .setToolId(materialTools.getId())
                                .setMeasure(6 == materialTools.getToolType() ? 1 : 0)
                                .setAmount(needAmount)
                                .setLack(lack);

                        orderTaskToolList.add(orderTaskTool);
                    }
                } else {
                    BuWorkOrderTool orderTaskTool = new BuWorkOrderTool()
                            .setId(UUIDGenerator.generate())
                            .setOrderId(orderId)
                            .setOrderTaskId(orderTaskId)
                            .setToolTypeId(planTaskTool.getToolTypeId())
                            .setToolId("-1")
                            .setMeasure(0)
                            .setAmount(needAmount)
                            .setLack(lack);

                    orderTaskToolList.add(orderTaskTool);
                }
            }
        }
        return orderTaskToolList;
    }

    private List<BuWorkOrderTechFile> createOrderTaskTechFiles(List<BuRepairReguTechFile> planTaskReguTechFileList,
                                                               String orderId,
                                                               String orderTaskId) {
        List<BuWorkOrderTechFile> orderTaskTechFileList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(planTaskReguTechFileList)) {
            for (BuRepairReguTechFile planTaskReguTechFile : planTaskReguTechFileList) {
                BuWorkOrderTechFile orderTaskTechFile = new BuWorkOrderTechFile()
                        .setId(UUIDGenerator.generate())
                        .setOrderId(orderId)
                        .setOrderTaskId(orderTaskId)
                        .setFileId(planTaskReguTechFile.getFileId());
                orderTaskTechFileList.add(orderTaskTechFile);
            }
        }

        return orderTaskTechFileList;
    }

    private List<BuWorkOrderTaskEqu> createOrderTaskEquipments(List<BuRepairPlanTaskEqu> planTaskEquipmentList,
                                                               String orderId,
                                                               String orderTaskId,
                                                               String trainNo) {
        List<BuWorkOrderTaskEqu> orderTaskEquipmentList = new ArrayList<>();

        // 计划模板和列计划关联的车辆结构在生成工单时清空，因为工单需要关联maximo资产设备，现在无法根据车辆结构找到maximo资产设备
//        if (CollectionUtils.isNotEmpty(planTaskEquipmentList)) {
//            for (BuRepairPlanTaskEqu planTaskEquipment : planTaskEquipmentList) {
//                // 根据车辆结构明细id和车号，获取对应的车辆设备
//                String structDetailId = planTaskEquipment.getStructDetailId();
//                if (StringUtils.isNotBlank(structDetailId)) {
//                    List<BuTrainAsset> assetList = buTrainAssetDispatchMapper.selectAssetListByTrainNoAndStructDetailId(trainNo, structDetailId);
//                    if (CollectionUtils.isEmpty(assetList)) {
//                        BuWorkOrderTaskEqu orderTaskEquipment = new BuWorkOrderTaskEqu()
//                                .setId(UUIDGenerator.generate())
//                                .setOrderId(orderId)
//                                .setOrderTaskId(orderTaskId)
//                                .setAssetTypeId(planTaskEquipment.getAssetTypeId())
//                                .setStructDetailId(planTaskEquipment.getStructDetailId())
//                                .setTrainAssetId(null);
//                        orderTaskEquipmentList.add(orderTaskEquipment);
//                    } else {
//                        for (BuTrainAsset asset : assetList) {
//                            BuWorkOrderTaskEqu orderTaskEquipment = new BuWorkOrderTaskEqu()
//                                    .setId(UUIDGenerator.generate())
//                                    .setOrderId(orderId)
//                                    .setOrderTaskId(orderTaskId)
//                                    .setAssetTypeId(planTaskEquipment.getAssetTypeId())
//                                    .setStructDetailId(planTaskEquipment.getStructDetailId())
//                                    .setTrainAssetId(asset.getId());
//                            orderTaskEquipmentList.add(orderTaskEquipment);
//                        }
//                    }
//                }
//            }
//        }

        return orderTaskEquipmentList;
    }

    private void saveOrderAndTaskAndRelation(List<BuWorkOrder> orderList,
                                             List<BuWorkOrderTask> orderTaskList,
                                             List<BuWorkOrderTaskWorkstation> orderTaskWorkstationList,
                                             List<BuWorkOrderMaterial> orderTaskMaterialList,
                                             List<BuWorkOrderTool> orderTaskToolList,
                                             List<BuWorkOrderTechFile> orderTaskTechFileList,
                                             List<BuWorkOrderTaskEqu> orderTaskEquipmentList) {
        // 批量保存信息
        if (CollectionUtils.isNotEmpty(orderList)) {
            List<List<BuWorkOrder>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderList);
            for (List<BuWorkOrder> batchSub : batchSubList) {
                buWorkOrderMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(orderTaskList)) {
            List<List<BuWorkOrderTask>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskList);
            for (List<BuWorkOrderTask> batchSub : batchSubList) {
                buWorkOrderTaskMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(orderTaskWorkstationList)) {
            List<List<BuWorkOrderTaskWorkstation>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskWorkstationList);
            for (List<BuWorkOrderTaskWorkstation> batchSub : batchSubList) {
                buWorkOrderTaskWorkstationMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(orderTaskMaterialList)) {
            List<List<BuWorkOrderMaterial>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskMaterialList);
            for (List<BuWorkOrderMaterial> batchSub : batchSubList) {
                buWorkOrderMaterialMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(orderTaskToolList)) {
            List<List<BuWorkOrderTool>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskToolList);
            for (List<BuWorkOrderTool> batchSub : batchSubList) {
                buWorkOrderToolMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(orderTaskTechFileList)) {
            List<List<BuWorkOrderTechFile>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskTechFileList);
            for (List<BuWorkOrderTechFile> batchSub : batchSubList) {
                buWorkOrderTechFileMapper.insertList(batchSub);
            }
        }
        if (CollectionUtils.isNotEmpty(orderTaskEquipmentList)) {
            List<List<BuWorkOrderTaskEqu>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderTaskEquipmentList);
            for (List<BuWorkOrderTaskEqu> batchSub : batchSubList) {
                buWorkOrderTaskEquMapper.insertList(batchSub);
            }
        }
    }

    private void startOrderWorkflow(BuWorkOrder order) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("groupId", order.getGroupId());
        variables.put("businessCode", order.getOrderCode());

        StartVO startVO = new StartVO()
                .setBusinessKey(order.getId())
                .setSolutionCode(WfConstant.SOLUTION_CODE_ORDER)
                .setTitle("工单：" + order.getOrderName())
                .setVariables(variables);
        workflowForwardService.startSolution(startVO);
    }

}
