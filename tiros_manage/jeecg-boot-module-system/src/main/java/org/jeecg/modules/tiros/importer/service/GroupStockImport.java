package org.jeecg.modules.tiros.importer.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yuyougen
 * @title: GroupStockImport
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/7/19:36
 */
@Component
public class GroupStockImport {
    @Resource
    private ImporterSql importerSql;

    public void execute(String fileName, InputStream inputStream, Boolean cleanup) throws Exception {
        Map<String, String> groupNameMap;
        Map<String, BuMaterialType> materialTypeMap;
        List<String> groupIdList = new ArrayList<>();
        List<BuMaterialGroupStock> groupStockList = new ArrayList<>();
        List<BuMaterialType> materialTypeList = new ArrayList<>();
        List<BuMaterialTypeAttr> materialTypeAttrList = new ArrayList<>();
        try {
            groupNameMap = importerSql.selectWorkGroupNameMap();
            materialTypeMap = importerSql.selectMaterialTypeOfObjectMap();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("导入失败!");
        }
        try {
            Workbook workbook = ExcelUtil.getWorkbookByStream(fileName, inputStream);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                String groupId = groupNameMap.get(sheet.getSheetName());
                groupIdList.add(groupId);
                for (int rowIndex =1; rowIndex <=lastRowNum; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    BuMaterialGroupStock groupStock = null;
                    BuMaterialType buMaterialType = null;
                    BuMaterialTypeAttr buMaterialTypeAttr = null;
                    for (int cellIndex = 0; cellIndex < sheet.getRow(rowIndex).getLastCellNum(); cellIndex++) {
                        String cellValue = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                        if (0 < cellIndex && cellIndex < 6) {
                            if (cellIndex == 1 && StringUtils.isEmpty(cellValue)) break;
                           // if (cellIndex == 2 || cellIndex == 3) continue;
                            if (cellIndex == 1) {
                                String materialTypeId = "";
                                BuMaterialType materialType = materialTypeMap.get(cellValue);
                                if (materialType != null) {
                                    materialTypeId = materialType.getId();
                                } else {
                                    materialTypeId = cellValue;
                                    buMaterialType = new BuMaterialType()
                                            .setId(cellValue)
                                            .setCode(cellValue)
                                            .setCategory1(-1)
                                            .setPrice(BigDecimal.ZERO)
                                            .setKind(1)
                                            .setStatus(1)
                                            .setTheshold(new BigDecimal(-1.00))
                                            .setIsAsset(0)
                                            .setFromHead(0)
                                            .setCreateTime(new Date());

                                    //安全库存量
                                    buMaterialTypeAttr = new BuMaterialTypeAttr().setMaterialTypeId(cellValue);
                                }
                                groupStock = new BuMaterialGroupStock().setGroupId(groupId)
                                        .setMaterialTypeId(materialTypeId)
                                        .setPrice(materialType==null?buMaterialType.getPrice():materialType.getPrice());
                            }

                            if (cellIndex == 2 && buMaterialType != null) {
                                String spec = "";
                                Map<String, String> nameAndSpec = parseName(cellValue, spec);
                                buMaterialType.setName(nameAndSpec.get("name"));
                                buMaterialType.setSpec(nameAndSpec.get("spec"));
                            }
                            if (cellIndex == 3 && buMaterialType != null) {
                                buMaterialType.setUnit(cellValue);
                            }
                            if (cellIndex == 4) {
                                if (groupStock != null) {
                                    if (StringUtils.isNotBlank(cellValue) && isContainsNumber(cellValue)) {
                                        groupStock.setAmount(BigDecimal.valueOf(Double.parseDouble(cellValue)));
                                    } else {
                                        groupStock.setAmount(BigDecimal.ZERO);
                                    }
                                }
                            }
                            if (cellIndex == 5) {
                                if (groupStock != null) {
                                    if (StringUtils.isNotBlank(cellValue) && isContainsNumber(cellValue)) {
                                        groupStock.setAmount(BigDecimal.valueOf(Double.parseDouble(cellValue)));
                                    }
                                }
                            }
                        }
                    }

                    if (groupStock != null) {
                        groupStockList.add(groupStock);
                    }
                    if (buMaterialType != null) {
                        materialTypeList.add(buMaterialType);
                    }
                    if (buMaterialTypeAttr != null) {
                        materialTypeAttrList.add(buMaterialTypeAttr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("文件内容格式不正确!");
        }

        try {
            if (CollectionUtils.isNotEmpty(materialTypeList)) {
                importerSql.saveMaterialType(materialTypeList);
            }
            if (CollectionUtils.isNotEmpty(materialTypeAttrList)) {
                importerSql.saveMaterialTypeAttr(materialTypeAttrList);
            }
            if (CollectionUtils.isNotEmpty(groupStockList) &&
                    CollectionUtils.isNotEmpty(groupIdList)) {
                Map<String, BuMaterialGroupStock> map = new HashMap<>();
                groupStockList.forEach(item -> {
                    String key = item.getMaterialTypeId() + item.getGroupId();
                    if (map.containsKey(key)) {
                        item.setAmount(item.getAmount().add(map.get(key).getAmount()));
                    }
                    map.put(key, item);
                });
                importerSql.saveBatchGroupStock(groupIdList, map.values().stream().collect(Collectors.toList()), cleanup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("导入失败!");
        }
    }

    private Boolean isContainsNumber(String cellValue) {
        Pattern pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
        Matcher isNum = pattern.matcher(cellValue);
        return isNum.matches();

    }

    private Map<String, String> parseName(String name, String spec) {
        Map<String, String> map = new HashMap<>();
        String[] split = name.split("-\\[");
        if (split.length >= 2) {
            name = split[0];
            spec = split[1].trim();
            spec = spec.substring(0, spec.length() - 1);
            spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
        }
        map.put("name", name);
        map.put("spec", spec);
        return map;
    }

}
