package org.jeecg.modules.third.jdx.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.*;
import org.jeecg.modules.third.jdx.bean.bo.WarehouseIdBO;
import org.jeecg.modules.third.jdx.mapper.*;
import org.jeecg.modules.third.jdx.service.BuMaterialStockChangeThirdService;
import org.jeecg.modules.third.jdx.service.BuMaterialTypeThirdService;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;
import org.jeecg.modules.third.maximo.bean.JdxInvcostOut;
import org.jeecg.modules.third.trans.bean.BuMaximoTransData;
import org.jeecg.modules.third.trans.mapper.BuMaximoTransDataMapper;
import org.jeecg.modules.third.trans.service.BuMaximoTransDataService;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 第三方接口 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-09
 */
@Slf4j
@Service
public class BuMaterialTypeThirdServiceImpl extends ServiceImpl<BuMaterialTypeThirdMapper, BuMaterialType> implements BuMaterialTypeThirdService {

    @Resource
    private BuMaterialTypeThirdMapper buMaterialTypeThirdMapper;
    @Resource
    private BuMaterialStockThirdMapper buMaterialStockThirdMapper;
    @Resource
    private BuMtrWarehouseThirdMapper buMtrWarehouseThirdMapper;
    @Resource
    private BuMaximoTransDataMapper buMaximoTransDataMapper;
//    @Resource
//    private BuMaterialStockUseThirdMapper buMaterialStockUseThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;
    @Resource
    private BuMaximoTransDataService buMaximoTransDataService;
    @Resource
    private BuMaterialStockChangeThirdService buMaterialStockChangeThirdService;

    @Value("${tiros.base.rootWarehouse:2}")
    private String rootWarehouse;

    @Value("${tiros.base.defaultSysHouseCategory:AJ1}")
    private String defaultSysHouseCategory;


    /**
     * @see BuMaterialTypeThirdService#initConsumeMaximoStockData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean initConsumeMaximoStockData(List<JdxInvbalancesOut> maximoStockList) throws Exception {
        if (CollectionUtils.isEmpty(maximoStockList)) {
            return true;
        }

        Date now = new Date();

        // 初始化所有库存时，只关注库存数量，不进行其他操作如记录库存变动同步位置、修改库存占用量、修改4级库库存量等操作
        String sourceScene = "初始化";
        boolean needUpdateWarehouseCache = false;

        // 调试用
        boolean debug = false;
        String debugMaterial = "010113630008";

        // 查询仓库
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseThirdMapper.selectList(Wrappers.emptyWrapper());
//        Map<String, BuMtrWarehouse> wbsWarehouseMap = new HashMap<>();
//        for (BuMtrWarehouse warehouse : warehouseList) {
//            wbsWarehouseMap.put(warehouse.getWbs(), warehouse);
//        }

        // 查询物质类型
        List<String> materialTypeIdList = maximoStockList.stream()
                .map(JdxInvbalancesOut::getItemnum)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdList)) {
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                List<BuMaterialType> materialTypeList = buMaterialTypeThirdMapper.selectBatchIds(materialTypeIdBatchSub);
                for (BuMaterialType materialType : materialTypeList) {
                    idMaterialTypeMap.put(materialType.getId(), materialType);
                }
            }
        }

        // 查询旧的库存：同步所有库存时，只查所有3级库的库存
        List<BuMaterialStock> stockList = buMaterialStockThirdMapper.selectListByMaxLevel(4);
        Set<String> oldStockIdSet = new HashSet<>();
        for (BuMaterialStock stock : stockList) {
            stock.setNeedDelete(true)
                    .setNeedAdd(false)
                    .setNeedUpdate(false)
                    .setOldAmount(stock.getAmount());
            oldStockIdSet.add(stock.getId());
        }

        if (debug) {
            // 调试用
            List<BuMaterialStock> debugStockList = stockList.stream()
                    .filter(stock -> debugMaterial.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            System.out.println(debugStockList);
        }
        if (debug) {
            // 调试用
            List<JdxInvbalancesOut> debugMaximoStockList = maximoStockList.stream()
                    .filter(maximoStock -> debugMaterial.equals(maximoStock.getItemnum()))
                    .collect(Collectors.toList());
            System.out.println(debugMaximoStockList);
        }

        Map<String, BuMaterialType> needAddIdMaterialTypeMap = new HashMap<>();
        Map<String, BuMaterialType> needUpdateIdMaterialTypeMap = new HashMap<>();
        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            // 物料编码为空的，跳过
            if (StringUtils.isBlank(maximoStock.getItemnum())) {
                continue;
            }

            if (debug) {
                if (debugMaterial.equals(maximoStock.getItemnum())) {
                    System.out.println(maximoStock.getItemnum());
                }
            }

            // 批次
            String lotnum = maximoStock.getLotnum();
            // 物资编码
            String materialTypeId = maximoStock.getItemnum();
            // 库存量
            BigDecimal maximoAmount = null == maximoStock.getCurbal() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStock.getCurbal());
            // 价格
            BigDecimal materialPrice = null == maximoStock.getCAvgcost() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStock.getCAvgcost());
            // 仓库id
            WarehouseIdBO warehouseIdBO = getWarehouseId(maximoStock.getLocation(), maximoStock.getBinnum(), warehouseList, now);
            String warehouseId = warehouseIdBO.getWarehouseId();
            needUpdateWarehouseCache = warehouseIdBO.getNeedUpdateWarehouseCache();
            BuMtrWarehouse warehouse = getWarehouseFromList(warehouseList, warehouseId);

            String transAction = maximoStock.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                // 从库存列表中删除
                deleteStockFromList(stockList, lotnum, materialTypeId, warehouseId);
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                // 新增或更新
                // 获取物资类型（不存在新增、存在更新价格）
                BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                if (null == materialType) {
                    // 物资数据不存在，新建物资类型
                    materialType = buildNewMaterialType(maximoStock);
                    needAddIdMaterialTypeMap.put(materialTypeId, materialType);
                    idMaterialTypeMap.put(materialTypeId, materialType);
                } else {
                    // 物资数据存在且价格不一致，更新价格
                    if (BigDecimal.ZERO.compareTo(materialPrice) < 0 && materialPrice.compareTo(materialType.getPrice()) != 0) {
                        // 价格大于0才更新
                        materialType.setPrice(materialPrice);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                    // 物资数据存在且种类为工器具，修改为物料
                    if (2 == materialType.getKind()) {
                        // 同步库存来的都是物料
                        materialType.setKind(1)
                                .setCategory1(-1);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                }

                // 查找匹配对应当前库存
                List<BuMaterialStock> matchStockList = new ArrayList<>();
                for (BuMaterialStock stock : stockList) {
                    boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
                    boolean sameWarehouse = warehouseId.equals(stock.getWarehouseId());
                    boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(stock.getTradeNo()) : lotnum.equals(stock.getTradeNo());

                    if (sameMaterial && sameWarehouse && sameTradeNo) {
                        matchStockList.add(stock);
                    }
                }
                if (CollectionUtils.isEmpty(matchStockList)) {
                    // 添加到库存列表
                    BuMaterialStock stock = new BuMaterialStock()
                            .setId(UUIDGenerator.generate())
                            .setWarehouseId(warehouseId)
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(maximoAmount)
                            .setPrice(materialPrice)
                            .setEntryDetailId(null)
                            .setTradeNo(lotnum)
                            .setCompanyId(warehouse.getCompanyId())
                            .setWorkshopId(warehouse.getWorkshopId())
                            .setLineId(warehouse.getLineId())
                            .setNeedAdd(true)
                            .setNeedDelete(false)
                            .setOldAmount(BigDecimal.ZERO);
                    stockList.add(stock);
                } else {
                    matchStockList.sort(Comparator.comparing(BuMaterialStock::getNeedAdd).thenComparing(BuMaterialStock::getId));
                    for (int i = 0; i < matchStockList.size(); i++) {
                        BuMaterialStock stock = matchStockList.get(i);
                        if (i == 0) {
                            stock.setNeedDelete(false);
                            if (oldStockIdSet.contains(stock.getId())) {
                                // 1、数据库原来就有的库存，要更新；
                                if (maximoAmount.compareTo(stock.getAmount()) != 0) {
                                    stock.setAmount(maximoAmount)
                                            .setPrice(materialPrice)
                                            .setNeedUpdate(true);
                                }
                            } else {
                                // 2、数据库原来没有的库存=这次新增的库存，不要更新，只要修改数量，新增记录时按最新的数量来
                                if (maximoAmount.compareTo(stock.getAmount()) != 0) {
                                    stock.setAmount(maximoAmount)
                                            .setPrice(materialPrice)
                                            .setNeedAdd(true);
                                }
                            }
                        } else {
                            stock.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        if (debug) {
            // 调试用
            List<BuMaterialStock> debugStockList = stockList.stream()
                    .filter(stock -> debugMaterial.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            System.out.println(debugStockList);
        }

        // 过滤出需要处理的数据
        List<BuMaterialStock> needAddStockList = new ArrayList<>();
        List<BuMaterialStock> needUpdateStockList = new ArrayList<>();
        List<BuMaterialStock> needDeleteStockList = new ArrayList<>();
        extractDifferentProcessTypeStockList(stockList, oldStockIdSet, needAddStockList, needUpdateStockList, needDeleteStockList);
        // 需要删除的库存记录，子库存也删除
        List<BuMaterialStock> needDeleteChildStockList = getChildStockList(needDeleteStockList, warehouseList);
        needDeleteStockList.addAll(needDeleteChildStockList);

        // 保存数据
        saveMaterialTypeData(needAddIdMaterialTypeMap, needUpdateIdMaterialTypeMap, sourceScene);
        saveStockDataFromList(needAddStockList, needUpdateStockList, needDeleteStockList, sourceScene, now);

        if (needUpdateWarehouseCache) {
            // 仓库更新缓存
            thirdCommonMapper.updateSysConfig(MaximoThirdConstant.SYS_CONFIG_UPDATE_WAREHOUSE_CACHE, "true");
        }

        log.info(String.format(sourceScene + "从maximo同步数据--物资库存：同步了maximo物资库存信息%s条", maximoStockList.size()));

        return true;
    }

    /**
     * @see BuMaterialTypeThirdService#initConsumeMaximoStockDataByTopWarehouse(List, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean initConsumeMaximoStockDataByTopWarehouse(List<JdxInvbalancesOut> maximoStockList, String topWarehouseCode) throws Exception {
        if (StringUtils.isBlank(topWarehouseCode)) {
            throw new RuntimeException("请输入指定库房");
        }
        if (CollectionUtils.isEmpty(maximoStockList)) {
            return true;
        }

        // 初始化所有库存时，只关注库存数量，不进行其他操作如记录库存变动同步位置、修改库存占用量、修改4级库库存量等操作
        String sourceScene = "初始化";
        Date now = new Date();
        boolean needUpdateWarehouseCache = false;

        // 调试用
        boolean debug = false;
        String debugMaterial = "010113630008";

        // 查询仓库
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseThirdMapper.selectList(Wrappers.emptyWrapper());
//        Map<String, BuMtrWarehouse> wbsWarehouseMap = new HashMap<>();
//        for (BuMtrWarehouse warehouse : warehouseList) {
//            wbsWarehouseMap.put(warehouse.getWbs(), warehouse);
//        }
        Optional<BuMtrWarehouse> topWarehouseOptional = warehouseList.stream()
                .filter(item -> topWarehouseCode.equals(item.getCode()))
                .findFirst();
        BuMtrWarehouse topWarehouse;
        if (topWarehouseOptional.isPresent()) {
            topWarehouse = topWarehouseOptional.get();
        } else {
            throw new RuntimeException("指定库房不存在");
        }

        // 查询物质类型
        List<String> materialTypeIdList = maximoStockList.stream()
                .map(JdxInvbalancesOut::getItemnum)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdList)) {
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                List<BuMaterialType> materialTypeList = buMaterialTypeThirdMapper.selectBatchIds(materialTypeIdBatchSub);
                for (BuMaterialType materialType : materialTypeList) {
                    idMaterialTypeMap.put(materialType.getId(), materialType);
                }
            }
        }

        // 查询旧的库存：同步所有库存时，只查所有3级库的库存
        List<BuMaterialStock> allStockList = buMaterialStockThirdMapper.selectListByMaxLevel(4);
        // 过滤出属于本库房下的仓库的库存信息
        List<String> topWarehouseChildIdList = getWarehouseChildIdList(topWarehouse.getId(), warehouseList);
        topWarehouseChildIdList.add(topWarehouse.getId());
        List<BuMaterialStock> stockList = allStockList.stream()
                .filter(item -> topWarehouseChildIdList.contains(item.getWarehouseId()))
                .collect(Collectors.toList());
        Set<String> oldStockIdSet = new HashSet<>();
        for (BuMaterialStock stock : stockList) {
            stock.setNeedDelete(true)
                    .setNeedAdd(false)
                    .setNeedUpdate(false)
                    .setOldAmount(stock.getAmount());
            oldStockIdSet.add(stock.getId());
        }

        if (debug) {
            // 调试用
            List<BuMaterialStock> debugStockList = stockList.stream()
                    .filter(stock -> debugMaterial.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            System.out.println(debugStockList);
        }
        if (debug) {
            // 调试用
            List<JdxInvbalancesOut> debugMaximoStockList = maximoStockList.stream()
                    .filter(maximoStock -> debugMaterial.equals(maximoStock.getItemnum()))
                    .collect(Collectors.toList());
            System.out.println(debugMaximoStockList);
        }

        Map<String, BuMaterialType> needAddIdMaterialTypeMap = new HashMap<>();
        Map<String, BuMaterialType> needUpdateIdMaterialTypeMap = new HashMap<>();
        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            // 物料编码为空的，跳过
            if (StringUtils.isBlank(maximoStock.getItemnum())) {
                continue;
            }

            if (debug) {
                if (debugMaterial.equals(maximoStock.getItemnum())) {
                    System.out.println(maximoStock.getItemnum());
                }
            }

            // 批次
            String lotnum = maximoStock.getLotnum();
            // 物资编码
            String materialTypeId = maximoStock.getItemnum();
            // 库存量
            BigDecimal maximoAmount = null == maximoStock.getCurbal() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStock.getCurbal());
            // 价格
            BigDecimal materialPrice = null == maximoStock.getCAvgcost() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStock.getCAvgcost());
            // 仓库id
            WarehouseIdBO warehouseIdBO = getWarehouseId(maximoStock.getLocation(), maximoStock.getBinnum(), warehouseList, now);
            String warehouseId = warehouseIdBO.getWarehouseId();
            needUpdateWarehouseCache = warehouseIdBO.getNeedUpdateWarehouseCache();
            BuMtrWarehouse warehouse = getWarehouseFromList(warehouseList, warehouseId);

            String transAction = maximoStock.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                // 从库存列表中删除
                deleteStockFromList(stockList, lotnum, materialTypeId, warehouseId);
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                // 新增或更新
                // 获取物资类型（不存在新增、存在更新价格）
                BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                if (null == materialType) {
                    // 物资数据不存在，新建物资类型
                    materialType = buildNewMaterialType(maximoStock);
                    needAddIdMaterialTypeMap.put(materialTypeId, materialType);
                    idMaterialTypeMap.put(materialTypeId, materialType);
                } else {
                    // 物资数据存在且价格不一致，更新价格
                    if (BigDecimal.ZERO.compareTo(materialPrice) < 0 && materialPrice.compareTo(materialType.getPrice()) != 0) {
                        // 价格大于0才更新
                        materialType.setPrice(materialPrice);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                    // 物资数据存在且种类为工器具，修改为物料
                    if (2 == materialType.getKind()) {
                        // 同步库存来的都是物料
                        materialType.setKind(1)
                                .setCategory1(-1);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                }

                // 查找匹配对应当前库存
                List<BuMaterialStock> matchStockList = new ArrayList<>();
                for (BuMaterialStock stock : stockList) {
                    boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
                    boolean sameWarehouse = warehouseId.equals(stock.getWarehouseId());
                    boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(stock.getTradeNo()) : lotnum.equals(stock.getTradeNo());

                    if (sameMaterial && sameWarehouse && sameTradeNo) {
                        matchStockList.add(stock);
                    }
                }
                if (CollectionUtils.isEmpty(matchStockList)) {
                    // 添加到库存列表
                    BuMaterialStock stock = new BuMaterialStock()
                            .setId(UUIDGenerator.generate())
                            .setWarehouseId(warehouseId)
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(maximoAmount)
                            .setPrice(materialPrice)
                            .setEntryDetailId(null)
                            .setTradeNo(lotnum)
                            .setCompanyId(warehouse.getCompanyId())
                            .setWorkshopId(warehouse.getWorkshopId())
                            .setLineId(warehouse.getLineId())
                            .setNeedAdd(true)
                            .setNeedDelete(false)
                            .setOldAmount(BigDecimal.ZERO);
                    stockList.add(stock);
                } else {
                    matchStockList.sort(Comparator.comparing(BuMaterialStock::getNeedAdd).thenComparing(BuMaterialStock::getId));
                    for (int i = 0; i < matchStockList.size(); i++) {
                        BuMaterialStock stock = matchStockList.get(i);
                        if (i == 0) {
                            stock.setNeedDelete(false);
                            if (oldStockIdSet.contains(stock.getId())) {
                                // 1、数据库原来就有的库存，要更新；
                                if (maximoAmount.compareTo(stock.getAmount()) != 0) {
                                    stock.setAmount(maximoAmount)
                                            .setPrice(materialPrice)
                                            .setNeedUpdate(true);
                                }
                            } else {
                                // 2、数据库原来没有的库存=这次新增的库存，不要更新，只要修改数量，新增记录时按最新的数量来
                                if (maximoAmount.compareTo(stock.getAmount()) != 0) {
                                    stock.setAmount(maximoAmount)
                                            .setPrice(materialPrice)
                                            .setNeedAdd(true);
                                }
                            }
                        } else {
                            stock.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        if (debug) {
            // 调试用
            List<BuMaterialStock> debugStockList = stockList.stream()
                    .filter(stock -> debugMaterial.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            System.out.println(debugStockList);
        }

        // 过滤出需要处理的数据
        List<BuMaterialStock> needAddStockList = new ArrayList<>();
        List<BuMaterialStock> needUpdateStockList = new ArrayList<>();
        List<BuMaterialStock> needDeleteStockList = new ArrayList<>();
        extractDifferentProcessTypeStockList(stockList, oldStockIdSet, needAddStockList, needUpdateStockList, needDeleteStockList);
        // 需要删除的库存记录，子库存也删除
        List<BuMaterialStock> needDeleteChildStockList = getChildStockList(needDeleteStockList, warehouseList);
        needDeleteStockList.addAll(needDeleteChildStockList);

        // 保存数据
        saveMaterialTypeData(needAddIdMaterialTypeMap, needUpdateIdMaterialTypeMap, sourceScene);
        saveStockDataFromList(needAddStockList, needUpdateStockList, needDeleteStockList, sourceScene, now);

        if (needUpdateWarehouseCache) {
            // 仓库更新缓存
            thirdCommonMapper.updateSysConfig(MaximoThirdConstant.SYS_CONFIG_UPDATE_WAREHOUSE_CACHE, "true");
        }

        log.info(String.format(sourceScene + "从maximo同步数据--物资库存：同步了maximo物资库存信息%s条", maximoStockList.size()));

        return true;
    }

    /**
     * @see BuMaterialTypeThirdService#taskConsumeMaximoStockData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean taskConsumeMaximoStockData(List<JdxInvbalancesOut> maximoStockList) throws Exception {
        if (CollectionUtils.isEmpty(maximoStockList)) {
            return true;
        }

        // 定时任务处理库存变动时，除了修改库存数量，还需要记录库存变动同步位置、修改库存占用量、修改4级库库存量等操作
        String sourceScene = "定时";
        Date now = new Date();
        boolean needUpdateWarehouseCache = false;

        // 调试用
        boolean debug = false;
        String debugMaterial = "290020030036";

        // 查询仓库
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseThirdMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuMtrWarehouse> wbsWarehouseMap = new HashMap<>();
        for (BuMtrWarehouse warehouse : warehouseList) {
            wbsWarehouseMap.put(warehouse.getWbs(), warehouse);
        }

        List<String> materialTypeIdList = maximoStockList.stream()
                .map(JdxInvbalancesOut::getItemnum)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        List<BuMaterialStock> stockList = new ArrayList<>();
        Set<String> oldStockIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdList)) {
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                // 查询物质类型
                List<BuMaterialType> subMaterialTypeList = buMaterialTypeThirdMapper.selectBatchIds(materialTypeIdBatchSub);
                for (BuMaterialType materialType : subMaterialTypeList) {
                    idMaterialTypeMap.put(materialType.getId(), materialType);
                }

                // 查询旧的库存：定时同步库存变动时，只查该物资的库存记录，包括3级库和4级库的
                LambdaQueryWrapper<BuMaterialStock> stockWrapper = new LambdaQueryWrapper<BuMaterialStock>()
                        .in(BuMaterialStock::getMaterialTypeId, materialTypeIdBatchSub);
                List<BuMaterialStock> subStockList = buMaterialStockThirdMapper.selectList(stockWrapper);
                if (CollectionUtils.isNotEmpty(subStockList)) {
                    for (BuMaterialStock stock : subStockList) {
                        stock.setNeedDelete(false)
                                .setNeedAdd(false)
                                .setNeedUpdate(false)
                                .setOldAmount(stock.getAmount());
                        stockList.add(stock);
                        oldStockIdSet.add(stock.getId());
                    }
                }
            }
        }

        // 查询以前的未处理（根据库存变动处理消耗记录）的消耗记录
        List<BuMaximoTransData> notHandledMaterialCostList = buMaximoTransDataService.listAllTransferredNotHandledMaterialTransData();
        // 过滤出：未处理的物料消耗、退料
//        Map<String, BuMaterialAssignDetail> idAssignDetailMap = new HashMap<>();
//        Map<String, BuMaterialReturnedDetail> idReturnedDetailMap = new HashMap<>();
//        Map<String, BuMaximoTransData> idTransDataMap = new HashMap<>();
        List<BuMaterialAssignDetail> notHandledAssignDetailList = new ArrayList<>();
        List<BuMaterialReturnedDetail> notHandledReturnedDetailList = new ArrayList<>();
        for (BuMaximoTransData transData : notHandledMaterialCostList) {
            Integer type = transData.getType();
            if (3 == type) {
                BuMaterialAssignDetail assignDetail = JSON.parseObject(transData.getObjJson(), BuMaterialAssignDetail.class);
                assignDetail.setTransDataId(transData.getId())
                        .setTransId(transData.getTransId())
                        .setTransTime(transData.getTransTime());
//                idAssignDetailMap.put(assignDetail.getId(), assignDetail);
                notHandledAssignDetailList.add(assignDetail);
            } else if (4 == type) {
                BuMaterialReturnedDetail returnedDetail = JSON.parseObject(transData.getObjJson(), BuMaterialReturnedDetail.class);
                returnedDetail.setTransDataId(transData.getId())
                        .setTransId(transData.getTransId())
                        .setTransTime(transData.getTransTime());
//                idReturnedDetailMap.put(returnedDetail.getId(), returnedDetail);
                notHandledReturnedDetailList.add(returnedDetail);
            }

//            idTransDataMap.put(transData.getId(), transData);
        }

        if (debug) {
            // 调试用
            List<BuMaterialStock> debugStockList = stockList.stream()
                    .filter(stock -> debugMaterial.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            System.out.println(debugStockList);
        }
        if (debug) {
            // 调试用
            List<JdxInvbalancesOut> debugMaximoStockList = maximoStockList.stream()
                    .filter(maximoStock -> debugMaterial.equals(maximoStock.getItemnum()))
                    .collect(Collectors.toList());
            System.out.println(debugMaximoStockList);
        }

        Map<String, BuMaterialType> needAddIdMaterialTypeMap = new HashMap<>();
        Map<String, BuMaterialType> needUpdateIdMaterialTypeMap = new HashMap<>();
        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            // 物料编码为空的，跳过
            if (StringUtils.isBlank(maximoStock.getItemnum())) {
                continue;
            }

            // 批次
            String lotnum = maximoStock.getLotnum();
            // 物资编码
            String materialTypeId = maximoStock.getItemnum();
            // 库存量
            BigDecimal maximoAmount = null == maximoStock.getCurbal() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStock.getCurbal());
            // 价格
            BigDecimal materialPrice = null == maximoStock.getCAvgcost() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStock.getCAvgcost());
            // 仓库id
            WarehouseIdBO warehouseIdBO = getWarehouseId(maximoStock.getLocation(), maximoStock.getBinnum(), warehouseList, now);
            String warehouseId = warehouseIdBO.getWarehouseId();
            needUpdateWarehouseCache = warehouseIdBO.getNeedUpdateWarehouseCache();
            BuMtrWarehouse warehouse = getWarehouseFromList(warehouseList, warehouseId);

            String transAction = maximoStock.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                // 从库存列表中删除
                deleteStockFromList(stockList, lotnum, materialTypeId, warehouseId);
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                // 新增或更新
                // 获取物资类型（不存在新增、存在更新价格）
                BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                if (null == materialType) {
                    // 物资数据不存在，新建物资类型
                    materialType = buildNewMaterialType(maximoStock);
                    needAddIdMaterialTypeMap.put(materialTypeId, materialType);
                    idMaterialTypeMap.put(materialTypeId, materialType);
                } else {
                    // 物资数据存在且价格不一致，更新价格
                    if (BigDecimal.ZERO.compareTo(materialPrice) < 0 && materialPrice.compareTo(materialType.getPrice()) != 0) {
                        // 价格大于0才更新
                        materialType.setPrice(materialPrice);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                    // 物资数据存在且种类为工器具，修改为物料
                    if (2 == materialType.getKind()) {
                        // 同步库存来的都是物料
                        materialType.setKind(1)
                                .setCategory1(-1);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                }

                // 查找匹配对应当前库存
                List<BuMaterialStock> matchStockList = new ArrayList<>();
                for (BuMaterialStock stock : stockList) {
                    boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
                    boolean sameWarehouse = warehouseId.equals(stock.getWarehouseId());
                    boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(stock.getTradeNo()) : lotnum.equals(stock.getTradeNo());

                    if (sameMaterial && sameWarehouse && sameTradeNo) {
                        matchStockList.add(stock);
                    }
                }
                if (CollectionUtils.isEmpty(matchStockList)) {
                    // 添加到库存列表
                    BuMaterialStock stock = new BuMaterialStock()
                            .setId(UUIDGenerator.generate())
                            .setWarehouseId(warehouseId)
                            .setMaterialTypeId(materialTypeId)
                            .setAmount(maximoAmount)
                            .setPrice(materialPrice)
                            .setEntryDetailId(null)
                            .setTradeNo(lotnum)
                            .setCompanyId(warehouse.getCompanyId())
                            .setWorkshopId(warehouse.getWorkshopId())
                            .setLineId(warehouse.getLineId())
                            .setNeedAdd(true)
                            .setNeedDelete(false)
                            .setOldAmount(BigDecimal.ZERO);
                    stockList.add(stock);
                } else {
                    matchStockList.sort(Comparator.comparing(BuMaterialStock::getNeedAdd).thenComparing(BuMaterialStock::getId));
                    for (int i = 0; i < matchStockList.size(); i++) {
                        BuMaterialStock stock = matchStockList.get(i);
                        if (i == 0) {
                            stock.setNeedDelete(false);
                            if (oldStockIdSet.contains(stock.getId())) {
                                // 1、数据库原来就有的库存，要更新；
                                if (maximoAmount.compareTo(stock.getAmount()) != 0) {
                                    stock.setAmount(maximoAmount)
                                            .setPrice(materialPrice)
                                            .setNeedUpdate(true);
                                }
                            } else {
                                // 2、数据库原来没有的库存=这次新增的库存，不要更新，只要修改数量，新增记录时按最新的数量来
                                if (maximoAmount.compareTo(stock.getAmount()) != 0) {
                                    stock.setAmount(maximoAmount)
                                            .setPrice(materialPrice)
                                            .setNeedAdd(true);
                                }
                            }
                        } else {
                            stock.setNeedDelete(true)
                                    .setNeedAdd(false)
                                    .setNeedUpdate(false);
                        }
                    }
                }
            }
        }

        // 根据库存变动找到对应的消耗数据，并需要处理总库存占用量、架大修自己的4级库存
        List<BuMaterialAssignDetail> handledAssignDetailList = new ArrayList<>();
        List<BuMaterialReturnedDetail> handledReturnedDetailList = new ArrayList<>();
        List<JdxInvbalancesOut> notMatchMaximoStockDecreaseList = new ArrayList<>();
        List<JdxInvbalancesOut> notMatchMaximoStockIncreaseList = new ArrayList<>();
        for (JdxInvbalancesOut maximoStock : maximoStockList) {
            // 调试用
            if (debug) {
                if (debugMaterial.equals(maximoStock.getItemnum())) {
                    System.out.println(maximoStock.getItemnum());
                }
            }

            double stockChange = null == maximoStock.getStockChange() ? 0D : maximoStock.getStockChange();
            if (stockChange < 0D) {
                // 库存减少了，去找有没有对应的消耗
                BuMaterialAssignDetail consume = extractMatchConsume(notHandledAssignDetailList, maximoStock);
                if (null != consume) {
                    handledAssignDetailList.add(consume);
                    // 从未处理列表中去除，防止匹配的未处理列表个数大于1时一直重复第1个
                    notHandledAssignDetailList.removeIf(item -> consume.getId().equals(item.getId()));
                } else {
                    notMatchMaximoStockDecreaseList.add(maximoStock);
                }
            } else if (stockChange > 0D) {
                // 库存增加了，去找有没有对应的退料
                BuMaterialReturnedDetail returned = extractMatchReturned(notHandledReturnedDetailList, maximoStock);
                if (null != returned) {
                    handledReturnedDetailList.add(returned);
                    // 从未处理列表中去除，防止匹配的未处理列表个数大于1时一直重复第1个
                    notHandledReturnedDetailList.removeIf(item -> returned.getId().equals(item.getId()));
                } else {
                    notMatchMaximoStockIncreaseList.add(maximoStock);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(notHandledAssignDetailList) && CollectionUtils.isNotEmpty(notMatchMaximoStockDecreaseList)) {
            // 如果还有未处理消耗，可能是库存变动重叠了：如消耗了1+2=3个，只有一条变化=3的库存变动
            List<JdxInvbalancesOut> notMatchMaximoStockDecreaseListDelete = new ArrayList<>();
            for (JdxInvbalancesOut notMatchMaximoStockDecrease : notMatchMaximoStockDecreaseList) {
                List<BuMaterialAssignDetail> matchAssignDetailList = sumConsumeMatch(notHandledAssignDetailList, notMatchMaximoStockDecrease);
                if (CollectionUtils.isNotEmpty(matchAssignDetailList)) {
                    handledAssignDetailList.addAll(matchAssignDetailList);
                    notHandledAssignDetailList.removeAll(matchAssignDetailList);
                    notMatchMaximoStockDecreaseListDelete.add(notMatchMaximoStockDecrease);
                }
            }
            if (CollectionUtils.isNotEmpty(notMatchMaximoStockDecreaseListDelete)) {
                notMatchMaximoStockDecreaseList.removeAll(notMatchMaximoStockDecreaseListDelete);
            }
//            // 根据物资分组
//            Map<String, List<BuMaterialAssignDetail>> groupAssignDetailListMap = notHandledAssignDetailList.stream()
//                    .collect(Collectors.groupingBy(item -> item.getMaterialTypeId() + "," + item.getEbsWhCode() + "," + item.getEbsWhChildCode() + "," + item.getTradeNo()));
//            for (Map.Entry<String, List<BuMaterialAssignDetail>> groupAssignDetailListEntry : groupAssignDetailListMap.entrySet()) {
//                List<BuMaterialAssignDetail> assignDetailList = groupAssignDetailListEntry.getValue();
//
//                List<JdxInvbalancesOut> matchMaximoStockList = sumConsumeMatch(assignDetailList, notMatchMaximoStockDecreaseList);
//                if (CollectionUtils.isNotEmpty(matchMaximoStockList)) {
//                    handledAssignDetailList.addAll(assignDetailList);
//                    notHandledAssignDetailList.removeAll(assignDetailList);
//                    notMatchMaximoStockDecreaseList.removeAll(matchMaximoStockList);
//                }
//            }
        }
        if (CollectionUtils.isNotEmpty(notHandledReturnedDetailList) && CollectionUtils.isNotEmpty(notMatchMaximoStockIncreaseList)) {
            // 如果还有未处理退料，可能是库存变动重叠了：如消耗了1+2=3个，只有一条变化=3的库存变动
            List<JdxInvbalancesOut> notMatchMaximoStockIncreaseListDelete = new ArrayList<>();
            for (JdxInvbalancesOut notMatchMaximoStockIncrease : notMatchMaximoStockIncreaseList) {
                List<BuMaterialReturnedDetail> matchReturnedDetailList = sumReturnedMatch(notHandledReturnedDetailList, notMatchMaximoStockIncrease);
                if (CollectionUtils.isNotEmpty(matchReturnedDetailList)) {
                    handledReturnedDetailList.addAll(matchReturnedDetailList);
                    notHandledReturnedDetailList.removeAll(matchReturnedDetailList);
                    notMatchMaximoStockIncreaseListDelete.add(notMatchMaximoStockIncrease);
                }
            }
            if (CollectionUtils.isNotEmpty(notMatchMaximoStockIncreaseListDelete)) {
                notMatchMaximoStockIncreaseList.removeAll(notMatchMaximoStockIncreaseListDelete);
            }
//            // 根据物资分组
//            Map<String, List<BuMaterialReturnedDetail>> groupReturnedDetailListMap = notHandledReturnedDetailList.stream()
//                    .collect(Collectors.groupingBy(item -> item.getMaterialTypeId() + "," + item.getEbsWhCode() + "," + item.getEbsWhChildCode() + "," + item.getTradeNo()));
//            for (Map.Entry<String, List<BuMaterialReturnedDetail>> groupReturnedDetailListEntry : groupReturnedDetailListMap.entrySet()) {
//                List<BuMaterialReturnedDetail> returnedDetailList = groupReturnedDetailListEntry.getValue();
//
//                List<JdxInvbalancesOut> matchMaximoStockList = sumReturnedMatch(returnedDetailList, notMatchMaximoStockIncreaseList);
//                if (CollectionUtils.isNotEmpty(matchMaximoStockList)) {
//                    handledReturnedDetailList.addAll(returnedDetailList);
//                    notHandledReturnedDetailList.removeAll(returnedDetailList);
//                    notMatchMaximoStockIncreaseList.removeAll(matchMaximoStockList);
//                }
//            }
        }
        Set<String> handledTransDataIdSet = new HashSet<>();
//        Set<String> handledAssignDetailIdSet = new HashSet<>();
        List<BuMaterialStockChange> changeList = new ArrayList<>();
        for (BuMaterialAssignDetail consume : handledAssignDetailList) {
            String assignDetailId = consume.getId();
            Double consumeAmount = consume.getAmount();
            String transDataId = consume.getTransDataId();

            BuMtrWarehouse warehouse = wbsWarehouseMap.get(consume.getLocationWbs());
            if (null == warehouse) {
                log.error(sourceScene + "从maximo同步数据--物资库存-处理消耗成功回写-架大修系统异常：无法根据分配明细中的仓库wbs匹配到对应仓库，assignDetailId=" + assignDetailId + "，仓库wbs=" + consume.getLocationWbs());
            } else {
                if (warehouse.getWhLevel() <= 4) {
                    // 如果是3级库的库存：
                    // 不操作库存量，因为库存变动记录会进行此仓库库存数据的变动
                } else {
                    // 如果是4级库的库存：
                    // 操作库存量，因为库存是架大修自己分的，库存变动记录不会进行此仓库库存数据的变动
                    BuMaterialStock currentStock = checkoutStock(consume, stockList, wbsWarehouseMap);
                    if (null == currentStock) {
                        log.error(sourceScene + "从maximo同步数据--物资库存-处理消耗成功回写-架大修系统异常：无法根据分配明细匹配到当前库存【4级库的】，assignDetailId=" + assignDetailId + "，分配明细数据=" + JSON.toJSONString(consume));
                    } else {
                        BigDecimal stockAmount = currentStock.getAmount();
                        BigDecimal newAmount = stockAmount.subtract(BigDecimal.valueOf(consumeAmount));
                        currentStock.setAmount(newAmount)
                                .setNeedUpdate(true);

                        // 记录库存变动
                        BuMaterialStockChange currentStockChange = new BuMaterialStockChange()
                                .setId(UUIDGenerator.generate())
                                .setStockType(1)
                                .setStockId(currentStock.getId())
                                .setWarehouseId(currentStock.getWarehouseId())
                                .setMaterialTypeId(currentStock.getMaterialTypeId())
                                .setTradeNo(currentStock.getTradeNo())
                                .setChangeTime(now)
                                .setChangeReason(sourceScene + "从maximo同步数据--物资库存-处理消耗成功回写（消耗）")
                                .setChangeType(3)
                                .setOldValue(stockAmount)
                                .setNewValue(newAmount)
                                .setTrainNo(consume.getTrainNo())
                                .setOrderCode(consume.getOrderCode())
                                .setAssignDetailId(assignDetailId)
                                .setReturnedDetailId(null)
                                .setOrderMaterialActId(null)
                                .setOperationUser(null)
                                .setRemark("更新库存");
                        changeList.add(currentStockChange);
                    }
                }

                // 记录为已处理
                handledTransDataIdSet.add(transDataId);
//                handledAssignDetailIdSet.add(assignDetailId);
            }
        }
        for (BuMaterialReturnedDetail returned : handledReturnedDetailList) {
            String returnedDetailId = returned.getId();
            Double returnAmount = returned.getReturnAmount();
            String transDataId = returned.getTransDataId();

            BuMtrWarehouse warehouse = wbsWarehouseMap.get(returned.getLocationWbs());
            if (null == warehouse) {
                log.error(sourceScene + "从maximo同步数据--物资库存-处理消耗成功回写-架大修系统异常：无法根据退料明细中的仓库wbs匹配到对应仓库，returnedDetailId=" + returnedDetailId + "，仓库wbs=" + returned.getLocationWbs());
            } else {
                if (warehouse.getWhLevel() <= 4) {
                    // 如果是3级库的库存：
                    // 不操作库存量，因为库存变动记录会进行此仓库库存数据的变动
                } else {
                    // 如果是4级库的库存：
                    // 操作库存量，因为库存是架大修自己分的，库存变动记录不会进行此仓库库存数据的变动
                    BuMaterialStock currentStock = checkoutStock(returned, stockList, wbsWarehouseMap);
                    if (null == currentStock) {
                        log.error(sourceScene + "从maximo同步数据--物资库存-处理消耗成功回写-架大修系统异常：无法根据物料退料明细匹配到当前库存【4级库的】，returnedDetailId=" + returnedDetailId + "，物料退料明细数据=" + JSON.toJSONString(returned));
                    } else {
                        BigDecimal stockAmount = currentStock.getAmount();
                        BigDecimal newAmount = stockAmount.add(BigDecimal.valueOf(returnAmount));
                        currentStock.setAmount(newAmount)
                                .setNeedUpdate(true);

                        // 记录库存变动
                        BuMaterialStockChange currentStockChange = new BuMaterialStockChange()
                                .setId(UUIDGenerator.generate())
                                .setStockType(1)
                                .setStockId(currentStock.getId())
                                .setWarehouseId(currentStock.getWarehouseId())
                                .setMaterialTypeId(currentStock.getMaterialTypeId())
                                .setTradeNo(currentStock.getTradeNo())
                                .setChangeTime(now)
                                .setChangeReason(sourceScene + "从maximo同步数据--物资库存-处理消耗成功回写（退料）")
                                .setChangeType(2)
                                .setOldValue(stockAmount)
                                .setNewValue(newAmount)
                                .setTrainNo(returned.getTrainNo())
                                .setOrderCode(returned.getOrderCode())
                                .setAssignDetailId(null)
                                .setReturnedDetailId(returnedDetailId)
                                .setOrderMaterialActId(null)
                                .setOperationUser(null)
                                .setRemark("更新库存");
                        changeList.add(currentStockChange);
                    }
                }

                // 记录为已处理
                handledTransDataIdSet.add(transDataId);
            }
        }

        if (debug) {
            // 调试用
            List<BuMaterialStock> debugStockList = stockList.stream()
                    .filter(stock -> debugMaterial.equals(stock.getMaterialTypeId()))
                    .collect(Collectors.toList());
            System.out.println(debugStockList);
        }

        // 过滤出需要处理的数据
        List<BuMaterialStock> needAddStockList = new ArrayList<>();
        List<BuMaterialStock> needUpdateStockList = new ArrayList<>();
        List<BuMaterialStock> needDeleteStockList = new ArrayList<>();
        extractDifferentProcessTypeStockList(stockList, oldStockIdSet, needAddStockList, needUpdateStockList, needDeleteStockList);
        // 需要删除的库存记录，子库存也删除
        List<BuMaterialStock> needDeleteChildStockList = getChildStockList(needDeleteStockList, warehouseList);
        needDeleteStockList.addAll(needDeleteChildStockList);

        // 保存数据
        saveMaterialTypeData(needAddIdMaterialTypeMap, needUpdateIdMaterialTypeMap, sourceScene);
        saveStockDataFromList(needAddStockList, needUpdateStockList, needDeleteStockList, sourceScene, now);

        // 更新数据同步中间表为已处理
        if (CollectionUtils.isNotEmpty(handledTransDataIdSet)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(handledTransDataIdSet));
            for (List<String> batchSub : batchSubList) {
                buMaximoTransDataMapper.updateHandledByTransDataIdList(batchSub);
            }
        }
//        // 删除分配明细对应的库存占用
//        if (CollectionUtils.isNotEmpty(handledAssignDetailIdSet)) {
//            List<List<String>> handledAssignDetailIdBatchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(handledAssignDetailIdSet));
//            for (List<String> handledAssignDetailIdBatchSub : handledAssignDetailIdBatchSubList) {
//                LambdaQueryWrapper<BuMaterialStockUse> stockUseWrapper = new LambdaQueryWrapper<BuMaterialStockUse>()
//                        .in(BuMaterialStockUse::getAssignDetailId, handledAssignDetailIdBatchSub);
//                buMaterialStockUseThirdMapper.delete(stockUseWrapper);
//            }
//        }
        // 保存库存变动记录：来自处理消耗成功回写的修改
        if (CollectionUtils.isNotEmpty(changeList)) {
            buMaterialStockChangeThirdService.addChangeList(changeList);
        }

        if (needUpdateWarehouseCache) {
            // 仓库更新缓存
            thirdCommonMapper.updateSysConfig(MaximoThirdConstant.SYS_CONFIG_UPDATE_WAREHOUSE_CACHE, "true");
        }

        // 日志中记录本次处理的transId
        List<String> transIdList = maximoStockList.stream()
                .map(JdxInvbalancesOut::getTransid)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info(String.format(sourceScene + "从maximo同步数据--物资库存：处理物资库存信息%s条，transIds=%s",
                maximoStockList.size(),
                String.join(",", transIdList)));

        return true;
    }

    /**
     * @see BuMaterialTypeThirdService#taskConsumeMaximoStockPriceData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean taskConsumeMaximoStockPriceData(List<JdxInvcostOut> maximoStockPriceList) throws Exception {
        if (CollectionUtils.isEmpty(maximoStockPriceList)) {
            return true;
        }

        // 定时任务处理库存成本变动时，比对获取库存数据，并更新价格
        String sourceScene = "定时";

        // 查询仓库
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseThirdMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuMtrWarehouse> wbsWarehouseMap = new HashMap<>();
        for (BuMtrWarehouse warehouse : warehouseList) {
            wbsWarehouseMap.put(warehouse.getWbs(), warehouse);
        }

        List<String> materialTypeIdList = maximoStockPriceList.stream()
                .map(JdxInvcostOut::getItemnum)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        Map<String, BuMaterialType> idMaterialTypeMap = new HashMap<>();
        List<BuMaterialStock> stockList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(materialTypeIdList)) {
            List<List<String>> materialTypeIdBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeIdList);
            for (List<String> materialTypeIdBatchSub : materialTypeIdBatchSubList) {
                // 查询物质类型
                List<BuMaterialType> subMaterialTypeList = buMaterialTypeThirdMapper.selectBatchIds(materialTypeIdBatchSub);
                for (BuMaterialType materialType : subMaterialTypeList) {
                    materialType.setOldPrice(materialType.getPrice());
                    idMaterialTypeMap.put(materialType.getId(), materialType);
                }

                // 查询库存：定时同步库存成本时，只查该物资的3级库的库存记录
                List<BuMaterialStock> subStockList = buMaterialStockThirdMapper.selectListByMaxLevelAndMaterialTypeIdList(4, materialTypeIdBatchSub);
                if (CollectionUtils.isNotEmpty(subStockList)) {
                    for (BuMaterialStock stock : subStockList) {
                        stock.setOldPrice(stock.getPrice());
                        stockList.add(stock);
                    }
                }
            }
        }

        // 根据maximo库存成本数据，匹配对应的库存信息，更新价格
        Map<String, BuMaterialType> needUpdateIdMaterialTypeMap = new HashMap<>();
        Map<String, BuMaterialStock> needUpdateIdMaterialStockMap = new HashMap<>();
        for (JdxInvcostOut maximoStockPrice : maximoStockPriceList) {
            // 物资价格
            BigDecimal materialPrice = null == maximoStockPrice.getAvgcost() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoStockPrice.getAvgcost());

            // 物资价格价格大于0才更新
            if (BigDecimal.ZERO.compareTo(materialPrice) < 0) {
                // 物资
                String materialTypeId = maximoStockPrice.getItemnum();

                String itemsetid = maximoStockPrice.getItemsetid();
                // 库房
                String location = maximoStockPrice.getLocation();
                // 仓库id
//                WarehouseIdBO warehouseIdBO = getWarehouseId(maximoStockPrice.getLocation(), maximoStock.getBinnum(), warehouseList, now);

                // 更新物资类型价格
                BuMaterialType materialType = idMaterialTypeMap.get(materialTypeId);
                if (null != materialType) {
                    // 物资数据存在且价格不一致，更新价格
                    if (materialPrice.compareTo(materialType.getPrice()) != 0) {
                        materialType.setPrice(materialPrice);
                        needUpdateIdMaterialTypeMap.put(materialTypeId, materialType);
                    }
                }

                // 更新当前库存价格
                for (BuMaterialStock stock : stockList) {
                    boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
//                    boolean sameWarehouse = warehouseId.equals(stock.getWarehouseId());
//                    boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(stock.getTradeNo()) : lotnum.equals(stock.getTradeNo());
//
//                    if (sameMaterial && sameWarehouse && sameTradeNo) {
//                        BigDecimal stockPrice = stock.getPrice();
//                        // 库存价格为空 或者 库存价格小于等于0 或者 库存价格不等于物资价格，更新价格
//                        if (null == stockPrice || BigDecimal.ZERO.compareTo(stockPrice) >= 0 || materialPrice.compareTo(stockPrice) != 0) {
//                            stock.setPrice(materialPrice);
//                            needUpdateIdMaterialStockMap.put(stock.getId(), stock);
//                        }
//                    }
                }
            }
        }

        // 保存数据
        if (!needUpdateIdMaterialTypeMap.isEmpty()) {
            List<BuMaterialType> needUpdateList = new ArrayList<>(needUpdateIdMaterialTypeMap.values());
            List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuMaterialType> batchSub : batchSubList) {
                buMaterialTypeThirdMapper.updateListPrice(batchSub);
            }

            log.info(sourceScene + "从maximo同步数据--物资库存成本：更新了" + needUpdateList.size() + "条物资类型的价格，详情如下：");
            StringBuilder updateInfo = new StringBuilder();
            for (BuMaterialType materialType : needUpdateList) {
                updateInfo.append("物资【").append(materialType.getId()).append("】")
                        .append("价格【").append(materialType.getOldPrice().stripTrailingZeros().toString()).append("->").append(materialType.getPrice().stripTrailingZeros().toString()).append("】、");
            }
            log.info(updateInfo.deleteCharAt(updateInfo.length() - 1).toString());
        }
        if (!needUpdateIdMaterialStockMap.isEmpty()) {
            List<BuMaterialStock> needUpdateList = new ArrayList<>(needUpdateIdMaterialStockMap.values());
            List<List<BuMaterialStock>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuMaterialStock> batchSub : batchSubList) {
                buMaterialStockThirdMapper.updateListPrice(batchSub);
            }

            log.info(sourceScene + "从maximo同步数据--物资库存成本：更新了" + needUpdateList.size() + "条物资库存的价格，详情如下：");
            StringBuilder updateInfo = new StringBuilder();
            for (BuMaterialStock stock : needUpdateList) {
                updateInfo.append("库存【").append(stock.getId()).append("】")
                        .append("物资【").append(stock.getMaterialTypeId()).append("】")
                        .append("仓库【").append(stock.getWarehouseId()).append("】")
                        .append("批次【").append(stock.getTradeNo()).append("】")
                        .append("价格【").append(stock.getOldPrice().stripTrailingZeros().toString()).append("->").append(stock.getPrice().stripTrailingZeros().toString()).append("】、");
            }
            log.info(updateInfo.deleteCharAt(updateInfo.length() - 1).toString());
        }

        // 日志中记录本次处理的transId
        List<String> transIdList = maximoStockPriceList.stream()
                .map(JdxInvcostOut::getTransid)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info(String.format(sourceScene + "从maximo同步数据--物资库存成本：处理物资库存成本信息%s条，transIds=%s",
                maximoStockPriceList.size(),
                String.join(",", transIdList)));

        return true;
    }


    private void deleteStockFromList(List<BuMaterialStock> stockList, String lotnum, String materialTypeId, String warehouseId) {
        for (BuMaterialStock stock : stockList) {
            boolean sameMaterial = materialTypeId.equals(stock.getMaterialTypeId());
            boolean sameWarehouse = warehouseId.equals(stock.getWarehouseId());
            boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(stock.getTradeNo()) : lotnum.equals(stock.getTradeNo());

            if (sameMaterial && sameWarehouse && sameTradeNo) {
                stock.setNeedDelete(true)
                        .setNeedAdd(false)
                        .setNeedUpdate(false);
            }
        }
    }

    private void saveMaterialTypeData(Map<String, BuMaterialType> needAddIdMaterialTypeMap,
                                      Map<String, BuMaterialType> needUpdateIdMaterialTypeMap,
                                      String sourceScene) {
        if (!needAddIdMaterialTypeMap.isEmpty()) {
            List<BuMaterialType> needAddList = new ArrayList<>(needAddIdMaterialTypeMap.values());
            List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddList);
            for (List<BuMaterialType> batchSub : batchSubList) {
                buMaterialTypeThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "从maximo同步数据--物资库存：新增了" + needAddList.size() + "条物资类型记录；");
        }
        if (!needUpdateIdMaterialTypeMap.isEmpty()) {
            List<BuMaterialType> needUpdateList = new ArrayList<>(needUpdateIdMaterialTypeMap.values());
            List<List<BuMaterialType>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuMaterialType> batchSub : batchSubList) {
                buMaterialTypeThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "从maximo同步数据--物资库存：更新了" + needUpdateList.size() + "条物资类型记录；");
        }
    }

    private void saveStockDataFromList(List<BuMaterialStock> needAddStockList,
                                       List<BuMaterialStock> needUpdateStockList,
                                       List<BuMaterialStock> needDeleteStockList,
                                       String sourceScene,
                                       Date now) {
        List<BuMaterialStockChange> changeList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(needAddStockList)) {
            List<List<BuMaterialStock>> stockBatchSubList = DatabaseBatchSubUtil.batchSubList(needAddStockList);
            for (List<BuMaterialStock> stockBatchSub : stockBatchSubList) {
                buMaterialStockThirdMapper.insertList(stockBatchSub);
            }

            for (BuMaterialStock stock : needAddStockList) {
                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(1)
                        .setStockId(stock.getId())
                        .setWarehouseId(stock.getWarehouseId())
                        .setMaterialTypeId(stock.getMaterialTypeId())
                        .setTradeNo(stock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason(sourceScene + "从maximo同步数据--物资库存")
                        .setChangeType(1)
                        .setOldValue(BigDecimal.ZERO)
                        .setNewValue(stock.getAmount())
                        .setTrainNo(null)
                        .setOrderCode(null)
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(null)
                        .setRemark("新增库存");
                changeList.add(change);
            }
        }

        if (CollectionUtils.isNotEmpty(needUpdateStockList)) {
            List<List<BuMaterialStock>> stockBatchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateStockList);
            for (List<BuMaterialStock> stockBatchSub : stockBatchSubList) {
                buMaterialStockThirdMapper.updateListAmountPrice(stockBatchSub);
            }

            for (BuMaterialStock stock : needUpdateStockList) {
                int compare = stock.getAmount().compareTo(stock.getOldAmount());
                if (compare == 0) {
                    continue;
                }

                int changeType;
                if (compare > 0) {
                    changeType = 2;
                } else {
                    changeType = 3;
                }
                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(1)
                        .setStockId(stock.getId())
                        .setWarehouseId(stock.getWarehouseId())
                        .setMaterialTypeId(stock.getMaterialTypeId())
                        .setTradeNo(stock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason(sourceScene + "从maximo同步数据--物资库存")
                        .setChangeType(changeType)
                        .setOldValue(stock.getOldAmount())
                        .setNewValue(stock.getAmount())
                        .setTrainNo(null)
                        .setOrderCode(null)
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(null)
                        .setRemark("更新库存");
                changeList.add(change);
            }
        }

        if (CollectionUtils.isNotEmpty(needDeleteStockList)) {
            List<String> idList = needDeleteStockList.stream()
                    .map(BuMaterialStock::getId)
                    .collect(Collectors.toList());
            List<List<String>> idBatchSubList = DatabaseBatchSubUtil.batchSubList(idList);
            for (List<String> idBatchSub : idBatchSubList) {
                buMaterialStockThirdMapper.deleteBatchIds(idBatchSub);
            }

            for (BuMaterialStock stock : needDeleteStockList) {
                // 记录库存变动
                BuMaterialStockChange change = new BuMaterialStockChange()
                        .setId(UUIDGenerator.generate())
                        .setStockType(1)
                        .setStockId(stock.getId())
                        .setWarehouseId(stock.getWarehouseId())
                        .setMaterialTypeId(stock.getMaterialTypeId())
                        .setTradeNo(stock.getTradeNo())
                        .setChangeTime(now)
                        .setChangeReason(sourceScene + "从maximo同步数据--物资库存")
                        .setChangeType(4)
                        .setOldValue(stock.getOldAmount())
                        .setNewValue(BigDecimal.ZERO)
                        .setTrainNo(null)
                        .setOrderCode(null)
                        .setAssignDetailId(null)
                        .setReturnedDetailId(null)
                        .setOrderMaterialActId(null)
                        .setOperationUser(null)
                        .setRemark("删除库存");
                changeList.add(change);
            }
        }

        // 保存库存变动记录
        if (CollectionUtils.isNotEmpty(changeList)) {
            buMaterialStockChangeThirdService.addChangeList(changeList);
        }
    }

    private List<BuMaterialStock> getChildStockList(List<BuMaterialStock> stockList, List<BuMtrWarehouse> warehouseList) {
        if (CollectionUtils.isEmpty(stockList)) {
            return new ArrayList<>();
        }

        // 查询子库存
        Set<BuMaterialStock> childStockSet = new HashSet<>();
        for (BuMaterialStock stock : stockList) {
            List<String> childIdList = getWarehouseChildIdList(stock.getWarehouseId(), warehouseList);
            if (CollectionUtils.isNotEmpty(childIdList)) {
                List<List<String>> childIdBatchSubList = DatabaseBatchSubUtil.batchSubList(childIdList);
                for (List<String> childIdBatchSub : childIdBatchSubList) {
                    LambdaQueryWrapper<BuMaterialStock> stockWrapper = new LambdaQueryWrapper<BuMaterialStock>()
                            .eq(BuMaterialStock::getMaterialTypeId, stock.getMaterialTypeId())
                            .in(BuMaterialStock::getWarehouseId, childIdBatchSub);
                    List<BuMaterialStock> childStockList = buMaterialStockThirdMapper.selectList(stockWrapper);
                    childStockSet.addAll(childStockList);
                }
            }
        }

        return new ArrayList<>(childStockSet);
    }

    private List<String> getWarehouseChildIdList(String warehouseId, List<BuMtrWarehouse> warehouseList) {
        if (StringUtils.isBlank(warehouseId) || CollectionUtils.isEmpty(warehouseList)) {
            return new ArrayList<>();
        }

        Set<String> childIdSet = new HashSet<>();
        // 递归添加子节点id集合
        recurseAddChildId(warehouseId, warehouseList, childIdSet);
        return new ArrayList<>(childIdSet);
    }

    private void recurseAddChildId(String parentId, List<BuMtrWarehouse> warehouseList, Set<String> childIdSet) {
        List<BuMtrWarehouse> childList = warehouseList.stream()
                .filter(warehouse -> parentId.equals(warehouse.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(childList)) {
            for (BuMtrWarehouse child : childList) {
                String childId = child.getId();

                childIdSet.add(childId);
                recurseAddChildId(childId, warehouseList, childIdSet);
            }
        }
    }

    private BuMaterialType buildNewMaterialType(JdxInvbalancesOut maximoMaterial) {
        if (null == maximoMaterial) {
            return null;
        }

        String materialCode = maximoMaterial.getItemnum();
        BigDecimal materialPrice = null == maximoMaterial.getCAvgcost() ? BigDecimal.ZERO : BigDecimal.valueOf(maximoMaterial.getCAvgcost());
        // 物资数据不存在增加物资
        // 属性转换
        String spec = null;
        String name = maximoMaterial.getDescription();
        if (StringUtils.isNotBlank(name)) {
            String[] split = name.split("-\\[");
            if (split.length >= 2) {
                name = split[0];
                spec = split[1].trim();
                spec = spec.substring(0, spec.length() - 1);
                spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
            }
        } else {
            name = materialCode;
        }

        String unit = StringUtils.isBlank(maximoMaterial.getOrderunit()) ? "个" : maximoMaterial.getOrderunit();
        int statusInt = 1;
        if (StringUtils.isNotBlank(maximoMaterial.getIn20())) {
            statusInt = "Active".equals(maximoMaterial.getIn20()) ? 1 : 0;
        }
//        int kindInt = "工器具".equals(maximoMaterial.getIn21()) ? 2 : 1;
        // 同步库存来的都是物料
        int kindInt = 1;
        int category1 = getCategory1(maximoMaterial.getIn21());
        String category2 = "备品备件".equals(maximoMaterial.getIn21()) ? "2" : null;
        int isConsumeInt = 0;
        if ("消耗物资".equals(maximoMaterial.getIn19())) {
            isConsumeInt = 1;
        } else if ("列管物资".equals(maximoMaterial.getIn19())) {
            isConsumeInt = 2;
        }

        return new BuMaterialType()
                .setId(materialCode)
                .setCode(materialCode)
                .setName(name)
                .setSpec(spec)
                .setUnit(unit)
                .setPrice(materialPrice)
                .setStatus(statusInt)
                .setKind(kindInt)
                .setCategory1(category1)
                .setCategory2(category2)
                .setCategory3(maximoMaterial.getIn21())
                .setTheshold(new BigDecimal(-1))
                .setIsConsume(isConsumeInt)
                .setIsAsset(0)
                .setFromHead(0)
                .setRemark("maximo导入物资");
    }

    private WarehouseIdBO getWarehouseId(String locationEbsCode, String binnumEbsCode, List<BuMtrWarehouse> warehouseList, Date now) {
        WarehouseIdBO warehouseIdBO = new WarehouseIdBO()
                .setNeedUpdateWarehouseCache(false);

        String parentId = rootWarehouse;

        BuMtrWarehouse parent = warehouseList.stream()
                .filter(warehouse -> parentId.equals(warehouse.getId()))
                .collect(Collectors.toList())
                .get(0);
        Integer parentLevel = parent.getWhLevel();
        int locationLevel = parentLevel + 1;

        // 获取仓库编码，避免重复
        Set<String> warehouseCodeSet = warehouseList.stream()
                .map(BuMtrWarehouse::getCode)
                .collect(Collectors.toSet());

        // 库房
        List<BuMtrWarehouse> locationMatchList = warehouseList.stream()
                .filter(warehouse -> parentId.equals(warehouse.getParentId()))
                .filter(warehouse -> locationLevel == warehouse.getWhLevel())
                .filter(warehouse -> locationEbsCode.equals(warehouse.getSysHouseCode()))
                .collect(Collectors.toList());
        BuMtrWarehouse locationWarehouse;
        if (CollectionUtils.isEmpty(locationMatchList)) {
            int level = locationLevel;
            String selfCode = locationEbsCode;
            // 库存组织
            String parentSysHouseCategory = StringUtils.isBlank(parent.getSysHouseCategory()) ? defaultSysHouseCategory : parent.getSysHouseCategory();

            locationWarehouse = new BuMtrWarehouse()
                    .setId(UUIDGenerator.generate())
                    .setName(locationEbsCode)
                    .setCode(selfCode)
                    .setWhLevel(level)
                    .setType(1)
                    .setClose(0)
                    .setParentId(parentId)
                    .setWbs(parent.getWbs() + "." + selfCode)
                    .setSysHouseCode(locationEbsCode)
                    .setSysHouseCategory(parentSysHouseCategory)
                    .setLineId(parent.getLineId())
                    .setDepotId(parent.getDepotId())
                    .setWorkshopId(parent.getWorkshopId())
                    .setCompanyId(parent.getCompanyId())
                    .setSync(1)
                    .setStatus(1)
                    .setRemark("maximo导入")
                    .setCreateTime(now)
                    .setCreateBy("admin");

            buMtrWarehouseThirdMapper.insert(locationWarehouse);
            warehouseIdBO.setNeedUpdateWarehouseCache(true);
            warehouseList.add(locationWarehouse);
            warehouseCodeSet.add(selfCode);
        } else {
            locationWarehouse = locationMatchList.get(0);
        }
        if (StringUtils.isBlank(binnumEbsCode)) {
            warehouseIdBO.setWarehouseId(locationWarehouse.getId());
            return warehouseIdBO;
        }

        // 库位
        int childLevel = locationWarehouse.getWhLevel() + 1;
        String locationWarehouseId = locationWarehouse.getId();
        List<BuMtrWarehouse> binnumMatchList = warehouseList.stream()
                .filter(warehouse -> locationWarehouseId.equals(warehouse.getParentId()))
                .filter(warehouse -> childLevel == warehouse.getWhLevel())
                .filter(warehouse -> binnumEbsCode.equals(warehouse.getSysHouseCode()))
                .collect(Collectors.toList());
        BuMtrWarehouse binnumWarehouse;
        if (CollectionUtils.isEmpty(binnumMatchList)) {
            String childSelfCode = getWarehouseCode(childLevel, warehouseCodeSet);
            // 库存组织
            String parentSysHouseCategory = StringUtils.isBlank(locationWarehouse.getSysHouseCategory()) ? defaultSysHouseCategory : locationWarehouse.getSysHouseCategory();

            binnumWarehouse = new BuMtrWarehouse()
                    .setId(UUIDGenerator.generate())
                    .setName(binnumEbsCode)
                    .setCode(childSelfCode)
                    .setWhLevel(childLevel)
                    .setType(2)
                    .setClose(0)
                    .setParentId(locationWarehouse.getId())
                    .setWbs(locationWarehouse.getWbs() + "." + childSelfCode)
                    .setSysHouseCode(binnumEbsCode)
                    .setSysHouseCategory(parentSysHouseCategory)
                    .setLineId(locationWarehouse.getLineId())
                    .setDepotId(locationWarehouse.getDepotId())
                    .setWorkshopId(locationWarehouse.getWorkshopId())
                    .setCompanyId(locationWarehouse.getCompanyId())
                    .setSync(1)
                    .setStatus(1)
                    .setRemark("maximo导入")
                    .setCreateTime(now)
                    .setCreateBy("admin");

            buMtrWarehouseThirdMapper.insert(binnumWarehouse);
            warehouseIdBO.setNeedUpdateWarehouseCache(true);
            warehouseList.add(binnumWarehouse);
            warehouseCodeSet.add(childSelfCode);
        } else {
            binnumWarehouse = binnumMatchList.get(0);
        }
        warehouseIdBO.setWarehouseId(binnumWarehouse.getId());
        return warehouseIdBO;
    }

    private BuMtrWarehouse getWarehouseFromList(List<BuMtrWarehouse> warehouseList, String warehouseId) {
        if (CollectionUtils.isEmpty(warehouseList) || StringUtils.isBlank(warehouseId)) {
            return new BuMtrWarehouse();
        }

        for (BuMtrWarehouse warehouse : warehouseList) {
            if (warehouseId.equals(warehouse.getId())) {
                return warehouse;
            }
        }

        return new BuMtrWarehouse();
    }

    private Integer getCategory1(String kind) {
        int category1 = -1;

        if (StringUtils.isBlank(kind)) {
            return category1;
        }

        switch (kind) {
            case "备品备件":
//                category1 = -1;
                break;
            case "耗材":
                category1 = 3;
                break;
            case "设备":
//                category1 = -1;
                break;
            case "工器具":
                category1 = 4;
                break;
            case "消耗物资":
                category1 = 3;
                break;
            case "劳保用品":
//                category1 = -1;
                break;
            default:
                break;
        }

        return category1;
    }

    private String getWarehouseCode(int level, Set<String> warehouseCodeSet) {
        String childSelfCode = level + RandomStringUtils.randomAlphanumeric(5);
        if (warehouseCodeSet.contains(childSelfCode)) {
            return getWarehouseCode(level, warehouseCodeSet);
        } else {
            return childSelfCode;
        }
    }

    private void extractDifferentProcessTypeStockList(List<BuMaterialStock> stockList, Set<String> oldStockIdSet, List<BuMaterialStock> needAddStockList, List<BuMaterialStock> needUpdateStockList, List<BuMaterialStock> needDeleteStockList) {
        for (BuMaterialStock stock : stockList) {
            if (BigDecimal.ZERO.compareTo(stock.getAmount()) == 0) {
                stock.setNeedDelete(true)
                        .setNeedAdd(false)
                        .setNeedUpdate(false);
            }

            boolean needAdd = null != stock.getNeedAdd() && stock.getNeedAdd();
            boolean needUpdate = null != stock.getNeedUpdate() && stock.getNeedUpdate();
            boolean needDelete = null != stock.getNeedDelete() && stock.getNeedDelete();

            if (needDelete && oldStockIdSet.contains(stock.getId())) {
                needDeleteStockList.add(stock);
            } else {
                if (needAdd && !oldStockIdSet.contains(stock.getId())) {
                    if (stock.getAmount().compareTo(stock.getOldAmount()) != 0) {
                        needAddStockList.add(stock);
                    }
                }
                if (needUpdate && oldStockIdSet.contains(stock.getId())) {
                    needUpdateStockList.add(stock);
                }
            }
        }
    }

    private BuMaterialAssignDetail extractMatchConsume(List<BuMaterialAssignDetail> assignDetailList, JdxInvbalancesOut maximoStock) {
        if (CollectionUtils.isEmpty(assignDetailList) || null == maximoStock) {
            return null;
        }

        // 物资编码
        String materialTypeId = maximoStock.getItemnum();
        // 批次
        String lotnum = maximoStock.getLotnum();
        // 库位、货位
        String location = maximoStock.getLocation();
        String binnum = maximoStock.getBinnum();
        // 数量
        Double changeAmount = Math.abs(maximoStock.getStockChange());
        // 变动时间
        Date insertdate = maximoStock.getInsertdate();

        // 查找匹配的消耗
        List<BuMaterialAssignDetail> matchList = new ArrayList<>();
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            boolean sameMaterial = materialTypeId.equals(assignDetail.getMaterialTypeId());
            boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(assignDetail.getTradeNo()) : lotnum.equals(assignDetail.getTradeNo());
            boolean sameWarehouse1 = location.equals(assignDetail.getEbsWhCode());
            boolean sameWarehouse2 = binnum.equals(assignDetail.getEbsWhChildCode());
            boolean sameAmount = changeAmount.equals(assignDetail.getAmount());
            boolean stockChangeLater = null == insertdate || null == assignDetail.getTransTime() || !insertdate.before(assignDetail.getTransTime());

            if (sameMaterial && sameTradeNo && sameWarehouse1 && sameWarehouse2 && sameAmount && stockChangeLater) {
                matchList.add(assignDetail);
            }
        }

        if (CollectionUtils.isNotEmpty(matchList)) {
            return matchList.get(0);
        } else {
            return null;
        }
    }

    //    private List<JdxInvbalancesOut> sumConsumeMatch(List<BuMaterialAssignDetail> assignDetailList, List<JdxInvbalancesOut> maximoStockList) {
//        if (CollectionUtils.isEmpty(assignDetailList) || CollectionUtils.isEmpty(maximoStockList)) {
//            return new ArrayList<>();
//        }
//
//        // 物资编码
//        String materialTypeId = assignDetailList.get(0).getMaterialTypeId();
//        // 批次
//        String tradeNo = assignDetailList.get(0).getTradeNo();
//        // 库位、货位
//        String ebsWhCode = assignDetailList.get(0).getEbsWhCode();
//        String ebsWhChildCode = assignDetailList.get(0).getEbsWhChildCode();
//        // 数量
//        Double consumeAmountSum = assignDetailList.stream()
//                .mapToDouble(BuMaterialAssignDetail::getAmount)
//                .sum();
//        // 变动时间
//        Date lastTransTime = null;
//        Optional<Date> transTimeOptional = assignDetailList.stream()
//                .map(BuMaterialAssignDetail::getTransTime)
//                .filter(Objects::nonNull)
//                .max(Date::compareTo);
//        if (transTimeOptional.isPresent()) {
//            lastTransTime = transTimeOptional.get();
//        }
//
//        // 查找匹配的库存变动记录
//        List<JdxInvbalancesOut> matchList = new ArrayList<>();
//        for (JdxInvbalancesOut maximoStock : maximoStockList) {
//            boolean sameMaterial = materialTypeId.equals(maximoStock.getItemnum());
//            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(maximoStock.getLotnum()) : tradeNo.equals(maximoStock.getLotnum());
//            boolean sameWarehouse1 = ebsWhCode.equals(maximoStock.getLocation());
//            boolean sameWarehouse2 = ebsWhChildCode.equals(maximoStock.getBinnum());
//            boolean sameAmount = consumeAmountSum.equals(Math.abs(maximoStock.getStockChange()));
//            boolean stockChangeLater = null == lastTransTime || (null != maximoStock.getInsertdate() && lastTransTime.before(maximoStock.getInsertdate()));
//
//            if (sameMaterial && sameTradeNo && sameWarehouse1 && sameWarehouse2 && sameAmount && stockChangeLater) {
//                matchList.add(maximoStock);
//            }
//        }
//        return matchList;
//    }
    private List<BuMaterialAssignDetail> sumConsumeMatch(List<BuMaterialAssignDetail> assignDetailList, JdxInvbalancesOut maximoStock) {
        if (CollectionUtils.isEmpty(assignDetailList) || null == maximoStock || null == maximoStock.getStockChange()) {
            return new ArrayList<>();
        }
        // 变动数量
        double stockChange = Math.abs(maximoStock.getStockChange());
        if (0D == stockChange) {
            return new ArrayList<>();
        }

        // 排序：按同步到maximo时间正序，保证早消耗的早处理
        assignDetailList.sort(Comparator.comparing(BuMaterialAssignDetail::getTransTime, Comparator.nullsLast(Comparator.naturalOrder())));

        // 查找匹配的物料消耗记录
        List<BuMaterialAssignDetail> matchList = new ArrayList<>();
        double needChangeAmount = stockChange;
        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            // 物资编码
            String materialTypeId = assignDetail.getMaterialTypeId();
            // 批次
            String tradeNo = assignDetail.getTradeNo();
            // 库位、货位
            String ebsWhCode = assignDetail.getEbsWhCode();
            String ebsWhChildCode = assignDetail.getEbsWhChildCode();
            // 变动时间
            Date lastTransTime = assignDetail.getTransTime();
            // 数量
            double amount = assignDetail.getAmount();
            if (0D == amount) {
                continue;
            }
            if (amount > needChangeAmount) {
                break;
            }

            boolean sameMaterial = materialTypeId.equals(maximoStock.getItemnum());
            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(maximoStock.getLotnum()) : tradeNo.equals(maximoStock.getLotnum());
            boolean sameWarehouse1 = ebsWhCode.equals(maximoStock.getLocation());
            boolean sameWarehouse2 = ebsWhChildCode.equals(maximoStock.getBinnum());
            boolean stockChangeLater = null == lastTransTime || null == maximoStock.getInsertdate() || !maximoStock.getInsertdate().before(lastTransTime);

            if (sameMaterial && sameTradeNo && sameWarehouse1 && sameWarehouse2 && stockChangeLater) {
                matchList.add(assignDetail);
                needChangeAmount = needChangeAmount - amount;
                if (needChangeAmount <= 0D) {
                    break;
                }
            }
        }

        return matchList;
    }

    private BuMaterialReturnedDetail extractMatchReturned(List<BuMaterialReturnedDetail> returnedDetailList, JdxInvbalancesOut maximoStock) {
        if (CollectionUtils.isEmpty(returnedDetailList) || null == maximoStock) {
            return null;
        }

        // 物资编码
        String materialTypeId = maximoStock.getItemnum();
        // 批次
        String lotnum = maximoStock.getLotnum();
        // 库位、货位
        String location = maximoStock.getLocation();
        String binnum = maximoStock.getBinnum();
        // 数量
        Double changeAmount = Math.abs(maximoStock.getStockChange());
        // 变动时间
        Date insertdate = maximoStock.getInsertdate();

        // 查找匹配的消耗
        List<BuMaterialReturnedDetail> matchList = new ArrayList<>();
        for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
            boolean sameMaterial = materialTypeId.equals(returnedDetail.getMaterialTypeId());
            boolean sameTradeNo = StringUtils.isBlank(lotnum) ? StringUtils.isBlank(returnedDetail.getTradeNo()) : lotnum.equals(returnedDetail.getTradeNo());
            boolean sameWarehouse1 = location.equals(returnedDetail.getEbsWhCode());
            boolean sameWarehouse2 = binnum.equals(returnedDetail.getEbsWhChildCode());
            boolean sameAmount = changeAmount.equals(returnedDetail.getReturnAmount());
            boolean stockChangeLater = null == insertdate || null == returnedDetail.getTransTime() || !insertdate.before(returnedDetail.getTransTime());

            if (sameMaterial && sameTradeNo && sameWarehouse1 && sameWarehouse2 && sameAmount && stockChangeLater) {
                matchList.add(returnedDetail);
            }
        }

        if (CollectionUtils.isNotEmpty(matchList)) {
            return matchList.get(0);
        } else {
            return null;
        }
    }

    //    private List<JdxInvbalancesOut> sumReturnedMatch(List<BuMaterialReturnedDetail> returnedDetailList, List<JdxInvbalancesOut> maximoStockList) {
//        if (CollectionUtils.isEmpty(returnedDetailList) || CollectionUtils.isEmpty(maximoStockList)) {
//            return new ArrayList<>();
//        }
//
//        // 物资编码
//        String materialTypeId = returnedDetailList.get(0).getMaterialTypeId();
//        // 批次
//        String tradeNo = returnedDetailList.get(0).getTradeNo();
//        // 库位、货位
//        String ebsWhCode = returnedDetailList.get(0).getEbsWhCode();
//        String ebsWhChildCode = returnedDetailList.get(0).getEbsWhChildCode();
//        // 数量
//        Double returnedAmountSum = returnedDetailList.stream()
//                .mapToDouble(BuMaterialReturnedDetail::getReturnAmount)
//                .sum();
//        // 变动时间
//        Date lastTransTime = null;
//        Optional<Date> transTimeOptional = returnedDetailList.stream()
//                .map(BuMaterialReturnedDetail::getTransTime)
//                .filter(Objects::nonNull)
//                .max(Date::compareTo);
//        if (transTimeOptional.isPresent()) {
//            lastTransTime = transTimeOptional.get();
//        }
//
//        // 查找匹配的库存变动记录
//        List<JdxInvbalancesOut> matchList = new ArrayList<>();
//        for (JdxInvbalancesOut maximoStock : maximoStockList) {
//            boolean sameMaterial = materialTypeId.equals(maximoStock.getItemnum());
//            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(maximoStock.getLotnum()) : tradeNo.equals(maximoStock.getLotnum());
//            boolean sameWarehouse1 = ebsWhCode.equals(maximoStock.getLocation());
//            boolean sameWarehouse2 = ebsWhChildCode.equals(maximoStock.getBinnum());
//            boolean sameAmount = returnedAmountSum.equals(Math.abs(maximoStock.getStockChange()));
//            boolean stockChangeLater = null == lastTransTime || (null != maximoStock.getInsertdate() && lastTransTime.before(maximoStock.getInsertdate()));
//
//            if (sameMaterial && sameTradeNo && sameWarehouse1 && sameWarehouse2 && sameAmount && stockChangeLater) {
//                matchList.add(maximoStock);
//            }
//        }
//        return matchList;
//    }
    private List<BuMaterialReturnedDetail> sumReturnedMatch(List<BuMaterialReturnedDetail> returnedDetailList, JdxInvbalancesOut maximoStock) {
        if (CollectionUtils.isEmpty(returnedDetailList) || null == maximoStock || null == maximoStock.getStockChange()) {
            return new ArrayList<>();
        }
        // 变动数量
        double stockChange = Math.abs(maximoStock.getStockChange());
        if (0D == stockChange) {
            return new ArrayList<>();
        }

        // 排序：按同步到maximo时间正序，保证早退料的早处理
        returnedDetailList.sort(Comparator.comparing(BuMaterialReturnedDetail::getTransTime, Comparator.nullsLast(Comparator.naturalOrder())));

        // 查找匹配的物料退料记录
        List<BuMaterialReturnedDetail> matchList = new ArrayList<>();
        double needChangeAmount = stockChange;
        for (BuMaterialReturnedDetail returnedDetail : returnedDetailList) {
            // 物资编码
            String materialTypeId = returnedDetail.getMaterialTypeId();
            // 批次
            String tradeNo = returnedDetail.getTradeNo();
            // 库位、货位
            String ebsWhCode = returnedDetail.getEbsWhCode();
            String ebsWhChildCode = returnedDetail.getEbsWhChildCode();
            // 变动时间
            Date lastTransTime = returnedDetail.getTransTime();
            // 数量
            double returnAmount = returnedDetail.getReturnAmount();
            if (0D == returnAmount) {
                continue;
            }
            if (returnAmount > needChangeAmount) {
                break;
            }

            boolean sameMaterial = materialTypeId.equals(maximoStock.getItemnum());
            boolean sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(maximoStock.getLotnum()) : tradeNo.equals(maximoStock.getLotnum());
            boolean sameWarehouse1 = ebsWhCode.equals(maximoStock.getLocation());
            boolean sameWarehouse2 = ebsWhChildCode.equals(maximoStock.getBinnum());
            boolean stockChangeLater = null == lastTransTime || null == maximoStock.getInsertdate() || !maximoStock.getInsertdate().before(lastTransTime);

            if (sameMaterial && sameTradeNo && sameWarehouse1 && sameWarehouse2 && stockChangeLater) {
                matchList.add(returnedDetail);
                needChangeAmount = needChangeAmount - returnAmount;
                if (needChangeAmount <= 0D) {
                    break;
                }
            }
        }

        return matchList;
    }

    private BuMaterialStock checkoutStock(BuMaterialAssignDetail assignDetail, List<BuMaterialStock> currentStockList, Map<String, BuMtrWarehouse> wbsWarehouseMap) {
        BuMtrWarehouse warehouse = wbsWarehouseMap.get(assignDetail.getLocationWbs());
        if (null == warehouse) {
            return null;
        }

        String warehouseId = warehouse.getId();
        String materialTypeId = assignDetail.getMaterialTypeId();
        List<BuMaterialStock> stockList = currentStockList.stream()
                .filter(stock -> warehouseId.equals(stock.getWarehouseId()) && materialTypeId.equals(stock.getMaterialTypeId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(stockList)) {
            return null;
        } else {
            return stockList.get(0);
        }
    }

    private BuMaterialStock checkoutStock(BuMaterialReturnedDetail returnedDetail, List<BuMaterialStock> currentStockList, Map<String, BuMtrWarehouse> wbsWarehouseMap) {
        BuMtrWarehouse warehouse = wbsWarehouseMap.get(returnedDetail.getLocationWbs());
        if (null == warehouse) {
            return null;
        }

        String warehouseId = warehouse.getId();
        String materialTypeId = returnedDetail.getMaterialTypeId();
        List<BuMaterialStock> stockList = currentStockList.stream()
                .filter(stock -> warehouseId.equals(stock.getWarehouseId()) && materialTypeId.equals(stock.getMaterialTypeId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(stockList)) {
            return null;
        } else {
            return stockList.get(0);
        }
    }

}
