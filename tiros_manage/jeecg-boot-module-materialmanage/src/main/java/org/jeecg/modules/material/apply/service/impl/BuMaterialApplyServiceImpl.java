package org.jeecg.modules.material.apply.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.stock.change.bean.BuMaterialStockChange;
import org.jeecg.common.tiros.stock.change.service.BuMaterialStockChangeService;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;
import org.jeecg.common.tiros.stock.use.service.BuMaterialStockUseService;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.OrderStatusCheckUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.jeecg.modules.material.apply.bean.BuMaterialApply;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderQueryVO;
import org.jeecg.modules.material.apply.bean.vo.AppApplyOrderVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyConfirmVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialApplyQueryVO;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuWorkOrderForMaterialMapper;
import org.jeecg.modules.material.apply.service.BuMaterialApplyService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.mapper.BuMaterialMustListMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.jeecg.modules.material.stock.mapper.BuMaterialStockMapper;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料申请(领用) 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-16
 */
@Slf4j
@Service
public class BuMaterialApplyServiceImpl extends ServiceImpl<BuMaterialApplyMapper, BuMaterialApply> implements BuMaterialApplyService {

    @Resource
    private BuMaterialApplyMapper buMaterialApplyMapper;
    @Resource
    private BuMaterialApplyDetailMapper buMaterialApplyDetailMapper;
    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;
    @Resource
    private BuMaterialGroupStockMapper buMaterialGroupStockMapper;
    @Resource
    private BuMaterialStockMapper buMaterialStockMapper;
    @Resource
    private WarehouseCacheService warehouseCacheService;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private BuWorkOrderForMaterialMapper buWorkOrderForMaterialMapper;
    @Resource
    private WorkflowForwardService workflowForwardService;
    @Resource
    private BuMaterialMustListMapper buMaterialMustListMapper;
    @Resource
    private BuMaterialStockChangeService buMaterialStockChangeService;
    @Resource
    private BuMaterialStockUseService buMaterialStockUseService;


    /**
     * @see BuMaterialApplyService#page(BuMaterialApplyQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialApply> page(BuMaterialApplyQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuMaterialApply> pageResult = new Page<>();

        IPage<String> applyIdPage = buMaterialApplyMapper.selectApplyIdPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        pageResult
                .setCurrent(applyIdPage.getCurrent())
                .setSize(applyIdPage.getSize())
                .setTotal(applyIdPage.getTotal())
                .setPages(applyIdPage.getPages());

        List<String> applyIdList = applyIdPage.getRecords();
        if (CollectionUtils.isNotEmpty(applyIdList)) {
            List<BuMaterialApply> applyList = buMaterialApplyMapper.selectListByApplyIdList(applyIdList);
            for (BuMaterialApply buMaterialApply : applyList) {
                List<BuMaterialApplyDetail> detailList = buMaterialApply.getDetailList();
                if (CollectionUtils.isNotEmpty(detailList)) {
                    for (BuMaterialApplyDetail buMaterialApplyDetail : detailList) {
                        BigDecimal materialQuantity = buMaterialApply.getMaterialQuantity() == null ? new BigDecimal(0) : buMaterialApply.getMaterialQuantity();
                        BigDecimal receive = buMaterialApplyDetail.getReceive() == null ? new BigDecimal(0) : buMaterialApplyDetail.getReceive();
                        buMaterialApply
                                .setConfirmUserName(buMaterialApplyDetail.getConfirmUserName())
                                .setConfirmTime(buMaterialApplyDetail.getConfirmTime())
                                .setMaterialQuantity(materialQuantity.add(receive));
                    }
                }
            }
            pageResult.setRecords(applyList);
        } else {
            pageResult.setRecords(new ArrayList<>());
        }

        return pageResult;
    }

    /**
     * @see BuMaterialApplyService#pageApplyOrderForApp(AppApplyOrderQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<AppApplyOrderVO> pageApplyOrderForApp(AppApplyOrderQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<AppApplyOrderVO> pageResult = new Page<>();

        IPage<String> orderIdPage = buMaterialApplyMapper.selectOrderIdPageForAppByCondition(new Page<>(pageNo, pageSize), queryVO);
        pageResult
                .setCurrent(orderIdPage.getCurrent())
                .setSize(orderIdPage.getSize())
                .setTotal(orderIdPage.getTotal())
                .setPages(orderIdPage.getPages());

        List<String> orderIdList = orderIdPage.getRecords();
        if (CollectionUtils.isNotEmpty(orderIdList)) {
            // 查工单
            List<BuWorkOrder> orderList = buWorkOrderForMaterialMapper.selectListByIdList(orderIdList);
            // 查领用单
            LambdaQueryWrapper<BuMaterialApply> applyWrapper = new LambdaQueryWrapper<BuMaterialApply>()
                    .in(BuMaterialApply::getWorkOrderId, orderIdList);
            List<BuMaterialApply> applyList = buMaterialApplyMapper.selectList(applyWrapper);
            // 查领用明细
            List<String> applyIdList = applyList.stream()
                    .map(BuMaterialApply::getId)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<BuMaterialApplyDetail> applyDetailWrapper = new LambdaQueryWrapper<BuMaterialApplyDetail>()
                    .in(BuMaterialApplyDetail::getApplyId, applyIdList);
            List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailMapper.selectList(applyDetailWrapper);

            List<AppApplyOrderVO> applyOrderVOList = new ArrayList<>();
            for (BuWorkOrder order : orderList) {
                // 申请数量
                BigDecimal applyAmount = BigDecimal.ZERO;
                // 未发数量
                BigDecimal undeliveredAmount = BigDecimal.ZERO;
                for (BuMaterialApply apply : applyList) {
                    if (order.getId().equals(apply.getWorkOrderId())) {
                        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                            if (apply.getId().equals(applyDetail.getApplyId())) {
                                applyAmount = applyAmount.add(applyDetail.getAmount());
                                if (applyDetail.getStatus() == 0) {
                                    undeliveredAmount = undeliveredAmount.add(applyDetail.getAmount());
                                }
                            }
                        }
                    }
                }

                AppApplyOrderVO applyOrderVO = new AppApplyOrderVO()
                        .setOrderId(order.getId())
                        .setOrderCode(order.getOrderCode())
                        .setOrderName(order.getOrderName())
                        .setApplyAmount(applyAmount)
                        .setUndeliveredAmount(undeliveredAmount)
                        .setStatus(queryVO.getStatus())
                        .setGroupId(order.getGroupId())
                        .setGroupName(order.getGroupName())
                        .setOrderTime(order.getStartTime());
                applyOrderVOList.add(applyOrderVO);
            }
            pageResult.setRecords(applyOrderVOList);
        } else {
            pageResult.setRecords(new ArrayList<>());
        }

        return pageResult;
    }

    /**
     * @see BuMaterialApplyService#saveMaterialApply(BuMaterialApply)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveMaterialApply(BuMaterialApply buMaterialApply) {
        if (isCodeRepeated(buMaterialApply)) {
            throw new JeecgBootException("编码重复");
        }

        String materialApplyCode = serialNumberGenerate.generateSerialNumberByCode("MaterialApplyCode");
        buMaterialApply.setCode(materialApplyCode);
        buMaterialApplyMapper.insert(buMaterialApply);

        String applyId = buMaterialApply.getId();
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApply.getDetailList();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            if (null == applyDetail.getStatus()) {
                applyDetail.setStatus(0);
            }
            applyDetail.setApplyId(applyId);
            buMaterialApplyDetailMapper.insert(applyDetail);
        }

        return true;
    }

    /**
     * @see BuMaterialApplyService#updateMaterialApply(BuMaterialApply)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMaterialApply(BuMaterialApply buMaterialApply) throws Exception {
        if (isCodeRepeated(buMaterialApply)) {
            throw new JeecgBootException("编码重复");
        }

        String applyId = buMaterialApply.getId();

        // 修改领用记录
//        BuMaterialApply dbBuMaterialApply = buMaterialApplyMapper.selectById(applyId);
//        Integer status = dbBuMaterialApply.getStatus();
//        Integer readyStatus = dbBuMaterialApply.getReadyStatus();
//        if (status != CommonConstant.MaterialApply.STATUS_UN_RECEIVE.getCode()) {
//            throw new JeecgBootException("已领用不能修改，领料单编码=" + buMaterialApply.getCode());
//        }
//        if (readyStatus != null && readyStatus == CommonConstant.MaterialApply.READY_STATUS_YES.getCode()) {
//            throw new JeecgBootException("已备料不能修改，领料单编码=" + buMaterialApply.getCode());
//        }
        buMaterialApplyMapper.updateById(buMaterialApply);

        // 删除原有领用明细记录
        LambdaQueryWrapper<BuMaterialApplyDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(BuMaterialApplyDetail::getApplyId, applyId);
        buMaterialApplyDetailMapper.delete(detailWrapper);

        // 重新添加领用明细记录
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApply.getDetailList();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            if (null == applyDetail.getStatus()) {
                applyDetail.setStatus(0);
            }
            applyDetail.setApplyId(applyId);
            buMaterialApplyDetailMapper.insert(applyDetail);
        }

        return true;
    }

    /**
     * @see BuMaterialApplyService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

//        List<BuMaterialApply> applyList = buMaterialApplyMapper.selectBatchIds(idList);
//        for (BuMaterialApply buMaterialApply : applyList) {
//            Integer status = buMaterialApply.getStatus();
//            Integer readyStatus = buMaterialApply.getReadyStatus();
//            if (status != CommonConstant.MaterialApply.STATUS_UN_RECEIVE.getCode()) {
//                throw new JeecgBootException("已领用不能删除，领料单编码=" + buMaterialApply.getCode());
//            }
//            if (readyStatus != null && readyStatus == CommonConstant.MaterialApply.READY_STATUS_YES.getCode()) {
//                throw new JeecgBootException("已备料不能删除，领料单编码=" + buMaterialApply.getCode());
//            }
//        }
        buMaterialApplyMapper.deleteBatchIds(idList);
//        Map<String, BuMaterialApply> idApplyMap = applyList.stream().collect(Collectors.toMap(BuMaterialApply::getId, Function.identity()));

        LambdaQueryWrapper<BuMaterialApplyDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(BuMaterialApplyDetail::getApplyId, idList);
//        List<BuMaterialApplyDetail> detailList = buMaterialApplyDetailMapper.selectList(detailWrapper);
//        for (BuMaterialApplyDetail buMaterialApplyDetail : detailList) {
//            Integer confirm = buMaterialApplyDetail.getConfirm();
//            if (confirm == CommonConstant.MaterialApply.DETAIL_CONFIRM_YES.getCode()) {
//                BuMaterialApply buMaterialApply = idApplyMap.get(buMaterialApplyDetail.getApplyId());
//                throw new JeecgBootException("已备料不能删除，领料单编码=" + buMaterialApply.getCode());
//            }
//        }
        buMaterialApplyDetailMapper.delete(detailWrapper);

        return true;
    }

    /**
     * @see BuMaterialApplyService#readyConfirm(BuMaterialApplyConfirmVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean readyConfirm(BuMaterialApplyConfirmVO confirmVO) {
        Date now = new Date();
        Boolean onlySave = confirmVO.getOnlySave();
        if (null == onlySave) {
            onlySave = false;
        }

        BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(confirmVO.getId());
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        // 检查工单状态
        OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 6);

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        // 发料的领用明细
        List<BuMaterialApplyDetail> applyDetailList = confirmVO.getDetailList();
        if (CollectionUtils.isEmpty(applyDetailList)) {
            return true;
        }

        Set<String> applyDetailIdSet = new HashSet<>();
        Set<String> materialTypeIdSet = new HashSet<>();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            if (StringUtils.isNotBlank(applyDetail.getId())) {
                applyDetailIdSet.add(applyDetail.getId());
            }
            if (StringUtils.isNotBlank(applyDetail.getMaterialTypeId())) {
                materialTypeIdSet.add(applyDetail.getMaterialTypeId());
            }
        }

        // 先删除
        if (CollectionUtils.isNotEmpty(applyDetailIdSet)) {
            LambdaQueryWrapper<BuMaterialAssignDetail> assignDetailWrapper = new LambdaQueryWrapper<BuMaterialAssignDetail>()
                    .in(BuMaterialAssignDetail::getApplyDetailId, applyDetailIdSet);
            List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectList(assignDetailWrapper);
            if (CollectionUtils.isNotEmpty(assignDetailList)) {
                List<String> assignDetailIdList = assignDetailList.stream()
                        .map(BuMaterialAssignDetail::getId)
                        .collect(Collectors.toList());
                // 删除分配明细对应的库存占用
                buMaterialStockUseService.deleteStockUseByAssignDetailIdList(assignDetailIdList);
                // 删除旧的分配明细
                buMaterialAssignDetailMapper.delete(assignDetailWrapper);
            }
        }

        // 查询数据库原领用明细
        List<BuMaterialApplyDetail> dbApplyDetailList = buMaterialApplyDetailMapper.selectBatchIds(applyDetailIdSet);
        Map<String, BuMaterialApplyDetail> idDbApplyDetailMap = new HashMap<>();
        dbApplyDetailList.forEach(detail -> idDbApplyDetailMap.put(detail.getId(), detail));

        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        Map<String, CacheWarehouseBO> codeWarehouseBOMap = new HashMap<>();
        Map<String, CacheWarehouseBO> wbsWarehouseBOMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            codeWarehouseBOMap.put(warehouseBO.getCode(), warehouseBO);
            wbsWarehouseBOMap.put(warehouseBO.getWbs(), warehouseBO);
        }

        // 查询物资
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdSet)) {
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdSet);
            materialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));
        }

        // 更新领用明细
        List<BuMaterialApplyDetail> needDealApplyDetailList = new ArrayList<>();
        Set<String> needDealApplyIdSet = new HashSet<>();
        List<BuMaterialAssignDetail> needAddAssignDetailList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(applyDetailList)) {
            List<String> zeroAmountAssignDetailIdList = new ArrayList<>();

            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                // 避免流程循环时的重复修改错误，现在还由onlySave属性决定
                BuMaterialApplyDetail dbApplyDetail = idDbApplyDetailMap.get(applyDetail.getId());
                if (!onlySave && 0 != dbApplyDetail.getStatus()) {
                    // 只有0初始状态的，才改为1已发料，避免流程循环时的重复修改错误
                    continue;
                }

                // 发放数量
                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal totalPrice = BigDecimal.ZERO;
                List<BuMaterialAssignDetail> assignDetailList = applyDetail.getAssignDetailList();
                if (CollectionUtils.isNotEmpty(assignDetailList)) {
                    // 检验：批次号不能为空
                    long tradeNoBlackCount = assignDetailList.stream()
                            .filter(item -> StringUtils.isBlank(item.getTradeNo()))
                            .count();
                    if (tradeNoBlackCount > 0) {
                        throw new JeecgBootException("分配明细中批次号不能为空，请先核实修改（物料" + applyDetail.getMaterialTypeId() + "）");
                    }

                    // 设置分配明细属性
                    for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                        if (0D == assignDetail.getAmount()) {
                            zeroAmountAssignDetailIdList.add(assignDetail.getId());
                        } else {
                            totalAmount = totalAmount.add(BigDecimal.valueOf(assignDetail.getAmount()));
                            totalPrice = totalPrice.add(assignDetail.getPrice().multiply(BigDecimal.valueOf(assignDetail.getAmount())));

                            setDetailEbsInfo(assignDetail, codeWarehouseBOMap);
                            needAddAssignDetailList.add(assignDetail);
                        }
                    }
                }

                if (!onlySave) {
                    // 已领料确认的不需要处理：流程循环时，已领料确认的领命明细状态会是2或3
                    if (!Arrays.asList(2, 3).contains(dbApplyDetail.getStatus())) {
                        needDealApplyIdSet.add(applyDetail.getApplyId());
                        needDealApplyDetailList.add(applyDetail);
                        // 提交时，才修改领用明细状态为已发料1
                        applyDetail.setStatus(1);
                    }
                }

                BigDecimal unitPrice = BigDecimal.ZERO;
                if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    unitPrice = totalPrice.divide(totalAmount, 8, BigDecimal.ROUND_HALF_UP);
                }
                applyDetail.setReceive(totalAmount)
                        .setUnitPrice(unitPrice)
                        .setSendUser(userId)
                        .setSendTime(now);
                buMaterialApplyDetailMapper.updateById(applyDetail);
            }

            // 删除分配数量为0的分配明细
            if (CollectionUtils.isNotEmpty(zeroAmountAssignDetailIdList)) {
                List<List<String>> zeroAmountAssignDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(zeroAmountAssignDetailIdList);
                for (List<String> zeroAmountAssignDetailIdBatchSub : zeroAmountAssignDetailIdBatchSubList) {
                    buMaterialAssignDetailMapper.deleteBatchIds(zeroAmountAssignDetailIdBatchSub);
                }
            }
        }

        if (!onlySave) {
            // 验证库存
            if (CollectionUtils.isNotEmpty(needAddAssignDetailList)) {
                checkStockAmountEnough(materialTypeIdSet, warehouseBOMap, wbsWarehouseBOMap, idMaterialTypeMap, needAddAssignDetailList);
            }
        }

        if (CollectionUtils.isNotEmpty(needAddAssignDetailList)) {
            // 保存分配明细
            List<List<BuMaterialAssignDetail>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddAssignDetailList);
            for (List<BuMaterialAssignDetail> batchSub : batchSubList) {
                buMaterialAssignDetailMapper.insertList(batchSub);
            }

            // 新增分配明细对应的库存占用
            List<BuMaterialStockUse> stockUseList = getStockUseListByAssignDetailList(needAddAssignDetailList, warehouseBOMap, order, now, userId);
            buMaterialStockUseService.addStockUseList(stockUseList);
        }

        if (CollectionUtils.isNotEmpty(needDealApplyIdSet)) {
            // 查询领用单下所有明细
            LambdaQueryWrapper<BuMaterialApplyDetail> applyDetailWrapper = new LambdaQueryWrapper<BuMaterialApplyDetail>()
                    .in(BuMaterialApplyDetail::getApplyId, needDealApplyIdSet);
            List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailMapper.selectList(applyDetailWrapper);
            Map<String, List<BuMaterialApplyDetail>> applyIdApplyDetailListMap = allApplyDetailList.stream()
                    .collect(Collectors.groupingBy(BuMaterialApplyDetail::getApplyId));
            for (Map.Entry<String, List<BuMaterialApplyDetail>> applyIdApplyDetailListEntry : applyIdApplyDetailListMap.entrySet()) {
                String applyId = applyIdApplyDetailListEntry.getKey();
                List<BuMaterialApplyDetail> detailList = applyIdApplyDetailListEntry.getValue();

                // 领用明细全部已经备料确认，修改领用单为已备料
                boolean isAllReady = checkApplyDetailAllReady(detailList);
                if (isAllReady) {
                    BuMaterialApply apply = buMaterialApplyMapper.selectById(applyId);
                    apply.setId(applyId)
                            .setReadyStatus(1);
                    buMaterialApplyMapper.updateById(apply);

                    if (!onlySave) {
                        // 修改工单状态为6已发料
                        buWorkOrderForMaterialMapper.updateById(new BuWorkOrder().setId(apply.getWorkOrderId()).setOrderStatus(6));
                    }
                }
            }
        }

        return true;
    }

    /**
     * @see BuMaterialApplyService#readyConfirmForApp(BuMaterialApplyConfirmVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean readyConfirmForApp(BuMaterialApplyConfirmVO confirmVO) {
        // 发料确认方法同pc端
        readyConfirm(confirmVO);
        // app还需要处理流程
        Boolean onlySave = confirmVO.getOnlySave();
        if (null == onlySave) {
            onlySave = false;
        }
        if (!onlySave) {
            BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(confirmVO.getId());
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

            workflowForwardService.completeMaterialApplyFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP领料确认", new HashMap<>());
        }

        return true;
    }

    /**
     * @see BuMaterialApplyService#receiveConfirm(Integer, String, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean receiveConfirm(Integer fromType, String ids, Boolean onlySave) throws Exception {
        Date now = new Date();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        String departId = buMaterialApplyMapper.selectDepartIdByOrgCode(sysUser.getOrgCode());

        if (null == onlySave) {
            onlySave = false;
        }

        List<BuMaterialApplyDetail> applyDetailList = null;
        // 查询领用明细列表
        if (fromType == 1) {
            List<String> palletIdList = Arrays.asList(ids.split(","));
            applyDetailList = buMaterialApplyDetailMapper.selectListByPalletIdList(palletIdList);
        } else if (fromType == 2) {
            List<String> applyDetailIdList = Arrays.asList(ids.split(","));
            applyDetailList = buMaterialApplyDetailMapper.selectListByIdList(applyDetailIdList);
        }
        if (CollectionUtils.isEmpty(applyDetailList)) {
            return true;
        }

        List<String> orderIdList = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getOrderId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (orderIdList.size() > 1) {
            throw new JeecgBootException("不支持多个工单领料确认，请联系管理员");
        }
        String orderId = orderIdList.get(0);
        BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        // 检查工单状态
        OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 7);
        // 更新工单状态
        buWorkOrderForMaterialMapper.updateById(new BuWorkOrder().setId(orderId).setOrderStatus(4).setWorkStatus(2));
        boolean needAddGroupStock = order.getOrderType() == 4;

        Set<String> groupIdSet = new HashSet<>();
        Set<String> materialTypeIdSet = new HashSet<>();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            groupIdSet.add(applyDetail.getGroupId());
            materialTypeIdSet.add(applyDetail.getMaterialTypeId());
        }
        // 查询对应的班组库存
        List<BuMaterialGroupStock> groupStockList = new ArrayList<>();
        if (needAddGroupStock) {
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(materialTypeIdSet));
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                LambdaQueryWrapper<BuMaterialGroupStock> groupStockWrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                        .in(BuMaterialGroupStock::getGroupId, groupIdSet)
                        .in(BuMaterialGroupStock::getMaterialTypeId, materialTypeIdBatchSub);
                List<BuMaterialGroupStock> subGroupStockList = buMaterialGroupStockMapper.selectList(groupStockWrapper);
                for (BuMaterialGroupStock groupStock : subGroupStockList) {
                    groupStock.setOldAmount(groupStock.getAmount())
                            .setRelativeAssignDetailIdSet(new HashSet<>());
                    groupStockList.add(groupStock);
                }
            }
        }

        // 查询数据库原领用单
        Set<String> applyIdSet = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getApplyId)
                .collect(Collectors.toSet());
        List<BuMaterialApply> dbApplyList = buMaterialApplyMapper.selectBatchIds(applyIdSet);
        Map<String, BuMaterialApply> idDbApplyMap = new HashMap<>();
        dbApplyList.forEach(apply -> idDbApplyMap.put(apply.getId(), apply));

        // 查询分配明细
        List<BuMaterialAssignDetail> assignDetailList = new ArrayList<>();
        Set<String> applyDetailIdSet = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getId)
                .collect(Collectors.toSet());
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(applyDetailIdSet));
        for (List<String> batchSub : batchSubList) {
            List<BuMaterialAssignDetail> subAssignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailIdList(batchSub);
            assignDetailList.addAll(subAssignDetailList);
        }

        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        Map<String, CacheWarehouseBO> wbsWarehouseBOMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            wbsWarehouseBOMap.put(warehouseBO.getWbs(), warehouseBO);
        }

        // 查询物资
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdSet)) {
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdSet);
            materialTypeList.forEach(materialType -> idMaterialTypeMap.put(materialType.getId(), materialType));
        }

        List<BuMaterialApplyDetail> needDealApplyDetailList = new ArrayList<>();
        Set<String> needDealApplyIdSet = new HashSet<>();
        Set<String> needUpdateGroupStockIdSet = new HashSet<>();
        Set<String> needAddGroupStockIdSet = new HashSet<>();
        List<String> needTransToMaximoAssignDetailIdList = new ArrayList<>();
        List<BuMaterialAssignDetail> needTransToMaximoAssignDetailList = new ArrayList<>();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            if (null == applyDetail) {
                continue;
            }
            // 避免流程循环时的重复修改错误，现在还由onlySave属性决定
            if (!onlySave && 1 != applyDetail.getStatus()) {
                // 只有1已发料状态的，才改为2已领料，避免流程循环时的重复修改错误
                continue;
            }

            // 更新领用明细
            if (!onlySave) {
                // 对应领用单已领用的不需要处理：流程循环时，已领料确认的领命明细所属的领料单的状态会是2
                BuMaterialApply dbApply = idDbApplyMap.get(applyDetail.getApplyId());
                if (null == dbApply) {
                    throw new JeecgBootException("物料[" + applyDetail.getMaterialTypeCode() + "]的领用明细对应领用单不存在");
                }
                if (2 != dbApply.getStatus()) {
                    needDealApplyIdSet.add(applyDetail.getApplyId());
                    needDealApplyDetailList.add(applyDetail);

                    BigDecimal totalAmount = BigDecimal.ZERO;
                    BigDecimal totalPrice = BigDecimal.ZERO;

                    List<BuMaterialAssignDetail> matchAssignDetailList = assignDetailList.stream()
                            .filter(assignDetail -> applyDetail.getId().equals(assignDetail.getApplyDetailId()))
                            .collect(Collectors.toList());
                    for (BuMaterialAssignDetail assignDetail : matchAssignDetailList) {
                        if (!needTransToMaximoAssignDetailIdList.contains(assignDetail.getId())) {
                            needTransToMaximoAssignDetailIdList.add(assignDetail.getId());
                        }
                        if (!needTransToMaximoAssignDetailList.contains(assignDetail)) {
                            needTransToMaximoAssignDetailList.add(assignDetail);
                        }

                        totalAmount = totalAmount.add(BigDecimal.valueOf(assignDetail.getAmount()));
                        totalPrice = totalPrice.add(assignDetail.getPrice().multiply(BigDecimal.valueOf(assignDetail.getAmount())));

                        if (needAddGroupStock) {
                            String groupId = applyDetail.getGroupId();
                            String materialTypeId = applyDetail.getMaterialTypeId();
                            String trainNo = applyDetail.getTrainNo();
                            String tradeNo = assignDetail.getTradeNo();
                            Integer useCategory = applyDetail.getUseCategory();
                            if (null == useCategory) {
                                BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);
                                useCategory = materialType.getCategory1();
                            }

                            // 匹配班组库存
                            List<BuMaterialGroupStock> matchGroupStockList = new ArrayList<>();
                            for (BuMaterialGroupStock groupStock : groupStockList) {
                                boolean sameGroup = groupId.equals(groupStock.getGroupId());
                                boolean sameMaterial = materialTypeId.equals(groupStock.getMaterialTypeId());
                                boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(groupStock.getTradeNo()) : tradeNo.equals(groupStock.getTradeNo());
                                boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(groupStock.getTrainNo()) : trainNo.equals(groupStock.getTrainNo());

                                if (sameGroup && sameMaterial && sameTradeNo && sameTrainNo) {
                                    matchGroupStockList.add(groupStock);
                                }
                            }
                            if (CollectionUtils.isEmpty(matchGroupStockList)) {
                                // 没有新增

                                Map<String, BuMaterialMustList> materialMustListMap = buMaterialMustListMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery()
                                        .select(BuMaterialMustList::getMaterialTypeId, BuMaterialMustList::getWorkstationId, BuMaterialMustList::getSysId, BuMaterialMustList::getGroupId)
                                        .eq(BuMaterialMustList::getMaterialTypeId, materialTypeId).eq(BuMaterialMustList::getGroupId, groupId))
                                        .stream().collect(Collectors.toMap((item) -> item.getMaterialTypeId() + item.getGroupId(), (item) -> item, (k1, k2) -> k2));
                                String systemId = null;
                                String workstationId = null;
                                if (materialMustListMap.size() > 0) {
                                    BuMaterialMustList materialMustList = materialMustListMap.get(materialTypeId + groupId);
                                    if (materialMustList != null) {
                                        systemId = materialMustList.getSysId();
                                        workstationId = materialMustList.getWorkstationId();
                                    }
                                }

                                BuMaterialGroupStock groupStock = new BuMaterialGroupStock()
                                        .setId(UUIDGenerator.generate())
                                        .setMaterialTypeId(materialTypeId)
                                        .setAmount(BigDecimal.valueOf(assignDetail.getAmount()))
                                        .setPrice(assignDetail.getPrice())
                                        .setApplyId(applyDetail.getApplyId())
                                        .setApplyDetailId(applyDetail.getId())
                                        .setGroupId(groupId)
                                        .setAssignDetailId(assignDetail.getId())
                                        .setTradeNo(tradeNo)
                                        .setSystemId(systemId)
                                        .setWorkstationId(workstationId)
                                        .setUseCategory(useCategory)
                                        .setTrainNo(trainNo)
                                        .setCompanyId(order.getCompanyId())
                                        .setWorkshopId(order.getWorkshopId())
                                        .setLineId(order.getLineId())
                                        .setOldAmount(BigDecimal.ZERO)
                                        .setRelativeAssignDetailIdSet(new HashSet<>());
                                groupStock.getRelativeAssignDetailIdSet().add(assignDetail.getId());
                                groupStockList.add(groupStock);
                                needAddGroupStockIdSet.add(groupStock.getId());
                            } else {
                                // 有更新
                                BuMaterialGroupStock groupStock = matchGroupStockList.get(0);
                                BigDecimal newAmount = groupStock.getAmount().add(BigDecimal.valueOf(assignDetail.getAmount()));
                                groupStock.setAmount(newAmount)
                                        .setPrice(assignDetail.getPrice())
                                        .setApplyId(applyDetail.getApplyId())
                                        .setApplyDetailId(applyDetail.getId())
                                        .setAssignDetailId(assignDetail.getId())
                                        .setTradeNo(tradeNo)
                                        .setUseCategory(useCategory)
                                        .setTrainNo(trainNo);
                                groupStock.getRelativeAssignDetailIdSet().add(assignDetail.getId());
                                if (!needAddGroupStockIdSet.contains(groupStock.getId())) {
                                    needUpdateGroupStockIdSet.add(groupStock.getId());
                                }
                            }
                        }
                    }

                    BigDecimal unitPrice = BigDecimal.ZERO;
                    if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                        unitPrice = totalPrice.divide(totalAmount, 8, BigDecimal.ROUND_HALF_UP);
                    }
                    // 提交时，才修改领用明细状态为已领料2
                    applyDetail.setStatus(2)
                            .setUnitPrice(unitPrice);
                }
            }
            applyDetail.setConfirmUser(userId)
                    .setConfirmTime(now);
            buMaterialApplyDetailMapper.updateById(applyDetail);
        }

        // 验证库存
        if (CollectionUtils.isNotEmpty(needTransToMaximoAssignDetailIdList)) {
            checkStockAmountEnough(materialTypeIdSet, warehouseBOMap, wbsWarehouseBOMap, idMaterialTypeMap, needTransToMaximoAssignDetailList);
        }

        // 更新领用单，此时过滤，只有已发料的领用明细才去更新领用单的状态
        // （因为前端查询领用明细列表中加了必换件的，会把发料工单中的必换件的领用明细也传过来，发料工单必换件领用明细状态为已领料）
        updateApplyStatus(new ArrayList<>(needDealApplyIdSet), needDealApplyDetailList, userId, departId, now);

        if (!onlySave && CollectionUtils.isNotEmpty(needTransToMaximoAssignDetailIdList)) {
            // 添加数据到maximo同步中间表
            addMaximoTransData(needTransToMaximoAssignDetailIdList, now);
        }

        // 新增或更新班组库存
        if (needAddGroupStock) {
            updateGroupStockByIdSet(groupStockList, needAddGroupStockIdSet, needUpdateGroupStockIdSet, order, now, userId);
        }

        return true;
    }

    /**
     * @see BuMaterialApplyService#receiveConfirmForApp(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean receiveConfirmForApp(String orderId) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        // 查询工单
        BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(orderId);
        if (null == order) {
            throw new JeecgBootException("工单不存在");
        }
        // 检查工单状态
        OrderStatusCheckUtil.check(order.getOrderCode(), order.getOrderType(), order.getOrderStatus(), 7);
        boolean needAddGroupStock = order.getOrderType() == 4;

        // 根据工单查询待领料确认的领料单、领料明细、分配明细
        LambdaQueryWrapper<BuMaterialApply> applyWrapper = new LambdaQueryWrapper<BuMaterialApply>()
                .eq(BuMaterialApply::getWorkOrderId, orderId)
                .eq(BuMaterialApply::getStatus, 0)
                .eq(BuMaterialApply::getReadyStatus, 1);
        List<BuMaterialApply> allApplyList = buMaterialApplyMapper.selectList(applyWrapper);
        if (CollectionUtils.isEmpty(allApplyList)) {
            throw new JeecgBootException("工单没有需领料确认的领料单");
        }
        List<String> applyIdList = allApplyList.stream()
                .map(BuMaterialApply::getId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<BuMaterialApplyDetail> applyDetailWrapper = new LambdaQueryWrapper<BuMaterialApplyDetail>()
                .in(BuMaterialApplyDetail::getApplyId, applyIdList)
                .eq(BuMaterialApplyDetail::getStatus, 1);
        List<BuMaterialApplyDetail> allApplyDetailList = buMaterialApplyDetailMapper.selectList(applyDetailWrapper);
        if (CollectionUtils.isEmpty(allApplyDetailList)) {
            throw new JeecgBootException("工单没有需领料确认的领料明细");
        }
        List<String> applyDetailIdList = allApplyDetailList.stream()
                .map(BuMaterialApplyDetail::getId)
                .collect(Collectors.toList());
        List<BuMaterialAssignDetail> allAssignDetailList = new ArrayList<>();
        List<List<String>> applyDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(applyDetailIdList);
        for (List<String> applyDetailIdBatchSub : applyDetailIdBatchSubList) {
            LambdaQueryWrapper<BuMaterialAssignDetail> assignDetailWrapper = new LambdaQueryWrapper<BuMaterialAssignDetail>()
                    .in(BuMaterialAssignDetail::getApplyDetailId, applyDetailIdBatchSub);
            List<BuMaterialAssignDetail> subAssignDetailList = buMaterialAssignDetailMapper.selectList(assignDetailWrapper);
            buMaterialAssignDetailMapper.selectList(assignDetailWrapper);
            allAssignDetailList.addAll(subAssignDetailList);
        }

        String groupId = order.getGroupId();
        // 查询对应的班组库存
        List<BuMaterialGroupStock> groupStockList = new ArrayList<>();
        if (needAddGroupStock) {
            List<String> materialTypeIdList = allApplyDetailList.stream()
                    .map(BuMaterialApplyDetail::getMaterialTypeId)
                    .distinct()
                    .collect(Collectors.toList());
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                LambdaQueryWrapper<BuMaterialGroupStock> groupStockWrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                        .eq(BuMaterialGroupStock::getGroupId, groupId)
                        .in(BuMaterialGroupStock::getMaterialTypeId, materialTypeIdBatchSub);
                List<BuMaterialGroupStock> subGroupStockList = buMaterialGroupStockMapper.selectList(groupStockWrapper);
                for (BuMaterialGroupStock groupStock : subGroupStockList) {
                    groupStock.setOldAmount(groupStock.getAmount());
                    groupStockList.add(groupStock);
                }
            }
        }

        Set<String> needUpdateGroupStockIdSet = new HashSet<>();
        Set<String> needAddGroupStockIdSet = new HashSet<>();
        Set<String> needTransToMaximoAssignDetailIdSet = new HashSet<>();
        // 更新领料单、领料明细
        for (BuMaterialApply apply : allApplyList) {
            String applyId = apply.getId();

            List<BuMaterialApplyDetail> applyDetailList = allApplyDetailList.stream()
                    .filter(applyDetail -> applyId.equals(applyDetail.getApplyId()))
                    .collect(Collectors.toList());
            for (BuMaterialApplyDetail applyDetail : applyDetailList) {
                String applyDetailId = applyDetail.getId();

                List<BuMaterialAssignDetail> assignDetailList = allAssignDetailList.stream()
                        .filter(assignDetail -> applyDetailId.equals(assignDetail.getApplyDetailId()))
                        .collect(Collectors.toList());

                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal totalPrice = BigDecimal.ZERO;
                for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                    needTransToMaximoAssignDetailIdSet.add(assignDetail.getId());
                    totalAmount = totalAmount.add(BigDecimal.valueOf(assignDetail.getAmount()));
                    totalPrice = totalPrice.add(assignDetail.getPrice().multiply(BigDecimal.valueOf(assignDetail.getAmount())));

                    if (needAddGroupStock) {
                        String materialTypeId = applyDetail.getMaterialTypeId();
                        String trainNo = applyDetail.getTrainNo();
                        String tradeNo = assignDetail.getTradeNo();
                        // 匹配班组库存
                        List<BuMaterialGroupStock> matchGroupStockList = new ArrayList<>();
                        for (BuMaterialGroupStock groupStock : groupStockList) {
                            boolean sameGroup = groupId.equals(groupStock.getGroupId());
                            boolean sameMaterial = materialTypeId.equals(groupStock.getMaterialTypeId());
                            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(groupStock.getTradeNo()) : tradeNo.equals(groupStock.getTradeNo());
                            boolean sameTrainNo = StringUtils.isBlank(trainNo) ? StringUtils.isBlank(groupStock.getTrainNo()) : trainNo.equals(groupStock.getTrainNo());

                            if (sameGroup && sameMaterial && sameTradeNo && sameTrainNo) {
                                matchGroupStockList.add(groupStock);
                            }
                        }
                        if (CollectionUtils.isEmpty(matchGroupStockList)) {
                            // 没有新增
                            BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);
                            Map<String, BuMaterialMustList> materialMustListMap = buMaterialMustListMapper.selectList(Wrappers.<BuMaterialMustList>lambdaQuery()
                                    .select(BuMaterialMustList::getMaterialTypeId, BuMaterialMustList::getWorkstationId, BuMaterialMustList::getSysId, BuMaterialMustList::getGroupId)
                                    .eq(BuMaterialMustList::getMaterialTypeId, materialTypeId).eq(BuMaterialMustList::getGroupId, groupId))
                                    .stream().collect(Collectors.toMap((item) -> item.getMaterialTypeId() + item.getGroupId(), (item) -> item, (k1, k2) -> k2));
                            String systemId = "";
                            String workstationId = "";
                            Integer category = null;
                            if (materialMustListMap.size() > 0) {
                                BuMaterialMustList materialMustList = materialMustListMap.get(materialTypeId + groupId);
                                if (materialMustList != null) {
                                    systemId = materialMustList.getSysId();
                                    workstationId = materialMustList.getWorkstationId();
                                    category = 1;
                                }
                            }

                            BuMaterialGroupStock groupStock = new BuMaterialGroupStock()
                                    .setId(UUIDGenerator.generate())
                                    .setMaterialTypeId(materialTypeId)
                                    .setAmount(BigDecimal.valueOf(assignDetail.getAmount()))
                                    .setPrice(assignDetail.getPrice())
                                    .setApplyId(applyId)
                                    .setApplyDetailId(applyDetailId)
                                    .setGroupId(groupId)
                                    .setAssignDetailId(assignDetail.getId())
                                    .setTradeNo(tradeNo)
                                    .setSystemId(systemId)
                                    .setWorkstationId(workstationId)
                                    .setUseCategory(category != null ? category : materialType.getCategory1())
                                    .setTrainNo(trainNo)
                                    .setCompanyId(order.getCompanyId())
                                    .setWorkshopId(order.getWorkshopId())
                                    .setLineId(order.getLineId())
                                    .setOldAmount(BigDecimal.ZERO)
                                    .setRelativeAssignDetailIdSet(new HashSet<>());
                            groupStock.getRelativeAssignDetailIdSet().add(assignDetail.getId());
                            groupStockList.add(groupStock);
                            needAddGroupStockIdSet.add(groupStock.getId());
                        } else {
                            // 有更新
                            BuMaterialGroupStock groupStock = matchGroupStockList.get(0);
                            BigDecimal newAmount = groupStock.getAmount().add(BigDecimal.valueOf(assignDetail.getAmount()));
                            groupStock.setAmount(newAmount)
                                    .setPrice(assignDetail.getPrice())
                                    .setApplyId(applyId)
                                    .setApplyDetailId(applyDetailId)
                                    .setAssignDetailId(assignDetail.getId())
                                    .setTradeNo(tradeNo)
                                    .setTrainNo(trainNo);
                            groupStock.getRelativeAssignDetailIdSet().add(assignDetail.getId());
                            if (!needAddGroupStockIdSet.contains(groupStock.getId())) {
                                needUpdateGroupStockIdSet.add(groupStock.getId());
                            }
                        }
                    }
                }

                // 更新领用明细状态为2已领料
                applyDetail.setStatus(2)
                        .setConfirmUser(userId)
                        .setConfirmTime(now)
                        .setUnitPrice(totalPrice.divide(totalAmount, 8, BigDecimal.ROUND_HALF_UP));
            }

            // 更新领用单为2已领用
            apply.setStatus(2)
                    .setReceiveUserId(userId)
                    .setReceiveTime(now);
        }

        // 更新领料明细、领料单
        buMaterialApplyDetailMapper.updateListForReceive(allApplyDetailList);
        buMaterialApplyMapper.updateListForReceive(allApplyList);
        // 确认领料后更新工单为8填报中
        buWorkOrderForMaterialMapper.updateById(new BuWorkOrder().setId(orderId).setOrderStatus(4).setWorkStatus(2));
        // 新增或更新班组库存
        if (needAddGroupStock) {
            updateGroupStockByIdSet(groupStockList, needAddGroupStockIdSet, needUpdateGroupStockIdSet, order, now, userId);
        }
        // 添加数据到maximo同步中间表
        addMaximoTransData(new ArrayList<>(needTransToMaximoAssignDetailIdSet), now);

        // 提交工单当前流程的第一个任务
        workflowForwardService.completeMaterialApplyFirstWorkflowTask(order.getId(), order.getOrderCode(), sysUser, "APP领料确认", new HashMap<>());

        return true;
    }


    private void updateApplyStatus(List<String> applyIdList,
                                   List<BuMaterialApplyDetail> receiveApplyDetailList,
                                   String userId,
                                   String departId,
                                   Date now) {
        if (CollectionUtils.isEmpty(applyIdList)) {
            return;
        }

        List<BuMaterialApply> applyList = buMaterialApplyMapper.selectListByApplyIdList(applyIdList);
        for (BuMaterialApply apply : applyList) {
            List<BuMaterialApplyDetail> detailList = apply.getDetailList();

            boolean isAllReady = checkApplyDetailAllReady(detailList);
            if (isAllReady) {
                apply.setReadyStatus(1);
            }

//            List<BuMaterialApplyDetail> receiveDetailList = receiveApplyDetailList.stream()
//                    .filter(detail -> apply.getId().equals(detail.getApplyId()))
//                    .collect(Collectors.toList());
//            boolean isAllReceive = receiveDetailList.size() == detailList.size();
            // 暂时不验证，只要提交就认为全部已领料
            // 修改此处，会影响确认领料中的判断逻辑，需要修改时务必想清楚再修改
            boolean isAllReceive = true;
            if (isAllReceive) {
                apply.setStatus(2);
//                // 修改工单状态为7确认领料
//                buMaterialApplyMapper.updateWorkOrderStatus(apply.getWorkOrderId(), 7);
                // 流程修改，增加工单状态：填报中，当确认领料后修改工单为8填报中
                buWorkOrderForMaterialMapper.updateById(new BuWorkOrder().setId(apply.getWorkOrderId()).setOrderStatus(8));
            } else {
                apply.setStatus(1);
            }
            apply.setReceiveGroupId(departId)
                    .setReceiveUserId(userId)
                    .setReceiveTime(now);
            buMaterialApplyMapper.updateById(apply);
        }
    }

    private boolean isCodeRepeated(BuMaterialApply buMaterialApply) {
        LambdaQueryWrapper<BuMaterialApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuMaterialApply::getCode, buMaterialApply.getCode());
        List<BuMaterialApply> buMaterialApplyList = buMaterialApplyMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(buMaterialApplyList)) {
            return false;
        }
        if (StringUtils.isBlank(buMaterialApply.getId())) {
            return true;
        }
        return !buMaterialApply.getId().equals(buMaterialApplyList.get(0).getId());
    }

    private boolean checkApplyDetailAllReady(List<BuMaterialApplyDetail> detailList) {
        // 暂时不验证，只要提交就认为全部已发料
        return true;
//        if (CollectionUtils.isEmpty(detailList)) {
//            return true;
//        }
//
//        // 根据领用明细id查分配明细
//        Set<String> applyDetailIdSet = detailList.stream()
//                .map(BuMaterialApplyDetail::getId)
//                .collect(Collectors.toSet());
//        LambdaQueryWrapper<BuMaterialAssignDetail> assignDetailWrapper = new LambdaQueryWrapper<BuMaterialAssignDetail>()
//                .in(BuMaterialAssignDetail::getApplyDetailId, applyDetailIdSet);
//        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectList(assignDetailWrapper);
//        Set<String> assignDetailApplyDetailIdSet = assignDetailList.stream()
//                .map(BuMaterialAssignDetail::getApplyDetailId)
//                .collect(Collectors.toSet());
//
//        // 所有领用明细都已分配，表示领用明细全部备料完成
//        return applyDetailIdSet.size() == assignDetailApplyDetailIdSet.size();
    }

    private void setDetailEbsInfo(BuMaterialAssignDetail assignDetail, Map<String, CacheWarehouseBO> codeWarehouseBOMap) {
        String locationWbs = assignDetail.getLocationWbs();
        if (StringUtils.isBlank(locationWbs)) {
            return;
        }

        // 硬编码：
        // 仓库wbs格式如：1.2.JDX01.xxxxx.xxxx...
        if (locationWbs.length() <= 4) {
            return;
        }
        // 1.2为固定的2个上级编码，去掉
        String var1 = locationWbs.substring(4);
        // 逗号分割
        String[] codes = var1.split("\\.");
        int length = codes.length;
        // 根据第一个编码，获取EBS二级库编码ebsWhCode
        if (length < 1) {
            return;
        }
        CacheWarehouseBO ebsWhWarehouse = codeWarehouseBOMap.get(codes[0]);
        if (null != ebsWhWarehouse) {
            assignDetail.setEbsWhCode(ebsWhWarehouse.getSysHouseCode());
        }
        // 根据第二个编码，获取EBS库位编码ebsWhChildCode
        if (length < 2) {
            return;
        }
        CacheWarehouseBO ebsWhChildWarehouse = codeWarehouseBOMap.get(codes[1]);
        if (null != ebsWhChildWarehouse) {
            assignDetail.setEbsWhChildCode(ebsWhChildWarehouse.getSysHouseCode());
        }
    }


    private Integer getWarehouseLevel(String warehouseWbs, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseWbs) || CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }
        List<BuMtrWarehouse> matchList = warehouseList.stream()
                .filter(warehouse -> warehouseWbs.equals(warehouse.getWbs()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchList)) {
            return null;
        } else {
            return matchList.get(0).getWhLevel();
        }
    }

    private String getWarehouseId(String warehouseWbs, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseWbs) || CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }
        List<BuMtrWarehouse> matchList = warehouseList.stream()
                .filter(warehouse -> warehouseWbs.equals(warehouse.getWbs()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchList)) {
            return null;
        } else {
            return matchList.get(0).getId();
        }
    }

    private String getWarehouseWbs(String warehouseId, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseId) || CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }
        List<BuMtrWarehouse> matchList = warehouseList.stream()
                .filter(warehouse -> warehouseId.equals(warehouse.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchList)) {
            return null;
        } else {
            return matchList.get(0).getWbs();
        }
    }

    private String getLevel3WarehouseId(String warehouseId, Map<String, CacheWarehouseBO> warehouseBOMap) {
        CacheWarehouseBO warehouse = warehouseBOMap.get(warehouseId);
        if (warehouse.getWhLevel() <= 4) {
            return warehouseId;
        } else {
            String parentId = warehouse.getParentId();
            return getLevel3WarehouseId(parentId, warehouseBOMap);
        }
    }

    private void updateGroupStockByIdSet(List<BuMaterialGroupStock> groupStockList,
                                         Set<String> needAddGroupStockIdSet,
                                         Set<String> needUpdateGroupStockIdSet,
                                         BuWorkOrder order,
                                         Date now,
                                         String userId) {
        List<BuMaterialStockChange> changeList = new ArrayList<>();

        // 新增的
        List<BuMaterialGroupStock> needAddGroupStockList = groupStockList.stream()
                .filter(groupStock -> needAddGroupStockIdSet.contains(groupStock.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needAddGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.insertList(batchSub);
            }

            for (BuMaterialGroupStock groupStock : needAddGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeAssignDetailIdSet().size() > 1;
                String assignDetailIds = String.join(",", groupStock.getRelativeAssignDetailIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason("确认领料时，新增班组库存量")
                        .setChangeType(1)
                        .setOldValue(0D)
                        .setNewValue(groupStock.getAmount().doubleValue())
                        .setTrainNo(order.getTrainNo())
                        .setOrderCode(order.getOrderCode())
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null);
                if (noteInRemark) {
                    change.setAssignDetailId("见备忘")
                            .setRemark("新增班组库存量，assignDetailIds=" + assignDetailIds);
                } else {
                    change.setAssignDetailId(assignDetailIds)
                            .setRemark("新增班组库存量");
                }
                changeList.add(change);
            }
        }
        // 更新的
        List<BuMaterialGroupStock> needUpdateGroupStockList = groupStockList.stream()
                .filter(groupStock -> needUpdateGroupStockIdSet.contains(groupStock.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needUpdateGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.updateList(batchSub);
            }

            for (BuMaterialGroupStock groupStock : needUpdateGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeAssignDetailIdSet().size() > 1;
                String assignDetailIds = String.join(",", groupStock.getRelativeAssignDetailIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason("确认领料时，更新班组库存量")
                        .setChangeType(2)
                        .setOldValue(groupStock.getOldAmount().doubleValue())
                        .setNewValue(groupStock.getAmount().doubleValue())
                        .setTrainNo(order.getTrainNo())
                        .setOrderCode(order.getOrderCode())
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null);
                if (noteInRemark) {
                    change.setAssignDetailId("见备忘")
                            .setRemark("更新班组库存量，assignDetailIds=" + assignDetailIds);
                } else {
                    change.setAssignDetailId(assignDetailIds)
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

    private void addMaximoTransData(List<String> assignDetailIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(assignDetailIdList)) {
            return;
        }

        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListForMaximoByIdList(assignDetailIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            Double amount = assignDetail.getAmount();
            if (null == amount || amount <= 0D) {
                continue;
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(3)
                    .setObjId(assignDetail.getId())
                    .setObjJson(JSON.toJSONString(assignDetail))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);
    }

    private List<BuMaterialStockUse> getStockUseListByAssignDetailList(List<BuMaterialAssignDetail> assignDetailList,
                                                                       Map<String, CacheWarehouseBO> warehouseBOMap,
                                                                       BuWorkOrder order,
                                                                       Date now,
                                                                       String userId) {
        if (CollectionUtils.isEmpty(assignDetailList)) {
            return new ArrayList<>();
        }

        Map<String, CacheWarehouseBO> wbsWarehouseMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            wbsWarehouseMap.put(warehouseBO.getWbs(), warehouseBO);
        }

        List<BuMaterialStockUse> stockUseList = new ArrayList<>();
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            CacheWarehouseBO warehouse = wbsWarehouseMap.get(assignDetail.getLocationWbs());
            String warehouseId = warehouse.getId();

            BuMaterialStockUse stockUse = new BuMaterialStockUse()
                    .setId(UUIDGenerator.generate())
                    .setStockType(1)
                    .setStockId(null)
                    .setWarehouseId(warehouseId)
                    .setMaterialTypeId(assignDetail.getMaterialTypeId())
                    .setTradeNo(assignDetail.getTradeNo())
                    .setUseTime(now)
                    .setUseReason("发料时分配明细占用库存量")
                    .setUseAmount(assignDetail.getAmount())
                    .setTrainNo(null == order ? null : order.getTrainNo())
                    .setOrderCode(null == order ? assignDetail.getOrderCode() : order.getOrderCode())
                    .setAssignDetailId(assignDetail.getId())
                    .setOrderMaterialActId(null)
                    .setOperationUser(userId)
                    .setRemark(null)
                    .setCompanyId(null == order ? null : order.getCompanyId())
                    .setWorkshopId(null == order ? null : order.getWorkshopId())
                    .setLineId(null == order ? null : order.getLineId());
            stockUseList.add(stockUse);

            // 如果所属仓库是4级库，同时设置3级库的占用
            if (warehouse.getWhLevel() >= 5) {
                String level3WarehouseId = getLevel3WarehouseId(warehouseId, warehouseBOMap);
                BuMaterialStockUse level3StockUse = new BuMaterialStockUse()
                        .setId(UUIDGenerator.generate())
                        .setStockType(1)
                        .setStockId(null)
                        .setWarehouseId(level3WarehouseId)
                        .setMaterialTypeId(assignDetail.getMaterialTypeId())
                        .setTradeNo(assignDetail.getTradeNo())
                        .setUseTime(now)
                        .setUseReason("发料时分配明细占用库存量（分配4级库同时占用3级库）")
                        .setUseAmount(assignDetail.getAmount())
                        .setTrainNo(null == order ? null : order.getTrainNo())
                        .setOrderCode(null == order ? assignDetail.getOrderCode() : order.getOrderCode())
                        .setAssignDetailId(assignDetail.getId())
                        .setOrderMaterialActId(null)
                        .setOperationUser(userId)
                        .setRemark(null)
                        .setCompanyId(null == order ? null : order.getCompanyId())
                        .setWorkshopId(null == order ? null : order.getWorkshopId())
                        .setLineId(null == order ? null : order.getLineId());
                stockUseList.add(level3StockUse);
            }
        }
        return stockUseList;
    }

    private void checkStockAmountEnough(Set<String> materialTypeIdSet,
                                        Map<String, CacheWarehouseBO> warehouseBOMap,
                                        Map<String, CacheWarehouseBO> wbsWarehouseBOMap,
                                        Map<String, BuMaterialType> idMaterialTypeMap,
                                        List<BuMaterialAssignDetail> assignDetailList) {
        // 查询物资库存
        List<BuMaterialStock> stockList = new ArrayList<>();
        List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(materialTypeIdSet));
        for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
            List<BuMaterialStock> subStockList = buMaterialStockMapper.selectListByMaterialTypeIdList(materialTypeIdBatchSub);
            stockList.addAll(subStockList);
        }

        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            if (null == assignDetail.getAmount() || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(assignDetail.getAmount())) >= 0) {
                continue;
            }

            String materialTypeId = assignDetail.getMaterialTypeId();
            String locationWbs = assignDetail.getLocationWbs();
            String locationName = assignDetail.getLocationName();
            String tradeNo = assignDetail.getTradeNo();

            String realStockWarehouseWbs = locationWbs;
            CacheWarehouseBO warehouseBO = wbsWarehouseBOMap.get(locationWbs);
            if (null != warehouseBO) {
                Integer locationLevel = warehouseBO.getWhLevel();
                if (null != locationLevel && locationLevel >= 5 && StringUtils.isNotBlank(tradeNo)) {
                    // 4级库位以上且有批次，对应到3级库位：因为4级库位没有批次，这个批次号是从上级3级库位选择的
                    String level3WarehouseId = getLevel3WarehouseId(warehouseBO.getId(), warehouseBOMap);
                    if (StringUtils.isBlank(level3WarehouseId)) {
                        throw new JeecgBootException("仓库[" + locationName + "]物资[" + materialTypeId + "]批次[" + (StringUtils.isBlank(tradeNo) ? "" : tradeNo) + "]" + "对应的3级库位不存在，请确认");
                    } else {
                        CacheWarehouseBO level3Warehouse = warehouseBOMap.get(level3WarehouseId);
                        realStockWarehouseWbs = level3Warehouse.getWbs();
                    }
                }
            }

            // 查找匹配对应当前库存
            List<BuMaterialStock> matchStockList = new ArrayList<>();
            for (BuMaterialStock stock : stockList) {
                boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
                boolean sameWarehouse = realStockWarehouseWbs.equals(stock.getWarehouseWbs());
                boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(stock.getTradeNo()) : tradeNo.equals(stock.getTradeNo());

                if (sameMaterial && sameWarehouse && sameTradeNo) {
                    matchStockList.add(stock);
                }
            }
            BigDecimal stockAmount = matchStockList.stream()
                    .map(BuMaterialStock::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (BigDecimal.valueOf(assignDetail.getAmount()).compareTo(stockAmount) > 0) {
                BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                String lackMaterial = materialType.getName() + "(" + materialType.getCode() + ")";
                throw new JeecgBootException("仓库[" + locationName + "]物资[" + lackMaterial + "]批次[" + (StringUtils.isBlank(tradeNo) ? "" : tradeNo) + "]" + "现有库存量" + stockAmount + "，不足" + assignDetail.getAmount());
            }
        }
    }

}
