package org.jeecg.modules.dispatch.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.dispatch.workorder.bean.*;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuOrderMaterialQueryVO;
import org.jeecg.modules.dispatch.workorder.mapper.*;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderMaterialService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单物料 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Service
public class BuWorkOrderMaterialServiceImpl extends ServiceImpl<BuWorkOrderMaterialMapper, BuWorkOrderMaterial> implements BuWorkOrderMaterialService {

    @Resource
    private BuWorkOrderMaterialMapper buWorkOrderMaterialMapper;
    @Resource
    private BuMaterialApplyDetailDispatchMapper buMaterialApplyDetailDispatchMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;
    @Resource
    private BuWorkOrderTaskMapper buWorkOrderTaskMapper;
    @Resource
    private BuWorkOrderMaterialActsMapper buWorkOrderMaterialActsMapper;


    /**
     * @see BuWorkOrderMaterialService#listOrderMaterial(BuOrderMaterialQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrderMaterial> listOrderMaterial(BuOrderMaterialQueryVO queryVO) throws Exception {
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectListByQueryVO(queryVO);
        // 设置来源库位和托盘名称信息
        setLocationWarehouseNamesAndPalletNames(orderMaterialList);
        return orderMaterialList;
    }

    /**
     * @see BuWorkOrderMaterialService#saveOrderMaterial(BuWorkOrderMaterial)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrderMaterial(BuWorkOrderMaterial orderMaterial) throws Exception {
        setDefaultProperty(orderMaterial);

        if (StringUtils.isBlank(orderMaterial.getId())) {
            buWorkOrderMaterialMapper.insert(orderMaterial);
        } else {
            BuWorkOrderMaterial dbOrderMaterial = buWorkOrderMaterialMapper.selectById(orderMaterial.getId());
            if (null == dbOrderMaterial) {
                buWorkOrderMaterialMapper.insert(orderMaterial);
            } else {
                buWorkOrderMaterialMapper.updateById(orderMaterial);
            }
        }

        return true;
    }

    /**
     * @see BuWorkOrderMaterialService#saveOrderMaterialAct(BuWorkOrderMaterial)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrderMaterialAct(BuWorkOrderMaterial orderMaterial) throws Exception {
        // 保存工单物料
        saveOrderMaterial(orderMaterial);

        // 删除旧的实际消耗
        LambdaQueryWrapper<BuWorkOrderMaterialActs> deleteWrapper = new LambdaQueryWrapper<BuWorkOrderMaterialActs>()
                .eq(BuWorkOrderMaterialActs::getOrderMaterialId, orderMaterial.getId());
        buWorkOrderMaterialActsMapper.delete(deleteWrapper);

        // 插入实际消耗
        List<BuWorkOrderMaterialActs> actsList = orderMaterial.getMaterialActsList();
        actsList.removeIf(item -> item.getActAmount() <= 0D);
        if (CollectionUtils.isNotEmpty(actsList)) {
            for (BuWorkOrderMaterialActs acts : actsList) {
                if (StringUtils.isBlank(acts.getId())) {
                    acts.setId(UUIDGenerator.generate());
                }
            }

            List<List<BuWorkOrderMaterialActs>> batchSubList = DatabaseBatchSubUtil.batchSubList(actsList);
            for (List<BuWorkOrderMaterialActs> batchSub : batchSubList) {
                buWorkOrderMaterialActsMapper.insertList(batchSub);
            }
        }

        // 计算工单物料实际消耗数量
        double sumActAmount = actsList.stream()
                .mapToDouble(BuWorkOrderMaterialActs::getActAmount)
                .sum();
        // 更新工单物料
        orderMaterial.setActAmount(sumActAmount);
        buWorkOrderMaterialMapper.updateById(orderMaterial);

        return true;
    }

    /**
     * @see BuWorkOrderMaterialService#updateOrderMaterialList(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrderMaterialList(List<BuWorkOrderMaterial> orderMaterialList) throws Exception {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return true;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            buWorkOrderMaterialMapper.updateById(orderMaterial);
        }

        return true;
    }

    /**
     * @see BuWorkOrderMaterialService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        checkIsGenOrder(idList);

        buWorkOrderMaterialMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuWorkOrderMaterialService#listOrderMustMaterialAsApplyDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialApplyDetail> listOrderMustMaterialAsApplyDetail(String orderId) throws Exception {
        BuWorkOrder order = buWorkOrderMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        String planId = order.getPlanId();
        String groupId = order.getGroupId();

        List<BuMaterialApplyDetail> resultList = new ArrayList<>();
        List<BuWorkOrderMaterial> mustMaterialList = buWorkOrderMaterialMapper.selectMustMaterialListByOrderId(orderId);

        if (CollectionUtils.isNotEmpty(mustMaterialList)) {
            for (BuWorkOrderMaterial mustMaterial : mustMaterialList) {
                BuMaterialApplyDetail applyDetail = new BuMaterialApplyDetail();

                // 必换件工单物料的数量
                BigDecimal amount = null == mustMaterial.getAmount() ? BigDecimal.ZERO : BigDecimal.valueOf(mustMaterial.getAmount());

                // 查询必换件对应的发料工单的领用明细
                List<BuMaterialApplyDetail> materialOrderApplyDetailListWithAssign = buMaterialApplyDetailDispatchMapper.selectMaterialOrderApplyDetailListWithAssign(planId, groupId, mustMaterial.getMaterialTypeId());
                if (CollectionUtils.isNotEmpty(materialOrderApplyDetailListWithAssign)) {
                    // 一个列计划、一个工班、一个必换物资类型，只会对应一个发料工单的工单物料只会有一个对应的领料明细（因为在生成发料工单时做了去重和合并）
                    BuMaterialApplyDetail materialOrderApplyDetail = materialOrderApplyDetailListWithAssign.get(0);

                    // 领用状态：有对应的发料工单物料，使用对应的领用明细的状态
                    BeanUtils.copyProperties(materialOrderApplyDetail, applyDetail);
                    // 发放数量：根据领用状态判断，已领用时等于领用数量（必换件工单物料的数量），未领用等于0
                    Integer status = applyDetail.getStatus();
                    BigDecimal receive = null == status || 0 == status ? BigDecimal.ZERO : amount;
                    // 领用数量：必换件工单物料的数量
                    applyDetail.setAmount(amount)
                            .setReceive(receive);

                    // 设置来源库位和托盘名称信息
                    List<BuMaterialAssignDetail> assignDetailList = materialOrderApplyDetail.getAssignDetailList();
                    if (CollectionUtils.isNotEmpty(assignDetailList)) {
                        List<String> sourceLocationNameList = new ArrayList<>();
                        List<String> palletNameList = new ArrayList<>();

                        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                            String sourceLocationName = TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName());
                            String palletName = assignDetail.getPalletName();
                            if (!sourceLocationNameList.contains(sourceLocationName)) {
                                sourceLocationNameList.add(sourceLocationName);
                            }
                            if (!palletNameList.contains(palletName)) {
                                palletNameList.add(palletName);
                            }
                        }

                        applyDetail.setSourceLocationName(String.join(", ", sourceLocationNameList))
                                .setPalletName(String.join(", ", palletNameList));
                    }

                    applyDetail.setOrderMaterialId(mustMaterial.getId())
                            .setMaterialTypeCode(mustMaterial.getCode())
                            .setMaterialTypeName(mustMaterial.getName())
                            .setMaterialTypeSpec(mustMaterial.getSpec())
                            .setMaterialTypeUnit(mustMaterial.getUnit());

                    // 设置发料工单领用明细的id等属性为空，避免发料领料时再次操作到这些数据
                    applyDetail.setId(null)
                            .setApplyId(null);
                } else {
                    applyDetail.setMaterialTypeId(mustMaterial.getMaterialTypeId())
                            .setAmount(amount)
                            // 发放数量：没有有对应的发料工单物料，为0
                            .setReceive(BigDecimal.ZERO)
                            // 领用状态：没有有对应的发料工单物料，为0初始
                            .setStatus(0)
                            .setUnitPrice(null)
                            .setUseCategory(mustMaterial.getUseCategory())
                            .setOrderMaterialId(mustMaterial.getId())
                            .setMaterialTypeCode(mustMaterial.getCode())
                            .setMaterialTypeName(mustMaterial.getName())
                            .setMaterialTypeSpec(mustMaterial.getSpec())
                            .setMaterialTypeUnit(mustMaterial.getUnit())
                            .setSourceLocationName(null)
                            .setPalletName(null);
                }
                resultList.add(applyDetail);
            }
        }

        return resultList;
    }


    private void setLocationWarehouseNamesAndPalletNames(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        Set<String> orderMaterialIdSet = orderMaterialList.stream()
                .map(BuWorkOrderMaterial::getId)
                .collect(Collectors.toSet());
        List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailDispatchMapper.selectListWithAssignByOrderMaterialIdList(new ArrayList<>(orderMaterialIdSet));
        if (CollectionUtils.isEmpty(allApplyDetailList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            List<String> sourceLocationNameList = new ArrayList<>();
            List<String> palletNameList = new ArrayList<>();
            List<String> sourceLocationAndPalletNameList = new ArrayList<>();

            List<BuMaterialApplyDetail> applyDetailList = allApplyDetailList.stream()
                    .filter(applyDetail -> orderMaterial.getId().equals(applyDetail.getOrderMaterialId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(applyDetailList)) {
                continue;
            }

            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                List<BuMaterialAssignDetail> assignDetailList = applyDetail.getAssignDetailList();
                if (CollectionUtils.isEmpty(assignDetailList)) {
                    continue;
                }

                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    String sourceLocationName = TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName());
                    String palletName = assignDetail.getPalletName();
                    if (!sourceLocationNameList.contains(sourceLocationName)) {
                        sourceLocationNameList.add(sourceLocationName);
                    }
                    if (StringUtils.isNotBlank(palletName)) {
                        if (!palletNameList.contains(palletName)) {
                            palletNameList.add(palletName);
                        }
                    }
                    sourceLocationAndPalletNameList.add(sourceLocationName + " | " + assignDetail.getAmount() + " | " + (StringUtils.isNotBlank(palletName) ? palletName : ""));
                }
            }

            orderMaterial.setSourceLocationName(String.join(", ", sourceLocationNameList))
                    .setPalletName(String.join(", ", palletNameList))
                    .setSourceLocationAndPalletName(String.join(", ", sourceLocationAndPalletNameList));
        }
    }

    private void setDefaultProperty(BuWorkOrderMaterial orderMaterial) {
        if (null == orderMaterial) {
            return;
        }

        if (StringUtils.isBlank(orderMaterial.getOrderId())) {
            BuWorkOrderTask task = buWorkOrderTaskMapper.selectById(orderMaterial.getOrderTaskId());
            orderMaterial.setOrderId(task.getOrderId());
        }
        if (null == orderMaterial.getActAmount()) {
            orderMaterial.setActAmount(0D);
        }
        if (null == orderMaterial.getIsVerify()) {
            orderMaterial.setIsVerify(0);
        }
        if (null == orderMaterial.getOpType()) {
            orderMaterial.setOpType(1);
        }
        if (null == orderMaterial.getIsGenOrder()) {
            orderMaterial.setIsGenOrder(0);
        }
    }

    private void checkIsGenOrder(List<String> orderMaterialIdList) {
        if (CollectionUtils.isEmpty(orderMaterialIdList)) {
            return;
        }

        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialMapper.selectBatchIds(orderMaterialIdList);
        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            if (1 == orderMaterial.getIsGenOrder()) {
                throw new JeecgBootException("工单物料已生成单据，不能删除");
            }
        }
    }

}
