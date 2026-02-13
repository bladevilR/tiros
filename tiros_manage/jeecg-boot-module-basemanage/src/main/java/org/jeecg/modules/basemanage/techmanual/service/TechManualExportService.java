package org.jeecg.modules.basemanage.techmanual.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工艺手册导出服务
 * 将TinyMCE编辑的HTML内容转换为规范的Word文档
 *
 * @author TIROS Plugin
 * @version 1.0.0
 */
@Slf4j
@Service
public class TechManualExportService {

    private static final String TEMPLATE_PATH = "templates/template.docx";

    /**
     * HTML转Word文档
     *
     * @param htmlContent HTML内容
     * @param manualName 手册名称
     * @param fileNo 文件编号
     * @param fileVer 文件版本
     * @param projectNo 项目号
     * @return Word文档字节数组
     */
    public byte[] htmlToWord(String htmlContent, String manualName, String fileNo,
                             String fileVer, String projectNo) throws Exception {
        log.info("开始将HTML转换为Word: {}", manualName);

        try {
            // 加载模板
            ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
            if (!resource.exists()) {
                log.warn("模板文件不存在，使用默认生成方式");
                return generateWordFromHTML(htmlContent, manualName, fileNo, fileVer, projectNo);
            }

            XWPFDocument doc = new XWPFDocument(resource.getInputStream());

            // 清空模板中的默认内容（保留格式）
            while (doc.getParagraphs().size() > 1) {
                doc.removeBodyElement(doc.getParagraphs().size() - 1);
            }

            // 解析HTML并转换为Word段落和表格
            parseHTMLAndAddToDocument(doc, htmlContent, manualName, fileNo, fileVer, projectNo);

            // 输出为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            doc.close();

            byte[] documentBytes = out.toByteArray();
            log.info("Word文档生成成功，大小: {} bytes", documentBytes.length);
            return documentBytes;

        } catch (Exception e) {
            log.error("HTML转Word失败", e);
            throw e;
        }
    }

    /**
     * 直接从HTML生成Word（不使用模板）
     */
    private byte[] generateWordFromHTML(String htmlContent, String manualName, String fileNo,
                                       String fileVer, String projectNo) throws Exception {
        XWPFDocument doc = new XWPFDocument();

        // 添加标题
        XWPFParagraph titlePara = doc.createParagraph();
        titlePara.setStyle("Heading1");
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(manualName);
        titleRun.setFontSize(26);
        titleRun.setFontFamily("宋体");
        titleRun.setBold(true);

        // 添加基本信息表格
        addBasicInfoTable(doc, fileNo, fileVer, projectNo);

        // 解析并添加HTML内容
        parseHTMLAndAddToDocument(doc, htmlContent, manualName, fileNo, fileVer, projectNo);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();
        return out.toByteArray();
    }

    /**
     * 添加基本信息表格
     */
    private void addBasicInfoTable(XWPFDocument doc, String fileNo, String fileVer, String projectNo) {
        XWPFTable table = doc.createTable(2, 4);
        table.setCellMargins(80, 80, 80, 80);

        // 第一行：文件编号、版本、项目号、文件名
        XWPFTableRow row1 = table.getRow(0);
        setCellText(row1.getCell(0), "文件编号", true);
        setCellText(row1.getCell(1), fileNo, false);
        setCellText(row1.getCell(2), "版本号", true);
        setCellText(row1.getCell(3), fileVer, false);

        // 第二行：编制日期等
        XWPFTableRow row2 = table.getRow(1);
        setCellText(row2.getCell(0), "编制日期", true);
        setCellText(row2.getCell(1), getCurrentDate(), false);
        setCellText(row2.getCell(2), "项目号", true);
        setCellText(row2.getCell(3), projectNo, false);
    }

    /**
     * 设置表格单元格内容
     */
    private void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
        cell.setText("");
        XWPFParagraph para = cell.getParagraphs().get(0);
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontFamily("宋体");
        run.setFontSize(10);

        if (isHeader) {
            run.setBold(true);
            cell.setColor("d9d9d9");
        }
    }

    /**
     * 解析HTML并添加到Word文档
     */
    private void parseHTMLAndAddToDocument(XWPFDocument doc, String htmlContent,
                                          String manualName, String fileNo, String fileVer,
                                          String projectNo) throws Exception {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            return;
        }

        // 简化版HTML解析：提取表格和基本文本
        // 注：完整的HTML解析建议使用JSoup库

        // 替换占位符
        String content = htmlContent
                .replace("{{FILE_NO}}", fileNo)
                .replace("{{FILE_NAME}}", manualName)
                .replace("{{FILE_VER}}", fileVer)
                .replace("{{PROJECT_NO}}", projectNo);

        // 提取表格
        Pattern tablePattern = Pattern.compile("<table[^>]*>.*?</table>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher tableMatcher = tablePattern.matcher(content);
        int lastEnd = 0;

        while (tableMatcher.find()) {
            // 添加表格前的文本内容
            String textBefore = content.substring(lastEnd, tableMatcher.start());
            addHTMLTextToParagraphs(doc, textBefore);

            // 添加表格
            String tableHTML = tableMatcher.group();
            addHTMLTableToDocument(doc, tableHTML);

            lastEnd = tableMatcher.end();
        }

        // 添加剩余的文本内容
        String textAfter = content.substring(lastEnd);
        addHTMLTextToParagraphs(doc, textAfter);
    }

    /**
     * 将HTML文本添加到段落
     */
    private void addHTMLTextToParagraphs(XWPFDocument doc, String htmlText) {
        if (htmlText == null || htmlText.trim().isEmpty()) {
            return;
        }

        // 移除HTML标签但保留文本内容
        String text = htmlText.replaceAll("<[^>]*>", "").trim();

        if (!text.isEmpty()) {
            // 按换行符分割
            String[] lines = text.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    XWPFParagraph para = doc.createParagraph();
                    XWPFRun run = para.createRun();
                    run.setText(trimmed);
                    run.setFontFamily("宋体");
                    run.setFontSize(10);
                }
            }
        }
    }

    /**
     * 将HTML表格转换为Word表格
     */
    private void addHTMLTableToDocument(XWPFDocument doc, String tableHTML) {
        try {
            // 提取行数据
            Pattern trPattern = Pattern.compile("<tr[^>]*>.*?</tr>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher trMatcher = trPattern.matcher(tableHTML);

            List<List<String>> rows = new ArrayList<>();
            while (trMatcher.find()) {
                List<String> rowCells = new ArrayList<>();
                String trHTML = trMatcher.group();

                // 提取列数据
                Pattern tdPattern = Pattern.compile("<t[dh][^>]*>.*?</t[dh]>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                Matcher tdMatcher = tdPattern.matcher(trHTML);

                while (tdMatcher.find()) {
                    String cellHTML = tdMatcher.group();
                    String cellText = cellHTML.replaceAll("<[^>]*>", "").trim();
                    rowCells.add(cellText);
                }

                if (!rowCells.isEmpty()) {
                    rows.add(rowCells);
                }
            }

            if (!rows.isEmpty()) {
                // 创建表格
                int cols = rows.get(0).size();
                XWPFTable table = doc.createTable(rows.size(), cols);
                table.setCellMargins(80, 80, 80, 80);

                // 填充表格内容
                for (int i = 0; i < rows.size(); i++) {
                    XWPFTableRow row = table.getRow(i);
                    List<String> cells = rows.get(i);
                    for (int j = 0; j < cells.size() && j < row.getTableCells().size(); j++) {
                        setCellText(row.getTableCells().get(j), cells.get(j), i == 0);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("HTML表格转换失败，将作为文本处理", e);
        }
    }

    /**
     * 获取当前日期
     */
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
