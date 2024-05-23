package org.jeecg.modules.tiros.importer.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.dataisolation.bean.LineWorkshopCompany;
import org.jeecg.common.tiros.dataisolation.service.LineWorkshopCompanyService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainAssetTypeMapper;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainInfoMapper;
import org.jeecg.modules.board.cost.bean.BuRptBoardTrainMaterial;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author yfy
 * @title: RptBoardImport
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/10 10:35
 */
@Repository
public class RptBoardImport {

    @Resource
    private ImporterSql importerSql;
    @Resource
    private BuTrainInfoMapper buTrainInfoMapper;
    @Resource
    private BuTrainAssetTypeMapper buTrainAssetTypeMapper;
    @Resource
    private LineWorkshopCompanyService lineWorkshopCompanyService;


    public void importRptBoard(String fileName, InputStream inputStream) throws Exception {
        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = lineWorkshopCompanyService.mapLineWorkshopCompany();

        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        int cellIndexMax = 5;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String lineId = null;
        String workshopId = null;
        String companyId = null;
        String depotId = null;
        String trainId = null;
        String trainNo = null;

        List<BuRptBoardSysMaterial> boardSysMaterials = new ArrayList<>();
        List<BuRptBoardTrainMaterial> boardTrainMaterialList = new ArrayList<>();

        try {
            //读取每一行数据
            for (int rowIndex = 0; rowIndex < lastRowNum - 1; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                BuRptBoardSysMaterial sysMaterial = new BuRptBoardSysMaterial();
                BuRptBoardTrainMaterial trainMaterial = new BuRptBoardTrainMaterial();
                BuTrainInfo buTrainInfo = new BuTrainInfo();
                BuTrainAssetType buTrainAssetType = new BuTrainAssetType();
                for (int cellIndex = 0; cellIndex < cellIndexMax; cellIndex++) {
                    String value = null;
                    if (rowIndex == lastRowNum - 2 && cellIndex >= 1) {
                        row.getCell(cellIndex).setCellType(HSSFCell.CELL_TYPE_STRING);
                        value = String.valueOf(new BigDecimal(ExcelUtil.getCellValue(row.getCell(cellIndex))).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    if (rowIndex >= 2 && rowIndex != lastRowNum - 2) {
                        if (cellIndex == 2) {
                            row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
                            value = String.valueOf(new BigDecimal(ExcelUtil.getCellValue(row.getCell(2))).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (cellIndex >= 1 && cellIndex != 2) {
                            value = String.valueOf(new BigDecimal(ExcelUtil.getCellValue(row.getCell(cellIndex))).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (cellIndex == 0) {
                            value = ExcelUtil.getCellValue(row.getCell(cellIndex));
                        }
                    } else if (rowIndex == 0) {
                        value = ExcelUtil.getCellValue(row.getCell(cellIndex)).trim();
                        if (cellIndex == 0) {
                            int a = value.indexOf("车");
                            trainNo = value.substring(0, a);
                            List<BuTrainInfo> buTrainInfo1 = findBuTrainInfo(trainNo);
                            if (buTrainInfo1.size() >= 1) {
                                lineId = buTrainInfo1.get(0).getLineId();
                                trainId = buTrainInfo1.get(0).getId();

                                LineWorkshopCompany lineWorkshopCompany = lineWorkshopCompanyMap.get(lineId);
                                workshopId = lineWorkshopCompany.getWorkshopId();
                                companyId = lineWorkshopCompany.getCompanyId();
                                depotId = lineWorkshopCompany.getDepotId();
                            } else {
                                throw new JeecgBootException(trainNo + "车辆不存在,导入失败!");
                            }
                        }
                    }
                    if (StrUtil.isNotEmpty(value)) {
                        if (rowIndex == lastRowNum - 2) {
                            if (cellIndex == 1) {
                                trainMaterial.setMustCost(new BigDecimal(value).stripTrailingZeros());
                            } else if (cellIndex == 2) {
                                trainMaterial.setRandomCost(new BigDecimal(value).stripTrailingZeros());
                            } else if (cellIndex == 4) {
                                trainMaterial.setConsumeCost(new BigDecimal(value).stripTrailingZeros());
                                trainMaterial.setDepotId(depotId);
                                trainMaterial.setWorkshopId(workshopId);
                                trainMaterial.setCompanyId(companyId);
                                trainMaterial.setRepairPeriod("-1");
                                trainMaterial.setRepairIndex(-1);
                                trainMaterial.setProgramId("11");
                                trainMaterial.setYear(year);
                                trainMaterial.setMonth(month);
                                trainMaterial.setLineId(lineId);
                                trainMaterial.setTrainId(trainId);
                                trainMaterial.setTrainNo(trainNo);
                                boardTrainMaterialList.add(trainMaterial);
                            }
                        } else if (rowIndex >= 2 && rowIndex < lastRowNum - 2) {
                            if (cellIndex == 0) {
                                List<BuTrainAssetType> typeList = findBuTrainAssetType(value);
                                if (typeList.size() >= 1) {
                                    sysMaterial.setSysId(typeList.get(0).getId());
                                } else {
                                    throw new JeecgBootException(value + "设备类型不存在,导入失败!");
                                }
                            } else if (cellIndex == 1) {
                                sysMaterial.setMustCost(new BigDecimal(value).stripTrailingZeros());
                            } else if (cellIndex == 2) {
                                sysMaterial.setRandomCost(new BigDecimal(value).stripTrailingZeros());
                            } else if (cellIndex == 4) {
                                sysMaterial.setConsumeCost(new BigDecimal(value).stripTrailingZeros());
                                sysMaterial.setDepotId(depotId);
                                sysMaterial.setWorkshopId(workshopId);
                                sysMaterial.setCompanyId(companyId);
                                sysMaterial.setRepairPeriod("-1");
                                sysMaterial.setRepairIndex(-1);
                                sysMaterial.setProgramId("1");
                                sysMaterial.setYear(year);
                                sysMaterial.setMonth(month);
                                sysMaterial.setLineId(lineId);
                                sysMaterial.setTrainId(trainId);
                                sysMaterial.setTrainNo(trainNo);
                                boardSysMaterials.add(sysMaterial);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new JeecgBootException("文件内容格式不正确!");
        }
        try {
            save(boardSysMaterials, boardTrainMaterialList);
        } catch (Exception e) {
            throw new JeecgBootException("文件导入失败!");
        }

    }

    private List<BuTrainInfo> findBuTrainInfo(String trainNo) {
        LambdaQueryWrapper<BuTrainInfo> trainInfoLambdaQueryWrapper = new LambdaQueryWrapper<BuTrainInfo>()
                .eq(BuTrainInfo::getTrainNo, trainNo);

        List<BuTrainInfo> buTrainInfo = buTrainInfoMapper.selectList(trainInfoLambdaQueryWrapper);
        return buTrainInfo;
    }

    private List<BuTrainAssetType> findBuTrainAssetType(String name) {
        LambdaQueryWrapper<BuTrainAssetType> typeLambdaQueryWrapper = new LambdaQueryWrapper<BuTrainAssetType>()
                .eq(BuTrainAssetType::getStructType, "1")
                .like(BuTrainAssetType::getName, name);
        List<BuTrainAssetType> buTrainAssetType = buTrainAssetTypeMapper.selectList(typeLambdaQueryWrapper);
        return buTrainAssetType;
    }


    @Transactional(rollbackFor = Exception.class)
    public void save(List<BuRptBoardSysMaterial> sysMaterialList, List<BuRptBoardTrainMaterial> trainMaterialList) throws Exception {
        if (EmptyUtils.listNotEmpty(sysMaterialList)) {
            importerSql.saveRptBoardSysMaterial(sysMaterialList);
        }
        if (EmptyUtils.listNotEmpty(trainMaterialList)) {
            importerSql.saveRptBoardTrainMaterial(trainMaterialList);
        }

    }


}
