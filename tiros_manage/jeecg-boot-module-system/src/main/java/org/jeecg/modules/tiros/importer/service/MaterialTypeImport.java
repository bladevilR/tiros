package org.jeecg.modules.tiros.importer.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanWorkstation;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryOrder;

import org.jeecg.modules.material.material.bean.BuMaterialCategory;
import org.jeecg.modules.material.material.bean.BuMaterialType;

import org.jeecg.modules.material.material.mapper.BuMaterialCategoryMapper;
import org.jeecg.modules.material.material.mapper.BuMaterialTypeMapper;


import org.jeecg.modules.tiros.importer.dao.ImporterDao;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.jeecg.modules.tiros.importer.utils.UUIDUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yfy
 * @title: importMaterialType
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/9 11:35
 */
@Repository
public class MaterialTypeImport {
//    private final Connection connection;
//    public MaterialTypeImport(Connection connection) {
//        this.connection = connection;
//    }

    private final List<BuMaterialCategory> needAddMaterialCategoryList = new ArrayList<>();

    @Resource
    private ImporterSql importerSql;

    @Resource
    private BuMaterialTypeMapper buMaterialTypeMapper;

    @Resource
    private BuMaterialCategoryMapper buMaterialCategoryMapper;

    public Boolean importMaterialType(String fileName, InputStream inputStream) throws Exception {
        int rowSum = 0;
        try {
            Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();


            // 获取数据单元格的对应位置
            Integer codeCellNum = null;
            Integer specCellNum = null;
            Integer unitCellNum = null;
            Integer categoryCodeNum = null;
            Integer cateGoryNameNum = null;
            Integer isAssetCellNum = null;
            Integer category3CellNum = null;
            Integer manageCellNum = null;
            Integer priceCellNum = null;
            Integer fromHeadCellNum = null;
            Integer isConsumeCellNum = null;
            Integer typeCellNum = null;
            Integer statusCellNum = null;

            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValue(cell).trim();
                        switch (value) {
                            case "物资编码":
                                codeCellNum = cellNum;
                                break;
                            case "物资描述":
                                specCellNum = cellNum;
                                break;
                            case "单位":
                                unitCellNum = cellNum;
                                break;
                            case "类别":
                                categoryCodeNum = cellNum;
                                break;
                            case "类别描述":
                                cateGoryNameNum = cellNum;
                                break;
                            case "是否固资":
                                isAssetCellNum = cellNum;
                                break;
                            case "所属大类":
                                category3CellNum = cellNum;
                                break;
                            case "职能管理大类":
                                manageCellNum = cellNum;
                                break;
                            case "价目表价格":
                                priceCellNum = cellNum;
                                break;
                            case "是否直发":
                                fromHeadCellNum = cellNum;
                                break;
                            case "列管/消耗":
                                isConsumeCellNum = cellNum;
                                break;
                            case "类型":
                                typeCellNum = cellNum;
                                break;
                            case "状态":
                                statusCellNum = cellNum;
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
            List<BuMaterialCategory> categoryList = buMaterialCategoryMapper.selectAll();
            List<BuMaterialType> materialTypeList1 = buMaterialTypeMapper.selectAll();
            List<BuMaterialType> materialTypeList = new ArrayList<>();

            Map<String, String> typeMap = new HashMap<>();
            materialTypeList1.stream().forEach(item -> typeMap.put(item.getCode(), item.getName()));

            Map<String, String> categoryMap = new HashMap<>();
            categoryList.stream().forEach(item -> categoryMap.put(item.getCode() + item.getName() + item.getParentId(), item.getCode()));

            List<BuMaterialCategory> categoryList1 = new ArrayList<>();

            // 操作每行数据
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                String firstCode = null;
                String secondCode = null;
                String thirdCode = null;
                String firstName = null;
                String secondName = null;
                String thirdName = null;
                String code = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(codeCellNum)).split("\\'")[0].trim();
                String value1 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(specCellNum));
                String[] split = value1.split("-\\[");
                String name = null;
                String spec = null;
                if (split.length >= 2) {
                    name = split[0];
                    spec = split[1].trim();
                    spec = spec.substring(0, spec.length() - 1);
                    spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();

                } else {
                    name = value1;
                }
                String unit = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(unitCellNum)).trim();
                String value2 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(categoryCodeNum));
                String[] split1 = value2.split("\\.");
                if (split1.length >= 3) {
                    firstCode = split1[0];
                    secondCode = split1[1];
                    thirdCode = split1[2];
                }
                String value3 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(cateGoryNameNum));
                String[] split2 = value3.split("-");
                if (split2.length >= 3) {
                    firstName = split2[0];
                    secondName = split2[1];
                    thirdName = split2[2];
                } else if (split2.length == 1) {
                    firstName = getCategoryByCode(firstCode);
                    secondName = getCategoryByCode(secondCode);
                    thirdName = split2[0];
                }
                String isAsset = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(isAssetCellNum)).trim();
                int isAssetInt = "是".equals(isAsset.trim()) ? 1 : 0;

                String category3 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(category3CellNum)).trim();
                Integer kind = 1;
                if (category3.contains("工器具")) {
                    kind = 2;
                }
                String value4 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(manageCellNum)).trim();
                if (value4.contains("工器具")) {
                    kind = 2;
                }

                String value5 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(priceCellNum)).trim();
                BigDecimal price = new BigDecimal(value5).stripTrailingZeros();

                String value6 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(fromHeadCellNum)).trim();
                int fromHeadInt = "是".equals(value6.trim()) ? 1 : 0;


                String value7 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(isConsumeCellNum)).trim();
                int isConsume = 1;
                if (value7.contains("列管物资")) {
                    isConsume = 2;
                }
                String value8 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(typeCellNum)).trim();

                String value9 = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(statusCellNum)).trim();
                int status = "有效" == value9 ? 1 : 0;

                String categoryId = getMaterialCategoryId(categoryList, firstCode, secondCode, thirdCode);
                int category1 = getCategory1(category3);
                String category2 = "备品备件".equals(kind) ? "2" : null;

                if (!typeMap.keySet().contains(code)) {
                    BuMaterialType materialType = new BuMaterialType()
                            .setId(RandomStringUtils.randomAlphanumeric(32))
                            .setCode(code)
                            .setName(name)
                            .setSpec(spec)
                            .setUnit(unit)
                            .setPrice(price)
                            .setStatus(status)
                            .setKind(kind)
                            .setCategory(categoryId)
                            .setCategory1(category1)
                            .setCategory2(category2)
                            .setCategory3(category3)
                            .setIsAsset(isAssetInt)
                            .setFromHead(fromHeadInt)
                            .setIsConsume(isConsume)
                            .setCreateTime(new Date())
                            .setTheshold(new BigDecimal(-1).stripTrailingZeros())
                            .setRemark("物资类型导入创建");

                    materialTypeList.add(materialType);
                    rowSum = rowNum;
                    System.out.println("正在导入第" + rowNum);
                }


                List<BuMaterialCategory> categoryList2 = new ArrayList<>();
                List<BuMaterialCategory> categoryList3 = new ArrayList<>();
                List<BuMaterialCategory> categoryList4 = new ArrayList<>();
                Boolean flag = true;
                Boolean flag1 = true;
                List<String> ids2 = new ArrayList<>();
                List<String> ids3 = new ArrayList<>();


                String finalFirstCode = firstCode;
                String finalFirstName = firstName;
                categoryList2 = categoryList.stream()
                        .filter(item -> item.getCode().equals(finalFirstCode)
                                && item.getName().equals(finalFirstName)
                                && item.getParentId().equals("wzfl"))
                        .collect(Collectors.toList());

                if (categoryList2.size() == 0) {
                    BuMaterialCategory category = new BuMaterialCategory();
                    category = setCategory(firstCode, secondCode);
//                    category.setParentId('wzfl');
                    categoryList1.add(category);
                    BuMaterialCategory category5 = new BuMaterialCategory();
                    category5 = setCategory(secondCode, secondName);
                    category5.setParentId(category.getId());
                    categoryList1.add(category5);
                    BuMaterialCategory category6 = new BuMaterialCategory();
                    category6 = setCategory(thirdCode, thirdName);
                    category6.setParentId(category5.getId());
                    categoryList1.add(category6);
                } else {
                    String finalSecondCode = secondCode;
                    String finalSecondName = secondName;
                    List<BuMaterialCategory> finalCategoryList = categoryList2;
                    categoryList3 = categoryList.stream()
                            .filter(item -> item.getCode().equals(finalSecondCode)
                                    && item.getName().equals(finalSecondName)
                                    && item.getParentId().equals(finalCategoryList.get(0).getId()))
                            .collect(Collectors.toList());

                    if (categoryList3.size() == 0) {
                        BuMaterialCategory category5 = new BuMaterialCategory();
                        category5 = setCategory(secondCode, secondName);
                        category5.setParentId(finalCategoryList.get(0).getId());
                        categoryList1.add(category5);
                        BuMaterialCategory category6 = new BuMaterialCategory();
                        category6 = setCategory(thirdCode, thirdName);
                        category6.setParentId(category5.getId());
                        categoryList1.add(category6);
                    } else {
                        String finalThirdName = thirdName;
                        String finalThirdCode = thirdCode;
                        List<BuMaterialCategory> finalCategoryList1 = categoryList3;
                        categoryList4 = categoryList.stream()
                                .filter(item -> item.getCode().equals(finalThirdCode)
                                        && item.getName().equals(finalThirdName)
                                        && item.getParentId().equals(finalCategoryList1.get(0).getId()))
                                .collect(Collectors.toList());

                        if (categoryList4.size() == 0) {
                            BuMaterialCategory category6 = new BuMaterialCategory();
                            category6 = setCategory(thirdCode, thirdName);
                            category6.setParentId(categoryList4.get(0).getId());
                            categoryList1.add(category6);
                        }
                    }

                }


            }

            try {
                if (CollectionUtils.isNotEmpty(categoryList1)) {
                    saveCategoryList(categoryList1);
                }
            } catch (Exception e) {
                throw new JeecgBootException("导入失败!");
            }
            try {
                if (CollectionUtils.isNotEmpty(materialTypeList)) {
                    saveTypeList(materialTypeList);
                }
            } catch (Exception e) {
                throw new JeecgBootException("导入失败! 第" + String.valueOf(rowSum) + "行数据有误");
            }

        } catch (Exception e) {
            throw new JeecgBootException(",检查文件是否正确!");
        }

        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveCategoryList(List<BuMaterialCategory> categoryList) throws Exception {
        if (EmptyUtils.listNotEmpty(categoryList)) {
            System.out.println("开始导入物资分类" + categoryList.size());
            categoryList.forEach(category -> category.setStatus(1));
            buMaterialCategoryMapper.saveMaterialCategory(categoryList);
            System.out.println("导入物资分类结束");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveTypeList(List<BuMaterialType> typeList) throws Exception {
        if (EmptyUtils.listNotEmpty(typeList)) {
            System.out.println("开始导入物资类型" + typeList.size());
            buMaterialTypeMapper.saveTypeList(typeList);
            System.out.println("导入物资类型结束");
        }
    }


    private String getCategoryByCode(String code) {
        List<BuMaterialCategory> matchList = buMaterialCategoryMapper.selectCategoryByCode(code);
        return matchList.get(0).getName();
    }


    private String getMaterialCategoryId(List<BuMaterialCategory> categoryList, String firstCode, String secondCode, String thirdCode) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return null;
        }

        List<BuMaterialCategory> firstList = categoryList.stream()
                .filter(category -> firstCode.equals(category.getCode()))
                .collect(Collectors.toList());

        List<BuMaterialCategory> secondList = categoryList.stream()
                .filter(category -> secondCode.equals(category.getCode()))
                .collect(Collectors.toList());

        List<BuMaterialCategory> thirdList = categoryList.stream()
                .filter(category -> thirdCode.equals(category.getCode()))
                .collect(Collectors.toList());

        for (BuMaterialCategory third : thirdList) {
            String thirdParentId = third.getParentId();
            if (StringUtils.isBlank(thirdParentId)) {
                continue;
            }

            List<BuMaterialCategory> matchSecondList = secondList.stream()
                    .filter(category -> thirdParentId.equals(category.getId()))
                    .collect(Collectors.toList());

            for (BuMaterialCategory second : matchSecondList) {
                String secondParentId = second.getParentId();
                if (StringUtils.isBlank(secondParentId)) {
                    continue;
                }

                List<BuMaterialCategory> matchFirstList = firstList.stream()
                        .filter(category -> secondParentId.equals(category.getId()))
                        .collect(Collectors.toList());

                if (matchFirstList.size() > 0) {
                    return third.getId();
                }
            }
        }

        return null;
    }


    private Integer getCategory1(String kind) {
        if (StringUtils.isBlank(kind)) {
            return -1;
        }

        int category1 = -1;
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


    private BuMaterialCategory setCategory(String code, String name) {
        BuMaterialCategory category = new BuMaterialCategory()
                .setId(UUIDUtils.getUUID())
                .setCode(code)
                .setName(name)
                .setRemark("导入物资类型时创建")
                .setStatus(1)
                .setCreateTime(new Date());
        return category;
    }


}
