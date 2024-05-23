package org.jeecg.modules.tiros.importer.service;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordCategory;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordDetail;
import org.jeecg.modules.tiros.importer.bean.conf.ImportConfig;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.EmptyUtils;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.jeecg.modules.tiros.importer.utils.UUIDUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author yyg
 */
@Repository
public class ReguWorkRecord {
    @Resource
    private ImporterSql importerSql;
    private Map<String, Integer> categoryIdDetailSortMap = new HashMap<>(128);

    public void importReguWorkRecord(InputStream inputStream, BuWorkRecord workRecord, String fileName) throws Exception {
        List<BuWorkRecordDetail> workRecordDetailList = new ArrayList<>();
        List<BuWorkRecordCategory> workRecordCategoryList = new ArrayList<>();
        String id = workRecord.getId();
        Boolean isNumber = false;
        String categoryId = "";
        Integer checkLevel = 1;
        String techRequire = "";
        String workRecordId = UUIDGenerator.generate();
        int rowNo = 0;

        if (StringUtils.isBlank(workRecord.getId())) {
            workRecord.setId(workRecordId);
        } else {
            workRecordId = workRecord.getId();
        }
        try {
            if (ExcelUtil.isWord2007(fileName)) {
                try {
                    XWPFDocument xwpf = new XWPFDocument(inputStream);
                    Iterator<XWPFTable> it = xwpf.getTablesIterator();
                    while (it.hasNext()) {
                        XWPFTable table = it.next();
                        List<XWPFTableRow> rows = table.getRows();
                        //读取每一行数据
                        for (int i = 0; i < rows.size(); i++) {
                            XWPFTableRow row = rows.get(i);
                            //读取每一列数据
                            List<XWPFTableCell> cells = row.getTableCells();
                            BuWorkRecordDetail recordDetail = null;
                            BuWorkRecordCategory workRecordCategory = null;
                            for (int j = 0; j < cells.size(); j++) {
                                XWPFTableCell cell = cells.get(j);
                                //输出当前的单元格的数据
                                String value = cell.getText();
                                if (value.contains("项目")) {
                                    rowNo = i;
                                }
                                if (rowNo != 0 && i > rowNo) {
                                    if (StrUtil.isNotEmpty(value)) {
                                        if (j == 0) {
                                            isNumber = isContains(value);
                                            if (isNumber) {
                                                categoryId = UUIDUtils.getUUID();
                                                workRecordCategory = createBuWorkRecordCategory(categoryId, workRecordId, value);
                                            }
                                        } else if (j == 1) {
                                            if (workRecordCategory != null && isNumber) {
                                                workRecordCategory.setReguTitle(value);
                                            }
                                        } else if (j == 2) {
                                            if (isNumber) {
                                                recordDetail = createBuWorkRecordDetail(categoryId, value);
                                            }
                                        }
                                    }
                                    if (recordDetail != null && isNumber) {
                                        if (j == 3) {
                                            if (StrUtil.isEmpty(value)) {
                                                recordDetail.setCheckLevel(checkLevel);
                                            } else {
                                                int checkLevelIntValue = getCheckLevelIntValue(value);
                                                recordDetail.setCheckLevel(checkLevelIntValue);
                                                checkLevel = checkLevelIntValue;
                                            }
                                        } else if (j == 4) {
                                            if (StrUtil.isEmpty(value)) {
                                                recordDetail.setTechRequire(techRequire);
                                            } else {
                                                recordDetail.setTechRequire(value);
                                                techRequire = value;
                                            }
                                        } else if (j == 5) {
                                            recordDetail.setWorkInfo(value);
                                        } else if (j == 7) {
                                            recordDetail.setSelfCheck(value);
                                        } else if (j == 8) {
                                            recordDetail.setGuarderCheck(value);
                                        } else if (j == 9) {
                                            recordDetail.setMonitorCheck(value);
                                        }
                                    }
                                }
                            }
                            if (recordDetail != null) {
                                workRecordDetailList.add(recordDetail);
                            }
                            if (workRecordCategory != null) {
                                workRecordCategoryList.add(workRecordCategory);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            throw new JeecgBootException("文件内容格式不正确!");
        }
        try {
            saveData(id, workRecord, workRecordCategoryList, workRecordDetailList);
            categoryIdDetailSortMap.clear();
        } catch (Exception e) {
            throw new JeecgBootException("导入失败!");
        }

    }

    private int getCheckLevelIntValue(String value) {
        if ("H".equals(value)) {
            return 1;
        } else if ("W".equals(value)) {
            return 2;
        }
        return 1;
    }

    private BuWorkRecordDetail createBuWorkRecordDetail(String categoryId, String value) {
        Integer detailSort = categoryIdDetailSortMap.get(categoryId);
        if (null == detailSort || detailSort < 1) {
            detailSort = 1;
        }
        BuWorkRecordDetail recordDetail = new BuWorkRecordDetail();
        recordDetail.setId(UUIDUtils.getUUID());
        recordDetail.setCategoryId(categoryId);
        recordDetail.setWorkContent(value);
        recordDetail.setItemNo(detailSort);
        detailSort = detailSort + 1;
        categoryIdDetailSortMap.put(categoryId, detailSort);
        return recordDetail;
    }

    private BuWorkRecordCategory createBuWorkRecordCategory(String categoryId, String workRecordId, String value) {
        BuWorkRecordCategory workRecordCategory = new BuWorkRecordCategory();
        workRecordCategory.setId(categoryId);
        workRecordCategory.setWorkRecId(workRecordId);
        workRecordCategory.setRecIndex(Integer.valueOf(value));
        categoryIdDetailSortMap.put(categoryId, 1);
        return workRecordCategory;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveData(String id, BuWorkRecord workRecord, List<BuWorkRecordCategory> workRecordCategoryList, List<BuWorkRecordDetail> workRecordDetailList) throws Exception {
        if (workRecord != null) {
            if (StringUtils.isNotBlank(id)) {
                workRecord.updateById();
                importerSql.delWorkRecord(id);
            } else {
                workRecord.insert();
            }
            if (EmptyUtils.listNotEmpty(workRecordCategoryList)) {
                importerSql.saveWorkRecordCategory(workRecordCategoryList);
                if (EmptyUtils.listNotEmpty(workRecordDetailList)) {
                    importerSql.saveWorkRecordDetail(workRecordDetailList);
                }
            }
        }
    }


    private Boolean isContains(String cellValue) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(cellValue).matches();
    }

}
