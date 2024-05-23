package org.jeecg.modules.tiros.importer.utils;

/**
 * <p>
 * 中文数字和阿拉伯数字转换工具
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-22
 */
public class NumberUtil {
    public static String[] CHINESE = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
    public static String[] ARABIC = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

    public static String toArabic(String chineseNumber) {
        int length = CHINESE.length;
        for (int i = 0; i < length; i++) {
            String chinese = CHINESE[i];
            if (chinese.equals(chineseNumber)) {
                return ARABIC[i];
            }
        }
        return chineseNumber;
    }

}
