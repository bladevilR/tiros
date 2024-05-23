//package org.jeecg.modules.tiros.importer.service;
//
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.jeecg.modules.tiros.importer.bean.conf.ImportConfig;
//import org.jeecg.modules.tiros.importer.dao.ImporterDao;
//import org.jeecg.modules.tiros.importer.entity.BuBaseWorkstation;
//import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
//import org.jeecg.modules.tiros.importer.utils.UUIDUtils;
//
//import java.sql.Connection;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author yyg
// */
//public class WorkstationImport {
//
//    private String path;
//    private Connection con;
//    private List<BuBaseWorkstation> workstations = new ArrayList<>();
//
//    public WorkstationImport(String path, Connection con) {
//        this.path = path;
//        this.con = con;
//    }
//
//    public void importWorkstation(ImportConfig importConfig) {
//        try {
//            String workshopId = "CJ1";
//            if (StringUtils.isNotBlank(importConfig.getWorkshopId())) {
//                workshopId = importConfig.getWorkshopId();
//            }
//
//            Sheet sheet = ExcelUtil.getWorkbook(path).getSheetAt(0);
//            int rowCount = sheet.getLastRowNum();
//            int colCount = sheet.getRow(1).getLastCellNum();
//            for (int i = 1; i <= rowCount; i++) {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    continue;
//                }
//                BuBaseWorkstation workstation = new BuBaseWorkstation();
//                workstation.setId(UUIDUtils.getUUID());
//                workstation.setStatus(1);
//                workstation.setWorkshopId(workshopId);
//                for (int j = 0; j <= colCount; j++) {
//                    Cell cell = row.getCell(j);
//                    // 合并类型：0没有合并 1列合并(同行多列合并) 2行合并(多行同列合并) 3行列都合并
//                    int mergeType;
//                    String cellValue;
//                    mergeType = ExcelUtil.isMergedRegion(sheet, i, j);
//                    if (mergeType != 0) {
//                        cellValue = ExcelUtil.getMergedRegionValueAndInfo(sheet, row.getRowNum(), cell.getColumnIndex());
//                    } else {
//                        cellValue = ExcelUtil.getCellValue(cell);
//                    }
//                    if (StringUtils.isNotBlank(cellValue)) {
//                        if (j == 0) {
//                            workstation.setStationNo(Double.valueOf(cellValue).intValue() + "");
//                        } else if (j == 1) {
//                            workstation.setName(cellValue);
//                        } else if (j == 2) {
//                            workstation.setContent(cellValue);
//                        } else if (j == 3) {
//                            workstation.setLocation(cellValue);
//                        } else if (j == 4) {
//                            workstation.setRemark(ExcelUtil.subStrCellValue(cellValue));
//                        }
//                    }
//
//                }
//                workstations.add(workstation);
//            }
//            List<String> workstationIdList = ImporterDao.getWorkstationIdListByWorkshopId(con, workshopId);
//            if (CollectionUtils.isNotEmpty(workstationIdList)) {
//                String workstationIdsString = "'" + String.join("','", workstationIdList) + "'";
//                ImporterDao.deleteWorkstationForm(con, workstationIdsString);
//                ImporterDao.deleteWorkstation(con, workstationIdsString);
//            }
//            ImporterDao.saveWorkstation(con, workstations);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
