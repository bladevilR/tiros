package org.jeecg.modules.board.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;
import org.jeecg.common.tiros.stock.use.service.BuMaterialStockUseService;
import org.jeecg.common.tiros.util.DataTypeCastUtil;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.board.cost.bean.BuMaterialStock;
import org.jeecg.modules.board.cost.mapper.BuMaterialStockBoardMapper;
import org.jeecg.modules.board.group.bean.BuMaterialType;
import org.jeecg.modules.board.group.bean.vo.*;
import org.jeecg.modules.board.group.mapper.BuGroupBoardMapper;
import org.jeecg.modules.board.group.service.BuGroupBoardService;
import org.jeecg.modules.board.progress.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;
import org.jeecg.modules.board.progress.bean.BuWorkOrder;
import org.jeecg.modules.board.progress.mapper.BuMtrWorkshopGroupBoardMapper;
import org.jeecg.modules.board.progress.mapper.BuRepairPlanBoardMapper;
import org.jeecg.modules.board.progress.mapper.BuWorkOrderBoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 班组看板 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-09-15
 */
@Slf4j
@Service
public class BuGroupBoardServiceImpl implements BuGroupBoardService {

    @Resource
    private BuGroupBoardMapper buGroupBoardMapper;
    @Resource
    private BuRepairPlanBoardMapper buRepairPlanBoardMapper;
    @Resource
    private BuWorkOrderBoardMapper buWorkOrderBoardMapper;
    @Resource
    private BuMtrWorkshopGroupBoardMapper buMtrWorkshopGroupBoardMapper;
    @Resource
    private BuMaterialStockBoardMapper buMaterialStockBoardMapper;
    @Resource
    private BuMaterialStockUseService buMaterialStockUseService;


    /**
     * @see BuGroupBoardService#getGroupOrderCount()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuGroupOrderCount getGroupOrderCount() throws Exception {
        Date now = new Date();

        // 查询该班组、未完成列计划、的工单
        List<BuWorkOrder> orderList = listGroupOrder();

        int total = 0;
        int finish = 0;
        int delay = 0;
        int suspend = 0;

        List<Integer> finishOrderStatusList = Arrays.asList(3, 4);
        for (BuWorkOrder order : orderList) {
            Integer orderStatus = order.getOrderStatus();

            total++;
            if (finishOrderStatusList.contains(orderStatus)) {
                finish++;
            } else {
                if (5 == orderStatus) {
                    suspend++;
                } else {
                    Date finishTime = order.getFinishTime();
                    Date finishTimeEnd = DateUtils.transToDayEnd(finishTime);
                    if (now.after(finishTimeEnd)) {
                        delay++;
                    }
                }
            }
        }
        String groupName = buGroupBoardMapper.selectGroupName(getCurrentUserGroupId());
        return new BuGroupOrderCount().setTotal(total).setFinish(finish).setDelay(delay).setSuspend(suspend).setGroupName(groupName);
    }

    /**
     * @see BuGroupBoardService#listGroupTodayOrder()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrder> listGroupTodayOrder() throws Exception {
        Date now = new Date();

        // 查询该班组、未完成列计划、的工单
        List<BuWorkOrder> orderList = listGroupOrder();

        List<BuWorkOrder> todayOrderList = new ArrayList<>();
        for (BuWorkOrder order : orderList) {
            Integer orderStatus = order.getOrderStatus();

            Date finishTime = order.getFinishTime();
            if (DateUtils.isSameDay(finishTime, now) && 0 != orderStatus && 5 != orderStatus) {
                // 未下发、已暂停的不计入当日工单
                todayOrderList.add(order);
            }
        }
        if (CollectionUtils.isNotEmpty(todayOrderList)) {
            todayOrderList.sort(Comparator.comparing(BuWorkOrder::getOrderCode, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed());
        }

        return todayOrderList;
    }

    /**
     * @see BuGroupBoardService#listGroupPersonRank()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuGroupPersonRank> listGroupPersonRank() throws Exception {
        // 登录人所属班组
        String groupId = getCurrentUserGroupId();
        // 当前列计划
        List<String> planIdList = getCurrentRepairPlanIdList();

        if (StringUtils.isNotBlank(groupId) && CollectionUtils.isNotEmpty(planIdList)) {
            // 查询已提交或已关闭的工单的填报工时
            List<Map<String, Object>> orderUserIdWorkTimeMapList = buGroupBoardMapper.selectOrderUserIdWorkTimeByGroupAndPlan(groupId, planIdList);
            Map<String, BigDecimal> userIdWorkTimeMap = new HashMap<>();
            for (Map<String, Object> orderUserIdWorkTimeMap : orderUserIdWorkTimeMapList) {
                String userId = (String) orderUserIdWorkTimeMap.get("userId");
                BigDecimal workTime = (BigDecimal) orderUserIdWorkTimeMap.get("workTime");

                BigDecimal totalWorkTime = userIdWorkTimeMap.getOrDefault(userId, BigDecimal.ZERO);
                userIdWorkTimeMap.put(userId, totalWorkTime.add(workTime));
            }
            // 查询提交故障
            List<String> faultReportUserIdList = buGroupBoardMapper.selectFaultReportUserIdByGroupAndPlan(groupId, planIdList);
            Map<String, Integer> userIdReportFaultTimesMap = new HashMap<>();
            for (String faultReportUserId : faultReportUserIdList) {
                Integer totalReportFaultTimes = userIdReportFaultTimesMap.getOrDefault(faultReportUserId, 0);
                userIdReportFaultTimesMap.put(faultReportUserId, totalReportFaultTimes + 1);
            }

            // 查询该班组下的所有人员
            List<Map<String, Object>> groupUserInfoList = buGroupBoardMapper.selectGroupUserInfoList(groupId);

            // 获取排名数据
            List<BuGroupPersonRank> personRankList = new ArrayList<>();
            for (Map<String, Object> groupUserInfo : groupUserInfoList) {
                String userId = (String) groupUserInfo.get("userId");
                String workNo = (String) groupUserInfo.get("workNo");
                String realname = (String) groupUserInfo.get("realname");

                Integer reportFaultTimes = userIdReportFaultTimesMap.getOrDefault(userId, 0);
                BigDecimal workTime = userIdWorkTimeMap.getOrDefault(userId, BigDecimal.ZERO);

                BuGroupPersonRank personRank = new BuGroupPersonRank()
                        .setUserId(userId)
                        .setWorkNo(workNo)
                        .setUserRealName(realname)
                        .setFaultAmount(reportFaultTimes)
                        .setWorkTime(workTime);
                personRankList.add(personRank);
            }
            // 排序
            if (CollectionUtils.isNotEmpty(personRankList)) {
                personRankList.sort(Comparator.comparing(BuGroupPersonRank::getFaultAmount)
                        .thenComparing(BuGroupPersonRank::getWorkTime)
                        .thenComparing(BuGroupPersonRank::getWorkNo)
                        .reversed()
                );
                int realSortNo = 1;
                int showSortNo = 1;
                Integer lastFaultAmount = personRankList.get(0).getFaultAmount();
                BigDecimal lastWorkTime = personRankList.get(0).getWorkTime();
                for (BuGroupPersonRank personRank : personRankList) {
                    Integer faultAmount = personRank.getFaultAmount();
                    BigDecimal workTime = personRank.getWorkTime();

                    boolean faultAmountEquals = lastFaultAmount.equals(faultAmount);
                    boolean workTimeEquals = lastWorkTime.compareTo(workTime) == 0;

                    if (!faultAmountEquals || !workTimeEquals) {
                        lastFaultAmount = faultAmount;
                        lastWorkTime = workTime;
                        showSortNo = realSortNo;
                    }

                    realSortNo++;
                    personRank.setSortNo(showSortNo);
                }
            }
            return personRankList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @see BuGroupBoardService#listGroupMaterialLack()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuGroupMaterialLack> listGroupMaterialLack() throws Exception {
        // 登录人所属班组
        String groupId = getCurrentUserGroupId();
        // 当前列计划
        List<BuRepairPlan> planList = getCurrentRepairPlanList();

        if (StringUtils.isNotBlank(groupId) && CollectionUtils.isNotEmpty(planList)) {
            Map<String, BigDecimal> materialTypeIdNeedAmountMap = new HashMap<>();
            // 根据每个列计划查询对应的必换件清单
            for (BuRepairPlan plan : planList) {
                String lineId = plan.getLineId();
                String programId = plan.getRepairProgramId();
                List<Map<String, Object>> mustMaterialList = buGroupBoardMapper.selectMustMaterialListByLineAndProgramAndGroup(lineId, programId, groupId, plan.getId());
                if (CollectionUtils.isNotEmpty(mustMaterialList)) {
                    for (Map<String, Object> mustMaterial : mustMaterialList) {
                        String materialTypeId = (String) mustMaterial.get("materialTypeId");
                        BigDecimal needAmount = (BigDecimal) mustMaterial.get("needAmount");

                        BigDecimal totalNeedAmount = materialTypeIdNeedAmountMap.getOrDefault(materialTypeId, BigDecimal.ZERO);
                        materialTypeIdNeedAmountMap.put(materialTypeId, totalNeedAmount.add(needAmount));
                    }
                }
            }

            if (materialTypeIdNeedAmountMap.isEmpty()) {
                return new ArrayList<>();
            } else {
                List<String> materialTypeIdList = new ArrayList<>(materialTypeIdNeedAmountMap.keySet());
                // 查询班组库存
                Map<String, BigDecimal> materialTypeIdGroupStockAmountMap = new HashMap<>();
                List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
                for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                    List<Map<String, Object>> subGroupStockList = buGroupBoardMapper.selectGroupStockAmount(groupId, materialTypeIdBatchSub);
                    addMaterialTypeIdAmountMap(materialTypeIdGroupStockAmountMap, subGroupStockList);
                }

                // 过滤获得缺料的物资：班组库存不够的
                List<BuGroupMaterialLack> lackList = new ArrayList<>();
                for (Map.Entry<String, BigDecimal> materialTypeIdNeedAmountEntry : materialTypeIdNeedAmountMap.entrySet()) {
                    String materialTypeId = materialTypeIdNeedAmountEntry.getKey();
                    BigDecimal needAmount = materialTypeIdNeedAmountEntry.getValue();

                    BigDecimal groupStockAmount = materialTypeIdGroupStockAmountMap.getOrDefault(materialTypeId, BigDecimal.ZERO);
                    if (groupStockAmount.compareTo(needAmount) < 0) {
                        BuGroupMaterialLack lack = new BuGroupMaterialLack()
                                .setMaterialTypeId(materialTypeId)
                                .setGroupStockAmount(groupStockAmount)
                                .setNeedAmount(needAmount)
                                .setDiffAmount(needAmount.subtract(groupStockAmount));
                        lackList.add(lack);
                    }
                }
                // 补全缺料数据的物资信息和车间库存
                if (CollectionUtils.isNotEmpty(lackList)) {
                    List<String> lackMaterialTypeIdList = lackList.stream()
                            .map(BuGroupMaterialLack::getMaterialTypeId)
                            .distinct()
                            .collect(Collectors.toList());
                    // 查询物资信息和车间库存
                    Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
                    Map<String, BigDecimal> materialTypeIdSumStockMap = new HashMap<>();
                    List<List<String>> lackMaterialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(lackMaterialTypeIdList);
                    for (List<String> lackMaterialTypeIdBatchSub : lackMaterialTypeIdBatchSubList) {
                        List<BuMaterialType> subMaterialTypeList = buGroupBoardMapper.selectMaterialTypeListByMaterialTypeIdList(lackMaterialTypeIdBatchSub);
                        subMaterialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));

                        // 库存
                        List<BuMaterialStock> stockList = buMaterialStockBoardMapper.selectListByMaterialTypeIdList(lackMaterialTypeIdBatchSub);
                        // 不统计3级库以下的
                        stockList.removeIf(stock -> null != stock.getWarehouseLevel() && stock.getWarehouseLevel() >= 5);
                        // 占用
                        List<BuMaterialStockUse> stockUseList = buMaterialStockUseService.listStockUseByMaterialTypeIdList(lackMaterialTypeIdBatchSub);
                        // 不统计3级库以下的
                        stockUseList.removeIf(stockUse -> null != stockUse.getWarehouseLevel() && stockUse.getWarehouseLevel() >= 5);
                        getMaterialTypeIdSumStockMap(materialTypeIdSumStockMap, stockList, stockUseList);
                    }

                    for (BuGroupMaterialLack lack : lackList) {
                        String materialTypeId = lack.getMaterialTypeId();
                        BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                        if (null != materialType) {
                            lack.setMaterialTypeCode(materialType.getCode())
                                    .setMaterialTypeName(materialType.getName());
                        }
                        BigDecimal stock = materialTypeIdSumStockMap.getOrDefault(materialTypeId, BigDecimal.ZERO);
                        lack.setWorkshopStockAmount(stock);
                    }
                }

                return lackList;
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @see BuGroupBoardService#listGroupToolCheckAlert()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuGroupToolCheckAlert> listGroupToolCheckAlert() throws Exception {
        Date now = new Date();

        // 登录人所属班组
        String groupId = getCurrentUserGroupId();

        if (StringUtils.isNotBlank(groupId)) {
            Date date = new Date(now.getTime() + TirosConstant.TOOLS_CHECK_EARLIER_WARNING_TIME);
            List<Map<String, Object>> groupToolCheckAlertList = buGroupBoardMapper.selectGroupToolCheckAlertList(groupId, date);
            if (CollectionUtils.isNotEmpty(groupToolCheckAlertList)) {
                List<BuGroupToolCheckAlert> toolCheckAlertList = new ArrayList<>();
                for (Map<String, Object> groupToolCheckAlert : groupToolCheckAlertList) {
                    String id = (String) groupToolCheckAlert.get("id");
                    String code = (String) groupToolCheckAlert.get("code");
                    String assetCode = (String) groupToolCheckAlert.get("assetCode");
                    String name = (String) groupToolCheckAlert.get("name");
                    String model = (String) groupToolCheckAlert.get("model");
                    Date nextCheckTime = DataTypeCastUtil.transDate(groupToolCheckAlert.get("nextCheckTime"));

                    int dayDiff = DateUtils.dayDiff(now, nextCheckTime, false);

                    BuGroupToolCheckAlert toolCheckAlert = new BuGroupToolCheckAlert()
                            .setToolId(id)
                            .setMaterialTypeCode(code)
                            .setAssetCode(assetCode)
                            .setToolName(name)
                            .setToolModel(model)
                            .setNextCheckTime(nextCheckTime)
                            .setOverDays(dayDiff);
                    toolCheckAlertList.add(toolCheckAlert);
                }
                if (CollectionUtils.isNotEmpty(toolCheckAlertList)) {
                    toolCheckAlertList.sort(Comparator.comparing(BuGroupToolCheckAlert::getOverDays, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed());
                }
                return toolCheckAlertList;
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @see BuGroupBoardService#listGroupMaterialLack()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuGroupRank getGroupRank() throws Exception {
        // 当前列计划
        List<String> planIdList = getCurrentRepairPlanIdList();
        if (CollectionUtils.isNotEmpty(planIdList)) {
            // 查询故障提报
            List<String> faultReportGroupIdList = buGroupBoardMapper.selectFaultReportGroupIdByPlan(planIdList);
            // 查询工单提交、工单关闭
            List<Map<String, Object>> orderList = buGroupBoardMapper.selectOrderGroupIdStatusByPlan(planIdList);

            // 查询所有工班
            List<BuMtrWorkshopGroup> groupList = buMtrWorkshopGroupBoardMapper.selectList(Wrappers.emptyWrapper());

            List<BuGroupRankItem> reportFaultList = new ArrayList<>();
            List<BuGroupRankItem> orderSubmitList = new ArrayList<>();
            List<BuGroupRankItem> orderCloseList = new ArrayList<>();
            for (BuMtrWorkshopGroup group : groupList) {
                String groupId = group.getId();
                String groupName = group.getGroupName();

                // 故障提报
                int faultAmount = (int) faultReportGroupIdList.stream()
                        .filter(faultReportGroupId -> faultReportGroupId.equals(groupId))
                        .count();
                reportFaultList.add(new BuGroupRankItem()
                        .setGroupId(groupId)
                        .setGroupName(groupName)
                        .setAmount(faultAmount));

                // 工单提交、工单关闭
                int orderSubmitAmount = 0;
                int orderCloseAmount = 0;
                for (Map<String, Object> order : orderList) {
                    String orderGroupId = (String) order.get("groupId");
                    BigDecimal orderStatusBigDecimal = (BigDecimal) order.get("orderStatus");
                    int orderStatus = null == orderStatusBigDecimal ? -1 : orderStatusBigDecimal.intValue();

                    if (orderGroupId.equals(groupId)) {
                        if (3 == orderStatus) {
                            orderSubmitAmount++;
                        } else if (4 == orderStatus) {
                            orderSubmitAmount++;
                            orderCloseAmount++;
                        }
                    }
                }
                orderSubmitList.add(new BuGroupRankItem()
                        .setGroupId(groupId)
                        .setGroupName(groupName)
                        .setAmount(orderSubmitAmount));
                orderCloseList.add(new BuGroupRankItem()
                        .setGroupId(groupId)
                        .setGroupName(groupName)
                        .setAmount(orderCloseAmount));
            }
            // 排序
            sortRankItem(reportFaultList);
            sortRankItem(orderSubmitList);
            sortRankItem(orderCloseList);

            return new BuGroupRank()
                    .setReportFaultList(reportFaultList)
                    .setOrderSubmitList(orderSubmitList)
                    .setOrderCloseList(orderCloseList);
        } else {
            return new BuGroupRank()
                    .setReportFaultList(new ArrayList<>())
                    .setOrderSubmitList(new ArrayList<>())
                    .setOrderCloseList(new ArrayList<>());
        }
    }


    private String getCurrentUserGroupId() {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null == sysUser) {
            log.error("无法获取到当前登录人信息；");
            return null;
        }

        String groupId = buGroupBoardMapper.selectGroupIdByUserId(sysUser.getId());
        if (StringUtils.isBlank(groupId)) {
            log.error("无法获取到当前登录人[" + sysUser.getRealname() + "]的所属班组；");
        }

        return groupId;
    }

    private List<String> getCurrentRepairPlanIdList() {
        List<BuRepairPlan> planList = buRepairPlanBoardMapper.selectNotFinishRepairPlanListNoTask();
        if (CollectionUtils.isEmpty(planList)) {
            log.error("当前没有未完成的列计划；");
        }

        return planList.stream()
                .map(BuRepairPlan::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<BuRepairPlan> getCurrentRepairPlanList() {
        List<BuRepairPlan> planList = buRepairPlanBoardMapper.selectNotFinishRepairPlanListNoTask();
        if (CollectionUtils.isEmpty(planList)) {
            log.error("当前没有未完成的列计划；");
        }

        return planList;
    }

    private List<BuWorkOrder> listGroupOrder() {
        // 登录人所属班组
        String groupId = getCurrentUserGroupId();
        // 当前列计划
        List<String> planIdList = getCurrentRepairPlanIdList();

        if (StringUtils.isNotBlank(groupId) && CollectionUtils.isNotEmpty(planIdList)) {
            LambdaQueryWrapper<BuWorkOrder> orderWrapper = new LambdaQueryWrapper<BuWorkOrder>()
                    .eq(BuWorkOrder::getGroupId, groupId)
                    .in(BuWorkOrder::getPlanId, planIdList)
                    .ne(BuWorkOrder::getOrderStatus, 0)
                    .ne(BuWorkOrder::getOrderType, 4);
            return buWorkOrderBoardMapper.selectList(orderWrapper);
        }

        return new ArrayList<>();
    }

    private void addMaterialTypeIdAmountMap(Map<String, BigDecimal> materialTypeIdAmountMap, List<Map<String, Object>> stockAmountList) {
        for (Map<String, Object> stockAmount : stockAmountList) {
            String materialTypeId = (String) stockAmount.get("materialTypeId");
            BigDecimal amount = (BigDecimal) stockAmount.get("amount");

            BigDecimal totalAmount = materialTypeIdAmountMap.getOrDefault(materialTypeId, BigDecimal.ZERO);
            materialTypeIdAmountMap.put(materialTypeId, totalAmount.add(amount));
        }
    }

    private void getMaterialTypeIdSumStockMap(Map<String, BigDecimal> materialTypeIdSumStockMap,
                                              List<BuMaterialStock> stockList,
                                              List<BuMaterialStockUse> stockUseList) {
        if (CollectionUtils.isEmpty(stockList)) {
            return;
        }

        Map<String, List<BuMaterialStock>> materialTypeIdListMap = stockList.stream()
                .collect(Collectors.groupingBy(BuMaterialStock::getMaterialTypeId));
        for (Map.Entry<String, List<BuMaterialStock>> materialTypeIdListEntry : materialTypeIdListMap.entrySet()) {
            String materialTypeId = materialTypeIdListEntry.getKey();
            List<BuMaterialStock> list = materialTypeIdListEntry.getValue();

            BigDecimal stockAmount = list.stream()
                    .map(BuMaterialStock::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            double stockUseAmount = stockUseList.stream()
                    .filter(stockUse -> materialTypeId.equals(stockUse.getMaterialTypeId()))
                    .mapToDouble(BuMaterialStockUse::getUseAmount)
                    .sum();
            BigDecimal amount = stockAmount.subtract(BigDecimal.valueOf(stockUseAmount));

            materialTypeIdSumStockMap.put(materialTypeId, amount);
        }
    }

    private void sortRankItem(List<BuGroupRankItem> rankItemList) {
        if (CollectionUtils.isNotEmpty(rankItemList)) {
            rankItemList.sort(Comparator.comparing(BuGroupRankItem::getAmount)
                    .reversed()
            );

            int realSortNo = 1;
            int showSortNo = 1;
            Integer lastAmount = rankItemList.get(0).getAmount();
            for (BuGroupRankItem rankItem : rankItemList) {
                Integer amount = rankItem.getAmount();
                boolean amountEquals = lastAmount.equals(amount);

                if (!amountEquals) {
                    lastAmount = amount;
                    showSortNo = realSortNo;
                }

                realSortNo++;
                rankItem.setSortNo(showSortNo);
            }
        }
    }

}
