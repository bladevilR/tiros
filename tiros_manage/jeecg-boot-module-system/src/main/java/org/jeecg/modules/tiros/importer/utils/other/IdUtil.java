package org.jeecg.modules.tiros.importer.utils.other;


import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IdUtil {
    static String lastId = "";
    public static synchronized String genId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date cur = new Date();
        String date = sdf.format(new Date());
        String id= date + "" + cur.getTime();
        while (StringUtils.equals(lastId, id)) {
            try {
                Thread.sleep(3);
                cur = new Date();
                id = date + "" + cur.getTime();
            } catch (Exception ex) {
                ex.printStackTrace();
                cur = new Date();
                id = date + "" + cur.getTime();
            }
        }
        lastId = id;
        return id;
    }

    /**
     *
     * @param maxNum 生成的密码的长度
     * @return
     */
    public static String getRamdonString(int maxNum) {

        int i; // 生成的随机数
        int count = 0;
        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer();
        Random r = new Random();
        while (count < maxNum) {
            // 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(50));
            if (i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }
}
