package org.jeecg.modules.material.turnovernew.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.turnovernew.bean.BuMaterialTurnoverNew;
import org.jeecg.modules.material.turnovernew.bean.vo.BuMaterialTurnoverQueryVONew;
import org.jeecg.modules.material.turnovernew.mapper.BuMaterialTurnoverNewMapper;
import org.jeecg.modules.material.turnovernew.service.BuMaterialTurnoverNewService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 周转件 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-09-18
 */
@Slf4j
@Service
public class BuMaterialTurnoverNewServiceImpl extends ServiceImpl<BuMaterialTurnoverNewMapper, BuMaterialTurnoverNew> implements BuMaterialTurnoverNewService {

    @Resource
    private BuMaterialTurnoverNewMapper buMaterialTurnoverNewMapper;
    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;


    /**
     * @see BuMaterialTurnoverNewService#pageTurnover(BuMaterialTurnoverQueryVONew, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialTurnoverNew> pageTurnover(BuMaterialTurnoverQueryVONew queryVO, Integer pageNo, Integer pageSize) {
        return buMaterialTurnoverNewMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialTurnoverNewService#getTurnoverById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialTurnoverNew getTurnoverById(String id) {
        return buMaterialTurnoverNewMapper.selectTurnoverById(id);
    }

    /**
     * @see BuMaterialTurnoverNewService#saveTurnover(BuMaterialTurnoverNew)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveTurnover(BuMaterialTurnoverNew turnover) {
        // 新增时，计算价格
        if (null == turnover.getPrice()) {
            turnover.setPrice(BigDecimal.ZERO);
        }
        if (null == turnover.getTaxPrice()) {
            turnover.setTaxPrice(BigDecimal.ONE);
        }
        if (null == turnover.getUseAmount()) {
            turnover.setUseAmount(BigDecimal.ZERO);
        }
        if (BigDecimal.ZERO.compareTo(turnover.getPrice()) < 0 && BigDecimal.ZERO.compareTo(turnover.getTaxPrice()) < 0) {
            BigDecimal taxRate = turnover.getTaxPrice().divide(turnover.getPrice(), 8, BigDecimal.ROUND_HALF_UP);
            turnover.setTaxRate(taxRate);
        } else {
            turnover.setTaxRate(BigDecimal.ZERO);
        }
        BigDecimal useAmountPrice = turnover.getTaxPrice().multiply(turnover.getUseAmount()).setScale(8, BigDecimal.ROUND_HALF_UP);
        turnover.setUseAmountPrice(useAmountPrice);

        // 更新
        return this.saveOrUpdate(turnover);
    }

    /**
     * @see BuMaterialTurnoverNewService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) {
        List<String> idList = new ArrayList<>(Arrays.asList(ids.split(",")));

        // 是否有不能删除的数据

        return this.removeByIds(idList);
    }

    /**
     * @see BuMaterialTurnoverNewService#importTurnoverFromExcel(MultipartFile, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean importTurnoverFromExcel(MultipartFile excelFile, Boolean clearOldData) throws Exception {
        Date now = new Date();
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = null == sysUser ? "admin" : sysUser.getUsername();

        if (excelFile.isEmpty()) {
            throw new JeecgBootException("文件为空");
        }
        String filename = excelFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new JeecgBootException("文件名为空");
        }

        InputStream inputStream = excelFile.getInputStream();
        Workbook workbook;

        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equals("xls", extName)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (StringUtils.equals("xlsx", extName)) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new JeecgBootException("错误的excel文件");
        }
        inputStream.close();

        int numberOfSheets = workbook.getNumberOfSheets();
        log.info("导入excel库位信息开始");
        log.info("文件【" + excelFile.getOriginalFilename() + "】总共表单个数=" + numberOfSheets);

        boolean hasMatchingForms = false;
        List<BuMaterialTurnoverNew> turnoverList = new ArrayList<>();
        String nowFormat = DateUtils.yyyyMMdd.get().format(now);
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            log.info(String.format("第%s个表单【%s】", sheetNum + 1, sheet.getSheetName()));

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer materialCodeCellNum = null;
            Integer materialSpecCellNum = null;
            Integer unitCellNum = null;
            Integer priceCellNum = null;
            Integer taxPriceCellNum = null;
            Integer useAmountCellNum = null;
//            Integer useAmountPriceCellNum = null;
            Integer orderCodeCellNum = null;
            Integer outOrderCodeCellNum = null;
            Integer warehouseCodeCellNum = null;
            Integer useTimeCellNum = null;
            Integer systemNameCellNum = null;
            Integer firstUsePlanNameCellNum = null;
            Integer serviceYearRemarkCellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = ExcelUtil.getCellValueByCell(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (value.equals("物料编码")) {
                            materialCodeCellNum = cellNum;
                        } else if (value.equals("物料描述")) {
                            materialSpecCellNum = cellNum;
                        } else if (value.contains("单位")) {
                            unitCellNum = cellNum;
                        } else if (value.contains("成本")) {
                            priceCellNum = cellNum;
                        } else if (value.contains("含税单价")) {
                            taxPriceCellNum = cellNum;
                        } else if (value.contains("消耗量")) {
                            useAmountCellNum = cellNum;
                        } else if (value.contains("总额")) {
//                            useAmountPriceCellNum = cellNum;
                        } else if (value.contains("工单号")) {
                            orderCodeCellNum = cellNum;
                        } else if (value.contains("出库单号")) {
                            outOrderCodeCellNum = cellNum;
                        } else if (value.contains("子库")) {
                            warehouseCodeCellNum = cellNum;
                        } else if (value.contains("消耗日期")) {
                            useTimeCellNum = cellNum;
                        } else if (value.contains("所属系统")) {
                            systemNameCellNum = cellNum;
                        } else if (value.contains("首次投入使用列车")) {
                            firstUsePlanNameCellNum = cellNum;
                        } else if (value.contains("使用年限")) {
                            serviceYearRemarkCellNum = cellNum;
                        }
                    }
                }
            }

            if (null == materialCodeCellNum || null == priceCellNum || null == taxPriceCellNum || null == useAmountCellNum) {
                log.info(String.format("第%s个表单%s,没有必要信息(物料编码/成本/含税单价/消耗量),不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                log.info(String.format("第%s个表单%s,有周转件信息,开始导入", sheetNum + 1, sheet.getSheetName()));

                hasMatchingForms = true;
                int rowCount = 0;

                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    String materialCode = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(materialCodeCellNum));
                    String materialSpec = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(materialSpecCellNum));
                    String unit = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(unitCellNum));
                    String priceStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(priceCellNum));
                    String taxPriceStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(taxPriceCellNum));
                    String useAmountStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(useAmountCellNum));
//                    String useAmountPriceStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(useAmountPriceCellNum));
                    String orderCode = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(orderCodeCellNum));
                    String outOrderCode = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(outOrderCodeCellNum));
                    String warehouseCode = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(warehouseCodeCellNum));
                    String useTimeStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(useTimeCellNum));
                    String systemName = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(systemNameCellNum));
                    String firstUsePlanName = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(firstUsePlanNameCellNum));
                    String serviceYearRemarkStr = ExcelUtil.getCellValueByCell(sheet.getRow(rowNum).getCell(serviceYearRemarkCellNum));

                    BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceStr));
                    BigDecimal taxPrice = BigDecimal.valueOf(Double.parseDouble(taxPriceStr));
                    BigDecimal taxRate = taxPrice.divide(price, 8, BigDecimal.ROUND_HALF_UP);
                    BigDecimal useAmount = BigDecimal.valueOf(Integer.parseInt(useAmountStr));
//                    BigDecimal useAmountPrice = BigDecimal.valueOf(Double.parseDouble(useAmountPriceStr));
                    BigDecimal useAmountPrice = taxPrice.multiply(useAmount).setScale(8, BigDecimal.ROUND_HALF_UP);
                    Date useTime = DateUtils.datetimeFormat.get().parse(useTimeStr);
                    double serviceYear = 0D;
                    if (StringUtils.isNumeric(serviceYearRemarkStr)) {
                        serviceYear = Double.parseDouble(serviceYearRemarkStr);
                    } else {
                        if (serviceYearRemarkStr.contains("年")) {
                            String serviceYearStr = serviceYearRemarkStr.split("年")[0];
                            serviceYear = Double.parseDouble(serviceYearStr);
                        }
                    }

                    BuMaterialTurnoverNew turnover = new BuMaterialTurnoverNew()
                            .setId(UUIDGenerator.generate())
                            .setMaterialTypeId(materialCode)
                            .setName(materialSpec)
                            .setUnit(unit)
                            .setPrice(price)
                            .setTaxRate(taxRate)
                            .setTaxPrice(taxPrice)
                            .setUseAmount(useAmount)
                            .setUseAmountPrice(useAmountPrice)
                            .setOrderCode(orderCode)
                            .setOutOrderCode(outOrderCode)
                            .setWarehouseCode(warehouseCode)
                            .setWarehouseId(null)
                            .setUseTime(useTime)
                            .setSystemShortName(systemName)
                            .setSystemId(null)
                            .setFirstUsePlanName(firstUsePlanName)
                            .setFirstUsePlanId(null)
                            .setProgramId(null)
                            .setServiceYearRemark(serviceYearRemarkStr)
                            .setServiceYear(serviceYear)
                            .setRemark(nowFormat + "导入")
                            .setCreateTime(now)
                            .setCreateBy(username)
                            .setUpdateTime(now)
                            .setUpdateBy(username);

                    turnoverList.add(turnover);

                    rowCount++;
                }
                log.info("总共行数" + rowCount);
            }
        }

        // 如果没有匹配到模板表单，返回异常
        if (!hasMatchingForms) {
            log.info("文件【" + excelFile.getOriginalFilename() + "】未匹配到仓库模板表单");
            throw new JeecgBootException("请选择正确的模板表单");
        } else {
            // 删除旧的
            int deleteCount = 0;
            if (null == clearOldData || clearOldData) {
                deleteCount = buMaterialTurnoverNewMapper.delete(Wrappers.emptyWrapper());
            }

            // 插入新的
            int saveCount = 0;
            if (CollectionUtils.isNotEmpty(turnoverList)) {
                saveCount = turnoverList.size();

                // 补全信息
                completeInfoForImport(turnoverList);

                List<List<BuMaterialTurnoverNew>> batchSubList = DatabaseBatchSubUtil.batchSubList(turnoverList);
                for (List<BuMaterialTurnoverNew> batchSub : batchSubList) {
                    buMaterialTurnoverNewMapper.insertList(batchSub);
                }
            }

            String importInfo = String.format("删除了%s条旧的周转件，插入了%s条新的周转件", deleteCount, saveCount);
            log.info(importInfo);
        }

        return true;
    }


    private void completeInfoForImport(List<BuMaterialTurnoverNew> turnoverList) {
        if (CollectionUtils.isEmpty(turnoverList)) {
            return;
        }

        // 仓库
        Map<String, String> warehouseNameIdMap = new HashMap<>();
        List<String> warehouseNameList = turnoverList.stream()
                .map(BuMaterialTurnoverNew::getWarehouseCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(warehouseNameList)) {
            LambdaQueryWrapper<BuMtrWarehouse> warehouseWrapper = new LambdaQueryWrapper<BuMtrWarehouse>()
                    .in(BuMtrWarehouse::getName, warehouseNameList);
            List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectList(warehouseWrapper);
            for (BuMtrWarehouse warehouse : warehouseList) {
                warehouseNameIdMap.put(warehouse.getName(), warehouse.getId());
            }
        }
        // 系统
        Map<String, String> systemShortNameIdMap = new HashMap<>();
        List<String> systemShortNameList = turnoverList.stream()
                .map(BuMaterialTurnoverNew::getSystemShortName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<Map<String, Object>> systemShortNameIdMapList = buMaterialTurnoverNewMapper.selectSystemShortNameIdMapList(systemShortNameList);
        for (Map<String, Object> systemShortNameIdMapItem : systemShortNameIdMapList) {
            String id = (String) systemShortNameIdMapItem.get("id");
            String shortName = (String) systemShortNameIdMapItem.get("shortName");
            if (StringUtils.isNotBlank(shortName) && StringUtils.isNotBlank(id)) {
                systemShortNameIdMap.put(shortName, id);
            }
        }
        // 列计划
        Map<String, String> planNameIdMap = new HashMap<>();
        Map<String, String> planNameProgramIdMap = new HashMap<>();
        List<String> planNameList = turnoverList.stream()
                .map(BuMaterialTurnoverNew::getFirstUsePlanName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<Map<String, Object>> planNameIdMapList = buMaterialTurnoverNewMapper.selectPlanNameIdMapList(planNameList);
        for (Map<String, Object> planNameIdMapItem : planNameIdMapList) {
            String id = (String) planNameIdMapItem.get("id");
            String planName = (String) planNameIdMapItem.get("planName");
            String programId = (String) planNameIdMapItem.get("programId");
            if (StringUtils.isNotBlank(planName) && StringUtils.isNotBlank(id)) {
                planNameIdMap.put(planName, id);
                planNameProgramIdMap.put(planName, programId);
            }
        }

        for (BuMaterialTurnoverNew turnover : turnoverList) {
            if (StringUtils.isNotBlank(turnover.getWarehouseCode())) {
                String warehouseId = warehouseNameIdMap.get(turnover.getWarehouseCode());
                turnover.setWarehouseId(warehouseId);
            }
            if (StringUtils.isNotBlank(turnover.getSystemShortName())) {
                String systemId = systemShortNameIdMap.get(turnover.getSystemShortName());
                turnover.setSystemId(systemId);
            }
            if (StringUtils.isNotBlank(turnover.getFirstUsePlanName())) {
                String planId = planNameIdMap.get(turnover.getFirstUsePlanName());
                String programId = planNameProgramIdMap.get(turnover.getFirstUsePlanName());
                turnover.setFirstUsePlanId(planId)
                        .setProgramId(programId);
            }
        }
    }

}
