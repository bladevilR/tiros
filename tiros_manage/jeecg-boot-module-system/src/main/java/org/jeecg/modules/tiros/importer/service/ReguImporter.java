package org.jeecg.modules.tiros.importer.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguInfo;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguMaterial;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTools;
import org.jeecg.modules.basemanage.regu.service.BuRepairReguDetailService;
import org.jeecg.modules.tiros.importer.common.Method;
import org.jeecg.modules.tiros.importer.common.ZhNumberConstant;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.jeecg.modules.tiros.importer.utils.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 规程、规程明细、规程明细关联信息 导入服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-28
 */
@Repository
public class ReguImporter {
    private BuRepairReguDetail currentDetailCategoryTop;
    private BuRepairReguDetail currentDetailCategory;
    private BuRepairReguDetail currentDetailItem;
    private BuRepairReguInfo baseReguInfo;
    private List<BuRepairReguDetail> reguDetailList = new ArrayList<>();
    private List<BuRepairReguTools> reguToolsList = new ArrayList<>();
    private List<BuRepairReguMaterial> reguMaterialList = new ArrayList<>();
    private String[] BRACKETS = {"(", ")", "（", "）"};
    @Resource
    private ImporterSql importerSql;
    @Resource
    private BuRepairReguDetailService buRepairReguDetailService;

    public void importRegu(String fileName, InputStream inputStream, BuRepairReguInfo reguInfo) throws Exception {

        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int maxRow = sheet.getLastRowNum();
        createReguInfo(sheet, reguInfo);

        // 查询物资类型
        //  Map<String, String> materialTypeNameIdMap = importerSql.selectMaterialTypeNameIdMap();
        Map<String, String> materialTypeMapSum = new HashMap<>(16);
        try {
            int nextNo = 1;
            int workNo = 0;
            for (int rowIndex = 1; rowIndex <= maxRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                // 合并类型：0没有合并 1列合并(同行多列合并) 2行合并(多行同列合并) 3行列都合并
                // 判断第2列”序号“是否列合并
                int mergeType = ExcelUtil.getMergeType(sheet, rowIndex, 0);
                if (mergeType == 1) {
                    // 列合并时，获取第0列的内容
                    String cellValue0 = ExcelUtil.getCellValue(row.getCell(0)).trim();
                    if (StringUtils.isBlank(cellValue0)) {
                        continue;
                    }

                    boolean beginWithChineseNumbers = beginWithChineseNumbers(cellValue0);
                    boolean beginWithBrackets = beginWithBrackets(cellValue0);
                    if (beginWithChineseNumbers) {
                        // 开头为中文数字，为顶级作业分类
                        String[] split = cellValue0.split("、");
                        String no = getNoIntValue(split[0]);
                        String title = split[1];
                        currentDetailCategory = buildDetail(null, 1, no, title);
                        currentDetailCategoryTop = currentDetailCategory;
                    } else if (beginWithBrackets) {
                        // 开头为括号，为下级级作业分类
                          /* String[] split = cellValue0.split("\\.");
                           String no = getNoIntValue(split[0]);
                           String title = split[1];*/
                        String no = nextNo + "";
                        nextNo = ++nextNo;
                        String title = cellValue0;

                        BuRepairReguDetail currentParent = currentDetailCategoryTop;
                        currentDetailCategory = buildDetail(currentParent, 1, no, title);
                    } else {
                        // 开头不为中文数字或括号，为安全提示
                        currentDetailCategory.setSafeNotice(cellValue0);
                    }
                } else {
                    // 不是列合并时，一行对应一个作业项目

                    // 第0列：作业分类序号
                    String cellValue0 = ExcelUtil.getCellValue(row.getCell(0)).trim();
                    if (StringUtils.isNotBlank(cellValue0)) {
                        String no = getNoIntValue(cellValue0);
                        BuRepairReguDetail currentParent = getCurrentParent(no);
                        currentDetailCategory = buildDetail(currentParent, 1, no, null);
                    }

                    // 第1列：作业分类标题
                    String cellValue1 = ExcelUtil.getCellValue(row.getCell(1)).trim();
                    if (StringUtils.isNotBlank(cellValue1)) {
                        currentDetailCategory.setTitle(cellValue1);
                    }

                    // 第2列：作业项目序号+标题
                    String cellValue2 = ExcelUtil.getCellValue(row.getCell(2)).trim();
                    if (StringUtils.isNotBlank(cellValue2)) {
                          /* String[] split = cellValue2.split("\\.");
                           String no = getNoIntValue(split[0]);
                           String title = split[1];*/
                        Integer no = 0;
                        if (mergeType == 2) {
                            int lastRow = ExcelUtil.getLastRowIndexIfRowMerged(sheet, rowIndex, 0);
                            if (rowIndex <= lastRow) {
                                workNo = workNo + 1;
                                no = workNo;
                                if (rowIndex == lastRow) {
                                    no = workNo;
                                    workNo = 0;
                                }
                            } else {
                                workNo = 0;
                                no = workNo;
                            }
                        } else {
                            workNo = 1;
                            no = workNo;
                            workNo = 0;
                        }


                        String title = cellValue2;
                        BuRepairReguDetail currentParent = currentDetailCategory;
                        currentDetailItem = buildDetail(currentParent, 2, no + "", title);
                    }

                    // 第3列：作业项目技术要求
                    String cellValue3 = ExcelUtil.getCellValue(row.getCell(3)).trim();
                    if (StringUtils.isNotBlank(cellValue3)) {
                        String requirement = currentDetailItem.getRequirement();
                        if (StringUtils.isBlank(requirement)) {
                            requirement = cellValue3;
                        } else {
                            requirement = requirement + "\r\n" + cellValue3;
                        }
                        currentDetailItem.setRequirement(requirement);
                    }

                    // 第4列：作业项目作业手段
                    String cellValue4 = ExcelUtil.getCellValue(row.getCell(4)).trim();
                    if (StringUtils.isNotBlank(cellValue4)) {
                        List<String> methodList = Arrays.asList(cellValue4.split("、"));
                        List<String> methodCodeList = new ArrayList<>();
                        methodList.forEach(method -> methodCodeList.add(Method.getMethodCode(method.trim())));
                        String methodCodes = String.join(",", methodCodeList);
                        currentDetailItem.setMethod(methodCodes);
                    }

                    // 第5列：作业项目工器具
                    String cellValue5 = ExcelUtil.getCellValue(row.getCell(5)).trim();
                    if (StringUtils.isBlank(cellValue5)) {
                        if (mergeType == 2) {
                            int thisTopRowIndex = ExcelUtil.getTopRowIndexIfRowMerged(sheet, rowIndex, 5);
                            cellValue5 = ExcelUtil.getCellValue(sheet.getRow(thisTopRowIndex).getCell(5)).trim();
                        }
                    }
                    if (StringUtils.isNotBlank(cellValue5)) {
                        List<String> toolNameList = Arrays.asList(cellValue5.split("、"));
                        for (String toolName : toolNameList) {
                            String materialTypeId = getMaterialTypeId(materialTypeMapSum, toolName);
                            if (StringUtils.isNotBlank(materialTypeId)) {
                                long count = reguToolsList.stream()
                                        .filter(reguTools ->
                                                currentDetailItem.getId().equals(reguTools.getReguDetailId())
                                                        && materialTypeId.equals(reguTools.getToolTypeId())).count();
                                if (count >= 1) {
                                    continue;
                                }
                                BuRepairReguTools reguTools = new BuRepairReguTools()
                                        .setId(UUIDGenerator.generate())
                                        .setReguDetailId(currentDetailItem.getId())
                                        .setToolTypeId(materialTypeId)
                                        .setAmount(1D);
                                reguToolsList.add(reguTools);
                            }

                        }
                    }

                    // 第6列：作业项目物料
                    String cellValue6 = ExcelUtil.getCellValue(row.getCell(6)).trim();
                    if (StringUtils.isBlank(cellValue6)) {
                        if (mergeType == 2) {
                            int thisTopRowIndex = ExcelUtil.getTopRowIndexIfRowMerged(sheet, rowIndex, 6);
                            cellValue6 = ExcelUtil.getCellValue(sheet.getRow(thisTopRowIndex).getCell(6)).trim();
                        }
                    }
                    if (StringUtils.isNotBlank(cellValue6)) {
                        List<String> materialNameList = Arrays.asList(cellValue6.split("、"));

                        for (String materialName : materialNameList) {
                            String materialTypeId = getMaterialTypeId(materialTypeMapSum, materialName);
                            if (StringUtils.isNotBlank(materialTypeId)) {
                                long count = reguMaterialList.stream()
                                        .filter(reguMaterial ->
                                                currentDetailItem.getId().equals(reguMaterial.getReguDetailId())
                                                        && materialTypeId.equals(reguMaterial.getMaterialTypeId())).count();
                                if (count >= 1) {
                                    continue;
                                }
                                BuRepairReguMaterial reguMaterial = new BuRepairReguMaterial()
                                        .setId(UUIDGenerator.generate())
                                        .setReguDetailId(currentDetailItem.getId())
                                        .setMaterialTypeId(materialTypeId)
                                        .setAmount(1D);
                                reguMaterialList.add(reguMaterial);
                            }

                        }
                    }

                    // 第7列：作业项目质量等级
                    String cellValue7 = ExcelUtil.getCellValue(row.getCell(7)).trim();
                    if (StringUtils.isBlank(cellValue7)) {
                        if (mergeType == 2) {
                            int thisTopRowIndex = ExcelUtil.getTopRowIndexIfRowMerged(sheet, rowIndex, 7);
                            cellValue7 = ExcelUtil.getCellValue(sheet.getRow(thisTopRowIndex).getCell(7)).trim();
                        }
                    }
                    if (StringUtils.isNotBlank(cellValue7)) {
                        currentDetailItem.setQualityLevel(cellValue7);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("文件内容格式不正确!");
        }

        try {
            saveReguAndDetail(baseReguInfo, reguInfo);
        } catch (Exception e) {
            throw new JeecgBootException("导入失败!");
        }

        resetData();
    }

    private String getMaterialTypeId(Map<String, String> materialTypeMapSum, String name) {
        String materialTypeId = materialTypeMapSum.get(name);
        if (StringUtils.isBlank(materialTypeId)) {
            Map<String, String> materialTypeMap = importerSql.selectMaterialTypeNameIdMap(name);
            materialTypeId = materialTypeMap.get(name);
            materialTypeMapSum.putAll(materialTypeMap);
        }
        return materialTypeId;
    }

    private void resetData() {
        baseReguInfo = null;
        currentDetailItem = null;
        reguDetailList.clear();
        reguToolsList.clear();
        reguMaterialList.clear();
    }


    private BuRepairReguInfo createReguInfo(Sheet sheet, BuRepairReguInfo reguInfo) {
        baseReguInfo = new BuRepairReguInfo();
        String reguId = UUIDGenerator.generate();
        BeanUtils.copyProperties(reguInfo, baseReguInfo);
        if (StringUtils.isBlank(baseReguInfo.getId())) {
            baseReguInfo.setId(reguId);
        }
        if (StringUtils.isBlank(baseReguInfo.getCode())) {
            baseReguInfo.setCode(RandomStringUtils.randomAlphanumeric(6));
        }
        if (StringUtils.isBlank(baseReguInfo.getName())) {
            baseReguInfo.setName(sheet.getSheetName());
        }
        return reguInfo;
    }

    private boolean beginWithChineseNumbers(String cellValue) {
        if (StringUtils.isBlank(cellValue)) {
            return false;
        }

        String begin = cellValue.substring(0, 1);
        for (char number : ZhNumberConstant.ZHNUMBER) {
            if (begin.equals(String.valueOf(number))) {
                return true;
            }
        }

        return false;
    }

    private boolean beginWithBrackets(String cellValue) {
        if (StringUtils.isBlank(cellValue)) {
            return false;
        }

        String begin = cellValue.substring(0, 1);
        for (String bracket : BRACKETS) {
            if (begin.equals(bracket)) {
                return true;
            }
        }

        return false;
    }

    private BuRepairReguDetail buildDetail(BuRepairReguDetail parent, int type, String no, String title) {
        BuRepairReguDetail reguDetail = new BuRepairReguDetail();
        reguDetail.setId(UUIDGenerator.generate());
        reguDetail.setReguId(baseReguInfo.getId());
        reguDetail.setNo(no);
        reguDetail.setTitle(title);
        reguDetail.setType(type);
        reguDetail.setMustReplace(0);
        reguDetail.setMeasure(0);
        reguDetail.setOutsource(0);
        reguDetail.setImportant(0);
        reguDetail.setWorkTime(0f);
        if (null == parent) {
            reguDetail.setWbs(no);
        } else {
            reguDetail.setParentId(parent.getId());
            reguDetail.setWbs(parent.getWbs() + "." + no);
        }

        reguDetailList.add(reguDetail);
        return reguDetail;
    }

    private BuRepairReguDetail getCurrentParent(String no) {
        if ("1".equals(no)) {
            return currentDetailCategory;
        }

        String wbs = currentDetailCategory.getWbs();
        String parentWbs;
        if (wbs.contains(".")) {
            parentWbs = wbs.substring(0, wbs.lastIndexOf("."));
        } else {
            parentWbs = wbs;
        }

        List<BuRepairReguDetail> parentList = reguDetailList.stream()
                .filter(detail -> parentWbs.equals(detail.getWbs()))
                .collect(Collectors.toList());
        if (parentList.size() > 0) {
            return parentList.get(0);
        }

        return null;
    }

    private String getNoIntValue(String no) {
        for (char c : ZhNumberConstant.ZHNUMBER) {
            if (no.contains(String.valueOf(c))) {
                String replace = no.replaceAll("（", "")
                        .replaceAll("）", "")
                        .replaceAll("\\(", "")
                        .replaceAll("\\)", "");
                no = NumberUtil.toArabic(replace);
            }
        }
        return String.valueOf(Double.valueOf(no).intValue());
    }


    private void saveReguAndDetail(BuRepairReguInfo baseReguInfo, BuRepairReguInfo reguInfo) throws Exception {
        if (StringUtils.isNotBlank(reguInfo.getId())) {
            buRepairReguDetailService.deleteByReguId(reguInfo.getId());
        } else {
            if (null == baseReguInfo.getStatus()) {
                baseReguInfo.setStatus(1);
            }
            importerSql.saveReguInfo(baseReguInfo);
        }
        if (CollectionUtils.isNotEmpty(reguDetailList)) {
            importerSql.saveReguDetail(reguDetailList);
            if (EmptyUtils.listNotEmpty(reguToolsList)) {
                importerSql.saveMaterialTools(reguToolsList);
            }
            if (EmptyUtils.listNotEmpty(reguMaterialList)) {
                importerSql.saveMaterial(reguMaterialList);
            }
        }
    }
}
