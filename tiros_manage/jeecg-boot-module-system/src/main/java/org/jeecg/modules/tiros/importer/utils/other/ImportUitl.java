package org.jeecg.modules.tiros.importer.utils.other;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportUitl {
    private MyExcelUtil excelUtil = new MyExcelUtil();
    /**
     * 导入指定excel中的数据
     * @param filePath excel 文件路径
     * @param startReadLine 真正的数据从第几行开始读取，注意EXCEL中的行数下标是从0开始的
     * @return 返回的数据为Map列表，excel中的一行中的每列数据都存放在map中，key 为 0,1,2..... 对应着excel中的A,B,C.....，  index 该数据行的序号
     * 就算是进行了行合并的单元格，也在每行中存在对应的数据，只是这些数据值都一样（列合并没有考虑）
     */
    public List<Map<String, Object>> importFile(String filePath, int startReadLine){
        boolean isE2007 = false;
        if(filePath.endsWith("xlsx")){
            isE2007 = true;
        }
        File excelFile = null;
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            excelFile = new File(filePath);
            inputStream = new FileInputStream(excelFile);

            if (isE2007) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }
            inputStream.close();
            // 只处理第一个sheet
            Sheet sheet = workbook.getSheetAt(0);
            //下标从0开始的
            return readSheet2(sheet, startReadLine);
        } catch (Exception ex) {
            System.out.println("导入excel异常：");
            ex.printStackTrace();
            return new ArrayList<>();
        } finally {
            try {
                if (workbook != null) {
                   // workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception exx) {
                System.out.println("关闭打开的文件异常");
                exx.printStackTrace();
            }
        }
    }

    /**
     * 原来版本模板导入
     * @param sheet
     * @param startReadLine
     * @return
     */
    private List<Map<String, Object>> readSheet(Sheet sheet, int startReadLine) {
        Row row = null;
        int endLine = sheet.getLastRowNum();
        List<Map<String, Object>> results = new ArrayList<>();
        // 循环数据行
        int x = 0, y = 0;
        for(int i=startReadLine; i <= endLine; i++) { // 这里获取到的最后行号要注意，因为行号是从0开始的啊
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            // 循环这行的列
            x = 0;
            for(Cell c : row) {
                MyExcelUtil.CellValue cv = excelUtil.isMergedRegion(sheet, i, c.getColumnIndex());
                String cvalue = cv.getCellValue();
                if(cvalue!=null && ! cvalue.isEmpty()) {
                    // System.out.print(cvalue + "=====");
                    String key = x +""; // x + ":" + y
                    // cvalue=cvalue.replaceAll("[\\t\\n\\r]", ";");
                    map.put(key, cvalue);
                }
                x++;
            }

            if(!map.isEmpty()) {
                // System.out.println();
                // System.out.println("---------------------------------------------------------------------------------");
                map.put("index", String.format("%07d", i));
                results.add(map);
                y++;
            }
        }
        return results;
    }

    /**
     * 最新模板数据导入
     * @param sheet
     * @param startReadLine  数据开始行
     * @return
     */
    private List<Map<String, Object>> readSheet2(Sheet sheet, int startReadLine) {
        Row row;
        int endLine = sheet.getLastRowNum();
        List<Map<String, Object>> results = new ArrayList<>();
        // 循环数据行
        int x = 0;
        // 用来存放对应列的上次非空值，因为模板没有合并行
        String[] lastCellValue = {"", "", "", ""};


        // 这里获取到的最后行号要注意，因为行号是从0开始的啊
        for(int r=startReadLine; r <= endLine; r++) {
            row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            // 循环这行的列
            x = 0;
            int actBeginCol = 1; // 实际开始取数据的列
            for(Cell c : row) {
                if (x < actBeginCol) {
                    // 还没到实际开始取数据的列
                    // 第0列为 车分类，不需要导入，从第1列开始
                    x++;
                    continue;
                }
                MyExcelUtil.CellValue cv = excelUtil.isMergedRegion(sheet, r, c.getColumnIndex());
                String cvalue = cv.getCellValue();

                // 超出要取数据的区域了
                if (x > (lastCellValue.length - 1)) {
                    break;
                }

                if (StringUtils.isBlank(cvalue)) {
                    // 如果这行的这列值为空，那么判断上一次的这列有没有值，有就用上次的值
                    if (StringUtils.isNotBlank(lastCellValue[x])) {
                        cvalue = lastCellValue[x];
                    } else {
                        // 上次也没有值
                        continue;
                    }
                } else {
                    if (x == startReadLine) {
                        // 当前第一列有新的值时，后面的都清除
                        for (int h = startReadLine + 1; h < lastCellValue.length; h++) {
                            lastCellValue[h] = "";
                        }
                    }
                    lastCellValue[x] = cvalue;
                }

                if(StringUtils.isNotBlank(cvalue)) {
                    // x-1 因为从第一列开始，所以换算成0列开始
                    String key = (x - 1) +"";
                    // cvalue=cvalue.replaceAll("[\\t\\n\\r]", ";");
                    map.put(key, StringUtils.trim(cvalue));
                }
                x++;
            }

            if (map.entrySet().size() < 3) {
                // 说明3列的值有空的，继续下一行
                continue;
            }

            if(!map.isEmpty()) {
                // System.out.println();
                // System.out.println("---------------------------------------------------------------------------------");
                map.put("index", String.format("%07d", r));
                results.add(map);
            }
        }
        return results;
    }
}
