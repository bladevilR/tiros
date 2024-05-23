package org.jeecg.modules.material.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.stock.change.bean.BuMaterialStockChange;
import org.jeecg.common.tiros.stock.change.service.BuMaterialStockChangeService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterialActs;
import org.jeecg.modules.material.cost.mapper.BuWorkOrderMaterialActsMaterialMapper;
import org.jeecg.modules.material.cost.mapper.BuWorkOrderMaterialMaterialMapper;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.bean.bo.GroupStockUsed;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialGroupStockQueryVO;
import org.jeecg.modules.material.stock.bean.vo.GroupStockAtrrVO;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.jeecg.modules.material.stock.service.BuMaterialGroupStockService;
import org.jeecg.modules.material.turnovernew.bean.BuMaterialTurnoverNew;
import org.jeecg.modules.material.turnovernew.mapper.BuMaterialTurnoverNewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 班组库存 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
@Service
public class BuMaterialGroupStockServiceImpl extends ServiceImpl<BuMaterialGroupStockMapper, BuMaterialGroupStock> implements BuMaterialGroupStockService {

    @Resource
    private BuMaterialGroupStockMapper buMaterialGroupStockMapper;
    @Resource
    private BuWorkOrderMaterialMaterialMapper buWorkOrderMaterialMaterialMapper;
    @Resource
    private BuWorkOrderMaterialActsMaterialMapper buWorkOrderMaterialActsMaterialMapper;
    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialMustListMapper buMaterialMustListMapper;
    @Resource
    private BuMaterialTurnoverNewMapper buMaterialTurnoverNewMapper;
    @Resource
    private BuMaterialStockChangeService buMaterialStockChangeService;
    @Resource
    private BuMaterialTypeReplaceService buMaterialTypeReplaceService;


    /**
     * @see BuMaterialGroupStockService#pageGroupStock(BuMaterialGroupStockQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialGroupStock> pageGroupStock(BuMaterialGroupStockQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        // 关联可替换物资
        if (StringUtils.isNotBlank(queryVO.getSearchText())) {
            List<String> canReplaceMaterialTypeIdList = buMaterialTypeReplaceService.listCanReplaceMaterialTypeIdByMaterialTypeText(queryVO.getSearchText());
            queryVO.setSearchMaterialTypeIdList(canReplaceMaterialTypeIdList);
        }
        IPage<BuMaterialGroupStock> page = buMaterialGroupStockMapper.selectPageByQueryVO(new Page<>(pageNo, pageSize), queryVO);

        String groupId = queryVO.getGroupId();
        if (StringUtils.isNotBlank(groupId)) {
            // 减去班组已占用的数量
            deleteGroupStockUsedAmount(page.getRecords(), groupId, null, null, null);
        } else {
            List<BuMaterialGroupStock> pageGroupStockList = page.getRecords();
            Map<String, List<BuMaterialGroupStock>> groupIdGroupStockListMap = pageGroupStockList.stream()
                    .collect(Collectors.groupingBy(BuMaterialGroupStock::getGroupId));
            for (Map.Entry<String, List<BuMaterialGroupStock>> groupIdGroupStockListEntry : groupIdGroupStockListMap.entrySet()) {
                // 减去班组已占用的数量
                deleteGroupStockUsedAmount(groupIdGroupStockListEntry.getValue(), groupIdGroupStockListEntry.getKey(), null, null, null);
            }
        }

        return page;
    }

    /**
     * @see BuMaterialGroupStockService#listGroupStockForTaskWrite(String, String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialGroupStock> listGroupStockForTaskWrite(String groupId, String materialTypeId, String orderMaterialId) throws Exception {
        List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectListByGroupAndMaterialType(groupId, materialTypeId);
        if (CollectionUtils.isEmpty(groupStockList)) {
            // 查询物资类型表中的价格
            BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);
            if (null == materialType) {
                throw new JeecgBootException("物资类型[" + materialTypeId + "]不存在");
            }

            // 根据班组id查询公司、车间、线路
            List<String> companyIdList = new ArrayList<>();
            List<String> workshopIdList = new ArrayList<>();
            List<String> lineIdList = new ArrayList<>();
            List<Map<String, Object>> idMapList = buMaterialGroupStockMapper.selectCompanyWorkshopLineByGroupId(groupId);
            if (CollectionUtils.isEmpty(idMapList)) {
                throw new JeecgBootException("请配置班组" + groupId + "所属车间等信息");
            } else {
                for (Map<String, Object> idMap : idMapList) {
                    String companyId = (String) idMap.get("companyId");
                    String workshopId = (String) idMap.get("workshopId");
                    String lineId = (String) idMap.get("lineId");
                    if (StringUtils.isNotBlank(companyId) && !companyIdList.contains(companyId)) {
                        companyIdList.add(companyId);
                    }
                    if (StringUtils.isNotBlank(workshopId) && !workshopIdList.contains(workshopId)) {
                        workshopIdList.add(workshopId);
                    }
                    if (StringUtils.isNotBlank(lineId) && !lineIdList.contains(lineId)) {
                        lineIdList.add(lineId);
                    }
                }
            }
            companyIdList.sort(Comparator.comparing(String::toString));
            workshopIdList.sort(Comparator.comparing(String::toString));
            lineIdList.sort(Comparator.comparing(String::toString));


            // 如果为空，新增库存量为0的记录
            BuMaterialGroupStock groupStock = new BuMaterialGroupStock()
                    .setId(UUIDGenerator.generate())
                    .setWarehouseId(null)
                    .setMaterialTypeId(materialTypeId)
                    .setAmount(BigDecimal.ZERO)
                    .setPrice(materialType.getPrice())
                    .setApplyId(null)
                    .setApplyDetailId(null)
                    .setAssignDetailId(null)
                    .setGroupId(groupId)
                    .setTradeNo(null)
                    .setCompanyId(String.join(",", companyIdList))
                    .setWorkshopId(String.join(",", workshopIdList))
                    .setLineId(String.join(",", lineIdList));
            buMaterialGroupStockMapper.insert(groupStock);

            groupStockList = buMaterialGroupStockMapper.selectListByGroupAndMaterialType(groupId, materialTypeId);
        }

        // 减去班组已占用的数量
        deleteGroupStockUsedAmount(groupStockList, groupId, null, orderMaterialId, null);

        // 去掉库存为0的
        groupStockList.removeIf(item -> BigDecimal.ZERO.compareTo(item.getAmount()) == 0);

        // 比对作业工单的车号和班组库存的车号是否一致
        BuWorkOrderMaterial orderMaterial = buWorkOrderMaterialMaterialMapper.selectOrderMaterialById(orderMaterialId);
        String workOrderTrainNo = orderMaterial.getTrainNo();
        for (BuMaterialGroupStock groupStock : groupStockList) {
            String trainNo = groupStock.getTrainNo();
            int trainNoIsSame = 0;
            if (StringUtils.isNotBlank(trainNo) && StringUtils.isNotBlank(workOrderTrainNo)
                    && trainNo.equals(workOrderTrainNo)) {
                trainNoIsSame = 1;
            }

            groupStock.setWorkOrderTrainNo(workOrderTrainNo)
                    .setTrainNoIsSame(trainNoIsSame);
        }

        return groupStockList;
    }

    /**
     * @see BuMaterialGroupStockService#getGroupMaterialStockAmount(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Double getGroupMaterialStockAmount(String groupId, String materialTypeId) throws Exception {
        LambdaQueryWrapper<BuMaterialGroupStock> wrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                .eq(BuMaterialGroupStock::getGroupId, groupId)
                .eq(BuMaterialGroupStock::getMaterialTypeId, materialTypeId);
        List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(groupStockList)) {
            return 0D;
        }

        // 减去班组已占用的数量
        deleteGroupStockUsedAmount(groupStockList, groupId, null, null, null);
        return groupStockList.stream()
                .map(BuMaterialGroupStock::getUsableAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    /**
     * @see BuMaterialGroupStockService#deleteGroupStockUsedAmount(List, String, List, String, List)
     */
    @Override
    public void deleteGroupStockUsedAmount(List<BuMaterialGroupStock> groupStockList, String groupId, List<BuWorkOrderMaterial> needUsedOrderMaterialList, String needIgnoreOrderMaterialId, List<String> needIgnoreOrderCodeList) {
        if (CollectionUtils.isEmpty(groupStockList)) {
            return;
        }
        for (BuMaterialGroupStock groupStock : groupStockList) {
            if (null == groupStock.getUsableAmount()) {
                groupStock.setUsableAmount(groupStock.getAmount());
            }
            if (StringUtils.isBlank(groupStock.getTradeNo())) {
                groupStock.setTradeNo(null);
            }
        }

        // 班组库存量减去“已核实”到“未关闭”的工单中的物料数量
        // 暂停状态的工单不需要减去：暂停时有可能原始状态为未核实不需要减去；重新激活时会回归原始状态根据原始状态计算
        if (CollectionUtils.isEmpty(needUsedOrderMaterialList)) {
            needUsedOrderMaterialList = buWorkOrderMaterialMaterialMapper.selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(groupId, TirosConstant.GROUP_STOCK_NEED_COUNT_USED_ORDER_STATUS);
        }
        if (StringUtils.isNotBlank(needIgnoreOrderMaterialId)) {
            // 如果是工单物料本身再次填写，不能减去
            needUsedOrderMaterialList.removeIf(orderMaterial -> needIgnoreOrderMaterialId.equals(orderMaterial.getId()));
        }
        if (CollectionUtils.isNotEmpty(needIgnoreOrderCodeList)) {
            // 不减去需要忽略的工单的工单物料
            needUsedOrderMaterialList.removeIf(orderMaterial -> StringUtils.isNotBlank(orderMaterial.getOrderCode()) && needIgnoreOrderCodeList.contains(orderMaterial.getOrderCode()));
        }
        if (CollectionUtils.isNotEmpty(needUsedOrderMaterialList)) {
            // 查询实际消耗列表
            List<String> orderMaterialIdList = needUsedOrderMaterialList.stream()
                    .map(BuWorkOrderMaterial::getId)
                    .collect(Collectors.toList());
            List<BuWorkOrderMaterialActs> actList = new ArrayList<>();
            List<List<String>> orderMaterialIdBatchSubList = DatabaseBatchSubUtil.batchSubList(orderMaterialIdList);
            for (List<String> orderMaterialIdBatchSub : orderMaterialIdBatchSubList) {
                List<BuWorkOrderMaterialActs> subActList = buWorkOrderMaterialActsMaterialMapper.selectListByOrderMaterialIdList(orderMaterialIdBatchSub);
                actList.addAll(subActList);
            }
            for (BuWorkOrderMaterial orderMaterial : needUsedOrderMaterialList) {
                List<BuWorkOrderMaterialActs> matchActList = actList.stream()
                        .filter(act -> orderMaterial.getId().equals(act.getOrderMaterialId()))
                        .collect(Collectors.toList());
                matchActList.forEach(act -> act.setOrderCode(orderMaterial.getOrderCode()));
                orderMaterial.setActList(matchActList);
            }

            // 获取各物资、各车号、各批次的占用量
            List<GroupStockUsed> usedList = getMaterialGroupStockUsedList(needUsedOrderMaterialList);

            // 计算：减去占用量
            // 物资分组
            Map<String, List<BuMaterialGroupStock>> materialTypeIdGroupStockListMap = groupStockList.stream()
                    .collect(Collectors.groupingBy(BuMaterialGroupStock::getMaterialTypeId));
            for (Map.Entry<String, List<BuMaterialGroupStock>> materialTypeIdStockListEntry : materialTypeIdGroupStockListMap.entrySet()) {
                String materialTypeId = materialTypeIdStockListEntry.getKey();
                List<BuMaterialGroupStock> materialTypeIdGroupStockList = materialTypeIdStockListEntry.getValue();
                // 车号分组
                Map<String, List<BuMaterialGroupStock>> trainNoGroupStockListMap = new HashMap<>();
                for (BuMaterialGroupStock groupStock : materialTypeIdGroupStockList) {
                    String trainNo = groupStock.getTrainNo();
                    List<BuMaterialGroupStock> trainNoGroupStockList = trainNoGroupStockListMap.getOrDefault(trainNo, new ArrayList<>());
                    trainNoGroupStockList.add(groupStock);
                    trainNoGroupStockListMap.put(trainNo, trainNoGroupStockList);
                }
                for (Map.Entry<String, List<BuMaterialGroupStock>> trainNoGroupStockListEntry : trainNoGroupStockListMap.entrySet()) {
                    String trainNo = trainNoGroupStockListEntry.getKey();
                    List<BuMaterialGroupStock> trainNoGroupStockList = trainNoGroupStockListEntry.getValue();
                    // 匹配物资、车号
                    List<GroupStockUsed> matchUsedList = new ArrayList<>();
                    for (GroupStockUsed groupStockUsed : usedList) {
                        boolean sameMaterial = materialTypeId.equals(groupStockUsed.getMaterialTypeId());
                        boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(groupStockUsed.getTrainNo()) : trainNo.equals(groupStockUsed.getTrainNo());

                        if (sameMaterial && sameTrainNo) {
                            matchUsedList.add(groupStockUsed);
                        }
                    }
                    matchUsedList.sort(Comparator.comparing(GroupStockUsed::getOrderCode, Comparator.nullsLast(Comparator.naturalOrder())));
                    if (CollectionUtils.isNotEmpty(matchUsedList)) {
                        for (GroupStockUsed used : matchUsedList) {
                            String usedTradeNo = used.getTradeNo();
                            String usedOrderCode = used.getOrderCode();
                            BigDecimal usedAmount = used.getAmount();
                            Integer usedType = used.getUsedType();
                            // 匹配批次
                            if (StringUtils.isNotBlank(usedTradeNo)) {
                                // 占用中有批次号，查找批次号相同的对应班组库存减去占用量
                                for (BuMaterialGroupStock groupStock : trainNoGroupStockList) {
                                    if (usedTradeNo.equals(groupStock.getTradeNo())) {
                                        decreaseGroupStock(groupStock, usedOrderCode, usedAmount, usedType);
                                    }
                                }
                            } else {
                                // 占用中没有批次号，先找批次号为空的对应班组库存减去占用，没有批次号为空的对应班组库存则需要按批次号排序后一个个的减去所有占用量
                                trainNoGroupStockList.sort(Comparator.comparing(BuMaterialGroupStock::getTradeNo, Comparator.nullsFirst(Comparator.naturalOrder())));
                                double totalAmount = usedAmount.doubleValue();
                                for (BuMaterialGroupStock groupStock : trainNoGroupStockList) {
                                    double groupUsableAmount = groupStock.getUsableAmount().doubleValue();
                                    if (groupUsableAmount > 0D) {
                                        double amount = Math.min(groupUsableAmount, totalAmount);

                                        decreaseGroupStock(groupStock, usedOrderCode, BigDecimal.valueOf(amount), usedType);
                                        totalAmount = totalAmount - amount;
                                        if (totalAmount <= 0D) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @see BuMaterialGroupStockService#updateGroupStockByGroupStockListForMaterialCostCheck(List, BuWorkOrder, Date, String, String)
     */
    @Override
    public void updateGroupStockByGroupStockListForMaterialCostCheck(List<BuMaterialGroupStock> groupStockList, BuWorkOrder order, Date now, String sourceScene, String userId) {
        List<BuMaterialStockChange> changeList = new ArrayList<>();

        // 新增的
        List<BuMaterialGroupStock> needAddGroupStockList = groupStockList.stream()
                .filter(groupStock -> null != groupStock.getNeedAdd() && groupStock.getNeedAdd())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needAddGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.insertList(batchSub);
            }

            for (BuMaterialGroupStock groupStock : needAddGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeOrderMaterialActIdSet().size() > 1;
                String orderMaterialActIds = String.join(",", groupStock.getRelativeOrderMaterialActIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason(sourceScene + "修改实际消耗量，新增班组库存量")
                        .setChangeType(1)
                        .setOldValue(0D)
                        .setNewValue(groupStock.getAmount().doubleValue())
                        .setTrainNo(null == order ? null : order.getTrainNo())
                        .setOrderCode(null == order ? null : order.getOrderCode())
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null);
                if (noteInRemark) {
                    change.setOrderMaterialActId("见备忘")
                            .setRemark("新增班组库存量，orderMaterialActIds=" + orderMaterialActIds);
                } else {
                    change.setOrderMaterialActId(orderMaterialActIds)
                            .setRemark("新增班组库存量");
                }
                changeList.add(change);
            }
        }
        // 更新的
        List<BuMaterialGroupStock> needUpdateGroupStockList = groupStockList.stream()
                .filter(groupStock -> null != groupStock.getNeedUpdate() && groupStock.getNeedUpdate())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needUpdateGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.updateList(batchSub);
            }

            for (BuMaterialGroupStock groupStock : needUpdateGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeOrderMaterialActIdSet().size() > 1;
                String orderMaterialActIds = String.join(",", groupStock.getRelativeOrderMaterialActIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason(sourceScene + "修改实际消耗量，更新班组库存量")
                        .setChangeType(2)
                        .setOldValue(groupStock.getOldAmount().doubleValue())
                        .setNewValue(groupStock.getAmount().doubleValue())
                        .setTrainNo(null == order ? null : order.getTrainNo())
                        .setOrderCode(null == order ? null : order.getOrderCode())
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null);
                if (noteInRemark) {
                    change.setOrderMaterialActId("见备忘")
                            .setRemark("更新班组库存量，orderMaterialActIds=" + orderMaterialActIds);
                } else {
                    change.setOrderMaterialActId(orderMaterialActIds)
                            .setRemark("更新班组库存量");
                }
                changeList.add(change);
            }
        }

        // 保存库存变动记录
        if (CollectionUtils.isNotEmpty(changeList)) {
            buMaterialStockChangeService.addChangeList(changeList);
        }
    }

    @Override
    public HSSFWorkbook exportGroupStock(BuMaterialGroupStockQueryVO queryVO) {
        IPage<BuMaterialGroupStock> page = buMaterialGroupStockMapper.selectPageByQueryVO(new Page<>(-1, -1), queryVO);
        List<BuMaterialGroupStock> groupStockList = page.getRecords();
        Map<String, List<BuMaterialGroupStock>> groupStockMap = groupStockList.stream().collect(Collectors.groupingBy(BuMaterialGroupStock::getGroupId));

        return getExcel(groupStockMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setMaterialAttribute(GroupStockAtrrVO stockAtrrVO) throws Exception {
        List<String> idList = Arrays.asList(stockAtrrVO.getIds().split(","));
        idList.forEach(id -> buMaterialGroupStockMapper.updateById(new BuMaterialGroupStock()
                .setId(id).setSystemId(stockAtrrVO.getSystemId())
                .setWorkstationId(stockAtrrVO.getWorkstationId())
                .setUseCategory(stockAtrrVO.getUseCategory())));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recoverMaterialAttribute(String ids, String groupIds) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        List<String> groupIdList = Arrays.asList(groupIds.split(","));

        //物资类型id
        Map<String, String> materialGroupStockMap = buMaterialGroupStockMapper.selectList(Wrappers.<BuMaterialGroupStock>lambdaQuery()
                .select(BuMaterialGroupStock::getMaterialTypeId, BuMaterialGroupStock::getId).in(BuMaterialGroupStock::getId, idList))
                .stream().collect(Collectors.toMap(BuMaterialGroupStock::getMaterialTypeId, BuMaterialGroupStock::getId, (k1, k2) -> k2));

        List<String> materialTypeIdList = new ArrayList<>(materialGroupStockMap.keySet());
        //物资分类
        Map<String, Integer> categoryMap = buMaterialTypeMapper.selectList(Wrappers.<BuMaterialType>lambdaQuery()
                .select(BuMaterialType::getId, BuMaterialType::getCategory1)
                .in(BuMaterialType::getId, materialTypeIdList)).stream()
                .collect(Collectors.toMap(BuMaterialType::getId, BuMaterialType::getCategory1, (k1, k2) -> k2));
        //必换件
        Map<String, BuMaterialMustList> materialMustListMap = buMaterialMustListMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery()
                .select(BuMaterialMustList::getMaterialTypeId, BuMaterialMustList::getWorkstationId, BuMaterialMustList::getSysId, BuMaterialMustList::getGroupId)
                .in(BuMaterialMustList::getMaterialTypeId, materialTypeIdList)
                .in(BuMaterialMustList::getGroupId, groupIdList))
                .stream().collect(Collectors.toMap((item) -> item.getMaterialTypeId() + item.getGroupId(), (item) -> item, (k1, k2) -> k2));

        //更新物资属性
        for (int i = 0; i < materialTypeIdList.size(); i++) {
            String systemId = "";
            String workstationId = "";
            Integer category = null;
            String groupId = groupIdList.get(i);
            String materialTypeId = materialTypeIdList.get(i);
            if (materialMustListMap.size() > 0) {
                BuMaterialMustList materialMustList = materialMustListMap.get(materialTypeId + groupId);
                if (materialMustList != null) {
                    systemId = materialMustList.getSysId();
                    workstationId = materialMustList.getWorkstationId();
                    category = 1;
                }
            }
            buMaterialGroupStockMapper.updateById(new BuMaterialGroupStock().setId(materialGroupStockMap.get(materialTypeId))
                    .setUseCategory(category != null ? category : categoryMap.get(materialTypeId))
                    .setSystemId(systemId)
                    .setWorkstationId(workstationId));
        }
        return true;
    }

    /**
     * @see BuMaterialGroupStockService#transGroupStockToTurnover(String, BigDecimal)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String transGroupStockToTurnover(String groupStockId, BigDecimal transAmount) {
        if (BigDecimal.ZERO.compareTo(transAmount) == 0) {
            throw new JeecgBootException("转入数量不能为0");
        }

        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = null == sysUser ? "admin" : sysUser.getId();
        String username = null == sysUser ? "admin" : sysUser.getUsername();
        String realName = null == sysUser ? "admin" : sysUser.getRealname();

        // 查询班组库存
        BuMaterialGroupStock groupStock = buMaterialGroupStockMapper.selectById(groupStockId);
        if (null == groupStock) {
            throw new JeecgBootException("班组库存不存在");
        }
        groupStock.setOldAmount(groupStock.getAmount());
        BigDecimal amount = groupStock.getAmount();
        if (BigDecimal.ZERO.compareTo(amount) == 0) {
            throw new JeecgBootException("班组库存量为0");
        }
        if (amount.compareTo(transAmount) < 0) {
            throw new JeecgBootException(String.format("班组库存量为%s，少于%s",
                    amount.stripTrailingZeros().toPlainString(),
                    transAmount.stripTrailingZeros().toPlainString()));
        }

        // 减去班组已占用的数量
        deleteGroupStockUsedAmount(Collections.singletonList(groupStock), groupStock.getGroupId(), null, null, null);
        BigDecimal usableAmount = groupStock.getUsableAmount();
        if (BigDecimal.ZERO.compareTo(usableAmount) == 0) {
            throw new JeecgBootException("班组库存可用库存量为0");
        }
        if (usableAmount.compareTo(transAmount) < 0) {
            throw new JeecgBootException(String.format("班组可用库存量为%s，少于%s",
                    usableAmount.stripTrailingZeros().toPlainString(),
                    transAmount.stripTrailingZeros().toPlainString()));
        }

        // 转换成周转件
        String materialTypeId = groupStock.getMaterialTypeId();
        BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);
        // 规格拼接
        String description;
        if (StringUtils.isNotBlank(materialType.getSpec())) {
            description = materialType.getName() + "-[规格：" + materialType.getSpec() + "]";
        } else {
            description = materialType.getName();
        }
        BigDecimal price = materialType.getPrice();
        BigDecimal useAmountPrice = price.multiply(transAmount).setScale(8, BigDecimal.ROUND_HALF_UP);
        BuMaterialTurnoverNew turnover = new BuMaterialTurnoverNew()
                .setId(UUIDGenerator.generate())
                .setMaterialTypeId(materialTypeId)
                .setName(description)
                .setUnit(materialType.getUnit())
                .setPrice(price)
                .setTaxRate(BigDecimal.ONE)
                .setTaxPrice(price)
                .setUseAmount(transAmount)
                .setUseAmountPrice(useAmountPrice)
                .setOrderCode(null)
                .setOutOrderCode(null)
                .setWarehouseCode(null)
                .setWarehouseId(null)
                .setUseTime(now)
                .setSystemShortName(null)
                .setSystemId(null)
                .setFirstUsePlanName(null)
                .setFirstUsePlanId(null)
                .setProgramId(null)
                .setServiceYearRemark(null)
                .setServiceYear(10D)
                .setRemark(String.format("%s于%s操作，班组库存转入周转件", realName, DateUtils.datetimeFormat.get().format(now)))
                .setCreateTime(now)
                .setCreateBy(username)
                .setUpdateTime(now)
                .setUpdateBy(username);
        buMaterialTurnoverNewMapper.insert(turnover);

        // 减少班组库存记录
        BigDecimal newAmount = amount.subtract(transAmount);
        groupStock.setAmount(newAmount);
        buMaterialGroupStockMapper.updateListAmount(Collections.singletonList(groupStock));
        // 记录库存变动
        BuMaterialStockChange change = new BuMaterialStockChange()
                .setId(UUIDGenerator.generate())
                .setStockType(4)
                .setStockId(groupStock.getId())
                .setWarehouseId(groupStock.getWarehouseId())
                .setMaterialTypeId(groupStock.getMaterialTypeId())
                .setTradeNo(groupStock.getTradeNo())
                .setChangeTime(now)
                .setChangeReason("班组库存转入周转件，减少班组库存量")
                .setChangeType(3)
                .setOldValue(groupStock.getOldAmount().doubleValue())
                .setNewValue(groupStock.getAmount().doubleValue())
                .setTrainNo(groupStock.getTrainNo())
                .setOrderCode(null)
                .setAssignDetailId(null)
                .setReturnedDetailId(null)
                .setOrderMaterialActId(null)
                .setOperationUser(userId)
                .setRemark(String.format("%s于%s操作", realName, DateUtils.datetimeFormat.get().format(now)));
        // 保存库存变动记录
        buMaterialStockChangeService.addChangeList(Collections.singletonList(change));

        return String.format("%s %s %s转为周转件，请在周转件管理查看并设置其他信息",
                transAmount.stripTrailingZeros().toPlainString(),
                materialType.getUnit(),
                materialType.getName());
    }


    private List<GroupStockUsed> getMaterialGroupStockUsedList(List<BuWorkOrderMaterial> allOrderMaterialList) {
        if (CollectionUtils.isEmpty(allOrderMaterialList)) {
            return new ArrayList<>();
        }

        List<GroupStockUsed> usedList = new ArrayList<>();

        Map<String, List<BuWorkOrderMaterial>> materialTypeIdOrderMaterialListMap = allOrderMaterialList.stream()
                .collect(Collectors.groupingBy(BuWorkOrderMaterial::getMaterialTypeId));
        for (Map.Entry<String, List<BuWorkOrderMaterial>> materialTypeIdOrderMaterialListEntry : materialTypeIdOrderMaterialListMap.entrySet()) {
            String materialTypeId = materialTypeIdOrderMaterialListEntry.getKey();
            List<BuWorkOrderMaterial> orderMaterialList = materialTypeIdOrderMaterialListEntry.getValue();

            List<String> needDispatchOrderMaterialIdList = new ArrayList<>();
            for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                if (1 == orderMaterial.getNeedDispatchin()) {
                    // 需要发料的工单物料：已领料的应该减去、未领料的不应该减去
                    needDispatchOrderMaterialIdList.add(orderMaterial.getId());
                } else {
                    // 无需发料的工单物料：全部应该减去
                    List<BuWorkOrderMaterialActs> actList = orderMaterial.getActList();
                    if (CollectionUtils.isNotEmpty(actList)) {
                        // 有实际消耗的，使用实际消耗列表
                        for (BuWorkOrderMaterialActs act : actList) {
                            addToUsedList(usedList, materialTypeId, act.getGroupStockTrainNo(), act.getTradeNo(), act.getOrderCode(), act.getActAmount(), 2);
                        }
                    } else {
                        // 工单已提交且没有实际消耗的工单物料，不需要减去，原因如下：
                        // 这个工单物料计划消耗但实际没消耗就提交了，此时说明物料不消耗
                        // 如果确实需要的消耗的话，工单提交后会被退回，工单状态变回填报中，此时再进行预占用
                        if (orderMaterial.getOrderStatus() != 3) {
                            // 没有实际消耗，使用计划数量
                            addToUsedList(usedList, materialTypeId, orderMaterial.getTrainNo(), null, orderMaterial.getOrderCode(), orderMaterial.getAmount(), 1);
                        }
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(needDispatchOrderMaterialIdList)) {
                List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListForGroupStockCount(needDispatchOrderMaterialIdList);
                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    addToUsedList(usedList, materialTypeId, assignDetail.getTrainNo(), assignDetail.getTradeNo(), assignDetail.getSourceOrderCode(), assignDetail.getAmount(), 1);
                }
            }
        }

        return usedList;
    }

    private void addToUsedList(List<GroupStockUsed> usedList, String materialTypeId, String trainNo, String tradeNo, String orderCode, Double amount, Integer usedType) {
        if (null == usedList) {
            usedList = new ArrayList<>();
        }
        BigDecimal amountBigDecimal = null == amount ? BigDecimal.ZERO : BigDecimal.valueOf(amount);

        boolean matched = false;
        for (GroupStockUsed used : usedList) {
            boolean sameMaterial = materialTypeId.equals(used.getMaterialTypeId());
            boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(used.getTrainNo()) : trainNo.equals(used.getTrainNo());
            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(used.getTradeNo()) : tradeNo.equals(used.getTradeNo());
            boolean sameOrder = orderCode.equals(used.getOrderCode());

            if (sameMaterial && sameTrainNo && sameTradeNo && sameOrder) {
                BigDecimal usedAmount = used.getAmount();
                used.setAmount(usedAmount.add(amountBigDecimal));

                matched = true;
                break;
            }
        }
        if (!matched) {
            GroupStockUsed used = new GroupStockUsed()
                    .setMaterialTypeId(materialTypeId)
                    .setTrainNo(trainNo)
                    .setTradeNo(tradeNo)
                    .setOrderCode(orderCode)
                    .setAmount(amountBigDecimal)
                    .setUsedType(usedType);
            usedList.add(used);
        }
    }

    private void decreaseGroupStock(BuMaterialGroupStock groupStock, String usedOrderCode, BigDecimal usedAmount, Integer usedType) {
        if (null == groupStock) {
            return;
        }
        if (BigDecimal.ZERO.compareTo(usedAmount) >= 0) {
            return;
        }
        String usedTypeString = "";
        if (1 == usedType) {
            usedTypeString = "预";
        }

        BigDecimal usableAmount = groupStock.getUsableAmount();
        String usedDetailInfo = groupStock.getUsedDetailInfo();

        groupStock.setUsableAmount(usableAmount.subtract(usedAmount));
        if (StringUtils.isNotBlank(usedDetailInfo)) {
            groupStock.setUsedDetailInfo(usedDetailInfo + "，" + "工单" + usedOrderCode + usedTypeString + "占用" + usedAmount.stripTrailingZeros().toString());
        } else {
            groupStock.setUsedDetailInfo("工单" + usedOrderCode + usedTypeString + "占用" + usedAmount.stripTrailingZeros().toString());
        }
    }

    private HSSFWorkbook getExcel(Map<String, List<BuMaterialGroupStock>> groupStockMap) {
        String[] excelHeader = {
                "序号",
                "  物料编码  ",
                "            物料描述                ",
                "单位",
                "上月结余数量",
                "当前暂存数量",
        };
        Map<String, String> sheetNameMap = getSheetNameMap();
        List<String> excelHeaderList = Arrays.asList(excelHeader);
        HSSFWorkbook workbook = new HSSFWorkbook();
        groupStockMap.keySet().forEach(key -> {
            HSSFSheet sheet = workbook.createSheet(sheetNameMap.get(key));
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
            List<BuMaterialGroupStock> groupStockList = groupStockMap.get(key);
            for (BuMaterialGroupStock groupStock : groupStockList) {
                if (null != groupStock) {
                    HSSFRow row = sheet.createRow(groupStockList.indexOf(groupStock) + 1);
                    row.createCell(0).setCellValue(groupStockList.indexOf(groupStock) + 1);
                    row.createCell(1).setCellValue(groupStock.getMaterialTypeCode());
                    row.createCell(2).setCellValue(groupStock.getMaterialTypeName() + "-[规格：" + groupStock.getSpec() + "]");
                    row.createCell(3).setCellValue(groupStock.getUnit());
                    row.createCell(4).setCellValue(null == groupStock.getAmount() ? "" : groupStock.getAmount().toString());
                    row.createCell(5).setCellValue("");
                }
            }
            // 设置自动列宽
            //  ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
            ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());
        });
        return workbook;
    }

    private Map<String, String> getSheetNameMap() {
        Map<String, String> sheetNameMap = new HashMap<>();
        sheetNameMap.put("tsgb", "调试工班");
        sheetNameMap.put("cggb", "车钩工班");
        sheetNameMap.put("zdgb", "制动工班");
        sheetNameMap.put("ldgb", "轮对工班");
        sheetNameMap.put("ctcmgb", "车体车门工班");
        sheetNameMap.put("zxjgb", "转向架工班");
        sheetNameMap.put("dzdqgb", "电子电气工班");
        sheetNameMap.put("zzgb", "总装工班");

        return sheetNameMap;
    }
}
