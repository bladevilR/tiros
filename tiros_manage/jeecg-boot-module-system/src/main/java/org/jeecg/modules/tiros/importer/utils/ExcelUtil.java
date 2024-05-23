package org.jeecg.modules.tiros.importer.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.exception.JeecgBootException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yyg
 */
public class ExcelUtil {


    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }


    public static boolean isWord2007(String filePath) {

        if (filePath.toLowerCase().endsWith("docx")) {
            return true;
        }
        return false;
    }


    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValueAndInfo(Sheet sheet, int row, int column) {
        String MergedVal = "";
        String MergedInfo = "";
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    fCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    MergedVal = getCellValue(fCell);
                    MergedInfo = firstColumn + "," + lastColumn + "," + firstRow + "," + lastRow;
                    return MergedVal + "&" + MergedInfo;
                }
            }
        }
        return null;
    }

    public static Map parseMargeValue(String str) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (str != null && str.length() > 0) {
            String[] strs = str.split("&");
            map.put("result", strs[0]);
            String[] strColumns = strs[1].split(",");
            map.put("columnVal", Integer.valueOf(strColumns[1]) - Integer.valueOf(strColumns[0]));
            map.put("rowVal", Integer.valueOf(strColumns[3]) - Integer.valueOf(strColumns[2]));
            return map;
        }
        return null;
    }

    /**
     * 判断合并类型
     *
     * @param sheet  表格
     * @param row    行
     * @param column 列
     * @return 合并类型：0没有合并 1列合并(同行多列合并) 2行合并(多行同列合并) 3行列都合并
     */
    public static int getMergeType(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();

            boolean columnMerge = lastColumn > firstColumn;
            boolean rowMerge = lastRow > firstRow;

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    if (columnMerge && rowMerge) {
                        return 3;
                    } else if (!columnMerge && rowMerge) {
                        return 2;
                    } else if (columnMerge && !rowMerge) {
                        return 1;
                    }
                }
            }
        }

        return 0;
    }

    /**
     * 如果单元格是行合并，获取合并行的首行的index
     *
     * @param sheet  表格
     * @param row    行
     * @param column 列
     * @return 合并行的首行的index
     */
    public static int getTopRowIndexIfRowMerged(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();

            if (firstColumn == column
                    && (row >= firstRow && row <= lastRow)) {
                return firstRow;
            }
        }

        return 0;
    }

    /**
     * 如果单元格是行合并，获取合并行的最后行的index
     *
     * @param sheet  表格
     * @param row    行
     * @param column 列
     * @return 合并行的首行的index
     */
    public static int getLastRowIndexIfRowMerged(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();

            if (firstColumn == column
                    && (row >= firstRow && row <= lastRow)) {
                return lastRow;
            }
        }

        return 0;
    }


    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    public static int isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();

            boolean columnMerge = lastColumn > firstColumn;
            boolean rowMerge = lastRow > firstRow;

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    if (columnMerge && rowMerge) {
                        return 3;
                    } else if (!columnMerge && rowMerge) {
                        return 2;
                    } else if (columnMerge && !rowMerge) {
                        return 1;
                    }
                }
            }
        }

        return 0;
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK || cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
            return "";
        } else {
            return "";
        }
    }

    public static Map<String, Integer> getMergedRegionValue(String value) {
        Map<String, Integer> map = new HashMap<String, Integer>(16);
        String[] strValue = value.split("&");
        String mRValue = strValue[1];
        String[] Num = mRValue.split(",");
        map.put("firstColumn", Integer.valueOf(Num[0]));
        map.put("lastColumn", Integer.valueOf(Num[1]));
        map.put("firstRow", Integer.valueOf(Num[2]));
        map.put("lastRow", Integer.valueOf(Num[3]));
        return map;
    }

    public static Workbook getWorkbook(String path) throws IOException {
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = null;
        if (ExcelUtil.isExcel2007(path)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        if (ExcelUtil.isExcel2003(path)) {
            workbook = new HSSFWorkbook(inputStream);
        }

        inputStream.close();

        return workbook;
    }

    public static Workbook getWorkbookByStream(String filename, InputStream inputStream) throws Exception {
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



      /*  if (ExcelUtil.isExcel2007(filename)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        if (ExcelUtil.isExcel2003(filename)) {
            workbook = new HSSFWorkbook(inputStream);
        }*/


    public static int isMerge(Sheet sheet, int i, Cell cell) {
        if (cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
            return ExcelUtil.isMergedRegion(sheet, i, cell.getColumnIndex());
        }
        return 0;
    }

    public static String subStrCellValue(String cellValue) {
        if (cellValue.contains("&")) {
            return cellValue.substring(0, cellValue.indexOf("&"));
        }
        return cellValue;
    }


    /**
     * 判断该行是否为空
     * @param row 行对象
     * @return
     */
    public static  boolean isEmptyRow(Row row) {
        //行不存在
        if (row == null) {
            return true;
        }
        //第一个列位置
        int firstCellNum = row.getFirstCellNum();
        //最后一列位置
        int lastCellNum = row.getLastCellNum();
        //空列数量
        int nullCellNum = 0;
        for (int c = firstCellNum; c < lastCellNum; c++) {
            Cell cell = row.getCell(c);
            if (null == cell || HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
                nullCellNum++;
                continue;
            }
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String cellValue = cell.getStringCellValue().trim();
            if (StringUtils.isEmpty(cellValue)) {
                nullCellNum++;
            }
        }
        //所有列都为空
        if (nullCellNum == (lastCellNum - firstCellNum)) {
            return true;
        }
        return false;
    }
}
