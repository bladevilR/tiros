package org.jeecg.modules.tiros.importer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeAttr;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yuyougen
 * @title: MaterialMustImporter
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/111:33
 */
@Repository
@Slf4j
public class MaterialMustImporter {

    @Resource
    private ImporterSql importerSql;

    public void importer(String fileName, InputStream inputStream, String lineId, String repairProgramId) throws Exception {
        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        int cellIndexMax = 9;

        List<BuMaterialType> materialTypeList = new ArrayList<>();
        List<BuMaterialType> materialTypeUpdateList = new ArrayList<>();
        List<BuMaterialMustList> materialMustLists = new ArrayList<>();
        List<BuMaterialTypeAttr> materialTypeAttrList = new ArrayList<>();
        try {
            Map<String, String> materialTypeCodeMap = importerSql.selectMaterialTypeMap();
            Map<String, String> groupMap = importerSql.selectGroupIdMap();
            if (materialTypeCodeMap == null) {
                throw new JeecgBootException("没有物资类型,不可导入,请先添加!");
            }
            Map<String, String> trainSysShortNameIdMap = importerSql.selectTrainSysShortNameIdMap();
            String sysShortNames = trainSysShortNameIdMap.keySet().stream()
                    .filter(StringUtils::isNotBlank)
                    .sorted(Comparator.comparing(String::toString, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.joining(","));
            Map<String, String> workstationIdMap = importerSql.selectWorkstationIdMap();

            String sysId = null;
            String workstationId = null;
            String locationId = null;
            for (int rowIndex = firstRowNum + 1; rowIndex < lastRowNum; rowIndex++) {
//                if (rowIndex == 283) {
//                    log.info("debug：" + rowIndex);
//                }

                Row row = sheet.getRow(rowIndex);
                BuMaterialType buMaterialType = null;
                BuMaterialType buMaterialTypeUpdate = null;
                BuMaterialMustList buMaterialMustList = null;
                BuMaterialTypeAttr buMaterialTypeAttr = null;
                //合并行开始行的位置
                Integer startIndex = null;
                //合并行之差
                Integer rowVal = null;
                //合并行之后的安装部位
                String equalLocaltion = "";
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    String cellValue = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                    // 合并类型：0 没有合并 1 列合并(同行多列合并) 2 行合并(多行同列合并) 3 行列都合并
                    int mergeType;
                    if (cellIndex == 7) {
                        mergeType = ExcelUtil.isMerge(sheet, rowIndex, row.getCell(cellIndex));
                        if (mergeType != 0) {
                            cellValue = ExcelUtil.getMergedRegionValueAndInfo(sheet, row.getRowNum(), row.getCell(cellIndex).getColumnIndex()).trim();
                            Map<String, Object> mergedRegionMap = ExcelUtil.parseMargeValue(cellValue);
                            cellValue = mergedRegionMap.get("result").toString().trim();
                            if (mergeType == 2) {
                                rowVal = (Integer) mergedRegionMap.get("rowVal");
                                startIndex = rowIndex;
                                equalLocaltion = cellValue;
                            }
                        }
                    }
                    if (cellIndex == 0 && StringUtils.isNotEmpty(cellValue)) {
                        if (trainSysShortNameIdMap.containsKey(cellValue)) {
                            sysId = trainSysShortNameIdMap.get(cellValue);
                        } else {
                            throw new JeecgBootException("表格中系统[" + cellValue + "]不匹配，存在的系统有[" + sysShortNames + "]，请修改表格数据或系统短名称");
                        }
                    }
                    if (cellIndex == 1 && StringUtils.isNotEmpty(cellValue)) {
                        cellValue = cellValue.substring(0, cellValue.indexOf("位") + 1);
                        if (workstationIdMap != null) {
                            workstationId = workstationIdMap.get(cellValue);
                        }
                    }
                    if (cellIndex == 2) {
                        if (StringUtils.isNotBlank(cellValue)) {
                            if (cellValue.contains("编码申请中")) {
                                break;
                            }
                            String materialId = materialTypeCodeMap.get(cellValue);

//                            if ("010023160014".equals(materialId)) {
//                                log.info("debug：" + materialId);
//                            }

                            if (StringUtils.isEmpty(materialId)) {
                                materialId = cellValue;
                                buMaterialType = new BuMaterialType();
                                buMaterialType.setId(materialId);
                                buMaterialType.setCode(cellValue);
                                buMaterialType.setCategory1(1);
                                buMaterialType.setPrice(BigDecimal.ZERO);
                                buMaterialType.setKind(1);
                                buMaterialType.setStatus(1);
                                buMaterialType.setTheshold(BigDecimal.valueOf(-1.00));
                                buMaterialType.setIsAsset(0);
                                buMaterialType.setFromHead(0);
                                buMaterialType.setCreateTime(new Date());
                                materialTypeCodeMap.put(buMaterialType.getCode(), buMaterialType.getId());
                            } else {
                                buMaterialTypeUpdate = new BuMaterialType().setId(materialId);
                            }
                            buMaterialMustList = new BuMaterialMustList();
                            buMaterialMustList.setId(UUIDGenerator.generate());
                            buMaterialMustList.setMaterialTypeId(materialId);
                            buMaterialMustList.setSysId(sysId);
                            buMaterialMustList.setWorkstationId(workstationId);
                            buMaterialMustList.setLineId(lineId);
                            buMaterialMustList.setRepairProgramId(repairProgramId);
                            buMaterialMustList.setGroupId(groupMap.get(workstationId));
                            buMaterialMustList.setStatus(1);
                            //安全库存量
                            buMaterialTypeAttr = new BuMaterialTypeAttr().setMaterialTypeId(materialId);

                        } else {
                            break;
                        }
                    }
                    if (buMaterialType != null) {
                        setMaterialTypeOtherInfo(buMaterialType, cellIndex, cellValue);
                    }
                    if (buMaterialTypeUpdate != null) {
                        setMaterialTypeOtherInfo(buMaterialTypeUpdate, cellIndex, cellValue);
                        materialTypeUpdateList.add(buMaterialTypeUpdate);
                    }
                    if (buMaterialMustList != null) {
                        if (cellIndex == 6) {
                            double needAmount = 0D;
                            if (StringUtils.isNotEmpty(cellValue)) {
                                needAmount = isContains(cellValue) ? Double.parseDouble(cellValue) : 0D;
                            }
                            buMaterialMustList.setNeedAmount(needAmount);
                            if (buMaterialTypeUpdate != null) {
                                buMaterialTypeUpdate.setTheshold(new BigDecimal(needAmount * 3));
                            }
                            if (buMaterialType != null) {
                                buMaterialType.setTheshold(new BigDecimal(needAmount * 3));
                            }
                            if (buMaterialTypeAttr != null) {
                                buMaterialTypeAttr.setTheshold(needAmount * 3);
                            }
                        }
                      /*  if (cellIndex == 8) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                buMaterialMustList.setCanReplace(cellValue);
                            }
                        }*/
                        if (cellIndex >= 8 && cellIndex != row.getLastCellNum() - 1) {
                            if (StringUtils.isNotBlank(cellValue)) {
                                try {
                                    Long replaceCode = Long.valueOf(cellValue);
                                    String replaceCodeStr = buMaterialMustList.getCanReplace();
                                    if (StringUtils.isBlank(replaceCodeStr)) {
                                        buMaterialMustList.setCanReplace(cellValue);
                                    } else {
                                        buMaterialMustList.setCanReplace(replaceCodeStr + "," + cellValue);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }

                        //暂时不导入安装部位
                        if (cellIndex == 7) {
                           /* if (StringUtils.isNotEmpty(cellValue)) {
                                locationId = importerSql.selectTrainStructId(cellValue, lineId);
                            }*/
                            if (rowVal != null && rowIndex != startIndex && rowIndex <= rowVal) {
                                buMaterialMustList.setLocationDesc(equalLocaltion);
                            } else {
                                buMaterialMustList.setLocationDesc(cellValue);
                            }

                        }
                    }
                }
                if (buMaterialType != null) {
                    materialTypeList.add(buMaterialType);
                }
                if (buMaterialMustList != null) {
                    materialMustLists.add(buMaterialMustList);
                }
                if (buMaterialTypeAttr != null) {
                    materialTypeAttrList.add(buMaterialTypeAttr);
                }
            }
        } catch (JeecgBootException je) {
            je.printStackTrace();
            log.error(je.getMessage(), je);
            throw je;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new JeecgBootException("文件内容格式不正确");
        }
        try {
            if (CollectionUtils.isNotEmpty(materialTypeList)) {
                importerSql.saveMaterialType(materialTypeList);
            }
            if (CollectionUtils.isNotEmpty(materialMustLists)) {
                Map<String, BuMaterialMustList> map = new HashMap<>();
                materialMustLists.forEach(item -> {
                    String key = item.getMaterialTypeId() + item.getGroupId() + item.getWorkstationId();
                    if (map.containsKey(key)) {
                        BuMaterialMustList materialMustList = map.get(key);
                        materialMustList.setNeedAmount(materialMustList.getNeedAmount() + item.getNeedAmount());
                        map.put(key, materialMustList);
                    } else {
                        map.put(key, item);
                    }

                });
                materialMustLists = map.values().stream().collect(Collectors.toList());
                List<BuMaterialMustList> localMaterialMustList = importerSql.selectAllMaterialMust(repairProgramId, lineId);
                List<String> delLocalMaterialMustId = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(localMaterialMustList)) {
                    materialMustLists.forEach(materialMust -> {
                        localMaterialMustList.forEach(localMaterialMust -> {
                            if (materialMust.getMaterialTypeId().equals(localMaterialMust.getMaterialTypeId()) &&
                                    materialMust.getGroupId().equals(localMaterialMust.getGroupId())
                                    && materialMust.getWorkstationId().equals(localMaterialMust.getWorkstationId())) {
                                materialMust.setId(localMaterialMust.getId());
                                delLocalMaterialMustId.add(localMaterialMust.getId());
                            }
                        });
                    });
                    List<String> materialMustIdList = materialMustLists.stream().map(BuMaterialMustList::getMaterialTypeId).collect(Collectors.toList());
                    List<String> localMaterialMustIdList = localMaterialMustList.stream().map(BuMaterialMustList::getMaterialTypeId).collect(Collectors.toList());
                    List<String> noMaterialMustIdList = localMaterialMustIdList.stream().filter(item -> !materialMustIdList.contains(item)).collect(Collectors.toList());
                    delLocalMaterialMustId.addAll(localMaterialMustList.stream().filter(item -> noMaterialMustIdList.contains(item.getMaterialTypeId())).map(BuMaterialMustList::getId).collect(Collectors.toList()));
                    if (CollectionUtils.isNotEmpty(delLocalMaterialMustId)) {
                        importerSql.delMaterialMust(delLocalMaterialMustId);
                    }
                }
                importerSql.saveMaterialMust(materialMustLists);
            }
            if (CollectionUtils.isNotEmpty(materialTypeUpdateList)) {
                new Thread(() -> {
                    importerSql.updateMaterialType(materialTypeUpdateList);
                }).start();
            }
            new Thread(() -> {
                saveOrUpdateMaterialTypeAttr(materialTypeAttrList);
            }).start();
        } catch (Exception e) {
            throw new JeecgBootException("导入失败!");
        }
    }

    private void setMaterialTypeOtherInfo(BuMaterialType buMaterialType, int cellIndex, String cellValue) {
        switch (cellIndex) {
            case 3:
                String spec = "";
                Map<String, String> nameAndSpec = parseName(cellValue, spec);
                buMaterialType.setName(nameAndSpec.get("name"));
                buMaterialType.setSpec(nameAndSpec.get("spec"));
                break;
            case 4:
                buMaterialType.setCategory1(getCategory1(cellValue));
                break;
            case 5:
                buMaterialType.setUnit(StringUtils.isNotBlank(cellValue) ? cellValue : "个");
                break;
            default:
                break;
        }
    }

    private void saveOrUpdateMaterialTypeAttr(List<BuMaterialTypeAttr> materialTypeAttrList) {
        if (CollectionUtils.isNotEmpty(materialTypeAttrList)) {
            Map<String, String> map = importerSql.selectMaterialTypeAttrList();
            List<BuMaterialTypeAttr> saveTypeAttrs = new ArrayList<>();
            List<BuMaterialTypeAttr> updateTypeAttrs = new ArrayList<>();
            Map<String, BuMaterialTypeAttr> saveTypeAttrsMap = new HashMap<>();
            if (map != null) {
                materialTypeAttrList.forEach(item -> {
                    if (StringUtils.isBlank(map.get(item.getMaterialTypeId()))) {
                        item.setId(UUIDGenerator.generate());
                        saveTypeAttrsMap.put(item.getMaterialTypeId(), item);
                    } else {
                        updateTypeAttrs.add(item);
                    }
                });
                saveTypeAttrs.addAll(saveTypeAttrsMap.values());
            }
            if (CollectionUtils.isNotEmpty(saveTypeAttrs)) {
                importerSql.saveMaterialTypeAttr(saveTypeAttrs);
            }
            if (CollectionUtils.isNotEmpty(updateTypeAttrs)) {
                importerSql.updateMaterialTypeAttr(updateTypeAttrs);
            }
        }
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

    private Integer getCategory1(String kind) {
        int category1 = -1;
        switch (kind) {
            case "紧固件":
                category1 = 1;
                break;
            case "备品备件":
                category1 = 2;
                break;
            case "车体":
                category1 = 3;
                break;
            case "车上电气":
                category1 = 4;
                break;
            case "辅助":
                category1 = 5;
                break;
            case "牵引":
                category1 = 6;
                break;
            case "制定":
                category1 = 7;
                break;
            case "转向架":
                category1 = 8;
                break;
            default:
                break;
        }

        return category1;
    }

    private Boolean isContains(String cellValue) {
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(cellValue);

        if (!isNum.matches()) {
            return false;
        }
        return true;

    }
}
