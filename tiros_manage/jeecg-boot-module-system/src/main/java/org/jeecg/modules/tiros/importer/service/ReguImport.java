package org.jeecg.modules.tiros.importer.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.modules.tiros.importer.bean.BuMaterialType;
import org.jeecg.modules.tiros.importer.bean.BuRepairReguInfo;
import org.jeecg.modules.tiros.importer.bean.conf.ImportConfig;
import org.jeecg.modules.tiros.importer.common.Method;
import org.jeecg.modules.tiros.importer.common.ZhNumberConstant;
import org.jeecg.modules.tiros.importer.dao.ImporterDao;
import org.jeecg.modules.tiros.importer.entity.BuRepairReguDetail;
import org.jeecg.modules.tiros.importer.entity.BuRepairReguMaterial;
import org.jeecg.modules.tiros.importer.entity.BuRepairReguTools;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.jeecg.modules.tiros.importer.utils.NumberUtil;
import org.jeecg.modules.tiros.importer.utils.UUIDUtils;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyg
 */
public class ReguImport {
    private Integer firstColumn = 0;
    private Integer lastColumn = 0;
    private Integer firstRow = 0;
    private Integer lastRow = 0;
    private List<String> list = new ArrayList<>();
    private List<BuRepairReguDetail> reguDetails = new ArrayList<>();
    private BuRepairReguDetail reguDetail = null;
    private String uuid = "";
    private Integer startRow = 0;
    private Boolean twoColumn = false;
    private Boolean isEmpty = false;
    private Boolean twoColumnOther = false;
    private Boolean outsource = false;
    private String outsoutceUUID = "";
    private Integer toolsIndexS = 0;
    private Integer toolsIndexE = 0;
    private List<BuRepairReguTools> reguToolsListMerge = new ArrayList<>();
    private List<BuRepairReguTools> reguToolsList = new ArrayList<>();
    private Integer materialIndexS = 0;
    private Integer materialIndexE = 0;
    private List<BuRepairReguMaterial> materialListMerge = new ArrayList<>();
    private List<BuRepairReguMaterial> materialList = new ArrayList<>();
    private Integer materialIndex = 0;
    private BuRepairReguMaterial reguMaterial = null;
    private Connection con;

    public ReguImport(Connection con) {
        this.con = con;

    }

    public void importRegu(String path, ImportConfig importConfig) {
        try {
            Sheet sheet = ExcelUtil.getWorkbook(path).getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();
            String reguId = createReguInfo(path, importConfig);
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    // 合并类型：0没有合并 1列合并(同行多列合并) 2行合并(多行同列合并) 3行列都合并
                    int mergeType;
                    boolean isMerge;
                    String cellValue;
                    mergeType = ExcelUtil.isMerge(sheet, i, cell);
                    isMerge = mergeType != 0;
                    if (isMerge) {
                        cellValue = ExcelUtil.getMergedRegionValueAndInfo(sheet, row.getRowNum(), cell.getColumnIndex());
                        if (StrUtil.isNotEmpty(cellValue)) {
                            getTablePortionValue(cellValue);
                        }
                    } else {
                        cellValue = ExcelUtil.getCellValue(cell);
                    }

                    if (isEmpty) {
                        reguDetail.setRequirement(reguDetail.getRequirement() + cellValue);
                        isEmpty = false;
                        continue;
                    }
                    if (j == 2 && StrUtil.isEmpty(cellValue)) {
                        isEmpty = true;
                    }
                    if (j == 2 && isMerge) {
                        twoColumnOther = true;
                    }
                    if (StrUtil.isNotEmpty(cellValue)) {
                        if (cellValue.contains("&")) {
                            if ((i > lastRow || i != firstRow) && j >= 1) {
                                continue;
                            }
                            j += (lastColumn - j);
                            if (cellValue.contains("、")) {
                                String[] title = cellValue.split("、");
                                if (isContains(cellValue) && lastColumn - firstColumn > 0) {
                                    startRow = i;
                                    reguDetail = new BuRepairReguDetail();
                                    uuid = UUIDUtils.getUUID();
                                    reguDetail.setNo(getNoIntValue(title[0]).trim());
                                    reguDetail.setTitle(title[1].split("&")[0]);
                                    reguDetail.setType(1);
                                    reguDetail.setId(uuid);
                                    reguDetail.setReguId(reguId);
                                    reguDetails.add(reguDetail);
                                    if (outsource) {
                                        outsource = false;
                                    }
                                    continue;
                                }
                            }
                        }
                        if (i == startRow + 1) {
                            getReguDetail(uuid).setSafeNotice(ExcelUtil.subStrCellValue(cellValue));
                        } else if (i > startRow + 1 && lastRow - firstRow > 0) {
                            addReguDetail(i, j, cellValue, isMerge, reguId);
                        } else if (isMerge && cellValue.contains("（") && cellValue.contains(".")) {
                            if (isContains(cellValue)) {
                                reguDetail = buildRepairReguDetail(uuid, cellValue, reguId);
                                reguDetail.setType(1);
                                reguDetails.add(reguDetail);
                                outsource = true;
                                outsoutceUUID = reguDetail.getId();
                            }
                        } else {
                            addReguDetail(i, j, cellValue, isMerge, reguId);
                        }
                    } else if (j == 5 || j == 6) {
                        addReguDetail(i, j, cellValue, isMerge, reguId);
                    }

                    list.add(cellValue);
                }
            }
            saveReguDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建规程信息
     *
     * @param path
     * @return
     */
    private String createReguInfo(String path, ImportConfig importConfig) {
        File file = new File(path);
        String fileName = file.getName();
        String reguId = UUIDUtils.getUUID();
        BuRepairReguInfo reguInfo = new BuRepairReguInfo();
        reguInfo.setId(reguId);
        reguInfo.setCode(RandomUtil.randomString(6));
        reguInfo.setName(fileName.substring(0, fileName.lastIndexOf(".")));
        reguInfo.setLineId(importConfig.getLineId());
        reguInfo.setRepairProId(importConfig.getRepairProId());
        reguInfo.setTrainTypeId(importConfig.getTrainTypeId());
        reguInfo.setWorkshopId(importConfig.getWorkshopId());
        setConfigProperties(importConfig, reguInfo);
        Integer version = ImporterDao.selectReguInfoVersion(con, importConfig.getLineId());
        if (version == null) {
            reguInfo.setVersion("1");
        } else {
            reguInfo.setVersion(version + 1 + "");
        }
        ImporterDao.saveReguInfo(con, reguInfo);

        // 规程id回写
        importConfig.setReguId(reguId);

        return reguId;
    }

    private Boolean isContains(String cellValue) {
        for (char z : ZhNumberConstant.ZHNUMBER) {
            if (cellValue.contains(String.valueOf(z))) {
                return true;
            }
        }
        return false;
    }

    private BuRepairReguDetail getReguDetail(String uuid) {
        return reguDetail = reguDetails.stream().filter(r -> r.getId().equals(uuid)).collect(Collectors.toList()).get(0);
    }

    private void addReguDetail(int i, int j, String cellValue, Boolean isMerge, String reguId) {
        switch (j) {
            case 0:
                String id = "";
                if (outsource) {
                    id = outsoutceUUID;
                } else {
                    id = uuid;
                }
                reguDetail = buildRepairReguDetail(id, cellValue, reguId);
                reguDetail.setType(1);
                reguDetails.add(reguDetail);
                break;
            case 1:
                if (Objects.nonNull(reguDetail)) {
                    reguDetail.setTitle(ExcelUtil.subStrCellValue(cellValue));
                    twoColumn = isMerge;
                }
                break;
            case 2:
                if (Objects.nonNull(reguDetail)) {
                    String parentId = "";
                    if (twoColumn) {
                        if (twoColumnOther) {
                            parentId = reguDetail.getParentId();
                            twoColumnOther = false;
                        } else if (i == firstRow) {
                            parentId = reguDetail.getId();
                            // twoColumnOther = true;
                        } else {
                            parentId = reguDetail.getParentId();
                        }
                    } else {
                        parentId = reguDetail.getId();

                    }
                    reguDetail = buildRepairReguDetail(parentId, cellValue, reguId);
                    reguDetail.setType(2);
                    reguDetails.add(reguDetail);
                }
                break;
            case 3:
                if (Objects.nonNull(reguDetail)) {
                    getReguDetail(reguDetail.getId()).setRequirement(ExcelUtil.subStrCellValue(cellValue));
                }
                break;
            case 4:
                if (Objects.nonNull(reguDetail)) {
                    String methods = ExcelUtil.subStrCellValue(cellValue);
                    if (StrUtil.isNotEmpty(methods)) {
                        List<String> methodList = Arrays.asList(methods.split("、"));
                        List<String> methodCodeList = new ArrayList<>();
                        methodList.forEach((m) -> methodCodeList.add(Method.getMethodCode(m)));
                        getReguDetail(reguDetail.getId()).setMethod(methodCodeList.stream()
                                .collect(Collectors.joining(",")));
                    }
                }
                break;
            case 5:
                if (Objects.nonNull(reguDetail)) {
                    if (isMerge && StrUtil.isNotEmpty(cellValue)) {
                        cellValue = ExcelUtil.subStrCellValue(cellValue);
                        toolsIndexS = firstRow;
                        toolsIndexE = lastRow;
                        getReguTools(cellValue);
                    } else if (toolsIndexS < i && i <= toolsIndexE) {
                        if (reguToolsListMerge != null && reguToolsListMerge.size() > 0) {
                            for (int k = 0; k < reguToolsListMerge.size(); k++) {
                                BuRepairReguTools reguTools = new BuRepairReguTools();
                                BeanUtils.copyProperties(reguToolsListMerge.get(k), reguTools);
                                reguTools.setId(UUIDUtils.getUUID());
                                reguTools.setReguDetailId(reguDetail.getId());
                                reguToolsList.add(reguTools);
                            }
                        }
                    } else {
                        getReguTools(cellValue);
                    }
                }
                break;
            case 6:
                if (Objects.nonNull(reguDetail)) {
                    if (isMerge && StrUtil.isNotEmpty(cellValue)) {
                        cellValue = ExcelUtil.subStrCellValue(cellValue);
                        materialIndexS = firstRow;
                        materialIndexE = lastRow;
                        getReguMaterials(cellValue);
                    } else if (materialIndexS < i && i <= materialIndexE) {
                        if (materialListMerge != null && materialListMerge.size() > 0) {
                            for (int k = 0; k < materialListMerge.size(); k++) {
                                BuRepairReguMaterial material = new BuRepairReguMaterial();
                                BeanUtils.copyProperties(materialListMerge.get(k), material);
                                material.setId(UUIDUtils.getUUID());
                                material.setReguDetailId(reguDetail.getId());
                                materialList.add(material);
                            }
                        }
                    } else {
                        getReguMaterials(cellValue);
                    }
                }
                break;
            case 7:
                if (Objects.nonNull(reguDetail)) {
                    getReguDetail(reguDetail.getId()).setQualityLevel(ExcelUtil.subStrCellValue(cellValue));
                }
                break;

            default:
                break;
        }
    }

    private void getReguTools(String cellValue) {
        List<String> nameList = Arrays.asList(cellValue.split("、"));
        reguToolsListMerge.clear();
        nameList.forEach(name -> {
            BuMaterialType materialType = ImporterDao.selectByName(con, name);
            if (Objects.nonNull(materialType)) {
                BuRepairReguTools reguTools = new BuRepairReguTools();
                reguTools.setId(UUIDUtils.getUUID());
                reguTools.setReguDetailId(reguDetail.getId());
                reguTools.setToolTypeId(materialType.getId());
                reguToolsList.add(reguTools);
                reguToolsListMerge.add(reguTools);
            }
//            else {
//                BuMaterialType material = saveMaterialType(name);
//                material.setKind(2);
//                ImporterDao.saveMaterialType(con, material);
//                getReguTools(cellValue);
//            }
        });
    }

    private void getReguMaterials(String cellValue) {
        List<String> nameList = Arrays.asList(cellValue.split("、"));
        materialListMerge.clear();
        nameList.forEach(name -> {
            BuMaterialType materialType = ImporterDao.selectByName(con, name);
            if (Objects.nonNull(materialType)) {
                BuRepairReguMaterial material = new BuRepairReguMaterial();
                material.setId(UUIDUtils.getUUID());
                material.setReguDetailId(reguDetail.getId());
                material.setMaterialTypeId(materialType.getId());
                materialList.add(material);
                materialListMerge.add(material);
            }
//            else {
//                BuMaterialType material = saveMaterialType(name);
//                material.setKind(1);
//                ImporterDao.saveMaterialType(con, material);
//                getReguMaterials(cellValue);
//            }
        });
    }

    private BuMaterialType saveMaterialType(String name) {
        BuMaterialType material = new BuMaterialType();
        material.setId(UUIDUtils.getUUID());
        material.setName(name);
        material.setCode(RandomUtil.randomNumbers(8));
        material.setUnit("件");
        return material;
    }


    private void getTablePortionValue(String cellValue) {
        Map<String, Integer> map = ExcelUtil.getMergedRegionValue(cellValue);
        firstColumn = map.get("firstColumn");
        lastColumn = map.get("lastColumn");
        firstRow = map.get("firstRow");
        lastRow = map.get("lastRow");
    }


    private BuRepairReguDetail buildRepairReguDetail(String uuid, String cellValue, String reguId) {
        BuRepairReguDetail reguDetail = new BuRepairReguDetail();
        reguDetail.setParentId(uuid);
        if (cellValue.contains(".")) {
            reguDetail.setNo(getNoIntValue(ExcelUtil.subStrCellValue(cellValue.substring(0, cellValue.indexOf(".")))).trim());
            reguDetail.setTitle(ExcelUtil.subStrCellValue(cellValue.substring(cellValue.indexOf(".") + 1)));
        } else {
            reguDetail.setNo(getNoIntValue(ExcelUtil.subStrCellValue(cellValue)).trim());
        }
        reguDetail.setId(UUIDUtils.getUUID());
        reguDetail.setReguId(reguId);
        return reguDetail;
    }


    private void saveReguDetails() {
        if (EmptyUtils.listNotEmpty(reguDetails)) {
           /* reguDetails.stream().filter(r-> StrUtil.isEmpty(r.getTitle())).collect(Collectors.toList())
            .forEach(System.out::println);*/
            ImporterDao.saveReguDetail(con, reguDetails);
            if (EmptyUtils.listNotEmpty(reguToolsList)) {
                ImporterDao.saveMaterialTools(con, reguToolsList);
            }
            if (EmptyUtils.listNotEmpty(materialList)) {
                ImporterDao.saveMaterial(con, materialList);
            }
        }
    }

    private String getNoIntValue(String no) {
        for (char c : ZhNumberConstant.ZHNUMBER) {
            if (no.contains(String.valueOf(c))) {
                String replace = no.replaceAll("（", "")
                        .replaceAll("）", "")
                        .replaceAll("\\(", "")
                        .replaceAll("\\)", "");
                return NumberUtil.toArabic(replace);
            }
        }
        return no;
    }

    private void setConfigProperties(ImportConfig importConfig, BuRepairReguInfo reguInfo) {
        if (StringUtils.isNotBlank(importConfig.getLineId())) {
            reguInfo.setLineId(importConfig.getLineId());
        }
        if (StringUtils.isNotBlank(importConfig.getRepairProId())) {
            reguInfo.setRepairProId(importConfig.getRepairProId());
        }
        if (StringUtils.isNotBlank(importConfig.getTrainTypeId())) {
            reguInfo.setTrainTypeId(importConfig.getTrainTypeId());
        }
        if (StringUtils.isNotBlank(importConfig.getWorkshopId())) {
            reguInfo.setWorkshopId(importConfig.getWorkshopId());
        }
    }

}
