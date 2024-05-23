package org.jeecg.modules.tiros.importer.service;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.tiros.importer.utils.ExcelUtil;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yyg
 */
@Repository
public class RepairPlanImport {


    public Map<String, Map<String, List<String>>> ImportRepairPlan(String fileName, InputStream inputStream) throws Exception {
        /**
         * 存放天数
         */
        List<String> dateStrs = new ArrayList<>();
        /**
         * 存放内容
         *格式是 {班组名：{第几天：任务},date:{第几天：具体日期}}
         */
        Map<String, Map<String, List<String>>> content = new HashMap<>(16);
        /**
         * 存放班组名称
         */
        List<String> workGroups = new ArrayList<>();
        /**
         * 1——50带圈数字的unicode编码
         */
        final String N_UNICODE = "\\u2460\\u2461\\u2462\\u2463\\u2464\\u2465\\u2466\\u2467\\u2468\\u2469\\u246a\\u246b\\u246c\\u246d\\u246e\\u246f\\u2470\\u2471\\u2472\\u2473\\u3251\\u3252\\u3253\\u3254\\u3255\\u3256\\u3257\\u3258\\u3259\\u325a\\u325b\\u325c\\u325d\\u325e\\u325f\\u32b1\\u32b2\\u32b3\\u32b4\\u32b5\\u32b6\\u32b7\\u32b8\\u32b9\\u32ba\\u32bb\\u32bc\\u32bd\\u32be\\u32bf";

        Sheet sheet = ExcelUtil.getWorkbookByStream(fileName, inputStream).getSheetAt(0);
        int rowCount = sheet.getLastRowNum();
        if (rowCount == 0) {
            throw new JeecgBootException("导入失败,请检查导入的文件是否有数据!");
        }
        int colCount = sheet.getRow(1).getLastCellNum();
        if (colCount == 0) {
            throw new JeecgBootException("第2行格式不正确,没有数据!");
        }
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            String workGroup = null;
            if (row == null) {
                continue;
            }
            if (i > 1) {
                workGroup = ExcelUtil.getCellValue(sheet.getRow(i).getCell(0)).trim();
                workGroups.add(workGroup);
            }

            Map<String, List<String>> map = new HashMap<>(16);
            //合并行开始行的位置
            Integer startIndex = null;
            //合并行之差
            Integer rowVal = null;
            //合并行之后的班组名称
            String equalWorkGroup="";
            for (int j = 0; j < colCount; j++) {
                if(j==0&&rowVal!=null&&i<=rowVal) continue;
                Cell cell = row.getCell(j);
                // 合并类型：0 没有合并 1 列合并(同行多列合并) 2 行合并(多行同列合并) 3 行列都合并
                int mergeType;
                String cellValue;
                mergeType = ExcelUtil.isMerge(sheet, i, cell);
                if (mergeType != 0) {
                    cellValue = ExcelUtil.getMergedRegionValueAndInfo(sheet, row.getRowNum(), cell.getColumnIndex()).trim();
                    Map<String, Object> mergedRegionMap = ExcelUtil.parseMargeValue(cellValue);
                    cellValue=mergedRegionMap.get("result").toString().trim();
                    if(mergeType==2&&j==0) {
                        rowVal = (Integer) mergedRegionMap.get("rowVal");
                        startIndex=i;
                        equalWorkGroup=workGroup;
                    }
                } else {
                    cellValue = ExcelUtil.getCellValue(cell).trim();
                }
                //读取天数信息
                if (i == 1 && j > 0) {
                    if (StrUtil.isNotEmpty(cellValue)) {
                        try {
                            String date = ExcelUtil.subStrCellValue(cellValue).substring(0, cellValue.lastIndexOf("）") + 1);
                            String index = date.substring(date.lastIndexOf("第") + 1, date.lastIndexOf("天"));
                            String dateStr = date.substring(date.lastIndexOf("（") + 1, date.lastIndexOf("）"));
                            dateStrs.add(index);
                            map.put(index, Arrays.asList(dateStr));
                            content.put("date", map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            throw new JeecgBootException("列名栏第" + (i + 1) + "行" + "第" + (j + 1) + "列错误,格式例如:第1天（2.18）");
                            }
                        }else{
                        throw new JeecgBootException("列名栏第" + (i + 1) + "行" + "第" + (j + 1) + "列内容为空");
                        }
                    continue;
                    }


                    if (j > 0 && i > 1) {
                        List<String> taskItems = new ArrayList<>();
                        if (StrUtil.isNotEmpty(cellValue)) {
                            AtomicBoolean flag = new AtomicBoolean(false);
                          /*  if (cellValue.contains(":")) {
                                throw new JeecgBootException(workGroup + "第" + dateStrs.get(j - 1) + "天任务格式不正确,可能有两列车的任务,请整理好,只需写任务即可!");
                            }*/
                            List<String> task = Arrays.asList(cellValue.split("\n"));
                            for (int k = 0; k < task.size(); k++) {
                                String taskValue = task.get(k);
                                if (StringUtils.isNotBlank(taskValue)) {
                                    String firstChar = taskValue.substring(0, 1);
                                    if (StringUtils.isNotBlank(firstChar) &&
                                            N_UNICODE.contains(UnicodeUtil.toUnicode(firstChar))) {
                                        taskItems.add(taskValue);
                                    } else {
                                        flag.set(true);
                                        break;
                                    }
                                }
                            }
                            if(flag.get()) throw new JeecgBootException(workGroup + "第" + dateStrs.get(j - 1) + "天任务格式不正确,要以带圈的数字开头如①!");

                            //班组有合并行处理
                            if(rowVal!=null&&i!=startIndex&&i<=rowVal){
                                Map<String, List<String>> c = content.get(equalWorkGroup);
                                List<String> value = c.get(dateStrs.get(j - 1));
                                value.addAll(taskItems);
                                c.put(dateStrs.get(j - 1), value);
                            }else{
                                map.put(dateStrs.get(j - 1), taskItems);
                            }

                        }
                      /*  if (j < 5 && i == 3) {
                            Map<String, List<String>> c = content.get("转向架工班");
                            List<String> value = c.get(dateStrs.get(j - 1));
                            value.addAll(taskItems);
                            c.put(dateStrs.get(j - 1), value);
                        } else {
                            map.put(dateStrs.get(j - 1), taskItems);
                        }*/
                    }
                }
                if (i > 1) {
                    if (StrUtil.isNotEmpty(workGroup)) {
                        content.put(workGroup, map);
                    }
                }
            }
        return content;
    }

}

