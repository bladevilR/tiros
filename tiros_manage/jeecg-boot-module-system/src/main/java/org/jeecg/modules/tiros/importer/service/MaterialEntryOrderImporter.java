//package org.jeecg.modules.tiros.importer.service;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.hssf.usermodel.HSSFDateUtil;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.shiro.SecurityUtils;
//import org.jeecg.common.exception.JeecgBootException;
//import org.jeecg.common.system.vo.LoginUser;
//import org.jeecg.common.util.UUIDGenerator;
//import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
//import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;
//import org.jeecg.modules.material.entry.mapper.BuMaterialEntryDetailMapper;
//import org.jeecg.modules.material.entry.mapper.BuMaterialEntryOrderMapper;
//import org.jeecg.modules.material.material.bean.BuMaterialType;
//import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;
//import org.jeecg.modules.material.stock.bean.BuMaterialStock;
//import org.jeecg.modules.material.stock.mapper.BuMaterialStockMapper;
//import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
//import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
//import org.jeecg.modules.system.entity.SysDepart;
//import org.jeecg.modules.system.mapper.SysDepartMapper;
//import org.jeecg.modules.tiros.importer.dao.ImporterSql;
//import org.jeecg.modules.tiros.importer.entity.EntryOrderExcelBO;
//import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
//import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author yfy
// * @title: MaterialEntryOrderImporter
// * @projectName tiros_manage
// * @description: TODO
// * @date 2021/5/7 11:35
// */
//@Repository
//public class MaterialEntryOrderImporter {
//
//    @Resource
//    private ImporterSql importerSql;
//    @Resource
//    private BuMaterialTypeMapper buMaterialTypeMapper;
//    @Resource
//    private SysDepartMapper sysDepartMapper;
//    @Resource
//    private BuMaterialEntryOrderMapper buMaterialEntryOrderMapper;
//    @Resource
//    private BuMaterialEntryDetailMapper buMaterialEntryDetailMapper;
//    @Resource
//    private BuMtrWarehouseMapper buMtrWarehouseMapper;
//    @Resource
//    private BuMaterialStockMapper buMaterialStockMapper;
//
//    public void importMaterialEntryOrder(String fileName, InputStream inputStream) throws Exception {
//
//        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
//        int lastRowNum = sheet.getLastRowNum();
//
//        // 获取登录用户信息
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        String userId = sysUser.getId();
//
//        try {
//            Row row1 = sheet.getRow(0);
//            String title = ExcelUtil.getCellValue(row1.getCell(0)).trim();
//            if (title == null || !title.equals("架大修二级库移入明细表")){
//                throw new JeecgBootException("请选择正确的模板导入！");
//            }
//
//
//            List<EntryOrderExcelBO> excelBOList = new ArrayList<>();
//            Map<String, Date> entryOrderDateMap = new HashMap<>();
//            Map<String, EntryOrderExcelBO> materialCodeExcelBOMap = new HashMap<>();
//            //读取每一行数据
//            for (int rowIndex = 4; rowIndex < lastRowNum + 1; rowIndex++) {
//                Row row = sheet.getRow(rowIndex);
//                String entryOrder = ExcelUtil.getCellValue(row.getCell(1)).trim();
//                Date entryDate = HSSFDateUtil.getJavaDate(Double.parseDouble(ExcelUtil.getCellValue(row.getCell(2)).trim()));
//                String[] split = ExcelUtil.getCellValue(row.getCell(3)).split("\\'");
//                String materialCode = split[1].trim();
//                String materialDesc = ExcelUtil.getCellValue(row.getCell(4)).trim();
//                String amount = ExcelUtil.getCellValue(row.getCell(5)).trim();
//                String price = ExcelUtil.getCellValue(row.getCell(6)).trim();
//                String totalPrice = ExcelUtil.getCellValue(row.getCell(7)).trim();
//                String departName = ExcelUtil.getCellValue(row.getCell(8)).trim();
//                String ebsLevel2 = ExcelUtil.getCellValue(row.getCell(9)).trim();
//                String ebsLevel3 = ExcelUtil.getCellValue(row.getCell(10)).trim();
//                String category3 = ExcelUtil.getCellValue(row.getCell(11)).trim();
//                String isAsset = ExcelUtil.getCellValue(row.getCell(12)).trim();
//                String isConsume = ExcelUtil.getCellValue(row.getCell(13)).trim();
//
//                EntryOrderExcelBO excelBO = new EntryOrderExcelBO()
//                        .setEntryOrder(entryOrder)
//                        .setEntryDate(entryDate)
//                        .setMaterialCode(materialCode)
//                        .setMaterialDesc(materialDesc)
//                        .setAmount(amount)
//                        .setPrice(price)
//                        .setTotalPrice(totalPrice)
//                        .setDepartName(departName)
//                        .setEbsLevel2(ebsLevel2)
//                        .setEbsLevel3(ebsLevel3)
//                        .setCategory3(category3)
//                        .setIsAsset(isAsset)
//                        .setIsConsume(isConsume);
//                excelBOList.add(excelBO);
//                entryOrderDateMap.put(entryOrder, entryDate);
//                materialCodeExcelBOMap.put(materialCode, excelBO);
//            }
//
//            // 查询入库单
//            LambdaQueryWrapper<BuMaterialEntryOrder> entryOrderWrapper = new LambdaQueryWrapper<BuMaterialEntryOrder>()
//                    .in(BuMaterialEntryOrder::getEntryNo, entryOrderDateMap.keySet());
//            List<BuMaterialEntryOrder> entryOrderList = buMaterialEntryOrderMapper.selectList(entryOrderWrapper);
//            // 判断是否需要新建入库单
//            Map<String, String> entryOrderCodeIdMap = new HashMap<>();
//            entryOrderList.forEach(item -> entryOrderCodeIdMap.put(item.getEntryNo(), item.getId()));
//            List<BuMaterialEntryOrder> needAddEntryOrderList = new ArrayList<>();
//            for (Map.Entry<String, Date> entryOrderDateEntry : entryOrderDateMap.entrySet()) {
//                String entryOrder = entryOrderDateEntry.getKey();
//                Date entryDate = entryOrderDateEntry.getValue();
//
//                if (!entryOrderCodeIdMap.containsKey(entryOrder)) {
//                    String id = UUIDGenerator.generate();
//                    BuMaterialEntryOrder order = new BuMaterialEntryOrder()
//                            .setId(id)
//                            .setEntryNo(entryOrder)
//                            .setEntryDate(entryDate)
//                            .setEntryDeptId("CJ1");
//                    needAddEntryOrderList.add(order);
//                    entryOrderCodeIdMap.put(order.getEntryNo(), id);
//                }
//            }
//
//            // 查物质
//            LambdaQueryWrapper<BuMaterialType> materialTypeWrapper = new LambdaQueryWrapper<BuMaterialType>()
//                    .in(BuMaterialType::getCode, materialCodeExcelBOMap.keySet());
//            List<BuMaterialType> materialTypeList = buMaterialTypeMapper.selectList(materialTypeWrapper);
//            // 判断是否需要新建物资
//            Map<String, String> materialCodeIdMap = new HashMap<>();
//            materialTypeList.forEach(item -> materialCodeIdMap.put(item.getCode(), item.getId()));
//            List<BuMaterialType> needAddMaterialList = new ArrayList<>();
//            for (Map.Entry<String, EntryOrderExcelBO> materialCodeExcelBOEntry : materialCodeExcelBOMap.entrySet()) {
//                String materialCode = materialCodeExcelBOEntry.getKey();
//                EntryOrderExcelBO excelBO = materialCodeExcelBOEntry.getValue();
//
//                if (!materialCodeIdMap.containsKey(materialCode)) {
//                    String name = excelBO.getMaterialDesc();
//                    // 属性转换
//                    String spec = null;
//                    String[] split = name.split("-\\[");
//                    if (split.length >= 2) {
//                        name = split[0];
//                        spec = split[1].trim();
//                        spec = spec.substring(0, spec.length() - 1);
//                        spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
//                    }
//
//                    int isAssetInt = "是".equals(excelBO.getIsAsset().trim()) ? 1 : 0;
//                    String isConsume = excelBO.getIsConsume();
//                    int isConsumeInt = 0;
//                    if ("消耗物资".equals(isConsume.trim())) {
//                        isConsumeInt = 1;
//                    } else if ("列管物资".equals(isConsume.trim())) {
//                        isConsumeInt = 2;
//                    }
//                    BigDecimal priceBigDecimal = StringUtils.isBlank(excelBO.getPrice()) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(excelBO.getPrice()));
//                    int kindInt = 1;
//                    int category1 = getCategory1(excelBO.getCategory3());
//                    String category2 = "备品备件".equals(excelBO.getCategory3()) ? "2" : null;
//
//                    BuMaterialType material = new BuMaterialType()
//                            .setId(materialCode)
//                            .setCode(materialCode)
//                            .setName(name)
//                            .setSpec(spec)
//                            .setUnit("个")
//                            .setPrice(priceBigDecimal)
//                            .setStatus(1)
//                            .setKind(kindInt)
//                            .setCategory(null)
//                            .setCategory1(category1)
//                            .setCategory2(category2)
//                            .setCategory3(excelBO.getCategory3())
//                            .setTheshold(BigDecimal.valueOf(-1))
//                            .setSubject(null)
//                            .setIsAsset(isAssetInt)
//                            .setFromHead(1)
//                            .setIsConsume(isConsumeInt)
//                            .setCreateTime(new Date())
//                            .setRemark("导入入库单excel创建");
//                    needAddMaterialList.add(material);
//                    materialCodeIdMap.put(materialCode, materialCode);
//                }
//            }
//
//            // 查仓库
//            List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectList(Wrappers.emptyWrapper());
//
//            // 查库存
//            LambdaQueryWrapper<BuMaterialStock> materialStockWrapper = new LambdaQueryWrapper<BuMaterialStock>()
//                    .in(BuMaterialStock::getMaterialTypeId, materialCodeIdMap.values());
//            List<BuMaterialStock> stockList = buMaterialStockMapper.selectList(materialStockWrapper);
//
//            // 入库明细导入
//            List<BuMaterialEntryDetail> needAddEntryDetailList = new ArrayList<>();
//            for (EntryOrderExcelBO excelBO : excelBOList) {
//                String entryOrder = excelBO.getEntryOrder();
//                String entryOrderId = entryOrderCodeIdMap.get(entryOrder);
//
//                String selfWarehouseId = getWarehouseId(warehouseList, excelBO.getEbsLevel2(), excelBO.getEbsLevel3());
//                String materialId = materialCodeIdMap.get(excelBO.getMaterialCode());
//                long stockCount = stockList.stream()
//                        .filter(stock -> materialId.equals(stock.getMaterialTypeId()))
//                        .count();
//                boolean hasStock = stockCount > 0;
//
//                String id = UUIDGenerator.generate();
//                BuMaterialEntryDetail entryDetail = new BuMaterialEntryDetail()
//                        .setId(id)
//                        .setEntryOrderId(entryOrderId)
//                        .setEbsWarehouseId(excelBO.getEbsLevel3())
//                        .setEbsWarehouseLevl2(excelBO.getEbsLevel2())
//                        .setSelfWarehouseId(selfWarehouseId)
//                        .setMaterialTypeId(materialId)
//                        .setAmount(BigDecimal.valueOf(Double.parseDouble(excelBO.getAmount())))
//                        .setPrice(BigDecimal.valueOf(Double.parseDouble(excelBO.getPrice())))
//                        .setEntryUserId(userId)
//                        .setEntryDate(entryOrderDateMap.get(excelBO.getEntryOrder()))
//                        .setLeaveFactory(null)
//                        .setProductionDate(null)
//                        .setExpirDay(null)
//                        .setExpirDate(null)
//                        .setConfirm(0)
//                        .setEntryClass(hasStock ? 2 : 1)
//                        .setConfirmAmount(null);
//                needAddEntryDetailList.add(entryDetail);
//            }
//
//            // 保存数据
//            if (CollectionUtils.isNotEmpty(needAddMaterialList)) {
//                buMaterialTypeMapper.insertList(needAddMaterialList);
//            }
//            if (CollectionUtils.isNotEmpty(needAddEntryOrderList)) {
//                buMaterialEntryOrderMapper.insertList(needAddEntryOrderList);
//            }
//            if (CollectionUtils.isNotEmpty(needAddEntryDetailList)) {
//                buMaterialEntryDetailMapper.insertList(needAddEntryDetailList);
//            }
//
//        } catch (Exception ex) {
//
//            throw new JeecgBootException(ex.getMessage());
//        }
//    }
//
//    private String getWarehouseId(List<BuMtrWarehouse> warehouseList, String ebsLevel2, String ebsLevel3) {
//
//
//        String parentId = null;
//        String selfWarehouseId = null;
//
//        List<BuMtrWarehouse> warehouse = warehouseList.stream()
//                .filter(item -> ebsLevel2.equals(item.getSysHouseCode()))
//                .collect(Collectors.toList());
//
//        if (warehouse.size() == 0) {
//            throw new JeecgBootException(ebsLevel2 + "目标二级库不存在，导入失败！");
//        } else {
//            parentId = warehouse.get(0).getId();
//        }
//
//
//        String finalParentId = parentId;
//        List<BuMtrWarehouse> warehouse1 = warehouseList.stream()
//                .filter(item -> ebsLevel3.equals(item.getSysHouseCode()))
//                .filter(item -> finalParentId.equals(item.getParentId()))
//                .collect(Collectors.toList());
//
//        if (warehouse1.size() == 0) {
//            throw new JeecgBootException(ebsLevel3 + "目标货位不存在，导入失败！");
//        } else {
//            selfWarehouseId = warehouse1.get(0).getId();
//        }
//
//        return selfWarehouseId;
//
//    }
//
//    private String getWarehouseCode(int level, Set<String> warehouseCodeSet) {
//        String childSelfCode = level + RandomStringUtils.randomAlphanumeric(5);
//        if (warehouseCodeSet.contains(childSelfCode)) {
//            return getWarehouseCode(level, warehouseCodeSet);
//        } else {
//            return childSelfCode;
//        }
//    }
//
//    private Integer getCategory1(String kind) {
//        if (StringUtils.isBlank(kind)) {
//            return -1;
//        }
//
//        int category1 = -1;
//        switch (kind) {
//            case "备品备件":
////                category1 = -1;
//                break;
//            case "耗材":
//                category1 = 3;
//                break;
//            case "设备":
////                category1 = -1;
//                break;
//            case "工器具":
//                category1 = 4;
//                break;
//            case "消耗物资":
//                category1 = 3;
//                break;
//            case "劳保用品":
////                category1 = -1;
//                break;
//            default:
//                break;
//        }
//
//        return category1;
//    }
//
//
//    @Transactional(rollbackFor = Exception.class)
//    public void save(List<BuMaterialType> typeList, List<BuMaterialEntryOrder> orderList, List<BuMaterialEntryDetail> detailList) throws Exception {
//        if (EmptyUtils.listNotEmpty(typeList)) {
//            importerSql.saveMaterialType(typeList);
//        }
//        if (EmptyUtils.listNotEmpty(orderList)) {
//            importerSql.saveMaterialEntryOrder(orderList);
//        }
//        if (EmptyUtils.listNotEmpty(detailList)) {
//            importerSql.saveMaterialEntryDetail(detailList);
//        }
//    }
//
//}
