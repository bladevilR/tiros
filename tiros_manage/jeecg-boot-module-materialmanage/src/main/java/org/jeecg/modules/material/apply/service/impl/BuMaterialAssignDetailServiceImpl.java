package org.jeecg.modules.material.apply.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.apply.bean.BuMaterialApplyDetail;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAutoAssignResultVO;
import org.jeecg.modules.material.apply.bean.vo.BuMaterialAutoAssignVO;
import org.jeecg.modules.material.apply.mapper.BuMaterialApplyDetailMapper;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.apply.service.BuMaterialAssignDetailService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockTradeNo;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料分配明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Service
public class BuMaterialAssignDetailServiceImpl extends ServiceImpl<BuMaterialAssignDetailMapper, BuMaterialAssignDetail> implements BuMaterialAssignDetailService {

    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;
    @Resource
    private BuMaterialApplyDetailMapper buMaterialApplyDetailMapper;
    @Resource
    private BuMaterialStockService buMaterialStockService;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private WarehouseCacheService warehouseCacheService;


    /**
     * @see BuMaterialAssignDetailService#listAssignDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialAssignDetail> listAssignDetail(String applyDetailId) throws Exception {
        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailId(applyDetailId);

        // 设置来源库位，暂时为硬编码
        if (CollectionUtils.isNotEmpty(assignDetailList)) {
            for (BuMaterialAssignDetail assignDetail : assignDetailList) {
                assignDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName()));
            }
        }

        return assignDetailList;
    }

    /**
     * @see BuMaterialAssignDetailService#autoAssignAndSave(BuMaterialAutoAssignVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BuMaterialAutoAssignResultVO autoAssignAndSave(BuMaterialAutoAssignVO autoAssignVO) throws Exception {
        List<String> applyDetailIdList = autoAssignVO.getApplyDetailIdList();
        if (CollectionUtils.isEmpty(applyDetailIdList)) {
            throw new JeecgBootException("请选择需要自动分配的领用明细");
        }
        Integer type = autoAssignVO.getType();
        if (null == type) {
            type = 1;
        }

        // 查询领用明细
        List<BuMaterialApplyDetail> applyDetailList = buMaterialApplyDetailMapper.selectListByIdList(applyDetailIdList);
        // 查询旧的分配明细（旧的分配明细不占用库存：可重复分配）
        List<BuMaterialAssignDetail> oldAssignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailIdList(applyDetailIdList);
        List<String> oldAssignDetailIdList = oldAssignDetailList.stream()
                .map(BuMaterialAssignDetail::getId)
                .distinct()
                .collect(Collectors.toList());
        // 查询物资
        Set<String> materialTypeIdSet = applyDetailList.stream()
                .map(BuMaterialApplyDetail::getMaterialTypeId)
                .collect(Collectors.toSet());
        Map<String, BigDecimal> materialTypeIdPriceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdSet)) {
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectBatchIds(materialTypeIdSet);
            materialTypeList.forEach(materialType -> materialTypeIdPriceMap.put(materialType.getId(), null == materialType.getPrice() ? BigDecimal.ZERO : materialType.getPrice()));
        }
        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        Map<String, CacheWarehouseBO> codeWarehouseBOMap = new HashMap<>();
        for (Map.Entry<String, CacheWarehouseBO> warehouseBOEntry : warehouseBOMap.entrySet()) {
            CacheWarehouseBO warehouseBO = warehouseBOEntry.getValue();
            codeWarehouseBOMap.put(warehouseBO.getCode(), warehouseBO);
        }
        // 查询物资库存
        List<BuMaterialStock> stockList = buMaterialStockService.listStockByMaterialTypeIdList(new ArrayList<>(materialTypeIdSet), false);
        // 减去库存已占用的数量
        buMaterialStockService.deleteStockUsedAmount(stockList, warehouseBOMap, oldAssignDetailIdList);
        // 设置批次选择
        buMaterialStockService.setTradeNoChoice(stockList, stockList, warehouseBOMap, oldAssignDetailIdList);

        // 自动分配
        StringBuilder assignResultMessageBuilder = new StringBuilder();
        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            List<BuMaterialAssignDetail> assignDetailList = new ArrayList<>();

            String materialTypeId = applyDetail.getMaterialTypeId();
            BigDecimal amount = applyDetail.getAmount();
            if (null == amount || BigDecimal.ZERO.compareTo(amount) == 0) {
                continue;
            }

            List<BuMaterialStock> materialStockList = stockList.stream()
                    .filter(stock -> materialTypeId.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(materialStockList)) {
                assignResultMessageBuilder.append(getLackString(applyDetail.getMaterialTypeCode(), applyDetail.getMaterialTypeUnit(), amount.doubleValue()))
                        .append(",");
            } else {
                if (1 == type) {
                    materialStockList.sort(Comparator.comparing(BuMaterialStock::getAmount).reversed());
                } else {
                    materialStockList.sort(Comparator.comparing(BuMaterialStock::getAmount));
                }
                // 3级库和4级库同时存在，优先安排4级库的：实现方式，通过仓库级别倒序排列，这样4级库硬仓库级别为5排到前面
                materialStockList.sort(Comparator.comparing(BuMaterialStock::getWarehouseLevel, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()
                        .thenComparing(BuMaterialStock::getWarehouseName, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(BuMaterialStock::getTradeNo, Comparator.nullsFirst(Comparator.naturalOrder())));

                double needAssignAmount = amount.doubleValue();
                assignAllFinishTag:
                for (BuMaterialStock stock : materialStockList) {
                    if (null != stock.getNeedChooseTradeNo() && stock.getNeedChooseTradeNo()) {
                        // 如果需要选择3级库批次号，从3级库位批次号列表中选择
                        List<BuMaterialStockTradeNo> tradeNoChoiceList = stock.getTradeNoChoiceList();
                        // 删除批次号为空的
                        tradeNoChoiceList.removeIf(item -> StringUtils.isBlank(item.getTradeNo()));
                        if (CollectionUtils.isNotEmpty(tradeNoChoiceList)) {
                            for (BuMaterialStockTradeNo stockTradeNo : tradeNoChoiceList) {
                                double tradeNoAmount = stockTradeNo.getAmount().doubleValue();
                                double assignAmount = Math.min(tradeNoAmount, needAssignAmount);
                                if (assignAmount <= 0D) {
                                    continue;
                                }

                                BuMaterialAssignDetail assignDetail = new BuMaterialAssignDetail()
                                        .setId(UUIDGenerator.generate())
                                        .setApplyDetailId(applyDetail.getId())
                                        .setMaterialTypeId(materialTypeId)
                                        .setLocationWbs(stock.getWarehouseWbs())
                                        .setLocationName(stock.getWarehouseName())
                                        .setPalletId(null)
                                        .setAmount(assignAmount)
                                        .setTradeNo(stockTradeNo.getTradeNo())
                                        .setPrice(stockTradeNo.getPrice());
                                setDetailEbsInfo(assignDetail, codeWarehouseBOMap);
                                assignDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName()));
                                assignDetailList.add(assignDetail);

                                needAssignAmount = needAssignAmount - assignAmount;

                                // 处理库存可用量
                                double newTradeNoAmount = tradeNoAmount - assignAmount;
                                stockTradeNo.setAmount(BigDecimal.valueOf(newTradeNoAmount));
                                String level3StockId = stockTradeNo.getLevel3StockId();
                                Optional<BuMaterialStock> level3StockOptional = materialStockList.stream()
                                        .filter(item -> level3StockId.equals(item.getId()))
                                        .findFirst();
                                if (level3StockOptional.isPresent()) {
                                    BuMaterialStock level3Stock = level3StockOptional.get();
                                    double stockUsableAmount = level3Stock.getUsableAmount().doubleValue();
                                    double newStockUsableAmount = stockUsableAmount - assignAmount;
                                    level3Stock.setUsableAmount(BigDecimal.valueOf(newStockUsableAmount));
                                }

                                if (needAssignAmount <= 0D) {
                                    break assignAllFinishTag;
                                }
                            }
                        }
                    } else {
                        String tradeNo = stock.getTradeNo();
                        if (StringUtils.isNotBlank(tradeNo)) {
                            // 3级库位，直接使用
                            double stockUsableAmount = stock.getUsableAmount().doubleValue();
                            double assignAmount = Math.min(stockUsableAmount, needAssignAmount);
                            if (assignAmount <= 0D) {
                                continue;
                            }

                            BuMaterialAssignDetail assignDetail = new BuMaterialAssignDetail()
                                    .setId(UUIDGenerator.generate())
                                    .setApplyDetailId(applyDetail.getId())
                                    .setMaterialTypeId(materialTypeId)
                                    .setLocationWbs(stock.getWarehouseWbs())
                                    .setLocationName(stock.getWarehouseName())
                                    .setPalletId(null)
                                    .setAmount(assignAmount)
                                    .setTradeNo(tradeNo)
                                    .setPrice(stock.getPrice());
                            setDetailEbsInfo(assignDetail, codeWarehouseBOMap);
                            assignDetail.setSourceLocationName(TirosUtil.extractSourceLocationName(assignDetail.getLocationWbs(), assignDetail.getLocationName()));
                            assignDetailList.add(assignDetail);

                            needAssignAmount = needAssignAmount - assignAmount;

                            // 处理库存可用量
                            double newStockUsableAmount = stockUsableAmount - assignAmount;
                            stock.setUsableAmount(BigDecimal.valueOf(newStockUsableAmount));

                            if (needAssignAmount <= 0D) {
                                break;
                            }
                        }
                    }
                }

                if (needAssignAmount > 0D) {
                    assignResultMessageBuilder.append(getLackString(applyDetail.getMaterialTypeCode(), applyDetail.getMaterialTypeUnit(), needAssignAmount))
                            .append(",");
                }
            }

            // 设置领用明细属性
            double sumAmount = assignDetailList.stream()
                    .mapToDouble(BuMaterialAssignDetail::getAmount)
                    .sum();
            applyDetail.setReceive(BigDecimal.valueOf(sumAmount))
                    .setUnitPrice(materialTypeIdPriceMap.getOrDefault(applyDetail.getMaterialTypeId(), BigDecimal.ZERO))
                    .setAssignDetailList(assignDetailList);
        }

        // 生成返回结果
        String assignResultMessage = "所有物料都已分配";
        if (assignResultMessageBuilder.length() > 0) {
            assignResultMessage = assignResultMessageBuilder.deleteCharAt(assignResultMessageBuilder.length() - 1).toString();
        }
        setLocationWarehouseNamesAndPalletNames(applyDetailList);

        return new BuMaterialAutoAssignResultVO()
                .setAssignResultMessage(assignResultMessage)
                .setApplyDetailList(applyDetailList);
    }


    private String getLackString(String materialTypeCode, String materialTypeUnit, double amount) {
        return "物料[" + materialTypeCode + "]缺少[" + amount + "]" + materialTypeUnit;
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

    private void setLocationWarehouseNamesAndPalletNames(List<BuMaterialApplyDetail> applyDetailList) {
        if (CollectionUtils.isEmpty(applyDetailList)) {
            return;
        }

        for (BuMaterialApplyDetail applyDetail : applyDetailList) {
            List<String> sourceLocationNameList = new ArrayList<>();
            List<String> palletNameList = new ArrayList<>();
            List<String> sourceLocationAndPalletNameList = new ArrayList<>();

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

            applyDetail.setSourceLocationName(String.join(", ", sourceLocationNameList))
                    .setPalletName(String.join(", ", palletNameList))
                    .setSourceLocationAndPalletName(String.join(", ", sourceLocationAndPalletNameList));
        }
    }

}
