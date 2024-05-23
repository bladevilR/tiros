package org.jeecg.modules.tiros.importer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author yyg
 */
public class ConvertUtil {
    enum ChineseNumber {
        YI('一', 1),
        ER('二', 2),
        SAN('三', 3),
        SI('四', 4),
        WU('五', 5),
        LIU('六', 6),
        QI('七', 7),
        BA('八', 8),
        JIU('九', 9),
        LING('零', 0),
        SHI('十', 10),
        BAI('百', 100),
        QIAN('千', 1000),
        WAN('万', 10000);
        private char cValue;
        private int aValue;

        ChineseNumber(char cValue, int aValue) {
            this.cValue = cValue;
            this.aValue = aValue;
        }

        /**
         * 将中文数字转换成阿拉伯数字
         * 0-1000
         * @param cValue 中文数字
         * @return 对应的阿拉伯数字
         */
        public static Integer getAFromC(String cValue) {
            int length = cValue.length() - 1;
            int total = 0;
            while (length >= 0) {
                char chinese = cValue.charAt(length);
                int arabic = c2a(chinese);
                if (arabic >= 10 && (length - 1) >= 0) {
                    length--;
                    int arabicValue = c2a(cValue.charAt(length));
                    total = total + arabic * arabicValue;
                    length--;
                    continue;
                }
                total += arabic;
                length--;
            }
            return total;
        }

        /**
         * 将阿拉伯数字转换成中文数字
         * 0-1000
         * @param aValue 阿拉伯数字
         * @return 对应的中文数字
         */
        public static String getCFromA(int aValue) {
            if (aValue==0){
                return String.valueOf(ChineseNumber.LING.cValue);
            }
            StringBuilder stringBuilder = new StringBuilder();
            int remainder;
            int currentValue;
            int count = 0;
            char c;
            List<Integer> arrayList = new ArrayList<>(10);
            // 得到数位
            while (aValue > 0) {
                remainder = aValue % 10;
                arrayList.add(remainder);
                aValue = aValue / 10;
            }
            // 10
            // 0 1
            // 123
            // 3 2 1
            int index = 0;
            boolean start = true;
            while (index < arrayList.size()) {
                currentValue = arrayList.get(index);
                if (count > 0 && currentValue != 0) {
                    stringBuilder.insert(0, a2c((int) Math.pow(10, count)));
                }
                c = a2c(currentValue);
                if (currentValue != 0) {
                    if (start) {
                        start = false;
                    }
                    stringBuilder.insert(0, c);
                }
                // 控制多个连续的0只打印一个 防止出现  一万零零零一(10001)
                if (currentValue == 0 && !start) {
                    stringBuilder.insert(0, c);
                    start = true;
                }
                index++;
                count++;
            }
            if (stringBuilder.length() > 1 && stringBuilder.charAt(0) == ChineseNumber.YI.cValue && stringBuilder.charAt(1) == ChineseNumber.SHI.cValue) {
                // 将一十 改为十
                stringBuilder.deleteCharAt(0);
            }
            return stringBuilder.toString();
        }

        static int c2a(char cValue) {
            for (ChineseNumber c : values()) {
                if (c.cValue == cValue) {
                    return c.aValue;
                }
            }
            return 0;
        }

        static char a2c(int aValue) {
            for (ChineseNumber c : values()) {
                if (c.aValue == aValue) {
                    return c.cValue;
                }
            }
            return ' ';
        }
    }

    /**
     * 将对应中文数字 加一
     *
     * @param chineseNumber 中文数字 如 一百
     * @return 一百零一
     */
    public static String chineseNumberPlusOne(String chineseNumber) {
        Integer aFromC = ChineseNumber.getAFromC(chineseNumber);
        String cFromA = ChineseNumber.getCFromA(aFromC + 1);
        return cFromA;
    }

    public static Integer getArabicFromChinese(String chineseValue) {
        return ChineseNumber.getAFromC(chineseValue);
    }

    public static String getChineseFromArabic(Integer arabic) {
        return ChineseNumber.getCFromA(arabic);
    }

    public static String randomGen(int place) {
        String base = "qwertyuioplkjhgfdsazxcvbnmQAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
        StringBuffer sb = new StringBuffer();
        Random rd = new Random();
        for (int i = 0; i < place; i++) {
            sb.append(base.charAt(rd.nextInt(base.length())));
        }
        return sb.toString();
    }
}
