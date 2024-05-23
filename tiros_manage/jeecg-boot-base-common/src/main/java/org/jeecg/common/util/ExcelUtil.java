package org.jeecg.common.util;


import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.exception.JeecgBootException;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * excel工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/4
 */
public class ExcelUtil {

    public static Workbook getWorkbookByStream(InputStream inputStream) throws Exception {
        if (!inputStream.markSupported()) {
            inputStream = new PushbackInputStream(inputStream, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(inputStream)) {
            return new HSSFWorkbook(inputStream);
        }
        if (POIXMLDocument.hasOOXMLHeader(inputStream)) {
            return new XSSFWorkbook(OPCPackage.open(inputStream));
        }
        throw new JeecgBootException("excel文件解析失败，文件损坏或文件扩展名失效!");
    }

    /**
     * 自适应宽度(中文支持)
     *
     * @param sheet excel表
     * @param size  列的数量
     */
    public static void setSizeColumn(HSSFSheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                HSSFRow currentRow;
                // 当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    HSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, Math.min(columnWidth, 254) * 256);
        }
    }

    /**
     * 自适应宽度(中文支持)
     *
     * @param sheet excel表
     * @param size  列的数量
     */
    public static void setSizeColumn1(HSSFSheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 3; rowNum < sheet.getLastRowNum(); rowNum++) {
                HSSFRow currentRow;
                // 当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    HSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }

    public static void setColumnWidth(HSSFSheet sheet, int size) {
        for (int i = 0; i < size; i++) {
            int colWidth = sheet.getColumnWidth(i) * 2;
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
            } else {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }


    /**
     * 根据行内容重新计算行高
     * @param sourceRow
     */
    public static void calcAndSetRowHeigt(HSSFRow sourceRow) {
        for (int cellIndex = sourceRow.getFirstCellNum(); cellIndex <= sourceRow.getPhysicalNumberOfCells(); cellIndex++) {
            //行高
            double maxHeight = sourceRow.getHeight();
            HSSFCell sourceCell = sourceRow.getCell(cellIndex);
            //单元格的内容
            String cellContent = getCellContentAsString(sourceCell);
            if(null == cellContent || "".equals(cellContent)){
                continue;
            }
            //单元格的宽高及单元格信息
            Map<String, Object> cellInfoMap = getCellInfo(sourceCell);
            Integer cellWidth = (Integer)cellInfoMap.get("width");
            Integer cellHeight = (Integer)cellInfoMap.get("height");
            if(cellHeight > maxHeight){
                maxHeight = cellHeight;
            }
            System.out.println("单元格的宽度 : " + cellWidth + "    单元格的高度 : " + maxHeight + ",    单元格的内容 : " + cellContent);
            HSSFCellStyle cellStyle = sourceCell.getCellStyle();
            HSSFFont font = cellStyle.getFont(sourceRow.getSheet().getWorkbook());
            //字体的高度
            short fontHeight = font.getFontHeight();

            //cell内容字符串总宽度
            double cellContentWidth = cellContent.getBytes().length * 2 * 256;

            //字符串需要的行数 不做四舍五入之类的操作
            double stringNeedsRows =(double)cellContentWidth / cellWidth;
            //小于一行补足一行
            if(stringNeedsRows < 1.0){
                stringNeedsRows = 1.0;
            }

            //需要的高度             (Math.floor(stringNeedsRows) - 1) * 40 为两行之间空白高度
            double stringNeedsHeight = (double)fontHeight * stringNeedsRows;
            //需要重设行高
            if(stringNeedsHeight > maxHeight){
                maxHeight = stringNeedsHeight;
                //超过原行高三倍 则为5倍 实际应用中可做参数配置
                if(maxHeight/cellHeight > 5){
                    maxHeight = 5 * cellHeight;
                }
                //最后取天花板防止高度不够
                maxHeight = Math.ceil(maxHeight);
                //重新设置行高 同时处理多行合并单元格的情况
                Boolean isPartOfRowsRegion = (Boolean)cellInfoMap.get("isPartOfRowsRegion");
                if(isPartOfRowsRegion){
                    Integer firstRow = (Integer)cellInfoMap.get("firstRow");
                    Integer lastRow = (Integer)cellInfoMap.get("lastRow");
                    //平均每行需要增加的行高
                    double addHeight = (maxHeight - cellHeight)/(lastRow - firstRow + 1);
                    for (int i = firstRow; i <= lastRow; i++) {
                        double rowsRegionHeight =sourceRow.getSheet().getRow(i).getHeight() + addHeight;
                        sourceRow.getSheet().getRow(i).setHeight((short)rowsRegionHeight);
                    }
                }else{
                    sourceRow.setHeight((short)maxHeight);
                }
            }
            System.out.println("字体高度 : " + fontHeight + ",    字符串宽度 : " + cellContentWidth + ",    字符串需要的行数 : " + stringNeedsRows + ",   需要的高度 : " + stringNeedsHeight + ",   现在的行高 : " + maxHeight);
            System.out.println();
        }
    }

    /**
     * 解析一个单元格得到数据
     * @param cell
     * @return
     */
    private static String getCellContentAsString(HSSFCell cell) {
        if(null == cell){
            return "";
        }
        String result = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                String s = String.valueOf(cell.getNumericCellValue());
                if (s != null) {
                    if (s.endsWith(".0")) {
                        s = s.substring(0, s.length() - 2);
                    }
                }
                result = s;
                break;
            case Cell.CELL_TYPE_STRING:
                result = String.valueOf(cell.getStringCellValue()).trim();
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                result = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 获取单元格及合并单元格的宽度
     * @param cell
     * @return
     */
    private static Map<String, Object> getCellInfo(HSSFCell cell) {
        HSSFSheet sheet = cell.getSheet();
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        boolean isPartOfRegion = false;
        int firstColumn = 0;
        int lastColumn = 0;
        int firstRow = 0;
        int lastRow = 0;
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            Region ca = sheet.getMergedRegionAt(i);
            firstColumn = ca.getColumnFrom();
            lastColumn = ca.getColumnTo();
            firstRow = ca.getRowFrom();
            lastRow = ca.getRowTo();
            if (rowIndex >= firstRow && rowIndex <= lastRow) {
                if (columnIndex >= firstColumn && columnIndex <= lastColumn) {
                    isPartOfRegion = true;
                    break;
                }
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Integer width = 0;
        Integer height = 0;
        boolean isPartOfRowsRegion = false;
        if(isPartOfRegion){
            for (int i = firstColumn; i <= lastColumn; i++) {
                width += sheet.getColumnWidth(i);
            }
            for (int i = firstRow; i <= lastRow; i++) {
                height += sheet.getRow(i).getHeight();
            }
            if(lastRow > firstRow){
                isPartOfRowsRegion = true;
            }
        }else{
            width = sheet.getColumnWidth(columnIndex);
            height += cell.getRow().getHeight();
        }
        map.put("isPartOfRowsRegion", isPartOfRowsRegion);
        map.put("firstRow", firstRow);
        map.put("lastRow", lastRow);
        map.put("width", width);
        map.put("height", height);
        return map;
    }

    public static String getCellValueByCell(Cell cell) {
        //判断是否为null或空串
        if (cell == null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue = "";
        int cellType = cell.getCellType();
        switch (cellType) {
            // 数字
            case HSSFCell.CELL_TYPE_NUMERIC:
                short format = cell.getCellStyle().getDataFormat();
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = null;
                    if (format == 20 || format == 32) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = DateUtil
                                .getJavaDate(value);
                        cellValue = sdf.format(date);
                    } else {
                        // 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    try {
                        // 日期
                        cellValue = sdf.format(cell.getDateCellValue());
                    } catch (Exception e) {
                        try {
                            throw new Exception("exception on get date data !".concat(e.toString()));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } finally {
                        sdf = null;
                    }
                } else {
                    BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                    // 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
                    cellValue = bd.toPlainString();
                }
                break;
            // 字符串
            case HSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            // Boolean
            case HSSFCell.CELL_TYPE_BOOLEAN:
                cellValue = cell.getBooleanCellValue() + "";
                ;
                break;
            // 公式
            case HSSFCell.CELL_TYPE_FORMULA:
                cellValue = cell.getCellFormula();
                break;
            // 空值
            case HSSFCell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            // 故障
            case HSSFCell.CELL_TYPE_ERROR:
                cellValue = "ERROR VALUE";
                break;
            default:
                cellValue = "UNKNOW VALUE";
                break;
        }
        return cellValue.trim();
    }

}
