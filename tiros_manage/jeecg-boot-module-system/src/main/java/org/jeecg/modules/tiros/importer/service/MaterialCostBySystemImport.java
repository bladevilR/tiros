package org.jeecg.modules.tiros.importer.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.dataisolation.bean.LineWorkshopCompany;
import org.jeecg.common.tiros.dataisolation.service.LineWorkshopCompanyService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainInfoMapper;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.report.cost.mapper.BuRptBoardSysMaterialMapper;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;


/**
 * @author yyg
 */
@Repository
@Slf4j
public class MaterialCostBySystemImport {
    @Resource
    private BuTrainInfoMapper trainInfoMapper;
    @Resource
    private BuTrainAssetTypeMapper assetTypeMapper;
    @Resource
    private BuRptBoardSysMaterialMapper buRptBoardSysMaterialMapper;
    @Resource
    private LineWorkshopCompanyService lineWorkshopCompanyService;


    public void execute(List<MultipartFile> files) throws Exception {
        try {
            // 获取系统id
            Map<String, String> sysIdMap = getSysIdMap();

            Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = lineWorkshopCompanyService.mapLineWorkshopCompany();

            Map<String, Date> dxTimeMap = getSuccessTimeMap(files.get(0));
            Map<String, Date> jxTimeMap = getSuccessTimeMap(files.get(1));
            Sheet sheet;
            Workbook workbook = ExcelUtil.getWorkbookByStream(files.get(2).getName(), files.get(2).getInputStream());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                List<BuRptBoardSysMaterial> sysMaterialList = new ArrayList<>();
                String programId = null;
                String trainId = null;
                String trainNo = null;
                //表示一辆车的开始位置
                int pageIndex = 0;
                //第几列车
                int repairIndex = 1;
                sheet = workbook.getSheetAt(i);
                //根据sheetName获取线路id和修程id
                String sheetName = sheet.getSheetName();
                if (sheetName.contains("Sheet")) {
                    continue;
                }

                String lineId = sheetName.substring(0, sheetName.indexOf("号"));
                LineWorkshopCompany lineWorkshopCompany = lineWorkshopCompanyMap.get(lineId);
                String workshopId = lineWorkshopCompany.getWorkshopId();
                String companyId = lineWorkshopCompany.getCompanyId();
                String depotId = lineWorkshopCompany.getDepotId();

                if (sheetName.contains("架修")) {
                    programId = "1";
                } else if (sheetName.contains("大修")) {
                    programId = "2";
                }
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                for (int rowIndex = firstRowNum; rowIndex < lastRowNum; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    BuRptBoardSysMaterial sysMaterial = null;
                    if (ExcelUtil.isEmptyRow(row)) {
                        pageIndex = rowIndex + 1;
                        repairIndex++;
                    }
                    if (row == null) {
                        continue;
                    }
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        String cellValue = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                        if ("以下是沿线车辆".equals(cellValue)) {
                            pageIndex = rowIndex + 2;
                        }
                        if (i == 0 && cellIndex == 0 && StringUtils.isNotBlank(cellValue) && rowIndex == pageIndex) {
                            trainNo = cellValue.substring(cellValue.indexOf("列") + 1, cellValue.indexOf("车"));
                            trainId = trainInfoMapper.selectByTrainNo(trainNo).getId();
                        } else {
                            if (i != 0 && StringUtils.isNotBlank(cellValue) && cellIndex == 0 && rowIndex == pageIndex) {
                                trainNo = cellValue.substring(0, cellValue.indexOf("车"));
                                trainId = trainInfoMapper.selectByTrainNo(trainNo).getId();
                            }
                        }

                        if (rowIndex == pageIndex + 1) {
                            break;
                        }
                        if (rowIndex > pageIndex + 1) {
                            if (cellIndex == 0) {
                                if (StringUtils.isNotBlank(cellValue)) {
                                    String sysId = sysIdMap.get(cellValue);
                                    if (StringUtils.isBlank(sysId)) {
                                        break;
                                    }
                                    sysMaterial = createSysMaterial(programId, trainId, repairIndex, lineId, workshopId, companyId, depotId, trainNo, sysId);
                                    Date successTime = "1".equals(programId) ? jxTimeMap.get(trainNo) : dxTimeMap.get(trainNo);
                                    if (successTime != null) {
                                        sysMaterial.setYear(successTime.getYear() + 1900)
                                                .setMonth(successTime.getMonth() + 1);
                                    }

                                }
                            }
                            //必换件
                            if (cellIndex == 1) {
                                if (sysMaterial != null) {
                                    sysMaterial.setMustCost(StringUtils.isBlank(cellValue) ? BigDecimal.ZERO : new BigDecimal(cellValue));
                                }
                            }
                            //偶换件
                            if (cellIndex == 2) {
                                if (sysMaterial != null) {
                                    sysMaterial.setRandomCost(StringUtils.isBlank(cellValue) ? BigDecimal.ZERO : new BigDecimal(cellValue));
                                }
                            }
                            //耗材
                            if (cellIndex == 4) {
                                if (sysMaterial != null) {
                                    sysMaterial.setConsumeCost(StringUtils.isBlank(cellValue) ? BigDecimal.ZERO : new BigDecimal(cellValue));
                                }
                            }

                        }

                    }
                    if (sysMaterial != null) {
                        sysMaterialList.add(sysMaterial);
                    }
                }
                buRptBoardSysMaterialMapper.insertBatch(sysMaterialList);
            }

        } catch (SQLException e) {
            throw new JeecgBootException("导入失败");
        } catch (Exception e) {
            e.printStackTrace();
            throw new JeecgBootException("文件格式错误");
        }

    }

    private Map<String, Date> getSuccessTimeMap(MultipartFile file) throws Exception {
        Map<String, Date> timeMap = new HashMap<>();
        Workbook dxWorkbook = ExcelUtil.getWorkbookByStream(file.getName(), file.getInputStream());
        Sheet dxSheet = dxWorkbook.getSheetAt(0);

        for (int rowIndex = dxSheet.getFirstRowNum() + 1; rowIndex < dxSheet.getLastRowNum(); rowIndex++) {
            Row row = dxSheet.getRow(rowIndex);
            String trainNo = "";
            Date successTime = null;
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                String cellValue = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                if (cellIndex == 1) {
                    trainNo = cellValue;
                }
                if (cellIndex == 6) {
                    successTime = parseDate(cellValue);
                }
                if (StringUtils.isNotBlank(trainNo) && successTime != null) {
                    timeMap.put(trainNo, successTime);
                }
            }
        }
        return timeMap;
    }

    private BuRptBoardSysMaterial createSysMaterial(String programId, String trainId, int repairIndex, String lineId, String workshopId, String companyId, String depotId, String trainNo, String sysId) {
        return new BuRptBoardSysMaterial()
                .setId(UUIDGenerator.generate())
                .setSysId(sysId)
                .setLineId(lineId)
                .setWorkshopId(workshopId)
                .setCompanyId(companyId)
                .setDepotId(depotId)
                .setYear(2018)
                .setMonth(1)
                .setProgramId(programId)
                .setTrainId(trainId)
                .setTrainNo(trainNo)
                .setRepairPeriod("-1")
                .setRepairIndex(repairIndex);
    }

    private Map<String, String> getSysIdMap() {
        Map<String, String> sysIdMap = new HashMap<>();
        List<String> excelSysName = Arrays.asList("转向架", "车门", "受电弓", "整车电气", "车体", "辅助", "牵引", "制动", "车钩", "空调");
        assetTypeMapper.selectList(Wrappers.<BuTrainAssetType>lambdaQuery().select(BuTrainAssetType::getId, BuTrainAssetType::getName)
                .isNull(BuTrainAssetType::getParentId)).forEach(item -> {
            excelSysName.forEach(name -> {
                if (item.getName().contains(name)) {
                    sysIdMap.put(name, item.getId());
                }
            });
        });
        String sysId = sysIdMap.get("整车电气");
        sysIdMap.put("车上电气", sysId);
        sysIdMap.remove("整车电气");
        return sysIdMap;
    }

    private Date parseDate(String cellValue) {
        if (StringUtils.isNotBlank(cellValue)) {
            Calendar calendar = new GregorianCalendar(1900, Calendar.JANUARY, -1);
            Date date = calendar.getTime();
            return DateUtils.addDays(date, Double.valueOf(cellValue).intValue());
        }
        return new Date();
    }

}


