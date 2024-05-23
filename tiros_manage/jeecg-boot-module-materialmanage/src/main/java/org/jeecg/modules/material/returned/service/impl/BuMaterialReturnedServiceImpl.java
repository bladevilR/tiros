package org.jeecg.modules.material.returned.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.jeecg.common.tiros.rpt.service.HomepageItemRptService;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.tiros.stock.change.bean.BuMaterialStockChange;
import org.jeecg.common.tiros.stock.change.service.BuMaterialStockChangeService;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;
import org.jeecg.common.tiros.third.maximo.service.BuMaximoTransDataService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.apply.bean.BuWorkOrder;
import org.jeecg.modules.material.apply.mapper.BuWorkOrderForMaterialMapper;
import org.jeecg.modules.material.returned.bean.BuMaterialReturned;
import org.jeecg.modules.material.returned.bean.BuMaterialReturnedDetail;
import org.jeecg.modules.material.returned.bean.vo.BuMaterialReturnedQueryVO;
import org.jeecg.modules.material.returned.mapper.BuMaterialReturnedDetailMapper;
import org.jeecg.modules.material.returned.mapper.BuMaterialReturnedMapper;
import org.jeecg.modules.material.returned.service.BuMaterialReturnedService;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.mapper.BuMaterialGroupStockMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 退料 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
@Service
public class BuMaterialReturnedServiceImpl extends ServiceImpl<BuMaterialReturnedMapper, BuMaterialReturned> implements BuMaterialReturnedService {

    @Resource
    private BuMaterialReturnedMapper buMaterialReturnedMapper;
    @Resource
    private BuMaterialReturnedDetailMapper buMaterialReturnedDetailMapper;
    @Resource
    private WarehouseCacheService warehouseCacheService;
    @Resource
    private BuMaterialGroupStockMapper buMaterialGroupStockMapper;
    @Resource
    private BuWorkOrderForMaterialMapper buWorkOrderForMaterialMapper;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private HomepageItemRptService homepageItemRptService;
    @Resource
    private BuMaterialStockChangeService buMaterialStockChangeService;


    /**
     * @see BuMaterialReturnedService#pageReturned(BuMaterialReturnedQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialReturned> pageReturned(BuMaterialReturnedQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialReturnedMapper.selectPageByQueryVO(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialReturnedService#getReturnedById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialReturned getReturnedById(String id) throws Exception {
        BuMaterialReturned returned = buMaterialReturnedMapper.selectReturnedById(id);
        if (null == returned) {
            throw new JeecgBootException("退料单不存在");
        }

        List<BuMaterialReturnedDetail> returnedDetailList = buMaterialReturnedDetailMapper.selectListByReturnedId(id);
        if (CollectionUtils.isNotEmpty(returnedDetailList)) {
            for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
                returnedDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(returnedDetail.getLocationWbs(), returnedDetail.getLocationName()));
            }
        }
        returned.setDetailList(returnedDetailList);

        return returned;
    }

    /**
     * @see BuMaterialReturnedService#saveReturned(BuMaterialReturned)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveReturned(BuMaterialReturned returned) throws Exception {
        // 设置为空的属性值
        setReturnedProperty(returned);

        String returnedId = returned.getId();

        // 删除退料明细
        LambdaQueryWrapper<BuMaterialReturnedDetail> detailWrapper = new LambdaQueryWrapper<BuMaterialReturnedDetail>()
                .eq(BuMaterialReturnedDetail::getReturnedId, returnedId);
        buMaterialReturnedDetailMapper.delete(detailWrapper);
        // 删除退料单
        buMaterialReturnedMapper.deleteById(returnedId);

        // 插入退料单
        buMaterialReturnedMapper.insert(returned);
        // 插入退料明细
        List<BuMaterialReturnedDetail> detailList = returned.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            // 获取仓库信息
            Map<String, CacheWarehouseBO> codeWarehouseBOMap = new HashMap<>();
            Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
            for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
                CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
                codeWarehouseBOMap.put(warehouseBO.getCode(), warehouseBO);
            }

            for (BuMaterialReturnedDetail returnedDetail : detailList) {
                returnedDetail.setReturnedId(returnedId)
                        .setWorkOrderId(returned.getWorkOrderId());
                setDetailEbsInfo(returnedDetail, codeWarehouseBOMap);
                if (null == returnedDetail.getReturnAmount()) {
                    returnedDetail.setReturnAmount(0D);
                }
            }

            buMaterialReturnedDetailMapper.insertList(detailList);
        }

        return true;
    }

    /**
     * @see BuMaterialReturnedService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> returnedIdList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuMaterialReturned> returnedWrapper = new LambdaQueryWrapper<BuMaterialReturned>()
                .in(BuMaterialReturned::getId, returnedIdList);
        List<BuMaterialReturned> returnedList = buMaterialReturnedMapper.selectList(returnedWrapper);
        long confirmedCount = returnedList.stream()
                .filter(returned -> null != returned.getStatus() && 1 == returned.getStatus())
                .count();
        if (confirmedCount > 0) {
            throw new JeecgBootException("已确认的退料单不能删除");
        }

        // 删除退料明细
        LambdaQueryWrapper<BuMaterialReturnedDetail> detailWrapper = new LambdaQueryWrapper<BuMaterialReturnedDetail>()
                .in(BuMaterialReturnedDetail::getReturnedId, returnedIdList);
        buMaterialReturnedDetailMapper.delete(detailWrapper);
        // 删除退料单
        buMaterialReturnedMapper.deleteBatchIds(returnedIdList);

        return true;
    }

    /**
     * @see BuMaterialReturnedService#confirmBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmBatch(String ids) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        List<String> returnedIdList = Arrays.asList(ids.split(","));
        LambdaQueryWrapper<BuMaterialReturned> returnedWrapper = new LambdaQueryWrapper<BuMaterialReturned>()
                .in(BuMaterialReturned::getId, returnedIdList);
        List<BuMaterialReturned> returnedList = buMaterialReturnedMapper.selectList(returnedWrapper);
        // 校验已确认的不能再次确认
        for (BuMaterialReturned returned : returnedList) {
            if (1 == returned.getStatus()) {
                throw new JeecgBootException("退料单[" + returned.getBillCode() + "]已确认，请勿再次确认");
            }
            BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(returned.getWorkOrderId());
            if (order.getOrderStatus() == 4) {
                // 工单已关闭，不能再发料或退料
                throw new JeecgBootException("退料单[" + returned.getBillCode() + "]关联工单[" + order.getOrderCode() + "]已关闭，请创建新的发料单，并在关闭前退料");
            }
        }

        // 修改状态为已确认
        BuMaterialReturned confirmReturned = new BuMaterialReturned()
                .setStatus(1);
        buMaterialReturnedMapper.update(confirmReturned, returnedWrapper);

        // 查询退料明细数据
        List<BuMaterialReturnedDetail> returnedDetailList = buMaterialReturnedDetailMapper.selectSimpleListByReturnedIdList(returnedIdList);
        if (CollectionUtils.isEmpty(returnedDetailList)) {
            return true;
        }

        // 确认退料后，减少班组库存
        for (BuMaterialReturned returned : returnedList) {
            BuWorkOrder order = buWorkOrderForMaterialMapper.selectById(returned.getWorkOrderId());
            decreaseMaterialGroupStock(returnedDetailList, order, now, userId);
        }

        // 更新首页数据区数据
        homepageItemRptService.rewriteDataItem();

        // 添加数据到maximo同步中间表
        addMaximoTransData(returnedIdList, now);

        return true;
    }

    /**
     * @see BuMaterialReturnedService#confirmBatchOnlyToEbs(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmBatchOnlyToEbs(String ids) throws Exception {
        List<String> returnedIdList = Arrays.asList(ids.split(","));

        Date now = new Date();

        LambdaQueryWrapper<BuMaterialReturned> returnedWrapper = new LambdaQueryWrapper<BuMaterialReturned>()
                .in(BuMaterialReturned::getId, returnedIdList);

        // 校验已确认的不能再次确认
        List<BuMaterialReturned> dbReturnedList = buMaterialReturnedMapper.selectList(returnedWrapper);
        for (BuMaterialReturned returned : dbReturnedList) {
            if (1 == returned.getStatus()) {
                throw new JeecgBootException("退料单[" + returned.getBillCode() + "]已确认，请勿再次确认");
            }
        }

        // 修改状态为已确认
        BuMaterialReturned confirmReturned = new BuMaterialReturned()
                .setStatus(1);
        buMaterialReturnedMapper.update(confirmReturned, returnedWrapper);

        // 查询退料明细数据
        List<BuMaterialReturnedDetail> returnedDetailList = buMaterialReturnedDetailMapper.selectSimpleListByReturnedIdList(returnedIdList);
        if (CollectionUtils.isEmpty(returnedDetailList)) {
            return true;
        }

        // 添加数据到maximo同步中间表，状态为已处理
        addMaximoTransDataHandled(returnedIdList, now);

        return true;
    }


    private void setReturnedProperty(BuMaterialReturned returned) {
        if (StringUtils.isBlank(returned.getId())) {
            returned.setId(UUIDGenerator.generate());
        }
        if (StringUtils.isBlank(returned.getBillCode())) {
            String code = serialNumberGenerate.generateSerialNumberByCode("RT");
            returned.setBillCode(code);
        }
        if (StringUtils.isBlank(returned.getHandleUserId())) {
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (null != sysUser) {
                returned.setHandleUserId(sysUser.getId());
            }
        }
        if (StringUtils.isBlank(returned.getGroupId())) {
            String groupId = buMaterialReturnedMapper.selectGroupIdByWorkOrder(returned.getWorkOrderId());
            returned.setGroupId(groupId);
        }
        if (null == returned.getStatus()) {
            returned.setStatus(0);
        }
    }

    private void setDetailEbsInfo(BuMaterialReturnedDetail returnedDetail, Map<String, CacheWarehouseBO> codeWarehouseBOMap) {
        String locationWbs = returnedDetail.getLocationWbs();
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
            returnedDetail.setEbsWhCode(ebsWhWarehouse.getSysHouseCode());
        }
        // 根据第二个编码，获取EBS库位编码ebsWhChildCode
        if (length < 2) {
            return;
        }
        CacheWarehouseBO ebsWhChildWarehouse = codeWarehouseBOMap.get(codes[1]);
        if (null != ebsWhChildWarehouse) {
            returnedDetail.setEbsWhChildCode(ebsWhChildWarehouse.getSysHouseCode());
        }
    }

    private void decreaseMaterialGroupStock(List<BuMaterialReturnedDetail> returnedDetailList, BuWorkOrder order, Date now, String userId) {
        if (CollectionUtils.isEmpty(returnedDetailList)) {
            return;
        }

        // 查询对应的班组库存
        Set<String> groupIdSet = new HashSet<>();
        Set<String> materialTypeIdSet = new HashSet<>();
        for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
            if (StringUtils.isNotBlank(returnedDetail.getGroupId())) {
                groupIdSet.add(returnedDetail.getGroupId());
            }
            if (StringUtils.isNotBlank(returnedDetail.getMaterialTypeId())) {
                materialTypeIdSet.add(returnedDetail.getMaterialTypeId());
            }
        }
        LambdaQueryWrapper<BuMaterialGroupStock> groupStockWrapper = new LambdaQueryWrapper<BuMaterialGroupStock>()
                .in(BuMaterialGroupStock::getGroupId, groupIdSet)
                .in(BuMaterialGroupStock::getMaterialTypeId, materialTypeIdSet);
        List<BuMaterialGroupStock> groupStockList = buMaterialGroupStockMapper.selectList(groupStockWrapper);
        if (CollectionUtils.isEmpty(groupStockList)) {
            StringBuilder lackMaterialBuilder = new StringBuilder();
            StringBuilder lackAmountBuilder = new StringBuilder();
            for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
                lackMaterialBuilder.append(returnedDetail.getMaterialTypeName())
                        .append("(").append(returnedDetail.getMaterialTypeCode()).append(")")
                        .append(";");
                lackAmountBuilder.append(returnedDetail.getReturnAmount())
                        .append(";");
            }
            String lackMaterial = null;
            if (lackMaterialBuilder.length() > 0) {
                lackMaterial = lackMaterialBuilder.deleteCharAt(lackMaterialBuilder.length() - 1).toString();
            }
            String lackAmount = null;
            if (lackAmountBuilder.length() > 0) {
                lackAmount = lackAmountBuilder.deleteCharAt(lackAmountBuilder.length() - 1).toString();
            }

            throw new JeecgBootException("班组[" + lackMaterial + "]库存量不足[" + lackAmount + "]");
        }
        for (BuMaterialGroupStock groupStock : groupStockList) {
            groupStock.setOldAmount(groupStock.getAmount())
                    .setRelativeReturnedDetailIdSet(new HashSet<>());
        }

        // 计算班组库存数据
        Set<String> needDecreaseGroupStockIdSet = new HashSet<>();
        for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
            String groupId = returnedDetail.getGroupId();
            String materialTypeId = returnedDetail.getMaterialTypeId();
            Double returnAmount = returnedDetail.getReturnAmount();
            String tradeNo = returnedDetail.getTradeNo();
            String trainNo = returnedDetail.getTrainNo();
            if (null == returnAmount) {
                continue;
            }

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
                String lackMaterial = returnedDetail.getMaterialTypeName() + "(" + returnedDetail.getMaterialTypeCode() + ")";
                throw new JeecgBootException("班组[" + lackMaterial + "]库存量不足" + returnAmount);
            }

            BuMaterialGroupStock groupStock = matchGroupStockList.get(0);
            double groupStockAmount = null == groupStock.getAmount() ? 0D : groupStock.getAmount().doubleValue();
            double newAmount = groupStockAmount - returnAmount;
            if (newAmount < 0) {
                String lackMaterial = returnedDetail.getMaterialTypeName() + "(" + returnedDetail.getMaterialTypeCode() + ")";
                throw new JeecgBootException("班组[" + lackMaterial + "]库存量不足" + returnAmount);
            } else {
                groupStock.setAmount(new BigDecimal(newAmount));
                groupStock.getRelativeReturnedDetailIdSet().add(returnedDetail.getId());
                needDecreaseGroupStockIdSet.add(groupStock.getId());
            }
        }

        // 减少班组库存数据库记录
        List<BuMaterialGroupStock> needDecreaseGroupStockList = groupStockList.stream()
                .filter(groupStock -> needDecreaseGroupStockIdSet.contains(groupStock.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needDecreaseGroupStockList)) {
            List<List<BuMaterialGroupStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needDecreaseGroupStockList);
            for (List<BuMaterialGroupStock> batchSub : batchSubList) {
                buMaterialGroupStockMapper.updateListAmount(batchSub);
            }

            List<BuMaterialStockChange> changeList = new ArrayList<>();
            for (BuMaterialGroupStock groupStock : needDecreaseGroupStockList) {
                boolean noteInRemark = groupStock.getRelativeReturnedDetailIdSet().size() > 1;
                String returnedDetailIds = String.join(",", groupStock.getRelativeReturnedDetailIdSet());

                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(4)
                        .setStockId(groupStock.getId())
                        .setWarehouseId(groupStock.getWarehouseId())
                        .setMaterialTypeId(groupStock.getMaterialTypeId())
                        .setTradeNo(groupStock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason("确认退料时，减少班组库存量")
                        .setChangeType(3)
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
                    change.setReturnedDetailId("见备忘")
                            .setRemark("减少班组库存量，returnedDetailIds=" + returnedDetailIds);
                } else {
                    change.setReturnedDetailId(returnedDetailIds)
                            .setRemark("减少班组库存量");
                }
                changeList.add(change);
            }
            // 保存库存变动记录
            if (CollectionUtils.isNotEmpty(changeList)) {
                buMaterialStockChangeService.addChangeList(changeList);
            }
        }
    }

    private void addMaximoTransData(List<String> returnedIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(returnedIdList)) {
            return;
        }

        List<BuMaterialReturnedDetail> returnedDetailList = buMaterialReturnedDetailMapper.selectListForMaximoByReturnedIdList(returnedIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
            Double amount = returnedDetail.getReturnAmount();
            if (null == amount || amount <= 0D) {
                continue;
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(4)
                    .setObjId(returnedDetail.getId())
                    .setObjJson(JSON.toJSONString(returnedDetail))
                    .setCreateTime(now)
                    .setSuccessStatus(0)
                    .setHandleStatus(0)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);
    }

    private void addMaximoTransDataHandled(List<String> returnedIdList, Date now) throws Exception {
        if (CollectionUtils.isEmpty(returnedIdList)) {
            return;
        }

        List<BuMaterialReturnedDetail> returnedDetailList = buMaterialReturnedDetailMapper.selectListForMaximoByReturnedIdList(returnedIdList);

        List<BuMaximoTransData> dataList = new ArrayList<>();
        for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
            Double amount = returnedDetail.getReturnAmount();
            if (null == amount || amount <= 0D) {
                continue;
            }

            BuMaximoTransData maximoTransData = new BuMaximoTransData()
                    .setId(UUIDGenerator.generate())
                    .setType(4)
                    .setObjId(returnedDetail.getId())
                    .setObjJson(JSON.toJSONString(returnedDetail))
                    .setCreateTime(now)
                    .setSuccessStatus(1)
                    .setHandleStatus(1)
                    .setMessage(null);
            dataList.add(maximoTransData);
        }

        buMaximoTransDataService.addTransDataList(dataList);
    }

}
