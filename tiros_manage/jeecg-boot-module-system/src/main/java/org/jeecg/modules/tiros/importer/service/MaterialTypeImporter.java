package org.jeecg.modules.tiros.importer.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.modules.tiros.importer.bean.BuMaterialCategory;
import org.jeecg.modules.tiros.importer.bean.BuMaterialType;
import org.jeecg.modules.tiros.importer.dao.ImporterDao;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 物资类型导入类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-19
 */
public class MaterialTypeImporter {

    private final Connection connection;
    private final List<BuMaterialCategory> needAddMaterialCategoryList = new ArrayList<>();

    public MaterialTypeImporter(Connection connection) {
        this.connection = connection;
    }

    public void importMaterialTypeCategory(String path) {
        try {
            List<BuMaterialCategory> categoryList = ImporterDao.selectAllMaterialTypeCategory(connection);
            BuMaterialCategory topCategory = getTopTreeByDBList(categoryList, "物资分类");
//            List<BuMaterialCategory> firstList = topCategory.getChildren();
//            List<BuMaterialCategory> secondList = new ArrayList<>();
//            for (BuMaterialCategory first : firstList) {
//                if (CollectionUtils.isNotEmpty(first.getChildren())) {
//                    secondList.addAll(first.getChildren());
//                }
//            }
//            List<BuMaterialCategory> thirdList = new ArrayList<>();
//            for (BuMaterialCategory second : secondList) {
//                if (CollectionUtils.isNotEmpty(second.getChildren())) {
//                    thirdList.addAll(second.getChildren());
//                }
//            }

            Sheet sheet = ExcelUtil.getWorkbook(path).getSheetAt(0);
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer firstCodeCellNum = null;
            Integer firstNameCellNum = null;
            Integer secondCodeCellNum = null;
            Integer secondNameCellNum = null;
            Integer thirdCodeCellNum = null;
            Integer thirdNameCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValue(cell).trim();
                        switch (value) {
                            case "一级分类代码":
                                firstCodeCellNum = cellNum;
                                break;
                            case "一级分类描述":
                                firstNameCellNum = cellNum;
                                break;
                            case "二级分类代码":
                                secondCodeCellNum = cellNum;
                                break;
                            case "二级分类描述":
                                secondNameCellNum = cellNum;
                                break;
                            case "三级分类代码":
                                thirdCodeCellNum = cellNum;
                                break;
                            case "三级分类描述":
                                thirdNameCellNum = cellNum;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            // 操作每行数据
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                String firstCode = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(firstCodeCellNum)).trim();
                String firstName = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(firstNameCellNum)).trim();
                String secondCode = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(secondCodeCellNum)).trim();
                String secondName = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(secondNameCellNum)).trim();
                String thirdCode = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(thirdCodeCellNum)).trim();
                String thirdName = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(thirdNameCellNum)).trim();

                List<BuMaterialCategory> firstList = topCategory.getChildren();
                BuMaterialCategory first = getCategoryByCodeAndName(firstList, firstCode, firstName);
                if (null == first) {
                    // 创建第一级
                    first = initNewCategory(firstCode, firstName);
                    firstList.add(first);
                }

                List<BuMaterialCategory> secondList = first.getChildren();
                BuMaterialCategory second = getCategoryByCodeAndName(secondList, secondCode, secondName);
                if (null == second) {
                    // 创建第二级
                    second = initNewCategory(secondCode, secondName);
                    second.setParentId(first.getId());
                    secondList.add(second);
                }

                List<BuMaterialCategory> thirdList = second.getChildren();
                BuMaterialCategory third = getCategoryByCodeAndName(thirdList, thirdCode, thirdName);
                if (null == third) {
                    // 创建第三级
                    third = initNewCategory(thirdCode, thirdName);
                    third.setParentId(second.getId());
                    thirdList.add(third);
                }

//                BuMaterialCategory third = getCategoryByCodeAndName(thirdList, thirdCode, thirdName);
//                if (null == third) {
//                    // 第三级不存在，判断第二级是否存在
//                    BuMaterialCategory second = getCategoryByCodeAndName(secondList, secondCode, secondName);
//                    if (null == second) {
//                        // 第二级不存在，判断第一级是否存在
//                        BuMaterialCategory first = getCategoryByCodeAndName(firstList, firstCode, firstName);
//                        if (null == first) {
//                            // 创建第一级
//                            first = initNewCategory(firstCode, firstName);
//                            firstList.add(first);
//                        }
//                        // 创建第二级
//                        second = initNewCategory(secondCode, secondName);
//                        second.setParentId(first.getId());
//                        secondList.add(second);
//                    }
//                    // 创建第三级
//                    third = initNewCategory(thirdCode, thirdName);
//                    third.setParentId(second.getId());
//                    thirdList.add(third);
//                }
            }

            int insertCount = 0;
            if (CollectionUtils.isNotEmpty(needAddMaterialCategoryList)) {
                insertCount = ImporterDao.insertMaterialCategoryList(connection, needAddMaterialCategoryList);
            }
            System.out.println("共导入了" + insertCount + "条物质类型分类记录。");
        } catch (Exception e) {
            System.out.println("导入物质类型分类错误，错误信息如下：");
            e.printStackTrace();
        }
    }

    public void importMaterialType(String path) {
        try {
            Sheet sheet = ExcelUtil.getWorkbook(path).getSheetAt(0);
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer codeCellNum = null;
            Integer nameCellNum = null;
            Integer unitCellNum = null;
            Integer statusCellNum = null;
            Integer subjectCellNum = null;
            Integer isAssetCellNum = null;
            Integer fromHeadCellNum = null;
            Integer isConsumeCellNum = null;
            Integer priceCellNum = null;
            Integer kindCellNum = null;

            Integer firstCodeCellNum = null;
            Integer secondCodeCellNum = null;
            Integer thirdCodeCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValue(cell).trim();
                        switch (value) {
                            case "物料编码":
                                codeCellNum = cellNum;
                                break;
                            case "物料名称":
                                nameCellNum = cellNum;
                                break;
                            case "单位名称":
                                unitCellNum = cellNum;
                                break;
                            case "状态":
                                statusCellNum = cellNum;
                                break;
                            case "费用科目":
                                subjectCellNum = cellNum;
                                break;
                            case "是否固定资产":
                                isAssetCellNum = cellNum;
                                break;
                            case "是否总库直发物资":
                                fromHeadCellNum = cellNum;
                                break;
                            case "列管消耗":
                                isConsumeCellNum = cellNum;
                                break;
                            case "价目表价格 SUM":
                                priceCellNum = cellNum;
                                break;
                            case "所属大类":
                                kindCellNum = cellNum;
                                break;
                            case "一级分类代码":
                                firstCodeCellNum = cellNum;
                                break;
                            case "二级分类代码":
                                secondCodeCellNum = cellNum;
                                break;
                            case "三级分类代码":
                                thirdCodeCellNum = cellNum;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            List<BuMaterialCategory> categoryList = ImporterDao.selectAllMaterialTypeCategory(connection);
            List<BuMaterialType> materialTypeList = new ArrayList<>();
            // 操作每行数据
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                String code = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(codeCellNum)).trim();
                String name = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(nameCellNum)).trim();
                String unit = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(unitCellNum)).trim();
                String status = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(statusCellNum)).trim();
                String subject = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(subjectCellNum)).trim();
                String isAsset = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(isAssetCellNum)).trim();
                String fromHead = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(fromHeadCellNum)).trim();
                String isConsume = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(isConsumeCellNum)).trim();
                String price = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(priceCellNum)).trim();
                String kind = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(kindCellNum)).trim();

                String firstCode = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(firstCodeCellNum)).trim();
                String secondCode = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(secondCodeCellNum)).trim();
                String thirdCode = ExcelUtil.getCellValue(sheet.getRow(rowNum).getCell(thirdCodeCellNum)).trim();

                // 属性转换
                String spec = null;
                String[] split = name.split("-\\[");
                if (split.length >= 2) {
                    name = split[0];
                    spec = split[1].trim();
                    spec = spec.substring(0, spec.length() - 1);
                    spec = spec.replaceFirst("规格：", "").replaceFirst("规格:", "").trim();
                }
                int statusInt = "有效".equals(status.trim()) ? 1 : 0;
                int isAssetInt = "是".equals(isAsset.trim()) ? 1 : 0;
                int fromHeadInt = "是".equals(fromHead.trim()) ? 1 : 0;
                int isConsumeInt = 0;
                if ("消耗物资".equals(isConsume.trim())) {
                    isConsumeInt = 1;
                } else if ("列管物资".equals(isConsume.trim())) {
                    isConsumeInt = 2;
                }
                BigDecimal priceBigDecimal = StringUtils.isBlank(price) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(price));
                int kindInt = "工器具".equals(kind) ? 2 : 1;
                String categoryId = getMaterialCategoryId(categoryList, firstCode, secondCode, thirdCode);
                int category1 = getCategory1(kind);
                String category2 = "备品备件".equals(kind) ? "2" : null;

                BuMaterialType materialType = new BuMaterialType()
                        .setId(RandomStringUtils.randomAlphanumeric(32))
                        .setCode(code)
                        .setName(name)
                        .setSpec(spec)
                        .setUnit(unit)
                        .setPrice(priceBigDecimal)
                        .setStatus(statusInt)
                        .setKind(kindInt)
                        .setCategory(categoryId)
                        .setCategory1(category1)
                        .setCategory2(category2)
                        .setCategory3(kind)
                        .setSubject(subject)
                        .setIsAsset(isAssetInt)
                        .setFromHead(fromHeadInt)
                        .setIsConsume(isConsumeInt)
                        .setRemark("导入创建");

                materialTypeList.add(materialType);
            }

            int insertCount = 0;
            if (CollectionUtils.isNotEmpty(materialTypeList)) {
                insertCount = ImporterDao.insertMaterialTypeList(connection, materialTypeList);
            }
            System.out.println("共导入了" + insertCount + "条物质类型记录。");
        } catch (Exception e) {
            System.out.println("导入物质类型错误，错误信息如下：");
            e.printStackTrace();
        }
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


    private BuMaterialCategory getCategoryByCodeAndName(List<BuMaterialCategory> categoryList, String code, String name) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return null;
        }

        List<BuMaterialCategory> matchList = categoryList.stream()
                .filter(category -> code.equals(category.getCode()) && name.equals(category.getName()))
                .collect(Collectors.toList());
        if (matchList.size() == 0) {
            return null;
        }
        return matchList.get(0);
    }

    private BuMaterialCategory getTopTreeByDBList(List<BuMaterialCategory> categoryList, String topCategoryName) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return initTopCategory(topCategoryName);
        }

//        categoryList.forEach(category -> category.setNeedInsert(false));

        Optional<BuMaterialCategory> topCategoryOptional = categoryList.stream()
                .filter(category -> StringUtils.isBlank(category.getParentId()) && topCategoryName.equals(category.getName()))
                .findFirst();

        if (topCategoryOptional.isPresent()) {
            BuMaterialCategory top = topCategoryOptional.get();
//            top.setNeedInsert(false);
            top.setChildren(new ArrayList<>());

            extractChildrenToTopCategory(top, categoryList);

            return top;
        } else {
            return initTopCategory(topCategoryName);
        }
    }

    private void extractChildrenToTopCategory(BuMaterialCategory parent, List<BuMaterialCategory> categoryList) {
        String parentId = parent.getId();
        List<BuMaterialCategory> children = categoryList.stream()
                .filter(category -> parentId.equals(category.getParentId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(children)) {
            for (BuMaterialCategory child : children) {
                extractChildrenToTopCategory(child, categoryList);
            }
        }

        parent.setChildren(children);
    }

    private BuMaterialCategory initTopCategory(String topCategoryName) {
        BuMaterialCategory top = new BuMaterialCategory()
                .setId("wzfl")
                .setCode("wzfl")
                .setName(topCategoryName)
                .setStatus(1)
                .setRemark("导入创建")
//                .setNeedInsert(true)
                .setChildren(new ArrayList<>());
        ;

        needAddMaterialCategoryList.add(top);

        return top;
    }

    private BuMaterialCategory initNewCategory(String code, String name) {
        BuMaterialCategory category = new BuMaterialCategory()
                .setId(RandomStringUtils.randomAlphanumeric(32))
                .setCode(code)
                .setName(name)
                .setStatus(1)
                .setRemark("导入创建")
//                .setNeedInsert(true)
                .setChildren(new ArrayList<>());

        needAddMaterialCategoryList.add(category);

        return category;
    }

}
