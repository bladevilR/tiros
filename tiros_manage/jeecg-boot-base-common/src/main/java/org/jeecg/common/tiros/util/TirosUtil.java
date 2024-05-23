package org.jeecg.common.tiros.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 业务工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-16
 */
public class TirosUtil {

    public static String extractSourceLocationName(String warehouseWbs, String warehouseName) {
        if (null == warehouseWbs) {
            warehouseWbs = "";
        }
        if (null == warehouseName) {
            warehouseName = "";
        }

        // 硬编码：
        // 仓库wbs格式如：1.2.JDX01.xxxxx.xxxx...
        if (warehouseWbs.length() <= 4) {
            return warehouseName;
        }
        // 1.2为固定的2个上级编码，去掉
        String var1 = warehouseWbs.substring(4);
        // 逗号分割
        String[] codes = var1.split("\\.");
        int length = codes.length;
        // 根据第一个编码，获取EBS二级库编码ebsWhCode
        if (length < 1) {
            return warehouseName;
        }
        return codes[0] + "=>" + warehouseName;
    }

    /**
     * @param excelData excelData 数据
     * @param row       行号
     * @param col       列号
     * @param data      要填入的数据
     * @return excelData
     */
    public static String writeExcelDataByRowAndCol(String excelData, int row, int col, String data) {
        JSONArray rowArray = JSONUtil.parseArray(excelData);
        JSONArray colArray = JSONUtil.parseArray(rowArray.get(row));
        JSONObject jsonObject = JSONUtil.parseObj(colArray.get(col));
        jsonObject.put("m", data);
        jsonObject.put("v", data);
        colArray.set(col, jsonObject);
        rowArray.set(row, colArray);
        return rowArray.toString();
    }

    /**
     * @param excelData excelData 数据
     * @param oldText   替换的老数据
     * @param newText   替换的新数据
     * @return excelData
     */
    public static String replaceExcelData(String excelData, String oldText, String newText) {
        JSONArray rowArray = JSONUtil.parseArray(excelData);
        for (int i = 0; i < rowArray.size(); i++) {
            JSONArray colArray = JSONUtil.parseArray(rowArray.get(i));
            for (int j = 0; j < colArray.size(); j++) {
                JSONObject jsonObject = JSONUtil.parseObj(colArray.get(j));

                if (jsonObject.toString().contains(oldText)) {
                    String m = jsonObject.get("m").toString();
                    String v = jsonObject.get("v").toString();
                    jsonObject.put("m", m.replace(oldText, newText));
                    jsonObject.put("v", v.replace(oldText, newText));
                    colArray.set(j, jsonObject);
                    rowArray.set(i, colArray);
                    return rowArray.toString();
                }
            }
        }
        return null;
    }

    /**
     * 过滤非数字
     *
     * @param input 输入字符串
     * @return 输出的纯数字字符串
     */
    public static String extractStringNumeric(String input) {
        String regex = "[^0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("").trim();
    }

    /**
     * 判断字符串全是数字
     *
     * @param input 输入字符串
     * @return 是否全是数字
     */
    public static boolean isStringNumeric(String input) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(input).matches();
    }

}
