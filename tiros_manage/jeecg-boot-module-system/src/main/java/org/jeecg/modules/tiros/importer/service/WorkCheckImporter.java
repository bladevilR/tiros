package org.jeecg.modules.tiros.importer.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheck;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckItem;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckTechLink;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.tiros.importer.dao.ImporterSql;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author yuyougen
 * @title: WorkCheckImporter
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/1718:08
 */
@Repository
public class WorkCheckImporter {

    @Resource
    private ImporterSql importerSql;
    @Transactional(rollbackFor = Exception.class)
    public void importerWorkCheck(InputStream inputStream, BuWorkCheck workCheck, String fileName) throws Exception{
        List<BuWorkCheckItem> checkItemList = new ArrayList<>();
        List<BuWorkCheckTechLink> checkTechLinks = new ArrayList<>();
        boolean isReadTech = false;
        Integer lastNo = 0;
        Integer sortNo = 1;
      Map<String,String> techBookMap=importerSql.selectTechBookMap();
        try {
            if (ExcelUtil.isWord2007(fileName)) {
                XWPFDocument xwpf = new XWPFDocument(inputStream);
                Iterator<XWPFTable> it = xwpf.getTablesIterator();
                while (it.hasNext()) {
                    XWPFTable table = it.next();
                    List<XWPFTableRow> rows = table.getRows();
                    //读取每一行数据
                    for (int i = 0; i < rows.size(); i++) {
                        //跳过不需要的行
                        if (i != 0 && i < 4) {
                            continue;
                        }
                        if (i != 0 && isReadTech && i > lastNo) {
                            break;
                        }
                        XWPFTableRow row = rows.get(i);
                        //读取每一列数据
                        List<XWPFTableCell> cells = row.getTableCells();
                        BuWorkCheckItem checkItem = null;
                        for (int j = 0; j < cells.size(); j++) {
                            XWPFTableCell cell = cells.get(j);
                            String text = cell.getText();
                            //取检查作业表的名字,存在就替换文件名
                            if (i == 0 && j == 0) {
                                if (StringUtils.isNotBlank(text)) {
                                    workCheck.setTitle(text);
                                }
                                break;
                            }
                            if (j == 0) {
                                if (StringUtils.isNotBlank(text) && !text.contains("参考工艺文件")) {
                                    checkItem = new BuWorkCheckItem()
                                            .setCheckId(workCheck.getId())
                                            .setSortNo(isContainsNumber(text) ? Integer.valueOf(text) : sortNo);
                                    ++sortNo;
                                }
                                if (text.contains("参考工艺文件")) {
                                    isReadTech = true;
                                    lastNo = i;
                                }
                            }

                            if (j == 1) {
                                if (StringUtils.isNotBlank(text) && checkItem != null) {
                                    checkItem.setTitle(text);
                                }
                                if (isReadTech && StringUtils.isNotBlank(text)) {
                                    String[] texts = text.split("艺");

                                    for (String t : texts) {
                                        if (techBookMap != null) {
                                            String techBookId = techBookMap.get(t+"艺");
                                            if (StringUtils.isNotBlank(techBookId)) {
                                                BuWorkCheckTechLink checkTechLink = new BuWorkCheckTechLink()
                                                        .setCheckId(workCheck.getId())
                                                        .setTeckBookId(techBookId);
                                                checkTechLinks.add(checkTechLink);
                                            }
                                        }
                                    }
                                }
                            }

                            if (StringUtils.isNotBlank(text) && checkItem != null) {
                                if (j == 2) {
                                    checkItem.setContent(text);
                                }

                                if (j == 3) {
                                    checkItem.setCheckLevel(text.split("").length);
                                }
                            }

                        }
                        if(checkItem!=null){
                            checkItemList.add(checkItem);
                        }
                    }
                }
            } else {
                // 处理doc格式 即office2003版本
                POIFSFileSystem pfs = new POIFSFileSystem(inputStream);
                HWPFDocument hwpf = new HWPFDocument(pfs);
                Range range = hwpf.getRange();//得到文档的读取范围
                TableIterator it = new TableIterator(range);
                // 迭代文档中的表格
                while (it.hasNext()) {
                    Table tb = (Table) it.next();
                    for (int i = 0; i < tb.numRows(); i++) {
                        //跳过不需要的行
                        if (i != 0 && i < 4) {
                            continue;
                        }
                        if (i != 0 && isReadTech && i > lastNo) {
                            break;
                        }
                        TableRow tr = tb.getRow(i);
                        BuWorkCheckItem checkItem = null;
                        for (int j = 0; j < tr.numCells(); j++) {
                            TableCell td = tr.getCell(j);//取得单元格
                            StringBuffer text = new StringBuffer();
                            for (int k = 0; k < td.numParagraphs(); k++) {
                                Paragraph para = td.getParagraph(k);
                                String s = para.text();
                                //去除后面的特殊符号
                                if (null != s && !"".equals(s)) {
                                    s = s.substring(0, s.length() - 1);
                                    text.append(s);
                                }
                            }

                            //取检查作业表的名字,存在就替换文件名
                            String content= text.toString();
                            if (i == 0 && j == 0) {
                                if (StringUtils.isNotBlank(content)) {
                                    workCheck.setTitle(content);
                                }
                                break;
                            }
                            if (j == 0) {
                                if (StringUtils.isNotBlank(content) && !content.contains("参考工艺文件")) {
                                    checkItem = new BuWorkCheckItem()
                                            .setCheckId(workCheck.getId())
                                            .setSortNo(isContainsNumber(content) ? Integer.valueOf(content) : sortNo);
                                    ++sortNo;
                                }
                                if (content.contains("参考工艺文件")) {
                                    isReadTech = true;
                                    lastNo = i;
                                }
                            }

                            if (j == 1) {
                                if (StringUtils.isNotBlank(content) && checkItem != null) {
                                    checkItem.setTitle(content);
                                }
                                if (isReadTech && StringUtils.isNotBlank(content)) {
                                    String[] texts = content.split("艺");
                                    for (String t : texts) {
                                        if (techBookMap != null) {
                                            String techBookId = techBookMap.get(t+"艺");
                                            if (StringUtils.isNotBlank(techBookId)) {
                                                BuWorkCheckTechLink checkTechLink = new BuWorkCheckTechLink()
                                                        .setCheckId(workCheck.getId())
                                                        .setTeckBookId(techBookId);
                                                checkTechLinks.add(checkTechLink);
                                            }
                                        }
                                    }
                                }
                            }


                            if (StringUtils.isNotBlank(content) && checkItem != null) {
                                if (j == 2) {
                                    checkItem.setContent(content);

                                }
                                if (j == 3) {
                                    checkItem.setCheckLevel(content.split("").length);
                                }
                            }
                        }
                        if(checkItem!=null){
                            checkItemList.add(checkItem);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
          throw new JeecgBootException("文件格式不正确!");

        }

        try {
            if(CollectionUtils.isNotEmpty(checkItemList)) {
                workCheck.setAssetTypeId("-1");
                workCheck.setAssetStructId("-1");
                 workCheck.insert();
                   importerSql.saveCheckItemList(checkItemList);
                if (CollectionUtils.isNotEmpty(checkTechLinks)) {
                    importerSql.saveCheckTechLink(checkTechLinks);
                }
            }else{
              throw new NullPointerException("读取文件内容为空，请您检查文件内容格式!");
            }
        }catch (NullPointerException e){
            throw  new JeecgBootException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw  new JeecgBootException("导入数据失败!");
        }
    }

    private Boolean isContainsNumber(String cellValue) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(cellValue).matches();
    }
}
