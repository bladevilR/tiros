package org.jeecg.modules.material.warehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.common.tiros.wbs.service.WbsService;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.bean.bo.BuWarehouseExcelBONew;
import org.jeecg.modules.material.warehouse.bean.vo.BuMtrWarehouseQueryVO;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.jeecg.modules.material.warehouse.service.BuMtrWarehouseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 仓库信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
@Slf4j
@Service
public class BuMtrWarehouseServiceImpl extends ServiceImpl<BuMtrWarehouseMapper, BuMtrWarehouse> implements BuMtrWarehouseService {

    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;
    @Resource
    private WbsService wbsService;

    @Value("${tiros.base.defaultSysHouseCategory:AJ1}")
    private String defaultSysHouseCategory;


    /**
     * @see BuMtrWarehouseService#getAllTrees(boolean)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMtrWarehouse> getAllTrees(boolean needFilterWorkshop) {
        String workshopId = null;
        if (needFilterWorkshop) {
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            workshopId = buMtrWarehouseMapper.selectUserWorkshopIdByUserId(sysUser.getId());
        }
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectValidList(workshopId);

        List<BuMtrWarehouse> topList = warehouseList.stream()
                .filter(warehouse -> StringUtils.isBlank(warehouse.getParentId()))
                .collect(Collectors.toList());
        for (BuMtrWarehouse top : topList) {
            recurseAddChild(top, warehouseList);
        }
        topList.sort(Comparator.comparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())));

        return topList;
    }

    /**
     * @see BuMtrWarehouseService#listByParentId(String, boolean)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMtrWarehouse> listByParentId(String parentId, boolean needFilterWorkshop) throws Exception {
        String workshopId = null;
        if (needFilterWorkshop) {
            // 获取登录人信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            workshopId = buMtrWarehouseMapper.selectUserWorkshopIdByUserId(sysUser.getId());
        }
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectListByParentId(parentId, workshopId);
        warehouseList.sort(Comparator.comparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())));

        return warehouseList;
    }

    /**
     * @see BuMtrWarehouseService#page(BuMtrWarehouseQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMtrWarehouse> page(BuMtrWarehouseQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMtrWarehouseMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMtrWarehouseService#getWarehouseById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMtrWarehouse getWarehouseById(String id) throws Exception {
        return buMtrWarehouseMapper.selectWarehouseById(id);
    }

    /**
     * @see BuMtrWarehouseService#save(Object)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveWarehouse(BuMtrWarehouse buMtrWarehouse) {
        setWbsAndSysHouseCategory(buMtrWarehouse);
        buMtrWarehouseMapper.insert(buMtrWarehouse);

        return true;
    }

    /**
     * @see BuMtrWarehouseService#updateById(Object)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWarehouse(BuMtrWarehouse buMtrWarehouse) {
        BuMtrWarehouse dbWarehouse = buMtrWarehouseMapper.selectById(buMtrWarehouse.getId());

        if (1 == dbWarehouse.getSync()) {
            throw new JeecgBootException("同步创建的仓库不能修改");
        }
        buMtrWarehouseMapper.updateById(buMtrWarehouse);

        // 更新wbs
        Set<String> workshopIdSet = new HashSet<>();
        workshopIdSet.add(buMtrWarehouse.getWorkshopId());
        workshopIdSet.add(dbWarehouse.getWorkshopId());
        updateWarehouseWbs(workshopIdSet);

        return true;
    }

    /**
     * @see BuMtrWarehouseService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        // 获取仓库及所有子仓库
        List<String> wbsList = buMtrWarehouseMapper.selectWbsListByIdList(idList);
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectListAndAllChildrenListByWbsList(wbsList);

        for (BuMtrWarehouse warehouse : warehouseList) {
            if (1 == warehouse.getSync()) {
                throw new JeecgBootException("仓库或子仓库是同步创建的，不能删除");
            }
        }

        // 删除仓库及所有子仓库
        List<String> allIdList = warehouseList.stream()
                .map(BuMtrWarehouse::getId)
                .collect(Collectors.toList());
        buMtrWarehouseMapper.deleteBatchIds(allIdList);

        // 更新wbs
        Set<String> workshopIdSet = warehouseList.stream()
                .map(BuMtrWarehouse::getWorkshopId)
                .collect(Collectors.toSet());
        updateWarehouseWbs(workshopIdSet);

        return true;
    }

    /**
     * @see BuMtrWarehouseService#changeClose(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changeClose(String id) throws Exception {
        BuMtrWarehouse buMtrWarehouse = buMtrWarehouseMapper.selectById(id);

        Integer close = buMtrWarehouse.getClose();
        Integer newClose = 0 == close ? 1 : 0;
        buMtrWarehouse.setClose(newClose);

        buMtrWarehouseMapper.updateById(buMtrWarehouse);

        return true;
    }

    /**
     * @see BuMtrWarehouseService#importWarehouseInfoFromExcel(MultipartFile, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean importWarehouseInfoFromExcel(MultipartFile excelFile, String parentId) throws Exception {
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

        // 查询并构建上级仓库树
        BuMtrWarehouse parentWarehouse = buMtrWarehouseMapper.selectById(parentId);
        if (null == parentWarehouse) {
            throw new JeecgBootException("选择的父仓库不存在");
        }
        List<BuMtrWarehouse> warehouseList = buMtrWarehouseMapper.selectList(Wrappers.emptyWrapper());
        warehouseList.sort(Comparator.comparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())));
        recurseAddChild(parentWarehouse, warehouseList);

        Set<String> warehouseCodeSet = warehouseList.stream()
                .map(BuMtrWarehouse::getCode)
                .collect(Collectors.toSet());

        int numberOfSheets = workbook.getNumberOfSheets();
        log.info("导入excel库位信息开始");
        log.info("文件【" + excelFile.getOriginalFilename() + "】总共表单个数=" + numberOfSheets);

        boolean hasMatchingForms = false;
        List<BuWarehouseExcelBONew> excelBOList = new ArrayList<>();
        // 操作每个表单
        for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            log.info(String.format("第%s个表单【%s】", sheetNum + 1, sheet.getSheetName()));

            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();

            // 获取数据单元格的对应位置
            Integer level2CellNum = null;
            Integer level3CellNum = null;
            Integer level4CellNum = null;
            Row row = sheet.getRow(firstRowNum);
            if (null != row) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (null != cell) {
                        String value = getCellValue(cell);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }

                        if (value.equals("库房")) {
                            level2CellNum = cellNum;
                        } else if (value.equals("货位")) {
                            level3CellNum = cellNum;
                        } else if (value.contains("四级货位")) {
                            level4CellNum = cellNum;
                        }
                    }
                }
            }

            if (null == level2CellNum || null == level3CellNum || null == level4CellNum) {
                log.info(String.format("第%s个表单%s,没有仓库信息,不进行处理", sheetNum + 1, sheet.getSheetName()));
            } else {
                log.info(String.format("第%s个表单%s,有仓库信息,开始导入", sheetNum + 1, sheet.getSheetName()));

                hasMatchingForms = true;
                int rowCount = 0;

                // 操作每行数据
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    String level2 = getCellValue(sheet.getRow(rowNum).getCell(level2CellNum));
                    String level3 = getCellValue(sheet.getRow(rowNum).getCell(level3CellNum));
                    String level4 = getCellValue(sheet.getRow(rowNum).getCell(level4CellNum));

                    excelBOList.add(
                            new BuWarehouseExcelBONew()
                                    .setLevel2(level2)
                                    .setLevel3(level3)
                                    .setLevel4(level4)
                    );

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
            dealSaveWarehouse(excelBOList, parentWarehouse, warehouseCodeSet);
        }

        return true;
    }


    private void recurseAddChild(BuMtrWarehouse warehouse, List<BuMtrWarehouse> warehouseList) {
        if (null == warehouse) {
            return;
        }

        String id = warehouse.getId();
        List<BuMtrWarehouse> children = warehouseList.stream()
                .filter(detail -> StringUtils.isNotBlank(id) && id.equals(detail.getParentId()))
                .sorted(Comparator.comparing(BuMtrWarehouse::getHasChildren, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BuMtrWarehouse::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        warehouse.setChildren(children);
        if (CollectionUtils.isNotEmpty(children)) {
            for (BuMtrWarehouse child : children) {
                recurseAddChild(child, warehouseList);
            }
        }
    }

    private void setWbsAndSysHouseCategory(BuMtrWarehouse buMtrWarehouse) {
        if (StringUtils.isNotBlank(buMtrWarehouse.getParentId())) {
            BuMtrWarehouse parent = buMtrWarehouseMapper.selectById(buMtrWarehouse.getParentId());
            String parentWbs = parent.getWbs();
            if (StringUtils.isNotBlank(parentWbs)) {
                buMtrWarehouse.setWbs(parentWbs + "." + buMtrWarehouse.getCode());
            } else {
                buMtrWarehouse.setWbs(buMtrWarehouse.getCode());
            }
            String sysHouseCategory = StringUtils.isBlank(parent.getSysHouseCategory()) ? defaultSysHouseCategory : parent.getSysHouseCategory();
            buMtrWarehouse.setSysHouseCategory(sysHouseCategory);
        } else {
            buMtrWarehouse.setWbs(buMtrWarehouse.getCode())
                    .setSysHouseCategory(defaultSysHouseCategory);
        }
    }

    private void updateWarehouseWbs(Set<String> workshopIdSet) {
        WbsConf wbsConf = new WbsConf("bu_mtr_warehouse");

        if (workshopIdSet.size() > 1) {
            String workshopIdsString = "'" + String.join("','", workshopIdSet) + "'";
            wbsConf.setFilter("workshop_id in (" + workshopIdsString + ")");
        } else if (workshopIdSet.size() == 1) {
            wbsConf.setFilter("workshop_id = '" + workshopIdSet.iterator().next() + "'");
        }

        wbsService.updateWbs(wbsConf);
    }

    private String getCellValue(Cell cell) {
        String value = "";
        if (null == cell) {
            return value;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }

        return value.trim();
    }

    private void dealSaveWarehouse(List<BuWarehouseExcelBONew> excelBOList,
                                   BuMtrWarehouse parentWarehouse,
                                   Set<String> warehouseCodeSet) {
        if (CollectionUtils.isEmpty(excelBOList)) {
            return;
        }

        extractChildrenToParentWarehouse(excelBOList, parentWarehouse, warehouseCodeSet);

        List<BuMtrWarehouse> needInsertWarehouseList = new ArrayList<>();
        getNeedInsertWarehouseList(parentWarehouse, needInsertWarehouseList);
        if (CollectionUtils.isNotEmpty(needInsertWarehouseList)) {
            List<List<BuMtrWarehouse>> batchSubList = DatabaseBatchSubUtil.batchSubList(needInsertWarehouseList);
            for (List<BuMtrWarehouse> batchSub : batchSubList) {
                buMtrWarehouseMapper.insertList(batchSub);
            }
        }
    }

    private void extractChildrenToParentWarehouse(List<BuWarehouseExcelBONew> excelBOList,
                                                  BuMtrWarehouse parentWarehouse,
                                                  Set<String> warehouseCodeSet) {
        if (CollectionUtils.isEmpty(excelBOList)) {
            return;
        }

        for (BuWarehouseExcelBONew excelBO : excelBOList) {
            String level2 = excelBO.getLevel2();
            String level3 = excelBO.getLevel3();
            String level4 = excelBO.getLevel4();

            List<BuMtrWarehouse> level2List = parentWarehouse.getChildren();
            BuMtrWarehouse level2Warehouse = getOrNewLevel2Warehouse(level2List, level2, parentWarehouse, warehouseCodeSet);
            List<BuMtrWarehouse> level3List = level2Warehouse.getChildren();
            BuMtrWarehouse level3Warehouse = getOrNewLevel3Warehouse(level3List, level3, level2Warehouse, warehouseCodeSet);
            List<BuMtrWarehouse> level4List = level3Warehouse.getChildren();
            BuMtrWarehouse level4Warehouse = getOrNewLevel4Warehouse(level4List, level4, level3Warehouse, warehouseCodeSet);
        }
    }

    private BuMtrWarehouse getOrNewLevel2Warehouse(List<BuMtrWarehouse> level2List,
                                                   String level2,
                                                   BuMtrWarehouse parentWarehouse,
                                                   Set<String> warehouseCodeSet) {
        BuMtrWarehouse level2Warehouse = null;

        for (BuMtrWarehouse warehouse : level2List) {
            if (level2.equals(warehouse.getName())) {
                level2Warehouse = warehouse;
                break;
            }
        }
        if (null == level2Warehouse) {
            // 库存组织
            String sysHouseCategory = StringUtils.isBlank(parentWarehouse.getSysHouseCategory()) ? defaultSysHouseCategory : parentWarehouse.getSysHouseCategory();
            // 库房
            int level = parentWarehouse.getWhLevel() + 1;
            String selfCode = level2;
            level2Warehouse = new BuMtrWarehouse()
                    .setId(UUIDGenerator.generate())
                    .setName(level2)
                    .setCode(selfCode)
                    .setWhLevel(level)
                    .setType(1)
                    .setClose(0)
                    .setParentId(parentWarehouse.getId())
                    .setWbs(parentWarehouse.getWbs() + "." + selfCode)
                    .setSysHouseCode(level2)
                    .setSysHouseCategory(sysHouseCategory)
                    .setLineId(parentWarehouse.getLineId())
                    .setDepotId(parentWarehouse.getDepotId())
                    .setWorkshopId(parentWarehouse.getWorkshopId())
                    .setCompanyId(parentWarehouse.getCompanyId())
                    .setSync(1)
                    .setStatus(1)
                    .setRemark("excel导入创建")
                    .setNeedInsert(true);
            level2List.add(level2Warehouse);
            warehouseCodeSet.add(selfCode);
        }

        if (CollectionUtils.isEmpty(level2Warehouse.getChildren())) {
            level2Warehouse.setChildren(new ArrayList<>());
        }
        return level2Warehouse;
    }

    private BuMtrWarehouse getOrNewLevel3Warehouse(List<BuMtrWarehouse> level3List,
                                                   String level3,
                                                   BuMtrWarehouse level2Warehouse,
                                                   Set<String> warehouseCodeSet) {
        BuMtrWarehouse level3Warehouse = null;

        for (BuMtrWarehouse warehouse : level3List) {
            if (level3.equals(warehouse.getName())) {
                level3Warehouse = warehouse;
                break;
            }
        }
        if (null == level3Warehouse) {
            // 库存组织
            String sysHouseCategory = StringUtils.isBlank(level2Warehouse.getSysHouseCategory()) ? defaultSysHouseCategory : level2Warehouse.getSysHouseCategory();
            // 库位
            int level = level2Warehouse.getWhLevel() + 1;
            String selfCode = getWarehouseCode(level, warehouseCodeSet);

            level3Warehouse = new BuMtrWarehouse()
                    .setId(UUIDGenerator.generate())
                    .setName(level3)
                    .setCode(selfCode)
                    .setWhLevel(level)
                    .setType(2)
                    .setClose(0)
                    .setParentId(level2Warehouse.getId())
                    .setWbs(level2Warehouse.getWbs() + "." + selfCode)
                    .setSysHouseCode(level3)
                    .setSysHouseCategory(sysHouseCategory)
                    .setLineId(level2Warehouse.getLineId())
                    .setDepotId(level2Warehouse.getDepotId())
                    .setWorkshopId(level2Warehouse.getWorkshopId())
                    .setCompanyId(level2Warehouse.getCompanyId())
                    .setSync(1)
                    .setStatus(1)
                    .setRemark("excel导入创建")
                    .setNeedInsert(true);
            level3List.add(level3Warehouse);
            warehouseCodeSet.add(selfCode);
        }

        if (CollectionUtils.isEmpty(level3Warehouse.getChildren())) {
            level3Warehouse.setChildren(new ArrayList<>());
        }
        return level3Warehouse;
    }

    private BuMtrWarehouse getOrNewLevel4Warehouse(List<BuMtrWarehouse> level4List,
                                                   String level4,
                                                   BuMtrWarehouse level3Warehouse,
                                                   Set<String> warehouseCodeSet) {
        BuMtrWarehouse level4Warehouse = null;

        for (BuMtrWarehouse warehouse : level4List) {
            if (level4.equals(warehouse.getName())) {
                level4Warehouse = warehouse;
                break;
            }
        }
        if (null == level4Warehouse) {
            // 库存组织
            String sysHouseCategory = StringUtils.isBlank(level3Warehouse.getSysHouseCategory()) ? defaultSysHouseCategory : level3Warehouse.getSysHouseCategory();
            // 库位
            int level = level3Warehouse.getWhLevel() + 1;
            String selfCode = getWarehouseCode(level, warehouseCodeSet);

            level4Warehouse = new BuMtrWarehouse()
                    .setId(UUIDGenerator.generate())
                    .setName(level4)
                    .setCode(selfCode)
                    .setWhLevel(level)
                    .setType(2)
                    .setClose(0)
                    .setParentId(level3Warehouse.getId())
                    .setWbs(level3Warehouse.getWbs() + "." + selfCode)
                    .setSysHouseCode(null)
                    .setSysHouseCategory(sysHouseCategory)
                    .setLineId(level3Warehouse.getLineId())
                    .setDepotId(level3Warehouse.getDepotId())
                    .setWorkshopId(level3Warehouse.getWorkshopId())
                    .setCompanyId(level3Warehouse.getCompanyId())
                    .setSync(0)
                    .setStatus(1)
                    .setRemark("excel导入创建")
                    .setNeedInsert(true);
            level4List.add(level4Warehouse);
            warehouseCodeSet.add(selfCode);
        }

        if (CollectionUtils.isEmpty(level4Warehouse.getChildren())) {
            level4Warehouse.setChildren(new ArrayList<>());
        }
        return level4Warehouse;
    }

    private String getWarehouseCode(int level, Set<String> warehouseCodeSet) {
        String childSelfCode = level + RandomStringUtils.randomAlphanumeric(5);
        if (warehouseCodeSet.contains(childSelfCode)) {
            return getWarehouseCode(level, warehouseCodeSet);
        } else {
            return childSelfCode;
        }
    }

    private List<String> getSelfNodes(String self) {
        List<String> nodes = new ArrayList<>();
        if (self.contains("/")) {
            String[] split = self.split("/");
            nodes.add(split[0]);
            for (int i = 1; i < split.length; i++) {
                String node = split[i];
                int barSplitNumber = getBarSplitNumber(node);
                String[] split0Splits = split[0].split("-");
                StringBuilder prefix = new StringBuilder();
                for (int j = 0; j < split0Splits.length - 1 - barSplitNumber; j++) {
                    prefix.append(split0Splits[j]).append("-");
                }
                nodes.add(prefix.toString() + node);
            }
        } else {
            nodes.add(self);
        }

        return nodes;
    }

    private int getBarSplitNumber(String node) {
        if (node.contains("-")) {
            String replace = node.replaceAll("-", "");
            return node.length() - replace.length();
        } else {
            return 0;
        }
    }

    private String getCurrentSysCode(String[] sysCodes, int sysCodesIndex) {
        StringBuilder currentSysCodeBuilder = new StringBuilder();
        int i = 0;
        while (i <= sysCodesIndex) {
            currentSysCodeBuilder.append(sysCodes[i]).append(".");
            i++;
        }
        String currentSysCode = null;
        if (currentSysCodeBuilder.length() > 0) {
            currentSysCode = currentSysCodeBuilder.deleteCharAt(currentSysCodeBuilder.length() - 1).toString();
        }

        return currentSysCode;
    }

    private void getNeedInsertWarehouseList(BuMtrWarehouse parentWarehouse, List<BuMtrWarehouse> needInsertWarehouseList) {
        if (null == parentWarehouse) {
            return;
        }
        List<BuMtrWarehouse> children = parentWarehouse.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        for (BuMtrWarehouse child : children) {
            getNeedInsertWarehouseList(child, needInsertWarehouseList);
            if (child.getNeedInsert() != null && child.getNeedInsert()) {
                needInsertWarehouseList.add(child.setChildren(null));
            }
        }
    }


}
