package org.jeecg.modules.tiros.importer.utils.other;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class MyExcelUtil {
    /**
     * 单元格对象
     */
    public class CellValue {
        private boolean isMerged=false;
        private String cellValue = "";
        private int firstColumn=-1;
        private int lastColumn=-1;
        private int firstRow=-1;
        private int lastRow=-1;

        /**
         * 是否合并单元格
         * @return
         */
        public boolean isMerged() {
            return isMerged;
        }


        public void setMerged(boolean merged) {
            isMerged = merged;
        }

        /**
         * 获取单元格值
         * @return
         */
        public String getCellValue() {
            return cellValue;
        }

        public void setCellValue(String cellValue) {
            this.cellValue = cellValue;
        }

        /**
         * 合并单元格-开始列
         * @return
         */
        public int getFirstColumn() {
            return firstColumn;
        }

        public void setFirstColumn(int firstColumn) {
            this.firstColumn = firstColumn;
        }

        /**
         * 合并单元格-结束列
         * @return
         */
        public int getLastColumn() {
            return lastColumn;
        }

        public void setLastColumn(int lastColumn) {
            this.lastColumn = lastColumn;
        }

        /**
         * 合并单元格-开始行
         * @return
         */
        public int getFirstRow() {
            return firstRow;
        }

        public void setFirstRow(int firstRow) {
            this.firstRow = firstRow;
        }

        /**
         * 合并单元格-结束行
         * @return
         */
        public int getLastRow() {
            return lastRow;
        }

        public void setLastRow(int lastRow) {
            this.lastRow = lastRow;
        }
    }

    /**
     * 判断sheet页中是否含有合并单元格
     * @param sheet
     * @return
     */
    public boolean hasMerged(Sheet sheet) {
        return sheet.getNumMergedRegions() > 0 ? true : false;
    }
    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public String getCellValue(Cell cell){

        if(cell == null){
            return "";
        }

        if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
            return cell.getStringCellValue();
        }else if(cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN){
            return String.valueOf(cell.getBooleanCellValue());
        }else if(cell.getCellType() ==HSSFCell.CELL_TYPE_FORMULA){
            return cell.getCellFormula();
        }else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
            return String.valueOf(cell.getNumericCellValue());
        }else if(cell.getCellType() == HSSFCell.CELL_TYPE_BLANK|| cell.getCellType()==HSSFCell.CELL_TYPE_ERROR){
            return "";
        }else{
            return "";
        }
    }

    /**
     * 判断指定的单元格是否是合并单元格
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    public CellValue isMergedRegion(Sheet sheet,int row ,int column) {
        CellValue cv = null;
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    cv = new CellValue();
                    cv.setMerged(true);
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    cv.setCellValue(getCellValue(fCell)) ;
                    cv.firstColumn = firstColumn;
                    cv.lastColumn = lastColumn;
                    cv.firstRow = firstRow;
                    cv.lastRow = lastRow;
                    return cv;
                }
            }
        }
        if (cv == null) {
            cv = new CellValue();
            cv.setMerged(false);
            cv.setCellValue(getCellValue(sheet.getRow(row).getCell(column)));
        }
        return cv;
    }


    /**
     * 获取合并单元格的值
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public String getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();

        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell) ;
                }
            }
        }

        return null ;
    }
}
