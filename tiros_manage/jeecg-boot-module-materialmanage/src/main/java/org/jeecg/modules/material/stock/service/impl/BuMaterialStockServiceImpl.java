package org.jeecg.modules.material.stock.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.common.tiros.cache.warehouse.WarehouseCacheService;
import org.jeecg.common.tiros.stock.use.bean.BuMaterialStockUse;
import org.jeecg.common.tiros.stock.use.service.BuMaterialStockUseService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.tiros.util.TirosUtil;
import org.jeecg.common.tiros.util.UploadFileCheckUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.apply.bean.BuMaterialAssignDetail;
import org.jeecg.modules.material.apply.mapper.BuMaterialAssignDetailMapper;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.mapper.BuMaterialEntryDetailMapper;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
import org.jeecg.modules.material.material.service.BuMaterialTypeReplaceService;
import org.jeecg.modules.material.stock.bean.BuMaterialEntryAttr;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockDetailVO;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockQueryVO;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockTradeNo;
import org.jeecg.modules.material.stock.mapper.BuMaterialEntryAttrMapper;
import org.jeecg.modules.material.stock.mapper.BuMaterialStockMapper;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.bean.bo.BuWarehouseExcelBONew;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.jeecg.modules.material.warehouse.service.BuMtrWarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Slf4j
@Service
public class BuMaterialStockServiceImpl extends ServiceImpl<BuMaterialStockMapper, BuMaterialStock> implements BuMaterialStockService {

    @Resource
    private BuMaterialStockMapper buMaterialStockMapper;
    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;
    @Resource
    private BuMaterialEntryDetailMapper buMaterialEntryDetailMapper;
    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;
    @Resource
    private BuMaterialAssignDetailMapper buMaterialAssignDetailMapper;
    @Resource
    private BuMaterialEntryAttrMapper buMaterialEntryAttrMapper;
    @Resource
    private BuMtrWarehouseService buMtrWarehouseService;
    @Resource
    private BuMaterialStockUseService buMaterialStockUseService;
    @Resource
    private WarehouseCacheService warehouseCacheService;
    @Resource
    private BuMaterialTypeReplaceService buMaterialTypeReplaceService;

    @Value("${tiros.base.rootWarehouse:2}")
    private String rootWarehouse;

    @Value("${tiros.sync.maximo:http://localhost:8090/}")
    private String maximoSyncUrl;


    /**
     * @see BuMaterialStockService#pageStock(BuMaterialStockQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialStock> pageStock(BuMaterialStockQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        if (StringUtils.isNotBlank(queryVO.getWarehouseId())) {
            Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
            CacheWarehouseBO warehouseBO = warehouseBOMap.get(queryVO.getWarehouseId());
            if (null != warehouseBO) {
                queryVO.setWbs(warehouseBO.getWbs());
            }
        }
        // 关联可替换物资
        if (StringUtils.isNotBlank(queryVO.getSearchText())) {
            List<String> canReplaceMaterialTypeIdList = buMaterialTypeReplaceService.listCanReplaceMaterialTypeIdByMaterialTypeText(queryVO.getSearchText());
            queryVO.setSearchMaterialTypeIdList(canReplaceMaterialTypeIdList);
        }

        IPage<BuMaterialStock> page = buMaterialStockMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        // 设置来源库位，暂时为硬编码
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            for (BuMaterialStock stock : page.getRecords()) {
                stock.setSourceLocationName(TirosUtil.extractSourceLocationName(stock.getWarehouseWbs(), stock.getWarehouseName()));
            }
        }

        // 减去库存已占用的数量
        deleteStockUsedAmount(page.getRecords(), null, null);
        return page;
    }

    /**
     * @see BuMaterialStockService#listStockByMaterialTypeId(String, List, String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialStock> listStockByMaterialTypeId(String materialTypeId, List<Integer> warehouseLevelList, String applyDetailId, String assignDetailId) throws Exception {
        List<String> oldAssignDetailIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(assignDetailId)) {
            oldAssignDetailIdList.add(assignDetailId);
        } else {
            if (StringUtils.isNotBlank(applyDetailId)) {
                // 查询旧的分配明细（旧的分配明细不占用库存：可重复分配）
                List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailMapper.selectListByApplyDetailId(applyDetailId);
                oldAssignDetailIdList = assignDetailList
                        .stream()
                        .map(BuMaterialAssignDetail::getId)
                        .distinct()
                        .collect(Collectors.toList());
            }
        }

        // 查询仓库
        Map<String, CacheWarehouseBO> warehouseBOMap = warehouseCacheService.map();
        // 查询库存
//        // 关联可替换物资
//        List<String> canReplaceMaterialTypeIdList = buMaterialTypeReplaceService.listCanReplaceMaterialTypeIdByMaterialTypeText(materialTypeId);
//        List<BuMaterialStock> stockList = buMaterialStockMapper.selectListByMaterialTypeIdList(canReplaceMaterialTypeIdList);
        List<BuMaterialStock> stockList = buMaterialStockMapper.selectListByMaterialTypeId(materialTypeId);
        // 减去库存已占用的数量
        deleteStockUsedAmount(stockList, warehouseBOMap, oldAssignDetailIdList);
        // 通过仓库级别要求过滤
        List<BuMaterialStock> resultStockList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(warehouseLevelList)) {
            for (Integer warehouseLevel : warehouseLevelList) {
                int dbLevel = warehouseLevel + 1;
                for (BuMaterialStock stock : stockList) {
                    if (stock.getWarehouseLevel() == dbLevel) {
                        BuMaterialStock resultStock = new BuMaterialStock();
                        BeanUtils.copyProperties(stock, resultStock);
                        resultStockList.add(resultStock);
                    }
                }
            }
        } else {
            for (BuMaterialStock stock : stockList) {
                BuMaterialStock resultStock = new BuMaterialStock();
                BeanUtils.copyProperties(stock, resultStock);
                resultStockList.add(resultStock);
            }
        }
        // 设置批次选择
        setTradeNoChoice(resultStockList, stockList, warehouseBOMap, oldAssignDetailIdList);

        // 设置来源库位，暂时为硬编码
        if (CollectionUtils.isNotEmpty(resultStockList)) {
            for (BuMaterialStock stock : resultStockList) {
                stock.setSourceLocationName(TirosUtil.extractSourceLocationName(stock.getWarehouseWbs(), stock.getWarehouseName()));
            }
        }

        return resultStockList;
    }

    /**
     * @see BuMaterialStockService#pageStock(BuMaterialStockQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialStockDetailVO getMaterialStockDetail(String materialTypeId) throws Exception {
        // 物资类型信息
        BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);

        BuMaterialStockDetailVO stockDetailVO = new BuMaterialStockDetailVO();
        BeanUtils.copyProperties(materialType, stockDetailVO);
        stockDetailVO.setMaterialTypeId(materialTypeId);

        // 当前库存总量
        List<BuMaterialStock> stockList = buMaterialStockMapper.selectListByMaterialTypeId(materialTypeId);
        if (CollectionUtils.isNotEmpty(stockList)) {
            // 不统计3级库以下的
            stockList.removeIf(stock -> null != stock.getWarehouseLevel() && stock.getWarehouseLevel() >= 5);
            // 减去库存已占用的数量
            deleteStockUsedAmount(stockList, null, null);

            BigDecimal usableAmountSum = stockList.stream()
                    .map(BuMaterialStock::getUsableAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stockDetailVO.setCurrentAmount(usableAmountSum);

            // 入库明细
            List<String> entryDetailIdList = stockList.stream()
                    .map(BuMaterialStock::getEntryDetailId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(entryDetailIdList)) {
                List<BuMaterialEntryDetail> entryDetailList = buMaterialEntryDetailMapper.selectBatchIds(entryDetailIdList);
                stockDetailVO.setEntryDetailList(entryDetailList);
            }
        }
        return stockDetailVO;
    }

    /**
     * @see BuMaterialStockService#listStockByMaterialTypeIdList(List, boolean)
     */
    @Override
    public List<BuMaterialStock> listStockByMaterialTypeIdList(List<String> materialTypeIdList, boolean filterWarehouseLevel4) {
        if (CollectionUtils.isEmpty(materialTypeIdList)) {
            return new ArrayList<>();
        }

        List<BuMaterialStock> stockList = new ArrayList<>();
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(new ArrayList<>(new HashSet<>(materialTypeIdList)));
        for (List<String> batchSub : batchSubList) {
            List<BuMaterialStock> subStockList = buMaterialStockMapper.selectListByMaterialTypeIdList(batchSub);
            stockList.addAll(subStockList);
        }
        // 去掉库存量为0的
        stockList.removeIf(stock -> null == stock.getAmount() || BigDecimal.ZERO.compareTo(stock.getAmount()) == 0);
        if (filterWarehouseLevel4) {
            // 不统计3级库以下的
            stockList.removeIf(stock -> null != stock.getWarehouseLevel() && stock.getWarehouseLevel() >= 5);
        }

        return stockList;
    }

    /**
     * @see BuMaterialStockService#deleteStockUsedAmount(List, Map, List)
     */
    @Override
    public void deleteStockUsedAmount(List<BuMaterialStock> stockList, Map<String, CacheWarehouseBO> warehouseBOMap, List<String> notNeedDeleteAssignDetailIdList) {
        if (CollectionUtils.isEmpty(stockList)) {
            return;
        }

        if (null == warehouseBOMap || warehouseBOMap.isEmpty()) {
            warehouseBOMap = warehouseCacheService.map();
        }

        List<String> materialTypeIdList = new ArrayList<>();
        for (BuMaterialStock stock : stockList) {
            stock.setUsableAmount(stock.getAmount())
                    .setUsedDetailInfo("");
            if (!materialTypeIdList.contains(stock.getMaterialTypeId())) {
                materialTypeIdList.add(stock.getMaterialTypeId());
            }
        }
        List<BuMaterialStockUse> stockUseList = buMaterialStockUseService.listStockUseByMaterialTypeIdList(materialTypeIdList);
        if (CollectionUtils.isNotEmpty(stockUseList)) {
            if (CollectionUtils.isNotEmpty(notNeedDeleteAssignDetailIdList)) {
                stockUseList.removeIf(stockUse -> notNeedDeleteAssignDetailIdList.contains(stockUse.getAssignDetailId()));
            }

            for (BuMaterialStock stock : stockList) {
                String warehouseId = stock.getWarehouseId();
                String materialTypeId = stock.getMaterialTypeId();
                String tradeNo = stock.getTradeNo();

                boolean warehouseLevel4 = false;
                CacheWarehouseBO warehouse = warehouseBOMap.get(warehouseId);
                if (null != warehouse && warehouse.getWhLevel() >= 5) {
                    warehouseLevel4 = true;
                }

                List<BuMaterialStockUse> matchStockUseList = new ArrayList<>();
                for (BuMaterialStockUse stockUse : stockUseList) {
                    boolean sameMaterial = materialTypeId.equals(stockUse.getMaterialTypeId());
                    boolean sameWarehouse = warehouseId.equals(stockUse.getWarehouseId());
                    boolean sameTradeNo = true;
                    if (!warehouseLevel4) {
                        sameTradeNo = StringUtils.isBlank(tradeNo) ? StringUtils.isBlank(stockUse.getTradeNo()) : tradeNo.equals(stockUse.getTradeNo());
                    }

                    if (sameMaterial && sameWarehouse && sameTradeNo) {
                        matchStockUseList.add(stockUse);
                    }
                }

                if (CollectionUtils.isNotEmpty(matchStockUseList)) {
                    double useAmountSum = 0;
                    StringBuilder usedDetailInfoBuilder = new StringBuilder();
                    for (BuMaterialStockUse stockUse : matchStockUseList) {
                        double useAmount = stockUse.getUseAmount();
                        String orderCode = stockUse.getOrderCode();

                        useAmountSum = useAmountSum + useAmount;
                        usedDetailInfoBuilder.append("工单").append(orderCode).append("占用").append(BigDecimal.valueOf(useAmount).stripTrailingZeros().toPlainString()).append("，");
                    }

                    BigDecimal usableAmount = stock.getAmount().subtract(BigDecimal.valueOf(useAmountSum));
                    String usedDetailInfo = "";
                    if (usedDetailInfoBuilder.length() > 0) {
                        usedDetailInfo = usedDetailInfoBuilder.deleteCharAt(usedDetailInfoBuilder.length() - 1).toString();
                    }
                    stock.setUsableAmount(usableAmount)
                            .setUsedDetailInfo(usedDetailInfo);
                }
            }
        }
    }

    /**
     * @see BuMaterialStockService#setTradeNoChoice(List, List, Map, List)
     */
    @Override
    public void setTradeNoChoice(List<BuMaterialStock> resultStockList, List<BuMaterialStock> materialStockList, Map<String, CacheWarehouseBO> warehouseBOMap, List<String> notNeedDeleteAssignDetailIdList) {
        if (CollectionUtils.isEmpty(resultStockList)) {
            return;
        }

        if (null == warehouseBOMap || warehouseBOMap.isEmpty()) {
            warehouseBOMap = warehouseCacheService.map();
        }

        if (null == materialStockList) {
            List<String> materialTypeIdList = resultStockList.stream()
                    .map(BuMaterialStock::getMaterialTypeId)
                    .distinct()
                    .collect(Collectors.toList());
            materialStockList = buMaterialStockMapper.selectListByMaterialTypeIdList(materialTypeIdList);
            // 减去库存已占用的数量
            deleteStockUsedAmount(materialStockList, warehouseBOMap, notNeedDeleteAssignDetailIdList);
        }

        for (BuMaterialStock resultStock : resultStockList) {
            if (resultStock.getWarehouseLevel() >= 5) {
                // 4级库位及以上，查3级库位
                String warehouseId = resultStock.getWarehouseId();
                String level3WarehouseId = getLevel3WarehouseId(warehouseId, warehouseBOMap);
                String materialTypeId = resultStock.getMaterialTypeId();

                // 查询3级库位的库存
                List<BuMaterialStock> level3StockList = getStockListByWarehouseAndMaterial(materialStockList, level3WarehouseId, materialTypeId);
                if (CollectionUtils.isNotEmpty(level3StockList)) {
                    Map<String, List<BuMaterialStock>> tradeNoLevel3StockListMap = level3StockList.stream()
                            .collect(Collectors.groupingBy(BuMaterialStock::getTradeNo));
                    // 只有1个批次时，也设置为 需要选择3级库批次号，用于自动分配时的处理库存可用量
//                    if (tradeNoLevel3StockListMap.size() == 1) {
//                        for (Map.Entry<String, List<BuMaterialStock>> tradeNoLevel3StockListEntry : tradeNoLevel3StockListMap.entrySet()) {
//                            String tradeNo = tradeNoLevel3StockListEntry.getKey();
//                            resultStock.setTradeNo(tradeNo)
//                                    .setNeedChooseTradeNo(false);
//                        }
//                    } else {
                        List<BuMaterialStockTradeNo> stockTradeNoList = new ArrayList<>();
                        for (Map.Entry<String, List<BuMaterialStock>> tradeNoLevel3StockListEntry : tradeNoLevel3StockListMap.entrySet()) {
                            String tradeNo = tradeNoLevel3StockListEntry.getKey();
                            List<BuMaterialStock> list = tradeNoLevel3StockListEntry.getValue();

                            BigDecimal totalUsableAmount = list.stream()
                                    .map(BuMaterialStock::getUsableAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            BuMaterialStockTradeNo stockTradeNo = new BuMaterialStockTradeNo()
                                    .setLevel3StockId(list.get(0).getId())
                                    .setLevel3WarehouseId(list.get(0).getWarehouseId())
                                    .setLevel3WarehouseName(list.get(0).getWarehouseName())
                                    .setTradeNo(tradeNo)
                                    .setAmount(totalUsableAmount)
                                    .setPrice(list.get(0).getPrice());
                            stockTradeNoList.add(stockTradeNo);
                        }
                        stockTradeNoList.sort(Comparator.comparing(BuMaterialStockTradeNo::getTradeNo, Comparator.nullsLast(Comparator.naturalOrder())));
                        resultStock.setTradeNoChoiceList(stockTradeNoList)
                                .setNeedChooseTradeNo(true);
//                    }
                } else {
//                    CacheWarehouseBO warehouse = warehouseBOMap.get(warehouseId);
//                    CacheWarehouseBO level3Warehouse = warehouseBOMap.get(level3WarehouseId);
//                    String errorMessage = String.format("物料%s：4级库位%s，数量%s，无法匹配到所属的上级3级库，请核实",
//                            materialTypeId, warehouse.getName(), resultStock.getAmount().stripTrailingZeros().toPlainString());
//                    throw new JeecgBootException(errorMessage);
                    resultStock.setTradeNoChoiceList(new ArrayList<>())
                            .setNeedChooseTradeNo(true);
                }
            } else {
                resultStock.setNeedChooseTradeNo(false);
            }
        }
    }

    /**
     * @see BuMaterialStockService#getMaterialTradeAttr(String, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialEntryAttr getMaterialTradeAttr(String materialTypeId, String tradeNo) throws Exception {
        BuMaterialType materialType = buMaterialTypeMapper.selectById(materialTypeId);
        if (null == materialType) {
            throw new JeecgBootException("物资类型" + materialTypeId + "不存在");
        }

        List<BuMaterialEntryAttr> entryAttrList = buMaterialEntryAttrMapper.selectListByMaterialAndTrade(materialTypeId, tradeNo);
        if (entryAttrList.size() == 1) {
            return entryAttrList.get(0);
        }

        if (entryAttrList.size() >= 2) {
            List<String> needDeleteIdList = new ArrayList<>();
            for (int i = 1; i < entryAttrList.size(); i++) {
                String id = entryAttrList.get(i).getId();
                needDeleteIdList.add(id);
            }
            buMaterialEntryAttrMapper.deleteBatchIds(needDeleteIdList);

            return entryAttrList.get(0);
        }

        BuMaterialEntryAttr tradeAttr = new BuMaterialEntryAttr()
                .setId(UUIDGenerator.generate())
                .setMaterialTypeId(materialTypeId)
                .setTradeNo(tradeNo)
                .setEntryDate(getEntryDateByTradeNo(tradeNo))
                .setLeaveFactory(null)
                .setProductionDate(null)
                .setExpirDay(0)
                .setExpirDate(null)
                .setMaterialTypeName(materialType.getName())
                .setMaterialTypeSpec(materialType.getSpec());
        buMaterialEntryAttrMapper.insert(tradeAttr);
        return tradeAttr;
    }

    /**
     * @see BuMaterialStockService#setMaterialTradeAttr(BuMaterialEntryAttr)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setMaterialTradeAttr(BuMaterialEntryAttr tradeAttr) throws Exception {
        if (StringUtils.isBlank(tradeAttr.getId())) {
            tradeAttr.setId(UUIDGenerator.generate());
        }

        buMaterialEntryAttrMapper.deleteById(tradeAttr.getId());
        buMaterialEntryAttrMapper.insert(tradeAttr);

        return true;
    }

    /**
     * @see BuMaterialStockService#getMaterialSumStockAvailableAmount(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public double getMaterialSumStockAvailableAmount(String materialTypeId) throws Exception {
        // 库存
        List<BuMaterialStock> stockList = buMaterialStockMapper.selectListByMaterialTypeId(materialTypeId);
        // 不统计3级库以下的
        stockList.removeIf(stock -> null != stock.getWarehouseLevel() && stock.getWarehouseLevel() >= 5);
        if (CollectionUtils.isEmpty(stockList)) {
            return 0D;
        }
        BigDecimal stockAmount = stockList.stream()
                .map(BuMaterialStock::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 占用
        List<BuMaterialStockUse> stockUseList = buMaterialStockUseService.listStockUseByMaterialTypeId(materialTypeId);
        // 不统计3级库以下的
        stockUseList.removeIf(stockUse -> null != stockUse.getWarehouseLevel() && stockUse.getWarehouseLevel() >= 5);
        if (CollectionUtils.isEmpty(stockUseList)) {
            return stockAmount.doubleValue();
        } else {
            double stockUseAmount = stockUseList.stream()
                    .mapToDouble(BuMaterialStockUse::getUseAmount)
                    .sum();
            return stockAmount.subtract(BigDecimal.valueOf(stockUseAmount)).doubleValue();
        }
    }

    /**
     * @see BuMaterialStockService#importLevel4Stock(MultipartFile)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean importLevel4Stock(MultipartFile excelFile) throws Exception {
        // 检查文件类型
        UploadFileCheckUtil.checkFileValid(excelFile, null, Arrays.asList(".xls", ".xlsx"));

        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }
        // 导入4级库位信息
        boolean flag = buMtrWarehouseService.importWarehouseInfoFromExcel(excelFile, this.rootWarehouse);

        if (!flag) {
            return false;
        }

        // 同步maximo 库存
        JSONObject result = RestUtil.get(this.maximoSyncUrl + "third/maximo/read/material-stock?clearAllOldStock=true");
        boolean success = result.getBoolean("success");
        if (!success) {
            // 同步库存没有成功
            throw new RuntimeException("同步库存失败！");
        }

        InputStream inputStream = excelFile.getInputStream();
        Workbook workbook;

        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equals("xls", extName)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals("xlsx", extName)) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new JeecgBootException("错误的excel文件");
        }
        inputStream.close();

        // 查询并构建二级库下仓库树
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectList(Wrappers.emptyWrapper());
        List<BuMtrWarehouse> level2WarehouseList = warehouseList.stream()
                .filter(warehouse -> warehouse.getWhLevel() == 3)
                .collect(Collectors.toList());
        for (BuMtrWarehouse level2Warehouse : level2WarehouseList) {
            recurseAddChild(level2Warehouse, warehouseList);
        }

        // 清除所有4级库的库存
        List<String> level5WarehouseIdList = warehouseList.stream()
                .filter(warehouse -> warehouse.getWhLevel() == 5)
                .map(BuMtrWarehouse::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(level5WarehouseIdList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(level5WarehouseIdList);
            for (List<String> batchSub : batchSubList) {
                LambdaQueryWrapper<BuMaterialStock> stockWrapper = new LambdaQueryWrapper<BuMaterialStock>()
                        .in(BuMaterialStock::getWarehouseId, batchSub);
                buMaterialStockMapper.delete(stockWrapper);
            }
        }

        int numberOfSheets = workbook.getNumberOfSheets();
        log.info("导入excel4级库库存数量开始");
        log.info("文件【" + filename + "】总共表单个数=" + numberOfSheets);

        boolean hasMatchingForms = false;
        List<BuWarehouseExcelBONew> excelBOList = new ArrayList<>();
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            log.info(String.format("第%s个表单【%s】", sheetNum + 1, sheet.getSheetName()));

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer level2CellNum = null;
            Integer level3CellNum = null;
            Integer level4CellNum = null;
            Integer amountCellNum = null;
            Integer materialTypeCodeCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = getCellValue(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (value.equals("库房")) {
                            level2CellNum = cellNum;
                        } else if (value.equals("货位")) {
                            level3CellNum = cellNum;
                        } else if (value.contains("四级货位")) {
                            level4CellNum = cellNum;
                        } else if (value.equals("数量")) {
                            amountCellNum = cellNum;
                        } else if (value.equals("物料编码")) {
                            materialTypeCodeCellNum = cellNum;
                        }
                    }
                }
            }

            if (null == level2CellNum || null == level3CellNum || null == level4CellNum || null == amountCellNum || null == materialTypeCodeCellNum) {
                log.info(String.format("第%s个表单%s,没有4级库库存数量信息,不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                log.info(String.format("第%s个表单%s,有4级库库存数量信息,开始导入", sheetNum + 1, sheet.getSheetName()));

                hasMatchingForms = true;
                int rowCount = 0;

                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    String level2 = getCellValue(sheet.getRow(rowNum).getCell(level2CellNum));
                    String level3 = getCellValue(sheet.getRow(rowNum).getCell(level3CellNum));
                    String level4 = getCellValue(sheet.getRow(rowNum).getCell(level4CellNum));
                    String amount = getCellValue(sheet.getRow(rowNum).getCell(amountCellNum));
                    String materialTypeCode = getCellValue(sheet.getRow(rowNum).getCell(materialTypeCodeCellNum));

                    excelBOList.add(
                            new BuWarehouseExcelBONew()
                                    .setLevel2(level2)
                                    .setLevel3(level3)
                                    .setLevel4(level4)
                                    .setAmount(Double.valueOf(amount))
                                    .setMaterialTypeCode(materialTypeCode)
                    );

                    rowCount++;
                }
                log.info("总共行数" + rowCount);
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            log.info("文件【" + filename + "】未匹配到4级库库存数量模板表单");
            throw new JeecgBootException("请选择正确的模板表单");
        } else {
            dealSaveLevel4Stock(excelBOList, level2WarehouseList);
        }

        return true;
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

    private List<BuMaterialStock> getStockListByWarehouseAndMaterial(List<BuMaterialStock> stockList, String warehouseId, String materialTypeId) {
        return stockList.stream()
                .filter(stock -> warehouseId.equals(stock.getWarehouseId())
                        && materialTypeId.equals(stock.getMaterialTypeId()))
                .collect(Collectors.toList());
    }

    private void recurseAddChild(BuMtrWarehouse warehouse, List<BuMtrWarehouse> warehouseList) {
        if (null == warehouse) {
            return;
        }

        String id = warehouse.getId();
        List<BuMtrWarehouse> children = warehouseList.stream()
                .filter(detail -> StringUtils.isNotBlank(id) && id.equals(detail.getParentId()))
                .sorted(Comparator.comparing(BuMtrWarehouse::getHasChildren, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        warehouse.setChildren(children);
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuMtrWarehouse child : children) {
                recurseAddChild(child, warehouseList);
            }
        }
    }

    private String getCellValue(Cell cell) {
        String value = "";
        if (null == cell) {
            return value;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }

        return value.trim();
    }

    private void dealSaveLevel4Stock(List<BuWarehouseExcelBONew> excelBOList, List<BuMtrWarehouse> level2WarehouseList) {
        if (CollectionUtils.isEmpty(excelBOList)) {
            return;
        }

        List<String> materialTypeCodeList = excelBOList.stream()
                .map(BuWarehouseExcelBONew::getMaterialTypeCode)
                .distinct()
                .collect(Collectors.toList());
        Map<String, BuMaterialType> codeMaterialTypeMap = new HashMap<>();
        List<List<String>> materialTypeCodeBatchSubList = DatabaseBatchSubUtil.batchSubList(materialTypeCodeList);
        for (List<String> materialTypeCodeBatchSub : materialTypeCodeBatchSubList) {
            LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
                    .in(BuMaterialType::getCode, materialTypeCodeBatchSub);
            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
            materialTypeList.forEach(materialType -> codeMaterialTypeMap.put(materialType.getCode(), materialType));
        }

        List<BuMaterialStock> level4StockList = new ArrayList<>();
        for (BuWarehouseExcelBONew excelBO : excelBOList) {
            String level2 = excelBO.getLevel2();
            String level3 = excelBO.getLevel3();
            String level4 = excelBO.getLevel4();
            Double amount = excelBO.getAmount();
            String materialTypeCode = excelBO.getMaterialTypeCode();
            BuMaterialType materialType = codeMaterialTypeMap.get(materialTypeCode);
            if (null == materialType || null == amount || amount <= 0D) {
                continue;
            }

            BuMtrWarehouse level4Warehouse = getLevel4Warehouse(level2, level3, level4, level2WarehouseList);
            BuMaterialStock level4Stock = new BuMaterialStock()
                    .setId(UUIDGenerator.generate())
                    .setWarehouseId(level4Warehouse.getId())
                    .setMaterialTypeId(materialType.getId())
                    .setAmount(BigDecimal.valueOf(amount))
                    .setPrice(materialType.getPrice())
                    .setEntryDetailId(null)
                    .setTradeNo(null)
                    .setCompanyId(level4Warehouse.getCompanyId())
                    .setWorkshopId(level4Warehouse.getWorkshopId())
                    .setLineId(level4Warehouse.getLineId());
            level4StockList.add(level4Stock);
        }

        if (CollectionUtils.isNotEmpty(level4StockList)) {
            List<List<BuMaterialStock>> level4StockListBatchSubList = DatabaseBatchSubUtil.batchSubList(level4StockList);
            for (List<BuMaterialStock> level4StockListBatchSub : level4StockListBatchSubList) {
                buMaterialStockMapper.insertList(level4StockListBatchSub);
            }
        }
    }

    private BuMtrWarehouse getLevel4Warehouse(String level2, String level3, String level4, List<BuMtrWarehouse> level2WarehouseList) {
        BuMtrWarehouse level2Warehouse = getWarehouse(level2, level2WarehouseList);
        if (null == level2Warehouse) {
            throw new JeecgBootException("二级库[" + level2 + "]不存在，请先导入仓库信息");
        }

        List<BuMtrWarehouse> level3WarehouseList = level2Warehouse.getChildren();
        BuMtrWarehouse level3Warehouse = getWarehouse(level3, level3WarehouseList);
        if (null == level3Warehouse) {
            throw new JeecgBootException("三级库[" + level3 + "]不存在，请先导入仓库信息");
        }

        List<BuMtrWarehouse> level4WarehouseList = level3Warehouse.getChildren();
        BuMtrWarehouse level4Warehouse = getWarehouse(level4, level4WarehouseList);
        if (null == level4Warehouse) {
            throw new JeecgBootException("四级库[" + level4 + "]不存在，请先导入仓库信息");
        }

        return level4Warehouse;
    }

    private BuMtrWarehouse getWarehouse(String name, List<BuMtrWarehouse> warehouseList) {
        if (CollectionUtils.isEmpty(warehouseList)) {
            return null;
        }

        List<BuMtrWarehouse> matchWarehouseList = warehouseList.stream()
                .filter(warehouse -> name.equals(warehouse.getName()))
                .sorted(Comparator.comparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchWarehouseList)) {
            return null;
        }
        return matchWarehouseList.get(0);
    }

    private Date getEntryDateByTradeNo(String tradeNo) throws ParseException {
        if (StringUtils.isBlank(tradeNo)) {
            return null;
        }

        // 批次号如：RKJXCG20180327001
        // 这里按硬编码方式处理：先获取数字，再解析日期
        String numeric = TirosUtil.extractStringNumeric(tradeNo);
        String dateString = numeric.substring(0, 8);
        return DateUtils.yyyyMMdd.get().parse(dateString);
    }

}
