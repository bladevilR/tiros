package org.jeecg.modules.material.cost.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.rpt.service.MaterialRptService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.apply.bean.BuWorkOrderTask;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuWorkOrderForMaterialMapper;
import org.jeecg.modules.material.cost.bean.BuRepairPlan;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterialActs;
import org.jeecg.modules.material.cost.bean.vo.CompareInfoVO;
import org.jeecg.modules.material.cost.bean.vo.CompareQueryVO;
import org.jeecg.modules.material.cost.bean.vo.CompareResultVO;
import org.jeecg.modules.material.cost.mapper.BuWorkOrderMaterialActsMaterialMapper;
import org.jeecg.modules.material.cost.mapper.BuWorkOrderMaterialMaterialMapper;
import org.jeecg.modules.material.cost.service.MaterialCostCompareService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.jeecg.modules.material.stock.service.BuMaterialGroupStockService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料消耗对比 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Slf4j
@Service
public class MaterialCostCompareServiceImpl implements MaterialCostCompareService {

    @Resource
    private BuMaterialApplyDetailMapper buMaterialApplyDetailMapper;
    @Resource
    private BuWorkOrderMaterialMaterialMapper buWorkOrderMaterialMaterialMapper;
    @Resource
    private BuWorkOrderMaterialActsMaterialMapper buWorkOrderMaterialActsMaterialMapper;
    @Resource
    private BuWorkOrderForMaterialMapper buWorkOrderForMaterialMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialGroupStockMapper buMaterialGroupStockMapper;
    @Resource
    private MaterialRptService materialRptService;
    @Resource
    private BuMaterialGroupStockService buMaterialGroupStockService;
    @Resource
    private BuMaterialMustListMapper buMaterialMustListMapper;
    @Resource
    private BuMaterialTypeReplaceService buMaterialTypeReplaceService;


    /**
     * @see MaterialCostCompareService#listMaterialCostCompare(CompareQueryVO, boolean)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public CompareInfoVO listMaterialCostCompare(CompareQueryVO queryVO, boolean forExport) throws Exception {
        if (StringUtils.isBlank(queryVO.getPlanId())) {
            throw new JeecgBootException("请选择列计划");
        }
        // 查询列计划
        BuRepairPlan plan = buWorkOrderMaterialMaterialMapper.selectPlanByPlanId(queryVO.getPlanId());
        if (null == plan) {
            throw new JeecgBootException("列计划不存在");
        }
        List<String> canReplaceMaterialTypeIdList = null;
        if (StringUtils.isNotBlank(queryVO.getMaterialSearchText())) {
            canReplaceMaterialTypeIdList = buMaterialTypeReplaceService.listCanReplaceMaterialTypeIdByMaterialTypeText(queryVO.getMaterialSearchText());
        }

        // 查询必换件清单
        List<BuMaterialMustList> allMustList = buMaterialMustListMapper.selectListByCompareQueryVO(queryVO);
        // 查询条件下的消耗：工单物料
        List<BuWorkOrderMaterial> allOrderMaterialList = buWorkOrderMaterialMaterialMapper.selectListForCostCompare(queryVO);
        // 查询工单物料实际消耗
        setOrderMaterialActList(allOrderMaterialList);
        // 查询条件下的发放：领用明细
        List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailMapper.selectListForCostCompare(queryVO);

        // 收集物资类型
        Set<String> allMaterialTypeIdSet = new HashSet<>();
        allMustList.forEach(item -> allMaterialTypeIdSet.add(item.getMaterialTypeId()));
        allOrderMaterialList.forEach(item -> allMaterialTypeIdSet.add(item.getMaterialTypeId()));
        allApplyDetailList.forEach(item -> allMaterialTypeIdSet.add(item.getMaterialTypeId()));
        List<String> allMaterialTypeIdList = new ArrayList<>(allMaterialTypeIdSet);
        allMaterialTypeIdList.sort(String::compareTo);
        // 查询物资类型
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(allMaterialTypeIdList);
        for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdBatchSub);
            materialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));
        }
        // 查询可替换物资
        Map<String, List<String>> materialTypeIdReplaceIdListMap = buMaterialTypeReplaceService.mapCanReplaceMaterialTypeIdListByMaterialTypeIdList(allMaterialTypeIdList);
        // 查询班组
        List<Map<String, Object>> groupMapList = buWorkOrderMaterialMaterialMapper.selectGroupList(queryVO.getGroupId());
        Map<String, String> groupIdNameMap = transToIdNameMap(groupMapList);

        // 列计划相关数据：计划id名称、线路、车辆
        String planId = queryVO.getPlanId();
        String planName = plan.getPlanName();
        String lineId = plan.getLineId();
        String lineName = plan.getLineName();
        String trainNo = plan.getTrainNo();

        // 处理数据得到结果
        List<CompareResultVO> compareResultVOList = new ArrayList<>();

        // 在必换件清单内的
        // 同系统、同工位、互为可替换的必换件清单合并
        mergeCanReplaceMustList(allMustList, materialTypeIdReplaceIdListMap);
        List<BuWorkOrderMaterial> mustOrderMaterialList = new ArrayList<>();
        List<BuMaterialApplyDetail> mustApplyDetailList = new ArrayList<>();
        for (BuMaterialMustList must : allMustList) {
            String sysId = must.getSysId();
            String workstationId = must.getWorkstationId();
            String groupId = must.getGroupId();
            String groupName = groupIdNameMap.get(groupId);

            String materialTypeId = must.getMaterialTypeId();
            BuMaterialType material = idMaterialTypeMap.get(materialTypeId);

            BigDecimal needAmount = null == must.getNeedAmount() ? BigDecimal.ZERO : BigDecimal.valueOf(must.getNeedAmount());

            Set<String> materialTypeIdSet = new HashSet<>(Collections.singletonList(materialTypeId));
            List<String> replaceIdList = materialTypeIdReplaceIdListMap.get(materialTypeId);
            if (CollectionUtils.isNotEmpty(replaceIdList)) {
                materialTypeIdSet.addAll(replaceIdList);
            }

            // 过滤出符合条件的工单物料和领用明细
            List<BuWorkOrderMaterial> orderMaterialList = allOrderMaterialList.stream()
                    .filter(orderMaterial -> groupId.equals(orderMaterial.getGroupId())
                            && materialTypeIdSet.contains(orderMaterial.getMaterialTypeId())
                            && sysId.equals(orderMaterial.getSystemId())
                            && workstationId.equals(orderMaterial.getWorkstationId()))
                    .collect(Collectors.toList());
            List<BuMaterialApplyDetail> applyDetailList = allApplyDetailList.stream()
                    .filter(applyDetail -> groupId.equals(applyDetail.getGroupId())
                            && materialTypeIdSet.contains(applyDetail.getMaterialTypeId()))
                    .collect(Collectors.toList());
            // 实际消耗
            BigDecimal consumeAmount = orderMaterialList.stream()
                    .filter(item -> null != item.getActAmount())
                    .map(item -> BigDecimal.valueOf(item.getActAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 领用数量
            BigDecimal receiveAmount = applyDetailList.stream()
                    .map(BuMaterialApplyDetail::getReceive)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 工单物料ids
            String orderMaterialIds = orderMaterialList.stream()
                    .map(BuWorkOrderMaterial::getId)
                    .collect(Collectors.joining(","));
            // 领用明细ids
            String applyDetailIds = applyDetailList.stream()
                    .map(BuMaterialApplyDetail::getId)
                    .collect(Collectors.joining(","));
            // 是否存在差异
            int amountIsDiff = 1;
            boolean same1 = needAmount.compareTo(receiveAmount) == 0;
            boolean same2 = needAmount.compareTo(consumeAmount) == 0;
            boolean same3 = receiveAmount.compareTo(consumeAmount) == 0;
            boolean diffSystemWorkstation = orderMaterialList.stream()
                    .anyMatch(item -> (StringUtils.isBlank(item.getSystemId()) || !must.getSysId().equals(item.getSystemId()))
                            || (StringUtils.isBlank(item.getWorkstationId()) || !must.getWorkstationId().equals(item.getWorkstationId())));
            if (same1 && same2 && same3 && !diffSystemWorkstation) {
                amountIsDiff = 0;
            }

            CompareResultVO mustResultVO = new CompareResultVO()
                    .setLineId(lineId)
                    .setLineName(lineName)
                    .setTrainNo(trainNo)
                    .setPlanId(planId)
                    .setPlanName(planName)
                    .setGroupId(groupId)
                    .setGroupName(groupName)
                    .setMaterialId(material.getId())
                    .setMaterialCode(material.getCode())
                    .setMaterialName(material.getName())
                    .setMaterialSpec(material.getSpec())
                    .setIsMustList(1)
                    .setMustListAmount(needAmount)
                    .setRequireSystemId(must.getSysId())
                    .setRequireSystemName(must.getSysName())
                    .setRequireWorkstationId(must.getWorkstationId())
                    .setRequireWorkstationNo(must.getWorkstationNo())
                    .setRequireWorkstationName(must.getWorkstationName())
                    .setRequireSystemWorkstationInfo(must.getSysName() + "-" + must.getWorkstationName())
                    .setCanReplace(String.join(",", replaceIdList))
                    .setUseCategory("1")
                    .setConsumeSystemWorkstationInfo(generateConsumeSystemWorkstationInfo(orderMaterialList))
                    .setNeedAmount(needAmount)
                    .setReceiveAmount(receiveAmount)
                    .setConsumeAmount(consumeAmount)
                    .setAmountIsDiff(amountIsDiff)
                    .setOrderMaterialList(orderMaterialList)
                    .setOrderMaterialIds(orderMaterialIds)
                    .setApplyDetailList(applyDetailList)
                    .setApplyDetailIds(applyDetailIds);
            compareResultVOList.add(mustResultVO);

            mustOrderMaterialList.addAll(orderMaterialList);
            mustApplyDetailList.addAll(applyDetailList);
        }
        // 去除已经归类到必换件清单的数据
        allOrderMaterialList.removeAll(mustOrderMaterialList);
        allApplyDetailList.removeAll(mustApplyDetailList);

        // 不在必换件清单内的
        // 非必换件清单不需要统计可替换，因为是额定数量=工单物料数量，核实工单的时候如果该物资班组库存不够就不能核实，需要修正为有库存的
        if (CollectionUtils.isNotEmpty(allOrderMaterialList) || CollectionUtils.isNotEmpty(allApplyDetailList)) {
            // 收集物资类型
            Set<String> notMustMaterialTypeIdSet = new HashSet<>();
            allOrderMaterialList.forEach(item -> notMustMaterialTypeIdSet.add(item.getMaterialTypeId()));
            allApplyDetailList.forEach(item -> notMustMaterialTypeIdSet.add(item.getMaterialTypeId()));
            List<String> notMustMaterialTypeIdList = new ArrayList<>(notMustMaterialTypeIdSet);
            notMustMaterialTypeIdList.sort(String::compareTo);

            // 班组
            for (Map.Entry<String, String> groupIdNameEntry : groupIdNameMap.entrySet()) {
                String groupId = groupIdNameEntry.getKey();
                String groupName = groupIdNameEntry.getValue();
                // 物资
                for (String materialTypeId : notMustMaterialTypeIdList) {
                    BuMaterialType material = idMaterialTypeMap.get(materialTypeId);

                    // 过滤出符合条件的工单物料和领用明细
                    List<BuWorkOrderMaterial> orderMaterialList = allOrderMaterialList.stream()
                            .filter(orderMaterial -> lineId.equals(orderMaterial.getLineId())
                                    && trainNo.equals(orderMaterial.getTrainNo())
                                    && groupId.equals(orderMaterial.getGroupId())
                                    && materialTypeId.equals(orderMaterial.getMaterialTypeId()))
                            .collect(Collectors.toList());
                    List<BuMaterialApplyDetail> applyDetailList = allApplyDetailList.stream()
                            .filter(applyDetail -> lineId.equals(applyDetail.getLineId())
                                    && trainNo.equals(applyDetail.getTrainNo())
                                    && groupId.equals(applyDetail.getGroupId())
                                    && materialTypeId.equals(applyDetail.getMaterialTypeId()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(orderMaterialList) && CollectionUtils.isEmpty(applyDetailList)) {
                        // 工单物料和领用明细都为空，跳过
                        continue;
                    }

                    // 额定数量
                    BigDecimal needAmount = BigDecimal.ZERO;
                    // 实际消耗
                    BigDecimal consumeAmount = BigDecimal.ZERO;
                    // 使用类型
                    List<Integer> useCategoryList = new ArrayList<>();

                    // 工单物料id列表
                    List<String> orderMaterialIdList = new ArrayList<>();

                    for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                        needAmount = needAmount.add(BigDecimal.valueOf(orderMaterial.getAmount()));
                        consumeAmount = consumeAmount.add(BigDecimal.valueOf(orderMaterial.getActAmount()));
                        if (!useCategoryList.contains(orderMaterial.getUseCategory())) {
                            useCategoryList.add(orderMaterial.getUseCategory());
                        }

                        orderMaterialIdList.add(orderMaterial.getId());
                    }
                    if (CollectionUtils.isEmpty(useCategoryList)) {
                        // 如果工单物料没有，使用领用明细中的类型
                        // 这种情况：发料了，但是没有消耗
                        useCategoryList = applyDetailList.stream()
                                .map(BuMaterialApplyDetail::getUseCategory)
                                .distinct()
                                .sorted(Integer::compareTo)
                                .collect(Collectors.toList());
                    }
                    String useCategory = useCategoryList.stream()
                            .sorted(Integer::compareTo)
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    String orderMaterialIds = String.join(",", orderMaterialIdList);
                    // 领用明细ids
                    String applyDetailIds = applyDetailList.stream()
                            .map(BuMaterialApplyDetail::getId)
                            .collect(Collectors.joining(","));

                    // 领用数量
                    BigDecimal receiveAmount = applyDetailList.stream()
                            .map(BuMaterialApplyDetail::getReceive)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    // 是否存在差异
                    int amountIsDiff = 1;
                    boolean same1 = needAmount.compareTo(receiveAmount) == 0;
                    boolean same2 = needAmount.compareTo(consumeAmount) == 0;
                    boolean same3 = receiveAmount.compareTo(consumeAmount) == 0;
                    if (same1 && same2 && same3) {
                        amountIsDiff = 0;
                    }

                    CompareResultVO compareResultVO = new CompareResultVO()
                            .setLineId(lineId)
                            .setLineName(lineName)
                            .setTrainNo(trainNo)
                            .setPlanId(planId)
                            .setPlanName(planName)
                            .setGroupId(groupId)
                            .setGroupName(groupName)
                            .setMaterialId(material.getId())
                            .setMaterialCode(material.getCode())
                            .setMaterialName(material.getName())
                            .setMaterialSpec(material.getSpec())
                            .setIsMustList(0)
                            .setMustListAmount(null)
                            .setRequireSystemId(null)
                            .setRequireSystemName(null)
                            .setRequireWorkstationId(null)
                            .setRequireWorkstationNo(null)
                            .setRequireWorkstationName(null)
                            .setRequireSystemWorkstationInfo(null)
                            .setCanReplace("非必换件清单不需要统计可替换")
                            .setUseCategory(useCategory)
                            .setConsumeSystemWorkstationInfo(generateConsumeSystemWorkstationInfo(orderMaterialList))
                            .setNeedAmount(needAmount)
                            .setReceiveAmount(receiveAmount)
                            .setConsumeAmount(consumeAmount)
                            .setAmountIsDiff(amountIsDiff)
                            .setOrderMaterialList(orderMaterialList)
                            .setOrderMaterialIds(orderMaterialIds)
                            .setApplyDetailList(applyDetailList)
                            .setApplyDetailIds(applyDetailIds);

                    // 设置excel导出属性
                    if (forExport) {
                        // 发料工单信息
                        StringBuilder applyOrderInfoBuilder = new StringBuilder();
                        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                            String receiveStr = applyDetail.getReceive().stripTrailingZeros().toPlainString();
                            applyOrderInfoBuilder.append("、").append(applyDetail.getOrderCode()).append("(").append(receiveStr).append(")");
                        }
                        String applyOrderInfo = null;
                        if (applyOrderInfoBuilder.length() > 0) {
                            applyOrderInfo = applyOrderInfoBuilder.deleteCharAt(applyOrderInfoBuilder.length() - 1).toString();
                        }
                        compareResultVO.setApplyOrderInfo(applyOrderInfo);

                        // 消耗工单信息
                        StringBuilder consumeOrderInfoBuilder = new StringBuilder();
                        // 修程类型
                        StringBuilder repairProNamesBuilder = new StringBuilder();
                        // 系统
                        StringBuilder systemNamesBuilder = new StringBuilder();
                        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                            String actAmountStr = BigDecimal.valueOf(orderMaterial.getActAmount()).stripTrailingZeros().toPlainString();
                            consumeOrderInfoBuilder.append("、").append(orderMaterial.getOrderCode()).append("(").append(actAmountStr).append(")");
                            if (StringUtils.isNotBlank(orderMaterial.getRepairProName()) && !repairProNamesBuilder.toString().contains(orderMaterial.getRepairProName())) {
                                repairProNamesBuilder.append("、").append(orderMaterial.getRepairProName());
                            }
                            if (StringUtils.isNotBlank(orderMaterial.getSystemName()) && !systemNamesBuilder.toString().contains(orderMaterial.getSystemName())) {
                                systemNamesBuilder.append("、").append(orderMaterial.getSystemName());
                            }
                        }
                        String consumeOrderInfo = null;
                        if (consumeOrderInfoBuilder.length() > 0) {
                            consumeOrderInfo = consumeOrderInfoBuilder.deleteCharAt(consumeOrderInfoBuilder.length() - 1).toString();
                        }
                        String repairProNames = null;
                        if (repairProNamesBuilder.length() > 0) {
                            repairProNames = repairProNamesBuilder.deleteCharAt(repairProNamesBuilder.length() - 1).toString();
                        }
                        String systemNames = null;
                        if (systemNamesBuilder.length() > 0) {
                            systemNames = systemNamesBuilder.deleteCharAt(systemNamesBuilder.length() - 1).toString();
                        }
                        compareResultVO.setConsumeOrderInfo(consumeOrderInfo);
                        compareResultVO.setRepairProNames(repairProNames);
                        compareResultVO.setSystemNames(systemNames);

                        // 使用类型
                        compareResultVO.setUseCategoryNames(getUseCategoryNames(compareResultVO.getUseCategory()));
                        // 物资属性
                        compareResultVO.setMaterialTypeCategory3(material.getCategory3());
                    }
                    compareResultVOList.add(compareResultVO);
                }
            }
        }

        // 过滤结果
        if (null != queryVO.getAmountIsDiff()) {
            if (0 == queryVO.getAmountIsDiff()) {
                compareResultVOList.removeIf(item -> 0 != item.getAmountIsDiff());
            } else if (1 == queryVO.getAmountIsDiff()) {
                compareResultVOList.removeIf(item -> 1 != item.getAmountIsDiff());
            }
        }
        if (null != queryVO.getIsMustList()) {
            if (0 == queryVO.getIsMustList()) {
                compareResultVOList.removeIf(item -> 0 != item.getIsMustList());
            } else if (1 == queryVO.getIsMustList()) {
                compareResultVOList.removeIf(item -> 1 != item.getIsMustList());
            }
        }
        if (CollectionUtils.isNotEmpty(canReplaceMaterialTypeIdList)) {
            List<String> finalCanReplaceMaterialTypeIdList = canReplaceMaterialTypeIdList;
            compareResultVOList.removeIf(item -> !finalCanReplaceMaterialTypeIdList.contains(item.getMaterialId()));
        }

        // 合并结果：工班+物料+是否必换件
        Map<String, List<CompareResultVO>> groupedCompareResultVOListMap = compareResultVOList.stream()
                .filter(item -> 1 == item.getAmountIsDiff())
                .collect(Collectors.groupingBy(item -> item.getGroupId() + item.getMaterialId() + item.getIsMustList()));
        for (Map.Entry<String, List<CompareResultVO>> groupedCompareResultVOListEntry : groupedCompareResultVOListMap.entrySet()) {
            List<CompareResultVO> groupedList = groupedCompareResultVOListEntry.getValue();
            Integer isMustList = groupedList.get(0).getIsMustList();

            if (groupedList.get(0).getMaterialId().equals("010031080002")) {
                System.out.println(JSON.toJSONString(groupedList));
            }

            // 按总和计算
            BigDecimal needAmountTotal = BigDecimal.ZERO;
            BigDecimal receiveAmountTotal = BigDecimal.ZERO;
            BigDecimal consumeAmountTotal = BigDecimal.ZERO;
            Set<String> applyDetailIdSet = new HashSet<>();
            Set<String> orderMaterialIdSet = new HashSet<>();
            for (CompareResultVO compareResultVO : groupedList) {
                List<BuWorkOrderMaterial> orderMaterialList = compareResultVO.getOrderMaterialList();
                List<BuMaterialApplyDetail> applyDetailList = compareResultVO.getApplyDetailList();

                // 额定数量
                BigDecimal needAmount = BigDecimal.ZERO;
                if (1 == isMustList) {
                    // 必换件清单的
                    needAmount = compareResultVO.getNeedAmount();
                } else {
                    // 非必换件清单的
                    for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                        if (!orderMaterialIdSet.contains(orderMaterial.getId())) {
                            needAmount = needAmount.add(BigDecimal.valueOf(orderMaterial.getAmount()));
                        }
                    }
                }
                // 领用数量
                BigDecimal receiveAmount = applyDetailList.stream()
                        .filter(item -> !applyDetailIdSet.contains(item.getId()))
                        .map(BuMaterialApplyDetail::getReceive)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                // 实际消耗
                BigDecimal consumeAmount = orderMaterialList.stream()
                        .filter(item -> null != item.getActAmount() && !orderMaterialIdSet.contains(item.getId()))
                        .map(item -> BigDecimal.valueOf(item.getActAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                needAmountTotal = needAmountTotal.add(needAmount);
                receiveAmountTotal = receiveAmountTotal.add(receiveAmount);
                consumeAmountTotal = consumeAmountTotal.add(consumeAmount);

                applyDetailList.forEach(item -> applyDetailIdSet.add(item.getId()));
                orderMaterialList.forEach(item -> orderMaterialIdSet.add(item.getId()));
            }
            // 是否存在差异
            int amountIsDiff = 1;
            boolean same1 = needAmountTotal.compareTo(receiveAmountTotal) == 0;
            boolean same2 = needAmountTotal.compareTo(consumeAmountTotal) == 0;
            boolean same3 = receiveAmountTotal.compareTo(consumeAmountTotal) == 0;
            if (same1 && same2 && same3) {
                amountIsDiff = 0;
            }
            int finalAmountIsDiff = amountIsDiff;
            groupedList.forEach(item -> item.setAmountIsDiff(finalAmountIsDiff));
        }

        // 结果排序
        compareResultVOList.sort(Comparator.comparing(CompareResultVO::getGroupId)
                .thenComparing(CompareResultVO::getMaterialId)
                .thenComparing(CompareResultVO::getIsMustList, Comparator.reverseOrder()));

        // 结果分析
        List<BuWorkOrderMaterial> notCloseConsumeOrderMaterialList = buWorkOrderMaterialMaterialMapper.selectListOfNotCloseConsumeOrderForCostCompare(queryVO);
        if (CollectionUtils.isNotEmpty(canReplaceMaterialTypeIdList)) {
            List<String> finalCanReplaceMaterialTypeIdList = canReplaceMaterialTypeIdList;
            notCloseConsumeOrderMaterialList.removeIf(item -> !finalCanReplaceMaterialTypeIdList.contains(item.getMaterialTypeId()));
        }
        List<String> notCloseConsumeOrderCodeList = notCloseConsumeOrderMaterialList.stream()
                .map(BuWorkOrderMaterial::getOrderCode)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        String consumeOrderInfo = "该条件下的物料消耗工单，都已关闭，可以核实物料。";
        if (CollectionUtils.isNotEmpty(notCloseConsumeOrderCodeList)) {
            consumeOrderInfo = String.format("该条件下的物料消耗工单，有%s个未关闭（%s），请工单关闭后再核实物料。",
                    notCloseConsumeOrderCodeList.size(),
                    String.join(",", notCloseConsumeOrderCodeList));
        }
        int totalCount = compareResultVOList.size();
        long diffCount = compareResultVOList.stream()
                .filter(item -> 1 == item.getAmountIsDiff())
                .count();
        String compareInfo = String.format("该条件下的数据共%s条，%s条有差异。", totalCount, diffCount);

        return new CompareInfoVO()
                .setConsumeOrderInfo(consumeOrderInfo)
                .setCompareInfo(compareInfo)
                .setCompareResultList(compareResultVOList);
    }

    /**
     * @see MaterialCostCompareService#listOrderMaterialByIds(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderMaterial> listOrderMaterialByIds(String orderMaterialIds) throws Exception {
        if (StringUtils.isBlank(orderMaterialIds)) {
            return new ArrayList<>();
        }

        List<String> orderMaterialIdList = Arrays.asList(orderMaterialIds.split(","));
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMaterialMapper.selectListByIdList(orderMaterialIdList);
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return new ArrayList<>();
        }

        // 查询工单物料实际消耗
        setOrderMaterialActList(orderMaterialList);

        return orderMaterialList;
    }

    /**
     * @see MaterialCostCompareService#verifyOrderMaterialBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean verifyOrderMaterialBatch(String orderMaterialIds) throws Exception {
        List<String> orderMaterialIdList = Arrays.asList(orderMaterialIds.split(","));

        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderMaterialIdList);
        for (List<String> batchSub : batchSubList) {
            LambdaQueryWrapper<BuWorkOrderMaterial> orderMaterialWrapper = new LambdaQueryWrapper<BuWorkOrderMaterial>()
                    .in(BuWorkOrderMaterial::getId, batchSub);
            List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMaterialMapper.selectList(orderMaterialWrapper);

            // 查询工单
            List<String> orderIdList = orderMaterialList.stream()
                    .map(BuWorkOrderMaterial::getOrderId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            List<BuWorkOrder> orderList = new ArrayList<>();
            List<List<String>> orderIdBatchSubList = DatabaseBatchSubUtil.batchSubList(orderIdList);
            for (List<String> orderIdBatchSub : orderIdBatchSubList) {
                List<BuWorkOrder> subOrderList = buWorkOrderForMaterialMapper.selectBatchIds(orderIdBatchSub);
                orderList.addAll(subOrderList);
            }
            checkOrderCloseReturnTrainNoList(orderList);

            // 更新工单物料核算状态
            BuWorkOrderMaterial orderMaterial = new BuWorkOrderMaterial()
                    .setIsVerify(1);
            buWorkOrderMaterialMaterialMapper.update(orderMaterial, orderMaterialWrapper);
        }

        return true;
    }

    /**
     * @see MaterialCostCompareService#verifyOrderMaterialList(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean verifyOrderMaterialList(List<BuWorkOrderMaterial> orderMaterialList) throws Exception {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return true;
        }

        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        // 查询工单
        List<String> orderIdList = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getOrderId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<BuWorkOrder> orderList = new ArrayList<>();
        List<List<String>> orderIdBatchSubList = DatabaseBatchSubUtil.batchSubList(orderIdList);
        for (List<String> orderIdBatchSub : orderIdBatchSubList) {
            List<BuWorkOrder> subOrderList = buWorkOrderForMaterialMapper.selectBatchIds(orderIdBatchSub);
            orderList.addAll(subOrderList);
        }
        // 检查工单是否已关闭
        List<String> trainNoList = checkOrderCloseReturnTrainNoList(orderList);
        Map<String, BuWorkOrder> idOrderMap = orderList.stream()
                .collect(Collectors.toMap(BuWorkOrder::getId, Function.identity()));

        // 查询旧的实际消耗
        Map<String, Map<String, BuWorkOrderMaterialActs>> orderMaterialIdOldActMapMap = new HashMap<>();
        List<String> orderMaterialIdList = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<List<String>> orderMaterialIdBatchSubList = DatabaseBatchSubUtil.batchSubList(orderMaterialIdList);
        for (List<String> orderMaterialIdBatchSub : orderMaterialIdBatchSubList) {
            LambdaQueryWrapper<BuWorkOrderMaterialActs> actWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                    .in(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterialIdBatchSub);
            List<BuWorkOrderMaterialActs> subOldActList = buWorkOrderMaterialActsMaterialMapper.selectList(actWrapper);
            for (BuWorkOrderMaterialActs act : subOldActList) {
                String orderMaterialId = act.getOrderMaterialId();
                Map<String, BuWorkOrderMaterialActs> oldActMap = orderMaterialIdOldActMapMap.getOrDefault(orderMaterialId, new HashMap<>());
                oldActMap.put(act.getId(), act);
                orderMaterialIdOldActMapMap.put(orderMaterialId, oldActMap);
            }
        }
        // 查询物资类型
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        List<String> materialTypeIdList = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getMaterialTypeId)
                .distinct()
                .collect(Collectors.toList());
        List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
        for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
            List<BuMaterialType> subMaterialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdBatchSub);
            subMaterialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));
        }

        // 按班组分组
        Map<String, List<BuWorkOrderMaterial>> groupIdListMap = orderMaterialList.stream()
                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getGroupId));
        for (Map.Entry<String, List<BuWorkOrderMaterial>> groupIdListEntry : groupIdListMap.entrySet()) {
            String groupId = groupIdListEntry.getKey();
            List<BuWorkOrderMaterial> list = groupIdListEntry.getValue();
            String groupName = list.get(0).getGroupName();

            // 班组库存量减去“已核实”到“未关闭”的工单中的物料数量
            // 暂停状态的工单不需要减去：暂停时有可能原始状态为未核实不需要减去；重新激活时会回归原始状态根据原始状态计算
            List<BuWorkOrderMaterial> needUsedOrderMaterialList = buWorkOrderMaterialMaterialMapper.selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(groupId, TirosConstant.GROUP_STOCK_NEED_COUNT_USED_ORDER_STATUS);

            for (BuWorkOrderMaterial orderMaterial : list) {
                String orderMaterialId = orderMaterial.getId();
                boolean isAdd = false;
                if (StringUtils.isBlank(orderMaterialId)) {
                    isAdd = true;
                    orderMaterialId = UUIDGenerator.generate();
                    orderMaterial.setId(orderMaterialId);
                }

                String materialTypeId = orderMaterial.getMaterialTypeId();
                Integer useCategory = orderMaterial.getUseCategory();
                String trainNo = orderMaterial.getTrainNo();
                BuWorkOrder order = idOrderMap.get(orderMaterial.getOrderId());

                List<BuWorkOrderMaterialActs> newActList = orderMaterial.getActList();
                if (CollectionUtils.isEmpty(newActList)) {
                    newActList = new ArrayList<>();
                }

                Map<String, BuWorkOrderMaterialActs> idOldActMap = orderMaterialIdOldActMapMap.getOrDefault(orderMaterialId, new HashMap<>());

                // 查询班组库存
                Map<String, BuMaterialGroupStock> idGroupStockMap = new HashMap<>();
                Set<String> newActGroupStockIdSet = newActList.stream()
                        .map(BuWorkOrderMaterialActs::getGroupStockId)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toSet());
                for (Map.Entry<String, BuWorkOrderMaterialActs> idOldActEntry : idOldActMap.entrySet()) {
                    String oldActGroupStockId = idOldActEntry.getValue().getGroupStockId();
                    if (StringUtils.isNotBlank(oldActGroupStockId)) {
                        newActGroupStockIdSet.add(oldActGroupStockId);
                    }
                }
                if (CollectionUtils.isNotEmpty(newActGroupStockIdSet)) {
                    List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectBatchIds(newActGroupStockIdSet);
                    for (BuMaterialGroupStock groupStock : groupStockList) {
                        groupStock.setOldAmount(groupStock.getAmount())
                                .setRelativeOrderMaterialActIdSet(new HashSet<>());
                    }
                    // 获得班组库存可用量
                    buMaterialGroupStockService.deleteGroupStockUsedAmount(groupStockList, groupId, needUsedOrderMaterialList, orderMaterial.getId(), null);
                    groupStockList.forEach(item -> idGroupStockMap.put(item.getId(), item));
                }

                // 如果有旧的实际消耗被删除了，新增的实际消耗列表中不存在，此时临时添加一条为0的数据做处理，不然会丢失这条删除的数据
                List<String> newActIdList = newActList.stream()
                        .map(BuWorkOrderMaterialActs::getId)
                        .collect(Collectors.toList());
                for (Map.Entry<String, BuWorkOrderMaterialActs> idOldActEntry : idOldActMap.entrySet()) {
                    BuWorkOrderMaterialActs oldAct = idOldActEntry.getValue();
                    if (!newActIdList.contains(oldAct.getId())) {
                        BuWorkOrderMaterialActs copyOldAct = new BuWorkOrderMaterialActs();
                        BeanUtils.copyProperties(oldAct, copyOldAct);
                        copyOldAct.setActAmount(0D);
                        newActList.add(copyOldAct);
                    }
                }
                // 对比更新实际消耗，并根据数量变化处理班组库存
                for (BuWorkOrderMaterialActs newAct : newActList) {
                    String actId = newAct.getId();
                    String tradeNo = newAct.getTradeNo();
                    double newActAmount = newAct.getActAmount();
//                        // 此处必须能找到旧的实际消耗
//                        BuWorkOrderMaterialActs oldAct = idOldActMap.get(actId);
//                        if (null == oldAct) {
//                            continue;
//                        }
//                        double oldActAmount = oldAct.getActAmount();
                    // 此处不一定能找到旧的实际消耗：因为可以新增实际消耗
                    BuWorkOrderMaterialActs oldAct = idOldActMap.get(actId);
                    double oldActAmount = 0;
                    if (null != oldAct) {
                        oldActAmount = oldAct.getActAmount();
                    }

                    // 对应的班组库存
                    String groupStockId = newAct.getGroupStockId();
                    BuMaterialGroupStock groupStock = idGroupStockMap.get(groupStockId);

                    double diff = newActAmount - oldActAmount;
                    if (diff > 0) {
                        // 实际消耗增加了，需要减班组库存
//                        double usableAmountSum = groupStockList.stream()
//                                .map(BuMaterialGroupStock::getUsableAmount)
//                                .filter(Objects::nonNull)
//                                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                                .doubleValue();
                        double groupUsableAmount = null == groupStock ? 0D : groupStock.getUsableAmount().doubleValue();
                        if (groupUsableAmount <= 0D || groupUsableAmount < diff) {
                            throw new JeecgBootException(groupName + "物料" + materialTypeId + "(" + tradeNo + ")" + "当前可用数量为" + groupUsableAmount + "，不足增加的" + diff + "（原" + oldActAmount + " -> 新" + newActAmount + "）");
                        } else {
//                            double totalAmount = diff;
//                            for (BuMaterialGroupStock groupStock : groupStockList) {
//                                double groupUsableAmount = groupStock.getUsableAmount().doubleValue();
//                                double groupAmount = groupStock.getAmount().doubleValue();
//                                if (groupUsableAmount > 0D) {
//                                    double amount = Math.min(groupUsableAmount, diff);
//                                    groupStock.setUsableAmount(BigDecimal.valueOf(groupUsableAmount - amount))
//                                            .setAmount(BigDecimal.valueOf(groupAmount - amount));
//                                    groupStock.getRelativeOrderMaterialActIdSet().add(actId);
//                                    if (null == groupStock.getNeedAdd() || !groupStock.getNeedAdd()) {
//                                        groupStock.setNeedUpdate(true);
//                                    }
//
//                                    totalAmount = totalAmount - amount;
//                                    if (totalAmount <= 0D) {
//                                        break;
//                                    }
//                                }
//                            }
                            double groupAmount = groupStock.getAmount().doubleValue();
                            groupStock.setUsableAmount(BigDecimal.valueOf(groupUsableAmount - diff))
                                    .setAmount(BigDecimal.valueOf(groupAmount - diff));
                            groupStock.getRelativeOrderMaterialActIdSet().add(actId);
                            if (null == groupStock.getNeedAdd() || !groupStock.getNeedAdd()) {
                                groupStock.setNeedUpdate(true);
                            }
                        }
                    } else if (diff < 0) {
                        diff = Math.abs(diff);
                        // 实际消耗减少了，需要加班组库存
                        if (null != groupStock) {
                            groupStock.setAmount(groupStock.getAmount().add(BigDecimal.valueOf(diff)))
                                    .setUsableAmount(groupStock.getUsableAmount().add(BigDecimal.valueOf(diff)));
                            groupStock.getRelativeOrderMaterialActIdSet().add(actId);
                            if (null == groupStock.getNeedAdd() || !groupStock.getNeedAdd()) {
                                groupStock.setNeedUpdate(true);
                            }
                        } else {
                            BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                            BigDecimal price = null == newAct.getPrice() ? materialType.getPrice() : newAct.getPrice();

                            groupStock = new BuMaterialGroupStock()
                                    .setId(groupStockId)
                                    .setMaterialTypeId(materialTypeId)
                                    .setAmount(BigDecimal.valueOf(diff))
                                    .setPrice(price)
                                    .setApplyId(null)
                                    .setApplyDetailId(null)
                                    .setGroupId(groupId)
                                    .setAssignDetailId(null)
                                    .setTradeNo(tradeNo)
                                    .setSystemId(null)
                                    .setWorkstationId(null)
                                    .setUseCategory(useCategory != null ? useCategory : materialType.getCategory1())
                                    .setTrainNo(trainNo)
                                    .setCompanyId(order.getCompanyId())
                                    .setWorkshopId(order.getWorkshopId())
                                    .setLineId(order.getLineId())
                                    .setOldAmount(BigDecimal.ZERO)
                                    .setUsableAmount(BigDecimal.valueOf(diff))
                                    .setNeedAdd(true)
                                    .setRelativeOrderMaterialActIdSet(new HashSet<>());
                            groupStock.getRelativeOrderMaterialActIdSet().add(actId);
//                            groupStockList.add(groupStock);
                            idGroupStockMap.put(groupStock.getId(), groupStock);
                        }
                    }
                }

                // 计算工单物料实际消耗数量
                double sumActAmount = newActList.stream()
                        .mapToDouble(BuWorkOrderMaterialActs::getActAmount)
                        .sum();
                // 新增或更新工单物料
                orderMaterial.setActAmount(sumActAmount)
                        .setIsVerify(1);
                if (!isAdd) {
                    buWorkOrderMaterialMaterialMapper.updateById(orderMaterial);
                    // 删除旧的实际消耗
                    buWorkOrderMaterialActsMaterialMapper.deleteByOrderMaterialId(orderMaterialId);
                } else {
                    if (StringUtils.isBlank(orderMaterial.getOrderTaskId())) {
                        List<BuWorkOrderTask> orderTaskList = buWorkOrderForMaterialMapper.selectOrderTaskListByOrderId(orderMaterial.getOrderId());
                        if (CollectionUtils.isNotEmpty(orderTaskList)) {
                            orderMaterial.setOrderTaskId(orderTaskList.get(0).getId());
                        }
                    }
                    orderMaterial.setAmount(orderMaterial.getActAmount())
                            .setPlanAmount(orderMaterial.getActAmount());
                    buWorkOrderMaterialMaterialMapper.insert(orderMaterial);
                }
                // 插入新的实际消耗
                if (CollectionUtils.isNotEmpty(newActList)) {
                    for (BuWorkOrderMaterialActs act : newActList) {
                        act.setOrderMaterialId(orderMaterialId);
                        BuMaterialGroupStock groupStock = idGroupStockMap.get(act.getGroupStockId());
                        if (null != groupStock) {
                            act.setApplyId(groupStock.getApplyId())
                                    .setApplyDetailId(groupStock.getApplyDetailId())
                                    .setAssignDetailId(groupStock.getAssignDetailId())
                                    .setPrice(groupStock.getPrice());
                        }
                    }
                    buWorkOrderMaterialActsMaterialMapper.insertList(newActList);
                    // 删除实际消耗数量为0的数据
                    List<String> zeroActAmountActIdList = newActList.stream()
                            .filter(act -> null == act.getActAmount() || 0D == act.getActAmount())
                            .map(BuWorkOrderMaterialActs::getId)
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(zeroActAmountActIdList)) {
                        buWorkOrderMaterialActsMaterialMapper.deleteBatchIds(zeroActAmountActIdList);
                    }
                }

                // 更新班组库存数据
                buMaterialGroupStockService.updateGroupStockByGroupStockListForMaterialCostCheck(new ArrayList<>(idGroupStockMap.values()), order, now, "消耗核实", userId);
            }
        }

        // 开线程重新统计车辆物料统计数据
        rebuildMaterialRptDataByNewThread(trainNoList, "消耗核实");

        return true;
    }

    /**
     * @see MaterialCostCompareService#exportMaterialCostCompare(CompareQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public HSSFWorkbook exportMaterialCostCompare(CompareQueryVO queryVO) throws Exception {
        // 获取数据
        CompareInfoVO compareInfoVO = listMaterialCostCompare(queryVO, true);
        List<CompareResultVO> compareResultVOList = compareInfoVO.getCompareResultList();

        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String[] excelHeader = {
                "线路",
                "车辆",
                "物资编码",
                "物资名称",
                "物资描述",
                "类型",
                "属性",
                "额定数量",
                "领用数量",
                "实际消耗",
                "修程类型",
                "所属班组",
                "所属系统",
                "发料工单",
                "消耗工单"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        for (CompareResultVO compareResultVO : compareResultVOList) {
            if (null != compareResultVO) {
                HSSFRow row = sheet.createRow(compareResultVOList.indexOf(compareResultVO) + 1);
                row.createCell(0).setCellValue(compareResultVO.getLineName());
                row.createCell(1).setCellValue(compareResultVO.getTrainNo());
                row.createCell(2).setCellValue(compareResultVO.getMaterialCode());
                row.createCell(3).setCellValue(compareResultVO.getMaterialName());
                row.createCell(4).setCellValue(compareResultVO.getMaterialSpec());
                row.createCell(5).setCellValue(compareResultVO.getUseCategoryNames());
                row.createCell(6).setCellValue(compareResultVO.getMaterialTypeCategory3());
                row.createCell(7).setCellValue(compareResultVO.getNeedAmount().stripTrailingZeros().toPlainString());
                row.createCell(8).setCellValue(compareResultVO.getReceiveAmount().stripTrailingZeros().toPlainString());
                row.createCell(9).setCellValue(compareResultVO.getConsumeAmount().stripTrailingZeros().toPlainString());
                row.createCell(10).setCellValue(compareResultVO.getRepairProNames());
                row.createCell(11).setCellValue(compareResultVO.getGroupName());
                row.createCell(12).setCellValue(compareResultVO.getSystemNames());
                row.createCell(13).setCellValue(compareResultVO.getApplyOrderInfo());
                row.createCell(14).setCellValue(compareResultVO.getConsumeOrderInfo());
            }
        }

        // 设置自动列宽
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());

        return workbook;
    }


    private Map<String, String> transToIdNameMap(List<Map<String, Object>> mapList) {
        Map<String, String> idNameMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            String id = (String) map.get("id");
            String name = (String) map.get("name");
            idNameMap.put(id, name);
        }
        return idNameMap;
    }

    private void mergeCanReplaceMustList(List<BuMaterialMustList> mustList, Map<String, List<String>> materialTypeIdReplaceIdListMap) {
        if (CollectionUtils.isEmpty(mustList) || materialTypeIdReplaceIdListMap.isEmpty()) {
            return;
        }

        // 排序
        mustList.sort(Comparator.comparing(BuMaterialMustList::getMaterialTypeId));

        // 处理
        List<BuMaterialMustList> mergedMustList = new ArrayList<>();
        List<String> mergedMustIdList = new ArrayList<>();
        for (BuMaterialMustList must : mustList) {
            if (mergedMustIdList.contains(must.getId())) {
                continue;
            }

            String materialTypeId = must.getMaterialTypeId();
            List<String> replaceIdList = materialTypeIdReplaceIdListMap.get(materialTypeId);
            if (CollectionUtils.isEmpty(replaceIdList)) {
                continue;
            }

            String sysId = must.getSysId();
            String workstationId = must.getWorkstationId();

            // 同系统、同工位、互为可替换的必换件清单合并
            List<BuMaterialMustList> canReplaceMustList = mustList.stream()
                    .filter(item -> !mergedMustIdList.contains(item.getId())
                            && sysId.equals(item.getSysId())
                            && workstationId.equals(item.getWorkstationId())
                            && replaceIdList.contains(item.getMaterialTypeId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(canReplaceMustList)) {
                double needAmount = must.getNeedAmount();
                for (BuMaterialMustList canReplaceMust : canReplaceMustList) {
                    needAmount = needAmount + canReplaceMust.getNeedAmount();
                    mergedMustList.add(canReplaceMust);
                    mergedMustIdList.add(canReplaceMust.getId());
                }
                must.setNeedAmount(needAmount);
            }
        }

        // 去掉已合并的
        mustList.removeAll(mergedMustList);
    }

    private String getUseCategoryNames(String useCategory) {
        List<String> useCategoryList = Arrays.asList(useCategory.split(","));
        if (CollectionUtils.isEmpty(useCategoryList)) {
            return "";
        }

        List<String> useCategoryNameList = new ArrayList<>();
        for (String useCategoryItem : useCategoryList) {
            String useCategoryName = getUseCategoryName(Integer.valueOf(useCategoryItem));
            useCategoryNameList.add(useCategoryName);
        }

        return String.join(",", useCategoryNameList);
    }

    private String getUseCategoryName(Integer useCategory) {
        String useCategoryName = "";
        switch (useCategory) {
            case 1:
                useCategoryName = "必换件";
                break;
            case 2:
                useCategoryName = "偶换件";
                break;
            case 3:
                useCategoryName = "耗材";
                break;
            case -1:
                useCategoryName = "其他";
                break;
            default:
                break;
        }
        return useCategoryName;
    }

    private String generateConsumeSystemWorkstationInfo(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return null;
        }

        Map<String, List<String>> systemNameWorkstationNameListMap = new HashMap<>();
        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            String systemName = orderMaterial.getSystemName();
            String workstationName = orderMaterial.getWorkstationName();

            List<String> workstationNameList = systemNameWorkstationNameListMap.getOrDefault(systemName, new ArrayList<>());
            if (!workstationNameList.contains(workstationName)) {
                workstationNameList.add(workstationName);
                workstationNameList.sort(Comparator.comparing(String::toString));
            }
            systemNameWorkstationNameListMap.put(systemName, workstationNameList);
        }

        StringBuilder consumeSystemWorkstationInfo = new StringBuilder();
        for (Map.Entry<String, List<String>> systemNameWorkstationNameListEntry : systemNameWorkstationNameListMap.entrySet()) {
            String systemName = systemNameWorkstationNameListEntry.getKey();
            List<String> workstationNameList = systemNameWorkstationNameListEntry.getValue();

            consumeSystemWorkstationInfo.append(systemName)
                    .append("-")
                    .append(String.join(",", workstationNameList))
                    .append("；");
        }
        if (consumeSystemWorkstationInfo.length() > 0) {
            return consumeSystemWorkstationInfo.deleteCharAt(consumeSystemWorkstationInfo.length() - 1).toString();
        } else {
            return null;
        }
    }

    private List<String> checkOrderCloseReturnTrainNoList(List<BuWorkOrder> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        }
        orderList.sort(Comparator.comparing(BuWorkOrder::getOrderCode, Comparator.nullsLast(Comparator.naturalOrder())));

        List<String> unCloseOrderCodeList = new ArrayList<>();
        List<String> trainNoList = new ArrayList<>();
        for (BuWorkOrder order : orderList) {
            if (4 != order.getOrderStatus()) {
                unCloseOrderCodeList.add(order.getOrderCode());
            }
            String trainNo = order.getTrainNo();
            if (StringUtils.isNotBlank(trainNo) && !trainNoList.contains(trainNo)) {
                trainNoList.add(trainNo);
            }
        }

        if (CollectionUtils.isNotEmpty(unCloseOrderCodeList)) {
            throw new JeecgBootException("工单[" + String.join(",", unCloseOrderCodeList) + "]未关闭，请关闭后再核实");
        }

        return trainNoList;
    }

    private void setOrderMaterialActList(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        List<BuWorkOrderMaterialActs> allActList = new ArrayList<>();
        List<String> orderMaterialIdList = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .distinct()
                .collect(Collectors.toList());
        List<List<String>> orderMaterialIdBatchSubList = DatabaseBatchSubUtil.batchSubList(orderMaterialIdList);
        for (List<String> orderMaterialIdBatchSub : orderMaterialIdBatchSubList) {
            LambdaQueryWrapper<BuWorkOrderMaterialActs> actWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                    .in(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterialIdBatchSub);
            List<BuWorkOrderMaterialActs> subActList = buWorkOrderMaterialActsMaterialMapper.selectList(actWrapper);
            allActList.addAll(subActList);
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            List<BuWorkOrderMaterialActs> actList = allActList.stream()
                    .filter(act -> orderMaterial.getId().equals(act.getOrderMaterialId()))
                    .collect(Collectors.toList());
            orderMaterial.setActList(actList);
        }
    }

    private void rebuildMaterialRptDataByNewThread(List<String> trainNoList, String sourceScene) {
        if (CollectionUtils.isEmpty(trainNoList)) {
            return;
        }

        ExecutorService singleExecutor = ThreadUtil.newSingleExecutor();
        singleExecutor.execute(() -> {
            try {
                for (String trainNo : trainNoList) {
                    materialRptService.reBuildRptMaterialByTrainNo(trainNo);
                }
            } catch (Exception ex) {
                log.error(sourceScene + "时，开线程重新统计车辆物料统计数据，执行失败，原因如下：" + ex.getMessage());
                StringWriter stringWriter = new StringWriter();
                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    ex.printStackTrace(printWriter);
                }
                log.error(stringWriter.toString());
            }
        });
        singleExecutor.shutdown();
    }

}