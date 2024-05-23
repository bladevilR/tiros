package org.jeecg.modules.tiros.importer.utils.other;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseUtil {
    private static String regEx = "[\u4e00-\u9fa5]+";
    public static boolean checkChineseCharacter(String str)
    {
        if(str.getBytes().length == str.length())
            return false;
        return true;
    }

    public static String returnChineseCharacter(String str) {
        return returnChineseCharacter(str, false);
    }

    public static String returnChineseCharacter(String str, Boolean first) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String chiResult = "";
        while (m.find()) {
            chiResult += m.group();
        }
        return chiResult;
    }
}
