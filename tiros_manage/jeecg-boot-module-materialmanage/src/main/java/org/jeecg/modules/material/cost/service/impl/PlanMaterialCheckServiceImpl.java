package org.jeecg.modules.material.cost.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.rpt.service.MaterialRptService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.apply.mapper.BuWorkOrderForMaterialMapper;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.material.cost.bean.BuWorkOrderMaterialActs;
import org.jeecg.modules.material.cost.bean.bo.TradeNoAmountDiff;
import org.jeecg.modules.material.cost.bean.vo.CheckQueryVO;
import org.jeecg.modules.material.cost.mapper.BuWorkOrderMaterialActsMaterialMapper;
import org.jeecg.modules.material.cost.mapper.BuWorkOrderMaterialMaterialMapper;
import org.jeecg.modules.material.cost.service.PlanMaterialCheckService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.jeecg.modules.material.stock.service.BuMaterialGroupStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p>
 * 列计划物料核实 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/15
 */
@Slf4j
@Service
public class PlanMaterialCheckServiceImpl implements PlanMaterialCheckService {

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
    private BuMaterialGroupStockService buMaterialGroupStockService;
    @Resource
    private MaterialRptService materialRptService;


    /**
     * @see PlanMaterialCheckService#pageOrderMaterial(CheckQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkOrderMaterial> pageOrderMaterial(CheckQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buWorkOrderMaterialMaterialMapper.selectPageForCostCheck(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see PlanMaterialCheckService#getOrderMaterialDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkOrderMaterial getOrderMaterialDetail(String orderMaterialId) throws Exception {
        BuWorkOrderMaterial orderMaterial = buWorkOrderMaterialMaterialMapper.selectOrderMaterialById(orderMaterialId);
        if (null == orderMaterial) {
            throw new JeecgBootException("工单物料不存在");
        }

        LambdaQueryWrapper<BuWorkOrderMaterialActs> actWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                .eq(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterialId);
        List<BuWorkOrderMaterialActs> actList = buWorkOrderMaterialActsMaterialMapper.selectList(actWrapper);
        orderMaterial.setActList(actList);

        return orderMaterial;
    }

    /**
     * @see PlanMaterialCheckService#saveOrderMaterial(BuWorkOrderMaterial)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrderMaterial(BuWorkOrderMaterial orderMaterial) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

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

        // 查询旧的实际消耗
        Map<String, BuWorkOrderMaterialActs> idOldActMap = new HashMap<>();
        if (!isAdd) {
            BuWorkOrderMaterial oldOrderMaterial = buWorkOrderMaterialMaterialMapper.selectOrderMaterialById(orderMaterialId);
            if (null == oldOrderMaterial) {
                throw new JeecgBootException("工单物料不存在");
            }
            LambdaQueryWrapper<BuWorkOrderMaterialActs> actWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                    .eq(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterialId);
            List<BuWorkOrderMaterialActs> oldActList = buWorkOrderMaterialActsMaterialMapper.selectList(actWrapper);
            oldActList.forEach(oldAct -> idOldActMap.put(oldAct.getId(), oldAct));
        }
        // 新的实际消耗
        List<BuWorkOrderMaterialActs> newActList = null == orderMaterial.getActList() ? new ArrayList<>() : orderMaterial.getActList();
        for (BuWorkOrderMaterialActs newAct : newActList) {
            if (StringUtils.isBlank(newAct.getId())) {
                newAct.setId(UUIDGenerator.generate());
            }
        }

        // 检查工单是否已关闭
        BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(orderMaterial.getOrderId());
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        if (4 != order.getOrderStatus()) {
            throw new JeecgBootException("工单[" + order.getOrderCode() + "]未关闭，请关闭后再核实");
        }

        // 查询物资类型
        BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);

        // 查询班组
        String groupId = order.getGroupId();
        List<Map<String, Object>> groupMapList = buWorkOrderMaterialMaterialMapper.selectGroupList(groupId);
        String groupName = (String) groupMapList.get(0).get("name");

        // 班组库存量减去“已核实”到“未关闭”的工单中的物料数量
        // 暂停状态的工单不需要减去：暂停时有可能原始状态为未核实不需要减去；重新激活时会回归原始状态根据原始状态计算
        List<BuWorkOrderMaterial> needUsedOrderMaterialList = buWorkOrderMaterialMaterialMapper.selectNotMaterialApplyOrderListByGroupIdAndOrderStatus(groupId, TirosConstant.GROUP_STOCK_NEED_COUNT_USED_ORDER_STATUS);

        // 查询班组库存
        List<String> groupStockIdList = new ArrayList<>();
        for (BuWorkOrderMaterialActs act : idOldActMap.values()) {
            String groupStockId = act.getGroupStockId();
            if (StringUtils.isNotBlank(groupStockId) && !groupStockIdList.contains(groupStockId)) {
                groupStockIdList.add(groupStockId);
            }
        }
        for (BuWorkOrderMaterialActs act : newActList) {
            String groupStockId = act.getGroupStockId();
            if (StringUtils.isNotBlank(groupStockId) && !groupStockIdList.contains(groupStockId)) {
                groupStockIdList.add(groupStockId);
            }
        }
        List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectBatchIds(groupStockIdList);
        for (BuMaterialGroupStock groupStock : groupStockList) {
            groupStock.setOldAmount(groupStock.getAmount())
                    .setRelativeOrderMaterialActIdSet(new HashSet<>());
        }
        // 获得班组库存可用量
        buMaterialGroupStockService.deleteGroupStockUsedAmount(groupStockList, groupId, needUsedOrderMaterialList, orderMaterial.getId(), null);

        // 对比更新实际消耗，并根据数量变化处理班组库存
        for (BuWorkOrderMaterialActs newAct : newActList) {
            String actId = newAct.getId();
            String tradeNo = newAct.getTradeNo();
            double newActAmount = newAct.getActAmount();

            // 此处不一定能找到旧的实际消耗：因为可以新增实际消耗
            BuWorkOrderMaterialActs oldAct = idOldActMap.get(actId);
            double oldActAmount = 0;
            if (null != oldAct) {
                oldActAmount = oldAct.getActAmount();
            }

            double diff = newActAmount - oldActAmount;
            if (diff > 0) {
                // 实际消耗增加了，需要减班组库存
                double usableAmountSum = groupStockList.stream()
                        .map(BuMaterialGroupStock::getUsableAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .doubleValue();
                if (usableAmountSum < diff) {
                    throw new JeecgBootException(groupName + "物料" + materialTypeId + "当前可用数量为" + usableAmountSum + "，不足增加的" + diff + "（原" + oldActAmount + " -> 新" + newActAmount + "）");
                } else {
                    double totalAmount = diff;
                    for (BuMaterialGroupStock groupStock : groupStockList) {
                        double groupUsableAmount = groupStock.getUsableAmount().doubleValue();
                        double groupAmount = groupStock.getAmount().doubleValue();
                        if (groupUsableAmount > 0D) {
                            double amount = Math.min(groupUsableAmount, diff);
                            groupStock.setUsableAmount(BigDecimal.valueOf(groupUsableAmount - amount))
                                    .setAmount(BigDecimal.valueOf(groupAmount - amount));
                            groupStock.getRelativeOrderMaterialActIdSet().add(actId);
                            if (null == groupStock.getNeedAdd() || !groupStock.getNeedAdd()) {
                                groupStock.setNeedUpdate(true);
                            }

                            totalAmount = totalAmount - amount;
                            if (totalAmount <= 0D) {
                                break;
                            }
                        }
                    }
                }
            } else if (diff < 0) {
                diff = Math.abs(diff);
                // 实际消耗减少了，需要加班组库存
                if (CollectionUtils.isNotEmpty(groupStockList)) {
                    BuMaterialGroupStock groupStock = groupStockList.get(0);
                    groupStock.setAmount(groupStock.getAmount().add(BigDecimal.valueOf(diff)))
                            .setUsableAmount(groupStock.getUsableAmount().add(BigDecimal.valueOf(diff)));
                    groupStock.getRelativeOrderMaterialActIdSet().add(actId);
                    if (null == groupStock.getNeedAdd() || !groupStock.getNeedAdd()) {
                        groupStock.setNeedUpdate(true);
                    }
                } else {
                    BigDecimal price = null == newAct.getPrice() ? materialType.getPrice() : newAct.getPrice();
                    BuMaterialGroupStock groupStock = new BuMaterialGroupStock()
                            .setId(UUIDGenerator.generate())
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(BigDecimal.valueOf(diff))
                            .setPrice(price)
                            .setApplyId(null)
                            .setApplyDetailId(null)
                            .setGroupId(groupId)
                            .setAssignDetailId(null)
                            .setTradeNo(tradeNo)
                            .setCompanyId(order.getCompanyId())
                            .setWorkshopId(order.getWorkshopId())
                            .setLineId(order.getLineId())
                            .setSystemId(null)
                            .setWorkstationId(null)
                            .setUseCategory(useCategory != null ? useCategory : materialType.getCategory1())
                            .setTrainNo(trainNo)
                            .setOldAmount(BigDecimal.ZERO)
                            .setUsableAmount(BigDecimal.valueOf(diff))
                            .setNeedAdd(true);
                    groupStock.getRelativeOrderMaterialActIdSet().add(actId);
                    groupStockList.add(groupStock);
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
            buWorkOrderMaterialMaterialMapper.insert(orderMaterial);
        }
        // 插入新的实际消耗
        if (CollectionUtils.isNotEmpty(newActList)) {
            for (BuWorkOrderMaterialActs act : newActList) {
                act.setOrderMaterialId(orderMaterialId);
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
        buMaterialGroupStockService.updateGroupStockByGroupStockListForMaterialCostCheck(groupStockList, order, now, "列计划物料核实", userId);

        // 开线程重新统计车辆物料统计数据
        rebuildMaterialRptDataByNewThread(Collections.singletonList(order.getTrainNo()), "列计划物料核实");

        return true;
    }

    private List<TradeNoAmountDiff> getTradeNoAmountDiffList(List<BuWorkOrderMaterialActs> oldActList, List<BuWorkOrderMaterialActs> newActList) {
        if (null == oldActList) {
            oldActList = new ArrayList<>();
        }
        if (null == newActList) {
            newActList = new ArrayList<>();
        }

        Set<String> tradeNoSet = new HashSet<>();
        for (BuWorkOrderMaterialActs act : oldActList) {
            tradeNoSet.add(act.getTradeNo());
        }
        for (BuWorkOrderMaterialActs act : newActList) {
            tradeNoSet.add(act.getTradeNo());
        }

        List<TradeNoAmountDiff> diffList = new ArrayList<>();
        for (String tradeNo : tradeNoSet) {
            BigDecimal oldAmount = BigDecimal.ZERO;
            BigDecimal newAmount = BigDecimal.ZERO;
            Set<String> relativeOrderMaterialActIdSet = new HashSet<>();

            for (BuWorkOrderMaterialActs act : oldActList) {
                boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(act.getTradeNo()) : tradeNo.equals(act.getTradeNo());
                if (sameTradeNo) {
                    oldAmount = oldAmount.add(BigDecimal.valueOf(act.getActAmount()));
                    relativeOrderMaterialActIdSet.add(act.getId());
                }
            }
            BigDecimal newPrice = BigDecimal.ZERO;
            for (BuWorkOrderMaterialActs act : newActList) {
                boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(act.getTradeNo()) : tradeNo.equals(act.getTradeNo());
                if (sameTradeNo) {
                    newAmount = newAmount.add(BigDecimal.valueOf(act.getActAmount()));
                    relativeOrderMaterialActIdSet.add(act.getId());

                    if (null != act.getPrice()) {
                        newPrice = act.getPrice();
                    }
                }
            }

            if (newAmount.compareTo(oldAmount) != 0) {
                TradeNoAmountDiff diff = new TradeNoAmountDiff()
                        .setTradeNo(tradeNo)
                        .setNewAmount(newAmount)
                        .setNewPrice(newPrice)
                        .setOldAmount(oldAmount)
                        .setDiffAmount(newAmount.subtract(oldAmount))
                        .setRelativeOrderMaterialActIdSet(relativeOrderMaterialActIdSet);
                diffList.add(diff);
            }
        }
        return diffList;
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
